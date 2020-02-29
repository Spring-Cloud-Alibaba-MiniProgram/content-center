package com.itchen.contentcenter.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限认证，检查是否有权限 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Authorization {

    /**
     * 需要的角色名称,例如:"admin"
     */
    String hasRole() default "";

    /**
     * <p>权限标识</p>
     * <p>使用注解时加上这个值表示限制只有当前用户拥有该权限标识才可以访问对应的资源</p>
     * <p>常用在某些资源限制只有超级管理员角色才可访问</p>
     * <p>@Authorization({permit1,permit2})</p>
     */
    String[] permit() default {};

}
