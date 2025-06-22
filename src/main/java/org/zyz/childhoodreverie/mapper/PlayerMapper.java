package org.zyz.childhoodreverie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.zyz.childhoodreverie.entity.PlayerEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 玩家信息 Mapper
 */
@Mapper
public interface PlayerMapper extends BaseMapper<PlayerEntity> {
}