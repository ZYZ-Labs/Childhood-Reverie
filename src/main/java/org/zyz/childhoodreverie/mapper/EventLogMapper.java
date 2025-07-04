package org.zyz.childhoodreverie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.zyz.childhoodreverie.entity.EventLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 事件记录 Mapper
 */
@Mapper
public interface EventLogMapper extends BaseMapper<EventLogEntity> {
    /**
     * 清表
     */
    @Delete("""
        DELETE FROM event_log where 1=1
    """)
    void clear();
}