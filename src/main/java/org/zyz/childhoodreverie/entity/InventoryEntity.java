
package org.zyz.childhoodreverie.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 玩家背包物品实体类
 */
@Data
@TableName("player_inventory")
public class InventoryEntity {
    /**
     * 自增主键
     */
    @TableId
    private Long id;

    /**
     * 玩家 ID
     */
    private String playerId;

    /**
     * 物品名称
     */
    private String itemName;

    /**
     * 数量
     */
    private int quantity;
}
