package com.itchen.contentcenter.domain.dto.content;

import com.itchen.contentcenter.domain.enums.AuditStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareAuditDTO {

    /**
     * 审核状态
     */
    private AuditStatusEnum auditStatusEnum;
    /**
     * 原因
     */
    private String reason;

}
