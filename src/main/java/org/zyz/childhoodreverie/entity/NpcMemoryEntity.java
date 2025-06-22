package org.zyz.childhoodreverie.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * NPC 记忆状态实体类
 * （保留复合主键设计，交给自定义 Mapper 处理）
 */
@Data
@TableName("npc_memory")
public class NpcMemoryEntity {
    /** NPC ID */
    private String npcId;

    /** 记忆键 */
    private String memoryKey;

    /** 记忆值 */
    private String memoryValue;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
