package com.itchen.contentcenter.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-09
 */
@Getter
@AllArgsConstructor
public enum AuditStatusEnum {

    /**
     * 待审核
     */
    NOT_YET,
    /**
     * 审核通过
     */
    PASS,
    /**
     * 审核不通过
     */
    REJECT

}
