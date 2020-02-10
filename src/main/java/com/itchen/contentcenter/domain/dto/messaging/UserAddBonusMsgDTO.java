package com.itchen.contentcenter.domain.dto.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户添加积分消息体 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddBonusMsgDTO {

    /**
     * 为谁添加积分
     */
    private Integer userId;
    /**
     * 加多少积分
     */
    private Integer bonus;
}
