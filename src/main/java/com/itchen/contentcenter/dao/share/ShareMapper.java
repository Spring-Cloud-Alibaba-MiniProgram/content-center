package com.itchen.contentcenter.dao.share;

import com.itchen.contentcenter.domain.entity.share.Share;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Component
public interface ShareMapper extends Mapper<Share> {
    List<Share> selectByParam(@Param("title") String title);
}