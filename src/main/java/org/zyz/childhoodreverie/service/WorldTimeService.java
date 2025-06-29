package org.zyz.childhoodreverie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.zyz.childhoodreverie.entity.WorldStateEntity;
import org.zyz.childhoodreverie.mapper.EventLogMapper;
import org.zyz.childhoodreverie.mapper.NpcMemoryMapper;
import org.zyz.childhoodreverie.mapper.PlayerMapper;
import org.zyz.childhoodreverie.mapper.WorldStateMapper;

import javax.annotation.PostConstruct;
import java.time.Instant;

/**
 * 世界时间推进服务，同步现实时间，可调速
 */
@Service
public class WorldTimeService {

    @Autowired
    private WorldStateMapper worldStateMapper;

    @Autowired
    private NpcMemoryMapper npcMemoryMapper;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private EventLogMapper eventLogMapper;

    private static final String KEY_WORLD_TIME = "world_time";
    private static final String KEY_LAST_REAL = "last_real_time";
    private static final String KEY_MULTIPLIER = "time_multiplier";

    @PostConstruct
    public void init() {
        initKey(KEY_WORLD_TIME, String.valueOf(Instant.now().toEpochMilli()));
        initKey(KEY_LAST_REAL, String.valueOf(Instant.now().toEpochMilli()));
        initKey(KEY_MULTIPLIER, "1.0");
    }

    private void initKey(String key, String defaultValue) {
        if (worldStateMapper.selectById(key) == null) {
            WorldStateEntity entity = new WorldStateEntity();
            entity.setKeyName(key);
            entity.setValue(defaultValue);
            worldStateMapper.insert(entity);
        }
    }

    /**
     * 每秒推进世界时间，并根据 multiplier 调速
     */
    @Scheduled(fixedRate = 1000)
    public void tick() {
        WorldStateEntity timeEntity = worldStateMapper.selectById(KEY_WORLD_TIME);
        WorldStateEntity realEntity = worldStateMapper.selectById(KEY_LAST_REAL);
        WorldStateEntity mulEntity = worldStateMapper.selectById(KEY_MULTIPLIER);

        long lastReal = Long.parseLong(realEntity.getValue());
        long nowReal = Instant.now().toEpochMilli();
        double multiplier = Double.parseDouble(mulEntity.getValue());

        long deltaReal = nowReal - lastReal;
        long deltaWorld = (long) (deltaReal * multiplier);

        long worldTime = Long.parseLong(timeEntity.getValue()) + deltaWorld;

        timeEntity.setValue(String.valueOf(worldTime));
        realEntity.setValue(String.valueOf(nowReal));

        worldStateMapper.updateById(timeEntity);
        worldStateMapper.updateById(realEntity);
    }

    /**
     * 获取世界历字符串, 格式: 年-月-日 时:分:秒 (自定义历法: 12个月, 每月30天)
     */
    public String getWorldCalendar() {
        long ms = getWorldTime();
        long totalSeconds = ms / 1000;
        long daysTotal = totalSeconds / 86400;
        long year = daysTotal / 360 + 1;
        long dayOfYear = daysTotal % 360;
        long month = dayOfYear / 30 + 1;
        long day = dayOfYear % 30 + 1;
        long secsOfDay = totalSeconds % 86400;
        long hour = secsOfDay / 3600;
        long minute = (secsOfDay % 3600) / 60;
        long second = secsOfDay % 60;
        return String.format("世界历 %d年%d月%d日 %02d:%02d:%02d",
                year, month, day, hour, minute, second);
    }

    public long getWorldTime() {
        return Long.parseLong(worldStateMapper.selectById(KEY_WORLD_TIME).getValue());
    }

    public double getMultiplier() {
        return Double.parseDouble(worldStateMapper.selectById(KEY_MULTIPLIER).getValue());
    }

    public void setMultiplier(double multiplier) {
        WorldStateEntity m = new WorldStateEntity();
        m.setKeyName(KEY_MULTIPLIER);
        m.setValue(String.valueOf(multiplier));
        worldStateMapper.updateById(m);
    }

    public void resetWorld() {
        // 重置时间、倍率等状态
        setMultiplier(1.0);
        npcMemoryMapper.clear();
        playerMapper.clear();
        eventLogMapper.clear();
        // 如果有其他状态（比如天数、天气、事件列表等）也在这里一并清空或初始化
    }
}
