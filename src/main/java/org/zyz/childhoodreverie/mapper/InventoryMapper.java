package org.zyz.childhoodreverie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.zyz.childhoodreverie.entity.InventoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Delete;

/**
 * 玩家背包 Mapper
 */
@Mapper
public interface InventoryMapper extends BaseMapper<InventoryEntity> {
    /**
     * \u6e05\u8868
     */
    @Delete("""
        DELETE FROM player_inventory WHERE 1=1
    """)
    void clear();
}