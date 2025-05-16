package org.zepe.pichub.mapper;

import org.apache.ibatis.annotations.Select;
import org.zepe.pichub.model.entity.Space;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author zzpus
 * @description 针对表【space(空间)】的数据库操作Mapper
 * @createDate 2025-05-14 14:22:49
 * @Entity org.zepe.pichub.model.entity.Space
 */
public interface SpaceMapper extends BaseMapper<Space> {
    @Select("SELECT id,spaceName,userId,totalSize FROM space ORDER BY totalSize DESC LIMIT #{topN}")
    List<Space> getTopNSpaceUsage(int topN);
}




