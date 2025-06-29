package org.zyz.childhoodreverie.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Item definition entity
 */
@Data
@TableName("item_info")
public class ItemEntity {
    /** item unique ID */
    @TableId
    private String itemId;
    /** display name */
    private String name;
    /** item type */
    private String type;
    /** item description */
    private String description;
}
