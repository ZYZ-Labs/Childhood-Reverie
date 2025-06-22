
package org.zyz.childhoodreverie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.zyz.childhoodreverie.entity.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 事件自动生成服务（仅在 event.auto=true 时启用）
 */
@Service
@ConditionalOnProperty(prefix = "event", name = "auto", havingValue = "true")
public class EventAutoGenerator {

    @Autowired
    private WorldTimeService worldTimeService;
    @Autowired
    private OllamaAgentService agentService;
    @Autowired
    private StorageService storageService;

    private static final String KEY_LAST_EVENT_TIME = "last_event_time";
    private static final long INTERVAL_MS = 6 * 60 * 60 * 1000; // 每6小时生成事件

    @Scheduled(fixedRate = 60 * 60 * 1000) // 每小时检查一次
    public void generateEventsIfDue() {
        long worldTime = worldTimeService.getWorldTime();
        WorldStateEntity checkpoint = storageService.getWorldState(KEY_LAST_EVENT_TIME);
        long last = Long.parseLong(checkpoint.getValue());

        if (worldTime - last >= INTERVAL_MS) {
            List<PlayerEntity> players = storageService.getAllPlayers();
            for (PlayerEntity p : players) {
                Map<String, Object> ctx = Map.of(
                        "player", Map.of(
                                "playerId", p.getPlayerId(),
                                "name", p.getName(),
                                "level", p.getLevel()
                        ),
                        "location", p.getLocation(),
                        "day", worldTime / (24 * 60 * 60 * 1000),
                        "history", storageService.getEventHistorySummaries(p.getPlayerId())
                );
                // 调用 generateEvent 返回 Map<String,String>
                Map<String, Object> aiEvent = agentService.generateEvent(ctx);

                EventLogEntity log = new EventLogEntity();
                log.setPlayerId(p.getPlayerId());
                log.setLocation(p.getLocation());
                log.setDay((int) (worldTime / (24 * 60 * 60 * 1000)));
                log.setEventTitle(aiEvent.get("eventTitle").toString());
                log.setEventDescription(aiEvent.get("eventDescription").toString());
                log.setRawAiResponse(aiEvent.toString());
                log.setCreatedAt(LocalDateTime.now());
                storageService.recordEvent(log);
            }
            checkpoint.setValue(String.valueOf(worldTime));
            checkpoint.setUpdatedAt(LocalDateTime.now());
            storageService.saveWorldState(checkpoint);
        }
    }
}
