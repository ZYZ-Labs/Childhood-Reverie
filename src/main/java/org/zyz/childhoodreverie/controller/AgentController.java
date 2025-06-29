package org.zyz.childhoodreverie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.zyz.childhoodreverie.service.OllamaAgentService;
import org.zyz.childhoodreverie.service.StorageService;
import org.zyz.childhoodreverie.entity.EventLogEntity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 事件触发 Controller
 */
@RestController
@RequestMapping("/agent")
public class AgentController {
    private Logger logger = Logger.getLogger(AgentController.class.getSimpleName());
    private final OllamaAgentService ollamaAgentService;
    private final StorageService storageService;

    @Autowired
    public AgentController(OllamaAgentService ollamaAgentService, StorageService storageService) {
        this.ollamaAgentService = ollamaAgentService;
        this.storageService = storageService;
    }

    @PostMapping("/event")
    public Map<String, Object> triggerEvent(@RequestBody Map<String, Object> request) {
        logger.info(request.toString());
        // 调用 AI 生成事件
        Map<String, Object> aiResult = ollamaAgentService.generateEvent(request);

        // 持久化到 event_log
        EventLogEntity log = new EventLogEntity();
        log.setPlayerId((String) ((Map<?, ?>) request.get("player")).get("playerId"));
        log.setLocation((String) request.get("location"));
        log.setDay((Integer) request.get("day"));
        log.setEventTitle((String) aiResult.get("eventTitle"));
        log.setEventDescription((String) aiResult.get("eventDescription"));
        log.setRawAiResponse(aiResult.toString());
        log.setCreatedAt(LocalDateTime.now());

        storageService.recordEvent(log);

        return aiResult;
    }
}