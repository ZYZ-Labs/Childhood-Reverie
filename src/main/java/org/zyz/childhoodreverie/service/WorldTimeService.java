package org.zyz.childhoodreverie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.zyz.childhoodreverie.entity.WorldStateEntity;
import org.zyz.childhoodreverie.mapper.EventLogMapper;
import org.zyz.childhoodreverie.mapper.NpcMemoryMapper;
import org.zyz.childhoodreverie.mapper.PlayerMapper;
import org.zyz.childhoodreverie.mapper.WorldStateMapper;
import org.zyz.childhoodreverie.mapper.InventoryMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

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

    @Autowired
    private InventoryMapper inventoryMapper;

    private static final String KEY_WORLD_TIME = "world_time";
    private static final String KEY_LAST_REAL = "last_real_time";
    private static final String KEY_MULTIPLIER = "time_multiplier";
    private static final String KEY_LAST_EVENT_TIME = "last_event_time";
    private static final String KEY_WORLD_DAY = "world_day";
    private static final String KEY_WEATHER = "weather";

    @PostConstruct
    public void init() {
        initKey(KEY_WORLD_TIME, String.valueOf(Instant.now().toEpochMilli()));
        initKey(KEY_LAST_REAL, String.valueOf(Instant.now().toEpochMilli()));
        initKey(KEY_LAST_EVENT_TIME, String.valueOf(Instant.now().toEpochMilli()));
        initKey(KEY_MULTIPLIER, "1.0");
        initKey(KEY_WORLD_DAY, "0");
        initKey(KEY_WEATHER, "clear");
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
        WorldStateEntity dayEntity = worldStateMapper.selectById(KEY_WORLD_DAY);

        long lastReal = Long.parseLong(realEntity.getValue());
        long nowReal = Instant.now().toEpochMilli();
        double multiplier = Double.parseDouble(mulEntity.getValue());

        long deltaReal = nowReal - lastReal;
        long deltaWorld = (long) (deltaReal * multiplier);

        long worldTime = Long.parseLong(timeEntity.getValue()) + deltaWorld;

        timeEntity.setValue(String.valueOf(worldTime));
        realEntity.setValue(String.valueOf(nowReal));
        if (dayEntity != null) {
            long worldDay = worldTime / (24 * 60 * 60 * 1000);
            dayEntity.setValue(String.valueOf(worldDay));
            worldStateMapper.updateById(dayEntity);
        }

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

    public String getWeather() {
        WorldStateEntity e = worldStateMapper.selectById(KEY_WEATHER);
        return e == null ? null : e.getValue();
    }

    public void setWeather(String weather) {
        WorldStateEntity e = new WorldStateEntity();
        e.setKeyName(KEY_WEATHER);
        e.setValue(weather);
        if (worldStateMapper.selectById(KEY_WEATHER) == null) {
            worldStateMapper.insert(e);
        } else {
            worldStateMapper.updateById(e);
        }
    }

    public long getWorldDay() {
        WorldStateEntity e = worldStateMapper.selectById(KEY_WORLD_DAY);
        return e == null ? 0L : Long.parseLong(e.getValue());
    }

    public void setWorldDay(long day) {
        WorldStateEntity e = new WorldStateEntity();
        e.setKeyName(KEY_WORLD_DAY);
        e.setValue(String.valueOf(day));
        if (worldStateMapper.selectById(KEY_WORLD_DAY) == null) {
            worldStateMapper.insert(e);
        } else {
            worldStateMapper.updateById(e);
        }
    }

    public void resetWorld() {
        // 重置时间、倍率等状态
        setMultiplier(1.0);
        npcMemoryMapper.clear();
        playerMapper.clear();
        inventoryMapper.clear();
        eventLogMapper.clear();
        // 重置除世界时间外的其他 world_state 键
        long now = Instant.now().toEpochMilli();

        WorldStateEntity lastReal = new WorldStateEntity();
        lastReal.setKeyName(KEY_LAST_REAL);
        lastReal.setValue(String.valueOf(now));
        worldStateMapper.updateById(lastReal);

        WorldStateEntity lastEvent = new WorldStateEntity();
        lastEvent.setKeyName(KEY_LAST_EVENT_TIME);
        lastEvent.setValue(String.valueOf(now));
        worldStateMapper.updateById(lastEvent);

        QueryWrapper<WorldStateEntity> wrapper = new QueryWrapper<>();
        wrapper.notIn("key_name", KEY_WORLD_TIME, KEY_LAST_REAL, KEY_MULTIPLIER, KEY_LAST_EVENT_TIME);
        worldStateMapper.delete(wrapper);
        // 初始化其他状态
        setWorldDay(0);
        setWeather("clear");
    }
}
