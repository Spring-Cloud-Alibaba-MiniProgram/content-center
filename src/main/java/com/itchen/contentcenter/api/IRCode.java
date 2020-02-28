package com.itchen.contentcenter.api;

/**
 * REST API 状态码接口 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-28
 */
public interface IRCode {

    /**
     * 业务状态码
     */
    long getCode();

    /**
     * 描述
     */
    String getMsg();
}
