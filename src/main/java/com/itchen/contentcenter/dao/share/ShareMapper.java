package com.itchen.contentcenter.dao.share;

import com.itchen.contentcenter.domain.entity.share.Share;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

@Component
public interface ShareMapper extends Mapper<Share> {
}