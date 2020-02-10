package com.itchen.contentcenter.controller.content;

import com.itchen.contentcenter.domain.dto.content.ShareAuditDTO;
import com.itchen.contentcenter.domain.entity.share.Share;
import com.itchen.contentcenter.service.content.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * TODO .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-09
 */
@RestController
@RequestMapping("/admin/shares")
public class ShareAdminController {

    @Autowired
    private ShareService shareService;

    @PutMapping("/audit/{id}")
    public Share auditById(@PathVariable Integer id, @RequestBody ShareAuditDTO auditDTO) {
        // 认证、授权

        return shareService.auditById(id, auditDTO);
    }


}
