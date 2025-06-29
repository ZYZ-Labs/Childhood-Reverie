package org.zyz.childhoodreverie.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.zyz.childhoodreverie.entity.*;
import org.zyz.childhoodreverie.mapper.*;
import org.zyz.childhoodreverie.exception.EmptyInventoryException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 存档管理服务，封装玩家、世界、事件等读写操作
 */
@Service
public class StorageService {

    @Autowired
    private PlayerMapper playerMapper;
    @Autowired
    private InventoryMapper inventoryMapper;
    @Autowired
    private NpcMapper npcMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private WorldStateMapper worldStateMapper;
    @Autowired
    private EventLogMapper eventLogMapper;
    @Autowired
    private NpcMemoryMapper npcMemoryMapper;

    private static final String KEY_WEATHER = "weather";
    private static final String KEY_WORLD_DAY = "world_day";

    // 玩家操作
    public void savePlayer(PlayerEntity player) {
        playerMapper.insert(player);
    }

    /**
     * 获取所有玩家，用于事件自动生成
     */
    public List<PlayerEntity> getAllPlayers() {
        return playerMapper.selectList(new QueryWrapper<>());
    }

    public PlayerEntity getPlayer(String playerId) {
        return playerMapper.selectById(playerId);
    }

    // NPC basic info
    public void saveNpc(NpcEntity npc) {
        npcMapper.insert(npc);
    }

    public NpcEntity getNpc(String npcId) {
        return npcMapper.selectById(npcId);
    }

    public void updateNpc(NpcEntity npc) {
        npcMapper.updateById(npc);
    }

    // Item definitions
    public void saveItem(ItemEntity item) {
        itemMapper.insert(item);
    }

    public ItemEntity getItem(String itemId) {
        return itemMapper.selectById(itemId);
    }

    // 背包操作
    public List<InventoryEntity> getInventory(String playerId) {
        return inventoryMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<InventoryEntity>()
                        .eq("player_id", playerId)
        );
    }

    public void saveInventory(List<InventoryEntity> items) {
        if (items == null || items.isEmpty()) {
            throw new EmptyInventoryException("inventory items cannot be empty");
        }
        inventoryMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<InventoryEntity>()
                        .eq("player_id", items.get(0).getPlayerId())
        );
        items.forEach(inventoryMapper::insert);
    }

    public void addInventoryItem(InventoryEntity item) {
        inventoryMapper.insert(item);
    }

    public InventoryEntity getInventoryItem(Long id) {
        return inventoryMapper.selectById(id);
    }

    public void updateInventoryItem(InventoryEntity item) {
        inventoryMapper.updateById(item);
    }

    public void deleteInventoryItem(Long id) {
        inventoryMapper.deleteById(id);
    }

    // 世界状态操作
    public WorldStateEntity getWorldState(String key) {
        return worldStateMapper.selectById(key);
    }

    public void saveWorldState(WorldStateEntity state) {
        if (worldStateMapper.selectById(state.getKeyName()) != null) {
            worldStateMapper.updateById(state);
        } else {
            worldStateMapper.insert(state);
        }
    }

    public String getWeather() {
        WorldStateEntity e = getWorldState(KEY_WEATHER);
        return e == null ? null : e.getValue();
    }

    public void setWeather(String weather) {
        WorldStateEntity e = new WorldStateEntity();
        e.setKeyName(KEY_WEATHER);
        e.setValue(weather);
        saveWorldState(e);
    }

    public long getWorldDay() {
        WorldStateEntity e = getWorldState(KEY_WORLD_DAY);
        return e == null ? 0L : Long.parseLong(e.getValue());
    }

    public void setWorldDay(long day) {
        WorldStateEntity e = new WorldStateEntity();
        e.setKeyName(KEY_WORLD_DAY);
        e.setValue(String.valueOf(day));
        saveWorldState(e);
    }

    // 事件记录
    public void recordEvent(EventLogEntity event) {
        eventLogMapper.insert(event);
    }

    public List<EventLogEntity> getEventLog(String playerId) {
        return eventLogMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<EventLogEntity>()
                        .eq("player_id", playerId)
                        .orderByAsc("created_at")
        );
    }

    /**
     * 获取事件标题列表，用于历史摘要
     */
    public List<String> getEventHistorySummaries(String playerId) {
        return getEventLog(playerId).stream()
                .map(EventLogEntity::getEventTitle)
                .collect(Collectors.toList());
    }

    // NPC 记忆操作
    public NpcMemoryEntity getNpcMemory(String npcId, String key) {
        return npcMemoryMapper.getMemory(npcId, key);
    }

    public void saveNpcMemory(NpcMemoryEntity memory) {
        memory.setUpdatedAt(LocalDateTime.now());
        npcMemoryMapper.saveMemory(memory);
    }

    public void deleteNpcMemory(String npcId, String key) {
        npcMemoryMapper.deleteMemory(npcId, key);
    }
}