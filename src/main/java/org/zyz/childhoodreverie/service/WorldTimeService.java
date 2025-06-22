package org.zyz.childhoodreverie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.zyz.childhoodreverie.entity.WorldStateEntity;
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
}
