package com.itchen.contentcenter.auth;

import com.itchen.contentcenter.api.R;
import com.itchen.contentcenter.exceptions.AuthSecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-28
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理认证异常
     * 可以使用 ResponseEntity 返回 HTTP 状态码，也可以使用 @ResponseStatus(HttpStatus.UNAUTHORIZED)
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AuthSecurityException.class)
    public ResponseEntity<R> error(AuthSecurityException e) {
        log.warn("【发生 AuthSecurityException 异常】", e);
        return new ResponseEntity<>(
                R.failed("Token 非法，用户不允许访问！"),
                HttpStatus.UNAUTHORIZED
        );
    }

}
