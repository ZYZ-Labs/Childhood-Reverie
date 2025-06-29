package org.zyz.childhoodreverie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.zyz.childhoodreverie.entity.PlayerEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 玩家信息 Mapper
 */
@Mapper
public interface PlayerMapper extends BaseMapper<PlayerEntity> {
    /**
     * 清表
     */
    @Delete("""
        DELETE FROM player_info where 1=1
    """)
    void clear();
}