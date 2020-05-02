package com.itchen.contentcenter.controller.content;

import com.github.pagehelper.PageInfo;
import com.itchen.contentcenter.auth.Authentication;
import com.itchen.contentcenter.domain.dto.content.ShareDTO;
import com.itchen.contentcenter.domain.entity.share.Share;
import com.itchen.contentcenter.service.content.ShareService;
import com.itchen.contentcenter.util.JwtOperator;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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

    @Autowired
    private JwtOperator jwtOperator;

    @GetMapping("/{id}")
    @Authentication
    public ShareDTO findById(@PathVariable Integer id) {
        return shareService.findById(id);
    }

    @GetMapping(value = "/q")
    public PageInfo<Share> q(@RequestParam(required = false) String title,
                             @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                             @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                             @RequestHeader(value = "X-Token", required = false) String token) {
        // 注意点：pageSize 务必做控制
        if (pageSize > 100) {
            pageSize = 100;
        }
        Integer userId = null;
        if (StringUtils.isNotBlank(token)) {
            Claims claims = this.jwtOperator.getClaimsFromToken(token);
            userId = (Integer) claims.get("id");
        }
        return this.shareService.q(title, pageNo, pageSize, userId);
    }

    @GetMapping(value = "/exchange/{id}")
    @Authentication
    public Share exchangeById(@PathVariable Integer id, HttpServletRequest request) {
        return this.shareService.exchangeById(id, request);
    }

}
