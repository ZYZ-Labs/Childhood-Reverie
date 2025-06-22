package org.zyz.childhoodreverie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.zyz.childhoodreverie.entity.InventoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 玩家背包 Mapper
 */
@Mapper
public interface InventoryMapper extends BaseMapper<InventoryEntity> {
}