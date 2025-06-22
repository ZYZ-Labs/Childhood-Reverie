
package org.zyz.childhoodreverie.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 玩家事件记录实体类
 */
@Data
@TableName("event_log")
public class EventLogEntity {
    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 玩家 ID
     */
    private String playerId;

    /**
     * 事件发生位置
     */
    private String location;

    /**
     * 第几天（游戏内）
     */
    private int day;

    /**
     * 事件标题
     */
    private String eventTitle;

    /**
     * 事件描述
     */
    private String eventDescription;

    /**
     * 原始 AI 返回数据（JSON）
     */
    private String rawAiResponse;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
