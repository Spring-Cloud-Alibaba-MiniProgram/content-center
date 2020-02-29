package com.itchen.contentcenter.exceptions;

import com.itchen.contentcenter.api.IRCode;

/**
 * 自定义安全认证异常 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-28
 */
public class AuthSecurityException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5885155226898287919L;

    /**
     * 状态码
     */
    private IRCode RCode;

    public AuthSecurityException(String message) {
        super(message);
    }

    public AuthSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthSecurityException(IRCode RCode, Throwable cause) {
        super(RCode.getMsg(), cause);
        this.RCode = RCode;
    }

    public AuthSecurityException(IRCode RCode) {
        super(RCode.getMsg());
        this.RCode = RCode;
    }

    public IRCode getRCode() {
        return RCode;
    }
}
