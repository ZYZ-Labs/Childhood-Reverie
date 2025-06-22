package org.zyz.childhoodreverie.mapper;

import org.apache.ibatis.annotations.*;
import org.zyz.childhoodreverie.entity.NpcMemoryEntity;
import java.time.LocalDateTime;

@Mapper
public interface NpcMemoryMapper {

    @Select("""
      SELECT
        npc_id AS npcId,
        memory_key AS memoryKey,
        memory_value AS memoryValue,
        updated_at AS updatedAt
      FROM npc_memory
      WHERE npc_id = #{npcId} AND memory_key = #{memoryKey}
    """)
    NpcMemoryEntity getMemory(@Param("npcId") String npcId,
                              @Param("memoryKey") String memoryKey);

    @Insert("""
      INSERT INTO npc_memory (npc_id, memory_key, memory_value, updated_at)
      VALUES (#{npcId}, #{memoryKey}, #{memoryValue}, #{updatedAt})
      ON DUPLICATE KEY UPDATE
        memory_value = VALUES(memory_value),
        updated_at   = VALUES(updated_at)
    """)
    void saveMemory(NpcMemoryEntity memory);

    @Delete("""
      DELETE FROM npc_memory
      WHERE npc_id = #{npcId} AND memory_key = #{memoryKey}
    """)
    void deleteMemory(@Param("npcId") String npcId,
                      @Param("memoryKey") String memoryKey);
}
