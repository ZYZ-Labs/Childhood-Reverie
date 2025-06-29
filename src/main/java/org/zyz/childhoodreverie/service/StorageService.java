package org.zyz.childhoodreverie.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.zyz.childhoodreverie.entity.*;
import org.zyz.childhoodreverie.mapper.*;

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
    private WorldStateMapper worldStateMapper;
    @Autowired
    private EventLogMapper eventLogMapper;
    @Autowired
    private NpcMemoryMapper npcMemoryMapper;

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

    // 背包操作
    public List<InventoryEntity> getInventory(String playerId) {
        return inventoryMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<InventoryEntity>()
                        .eq("player_id", playerId)
        );
    }

    public void saveInventory(List<InventoryEntity> items) {
        inventoryMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<InventoryEntity>()
                        .eq("player_id", items.get(0).getPlayerId())
        );
        items.forEach(inventoryMapper::insert);
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