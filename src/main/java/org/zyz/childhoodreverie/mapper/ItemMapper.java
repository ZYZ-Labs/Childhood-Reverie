package org.zyz.childhoodreverie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.zyz.childhoodreverie.entity.ItemEntity;

/**
 * Item mapper
 */
@Mapper
public interface ItemMapper extends BaseMapper<ItemEntity> {
    /**
     * clear table
     */
    @Delete("""
        DELETE FROM item_info WHERE 1=1
    """)
    void clear();
}
