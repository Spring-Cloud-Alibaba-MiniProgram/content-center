package com.itchen.contentcenter.dao.messaging;

import com.itchen.contentcenter.domain.entity.messaging.RocketmqTransactionLog;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

@Component
public interface RocketmqTransactionLogMapper extends Mapper<RocketmqTransactionLog> {
}