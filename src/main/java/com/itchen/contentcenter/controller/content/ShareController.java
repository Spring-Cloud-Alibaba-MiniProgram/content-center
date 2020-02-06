package com.itchen.contentcenter.controller.content;

import com.itchen.contentcenter.domain.dto.content.ShareDTO;
import com.itchen.contentcenter.service.content.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分析控制器 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-06
 */
@RestController
@RequestMapping("/shares")
public class ShareController {

    @Autowired
    private ShareService shareService;

    @GetMapping("/{id}")
    public ShareDTO findById(@PathVariable Integer id) {
        return shareService.findById(id);
    }

}
