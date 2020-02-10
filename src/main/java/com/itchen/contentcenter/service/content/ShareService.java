package com.itchen.contentcenter.service.content;

import com.alibaba.fastjson.JSON;
import com.itchen.contentcenter.dao.messaging.RocketmqTransactionLogMapper;
import com.itchen.contentcenter.dao.share.ShareMapper;
import com.itchen.contentcenter.domain.dto.content.ShareAuditDTO;
import com.itchen.contentcenter.domain.dto.content.ShareDTO;
import com.itchen.contentcenter.domain.dto.messaging.UserAddBonusMsgDTO;
import com.itchen.contentcenter.domain.dto.user.UserDTO;
import com.itchen.contentcenter.domain.entity.messaging.RocketmqTransactionLog;
import com.itchen.contentcenter.domain.entity.share.Share;
import com.itchen.contentcenter.domain.enums.AuditStatusEnum;
import com.itchen.contentcenter.feignclient.UserCenterFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.UUID;

/**
 * 分享服务 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-06
 */
@Service
@Slf4j
public class ShareService {

    @Autowired
    private ShareMapper shareMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserCenterFeignClient userCenterFeignClient;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;

    @Autowired
    private Source source;

    public ShareDTO findById(Integer id) {
        // 获取分享详情
        Share share = this.shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();
        // 怎么调用用户微服务的 /users/{id}

        // 1. 代码不可读
        // 2. 复制 URL 难以维护，https://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=1&tn=02003390_42_hao_pg&wd=%E8%A7%86%E9%A2%91&oq=aaa&rsv_pq=c27d61f2000068e5&rsv_t=ab73spP6qpdFF7go6UQnqMRcbceWjDqmAFkKY0FXaFe8%2FH7BIuVjMx3TUrZLDvmSCmU%2BFWt54U0a&rqlang=cn&rsv_enter=0&rsv_dl=tb&inputT=731&rsv_sug3=8&rsv_sug1=1&rsv_sug7=100&rsv_sug2=0&rsv_sug4=731
        // 3. 难以响应需求的变化，变化很没有幸福感
        // 4. 编程体验不统一
        // UserDTO userDTO = restTemplate.getForObject(
        //         "http://user-center/users/{userId}",
        //         UserDTO.class, userId
        // );

        // 使用 FeignClient 调用
        UserDTO userDTO = userCenterFeignClient.findById(userId);

        ShareDTO shareDTO = new ShareDTO();
        // 消息装配
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());

        return shareDTO;
    }

    public Share auditById(Integer id, ShareAuditDTO auditDTO) {
        // 1. 查询 share 是否存在，不存在或者当前的 audit_status != NOT_YET，那么抛异常
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("参数非法！该分享不存在！");
        }
        if (!Objects.equals("NOT_YET", share.getAuditStatus())) {
            throw new IllegalArgumentException("参数非法！该分享已审核通过或审核不通过！");
        }
        // 3. 如果是 PASS，那么发送消息给 rocketmq，让用户中心去消费，并为发布人添加积分
        if (AuditStatusEnum.PASS.equals(auditDTO.getAuditStatusEnum())) {
            // 发送半消息...
            UserAddBonusMsgDTO msgDTO = UserAddBonusMsgDTO.builder()
                    .userId(id)
                    .bonus(100)
                    .build();
            String transactionId = UUID.randomUUID().toString();

            this.source.output()
                    .send(
                            MessageBuilder.withPayload(msgDTO)
                                    // header 也有妙用...
                                    .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                                    .setHeader("share_id", id)
                                    .setHeader("dto", JSON.toJSONString(auditDTO))
                                    .build()
                    );

            // this.rocketMQTemplate.sendMessageInTransaction(
            //         "tx-add-bonus-group",
            //         "add-bonus",
            //         MessageBuilder.withPayload(msgDTO)
            //                 // header 也有妙用...
            //                 .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
            //                 .setHeader("share_id", id)
            //                 .build(),
            //         // arg 有大用处
            //         auditDTO
            // );
        } else {
            this.auditByIdInDB(id, auditDTO);
        }
        return share;
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDB(Integer id, ShareAuditDTO auditDTO) {
        Share share = Share.builder()
                .id(id)
                .auditStatus(auditDTO.getAuditStatusEnum().toString())
                .reason(auditDTO.getReason())
                .build();

        this.shareMapper.updateByPrimaryKeySelective(share);
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id, ShareAuditDTO auditDTO, String transactionId) {
        this.auditByIdInDB(id, auditDTO);

        this.rocketmqTransactionLogMapper.insert(
                RocketmqTransactionLog.builder()
                        .transactionId(transactionId)
                        .log("审核分享...")
                        .build()
        );
    }
}
