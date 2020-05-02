package com.itchen.contentcenter.service.content;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.itchen.contentcenter.dao.content.MidUserShareMapper;
import com.itchen.contentcenter.dao.messaging.RocketmqTransactionLogMapper;
import com.itchen.contentcenter.dao.share.ShareMapper;
import com.itchen.contentcenter.domain.dto.content.ShareAuditDTO;
import com.itchen.contentcenter.domain.dto.content.ShareDTO;
import com.itchen.contentcenter.domain.dto.messaging.UserAddBonusMsgDTO;
import com.itchen.contentcenter.domain.dto.user.UserAddBonusDTO;
import com.itchen.contentcenter.domain.dto.user.UserDTO;
import com.itchen.contentcenter.domain.entity.content.MidUserShare;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Autowired
    private MidUserShareMapper midUserShareMapper;

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

    public PageInfo<Share> q(String title, Integer pageNo, Integer pageSize, Integer userId) {
        // 它会切入下面这条不分页的 SQL，自动拼接分页的 SQL
        // 本质上是利用的 mybatis 的拦截器
        PageHelper.startPage(pageNo, pageSize);
        List<Share> shares = this.shareMapper.selectByParam(title);

        // 1. 如果用户未登录，那么 downloadUrl 全部设为 null
        List<Share> shareDeal = new ArrayList<>();
        if (userId == null) {
            shareDeal = shares.stream()
                    .peek(o -> o.setDownloadUrl(null))
                    .collect(Collectors.toList());
        }
        // 2. 如果用户登录了，那么查询一下 mid_user_share，如果没有数据，那么这条 share 的 downloadUrl 也设为 null
        else {
            shareDeal = shares.stream()
                    .peek(share -> {
                        MidUserShare midUserShare = this.midUserShareMapper.selectOne(
                                MidUserShare.builder()
                                        .userId(userId)
                                        .shareId(share.getId())
                                        .build()
                        );
                        if (midUserShare == null) {
                            share.setDownloadUrl(null);
                        }
                    })
                    .collect(Collectors.toList());
        }

        return new PageInfo<>(shareDeal);
    }

    public Share exchangeById(Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("id");
        // 1. 根据 id 查询 share，校验是否存在
        Share share = this.shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new IllegalArgumentException("该分享不存才！");
        }
        // 2. 如果当前用户已经兑换过该分享，则直接返回
        MidUserShare midUserShare = this.midUserShareMapper.selectOne(
                MidUserShare.builder()
                        .shareId(id)
                        .userId(userId)
                        .build()
        );
        if (midUserShare != null) {
            return share;
        }

        // 3. 根据当前登录的用户 id，查询积分是否够
        UserDTO userDTO = this.userCenterFeignClient.findById(userId);
        Integer price = share.getPrice();
        if (price > userDTO.getBonus()) {
            throw new IllegalArgumentException("用户积分不够！");
        }
        // 4. 扣减积分 & 往 mid_user_share 里插入一条数据
        this.userCenterFeignClient.addBonus(
                UserAddBonusDTO.builder()
                        .userId(userId)
                        .bonus(-price)
                        .build()
        );
        this.midUserShareMapper.insert(
                MidUserShare.builder()
                        .userId(userId)
                        .shareId(id)
                        .build()
        );

        return share;
    }
}
