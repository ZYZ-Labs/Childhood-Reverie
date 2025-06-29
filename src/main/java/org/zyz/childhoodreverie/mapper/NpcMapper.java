package org.zyz.childhoodreverie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.zyz.childhoodreverie.entity.NpcEntity;

/**
 * NPC info mapper
 */
@Mapper
public interface NpcMapper extends BaseMapper<NpcEntity> {
    /**
     * clear table
     */
    @Delete("""
        DELETE FROM npc_info WHERE 1=1
    """)
    void clear();
}
