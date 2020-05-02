package com.itchen.contentcenter.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-05-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddBonusDTO {

    private Integer userId;

    /**
     * 积分
     */
    private Integer bonus;

}
