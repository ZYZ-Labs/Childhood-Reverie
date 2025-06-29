package org.zyz.childhoodreverie.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * NPC basic info entity
 */
@Data
@TableName("npc_info")
public class NpcEntity {
    /** NPC unique ID */
    @TableId
    private String npcId;
    /** NPC name */
    private String name;
    /** level */
    private int level;
    /** current location */
    private String location;
}
