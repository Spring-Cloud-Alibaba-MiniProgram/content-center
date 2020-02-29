package com.itchen.contentcenter.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 身份认证，检查登录状态注解 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Authentication {

    /**
     * 实体的唯一标识,例如:用户实体的唯一标识为"id"
     */
    String key() default "id";

    /**
     * 业务的名称,例如:"用户认证"
     */
    String value() default "认证";

}
