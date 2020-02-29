package com.itchen.contentcenter.auth;

import com.itchen.contentcenter.enums.ApiRCode;
import com.itchen.contentcenter.exceptions.AuthSecurityException;
import com.itchen.contentcenter.util.JwtOperator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 身份认证，检查用户是否登录切面类 .
 *
 * @author BibiChen
 * @version v1.0
 * @since 2020-02-28
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthAop {

    private final JwtOperator jwtOperator;

    @Around("@annotation(com.itchen.contentcenter.auth.Authentication)")
    public Object authentication(ProceedingJoinPoint point) throws Throwable {
        handle(point);
        checkToken();

        return point.proceed();
    }

    @Around("@annotation(com.itchen.contentcenter.auth.Authorization)")
    public Object authorization(ProceedingJoinPoint point) throws Throwable {
        // 1. 验证 token 是否合法
        this.checkToken();
        try {
            // 2. 验证用户权限是否匹配
            HttpServletRequest request = getHttpServletRequest();
            String role = (String) request.getAttribute("role");

            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            Authorization annotation = method.getAnnotation(Authorization.class);
            String hasRole = annotation.hasRole();

            if (!Objects.equals(role, hasRole)) {
                throw new AuthSecurityException(ApiRCode.UN_AUTHORIZATION);
            }
        } catch (Throwable throwable) {
            throw new AuthSecurityException(ApiRCode.UN_AUTHORIZATION, throwable);
        }
        return point.proceed();
    }

    private void checkToken() {
        try {
            // 1. 从 header 里面获取 token
            HttpServletRequest request = getHttpServletRequest();

            String token = request.getHeader("X-Token");

            // 2. 检验 token 是否合法&是否过期，如果不合法或已过期直接抛异常；如果合法则放行

            Boolean isValidate = jwtOperator.validateToken(token);
            if (!isValidate) {
                throw new AuthSecurityException(ApiRCode.TOKEN_INVALIDED);
            }

            // 3. 如果校验成功，那么就将用户的信息设置到 request 的 attribute 里面
            Claims claims = jwtOperator.getClaimsFromToken(token);

            request.setAttribute("id", claims.get("id"));
            request.setAttribute("wxNickname", claims.get("wxNickname"));
            request.setAttribute("role", claims.get("role"));
        } catch (Exception e) {
            throw new AuthSecurityException(ApiRCode.TOKEN_INVALIDED);
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getRequest();
    }

    private void handle(ProceedingJoinPoint point) throws Exception {
        // 获取拦截的类名
        String className = point.getTarget().getClass().getName();
        // 获取拦截的方法名
        Signature sig = point.getSignature();
        MethodSignature mSig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        mSig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(mSig.getName(), mSig.getParameterTypes());
        String methodName = currentMethod.getName();

        // 获取拦截方法的参数
        Object[] args = point.getArgs();

        // 获取注解的参数值
        Authentication annotation = currentMethod.getAnnotation(Authentication.class);
        String key = annotation.key();
        String value = annotation.value();
        log.info("【AuthAop 拦截的类名 = {}, 拦截的方法名 = {}, 拦截的方法参数 = {}】", className, methodName, args);
        log.info("【{} 方法注解 Auth 参数值 : key = {}, value = {}】", methodName, key, value);
    }

}
