
package org.zyz.childhoodreverie.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 玩家基本信息实体类
 */
@Data
@TableName("player_info")
public class PlayerEntity {
    /**
     * 玩家唯一ID
     */
    @TableId
    private String playerId;

    /**
     * 玩家名称
     */
    private String name;

    /**
     * 当前等级
     */
    private int level;

    /**
     * 所在位置
     */
    private String location;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActive;
}
