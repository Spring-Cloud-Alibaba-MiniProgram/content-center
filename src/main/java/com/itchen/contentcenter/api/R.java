package com.itchen.contentcenter.api;

import com.itchen.contentcenter.enums.ApiRCode;
import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

/**
 * REST API 返回结果 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-28
 */
@Data
public class R<T> implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码 .
     */
    private Long code;
    /**
     * 描述 .
     */
    private String msg;
    /**
     * 结果集 .
     */
    private T data;

    public R() {

    }

    public R(IRCode RCode) {
        RCode = Optional.ofNullable(RCode).orElse(ApiRCode.FAILED);
        this.code = RCode.getCode();
        this.msg = RCode.getMsg();
    }

    public static <T> R<T> ok(T data) {
        ApiRCode aec = ApiRCode.SUCCESS;
        if (data instanceof Boolean && Boolean.FALSE.equals(data)) {
            aec = ApiRCode.FAILED;
        }
        return restResult(data, aec);
    }

    public static <T> R<T> failed(String msg) {
        return restResult(null, ApiRCode.FAILED.getCode(), msg);
    }

    public static <T> R<T> failed(IRCode RCode) {
        return restResult(null, RCode);
    }

    public static <T> R<T> restResult(T data, IRCode RCode) {
        return restResult(data, RCode.getCode(), RCode.getMsg());
    }

    private static <T> R<T> restResult(T data, long code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

}
