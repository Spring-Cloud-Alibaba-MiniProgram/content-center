package com.itchen.contentcenter.controller;

import com.itchen.contentcenter.dao.share.ShareMapper;
import com.itchen.contentcenter.domain.entity.share.Share;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * 测试 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-05
 */
@RestController
public class TestController {

    @Autowired
    private ShareMapper shareMapper;

    @GetMapping("test")
    public List<Share> testInsert() {
        // 1. 插入
        Share share = new Share();
        share.setTitle("xxx");
        share.setCover("xxx");
        share.setAuditStatus("chen");
        share.setBuyCount(1);
        share.setCreateTime(new Date());
        share.setUpdateTime(new Date());
        this.shareMapper.insertSelective(share);
        // 2. 查询
        List<Share> shares = this.shareMapper.selectAll();
        return shares;
    }

}
