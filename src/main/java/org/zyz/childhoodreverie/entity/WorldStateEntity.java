
package org.zyz.childhoodreverie.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 世界全局状态实体类
 */
@Data
@TableName("world_state")
public class WorldStateEntity {
    /**
     * 状态键名（如：world_day, weather）
     */
    @TableId
    private String keyName;

    /**
     * 状态对应值
     */
    private String value;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
