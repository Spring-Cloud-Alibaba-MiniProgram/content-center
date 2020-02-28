package com.itchen.contentcenter.auth;

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

    @Around("@annotation(com.itchen.contentcenter.auth.Auth)")
    public Object auth(ProceedingJoinPoint point) {
        try {
            handle(point);
            // 1. 从 header 里面获取 token
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = servletRequestAttributes.getRequest();

            String token = request.getHeader("X-Token");

            // 2. 检验 token 是否合法&是否过期，如果不合法或已过期直接抛异常；如果合法则放行
            Boolean isValidate = jwtOperator.validateToken(token);

            if (!isValidate) {
                throw new AuthSecurityException("Token 不合法！");
            }
            // 3. 如果校验成功，那么就将用户的信息设置到 request 的 attribute 里面
            Claims claims = jwtOperator.getClaimsFromToken(token);

            request.setAttribute("id", claims.get("id"));
            request.setAttribute("wxNickname", claims.get("wxNickname"));
            request.setAttribute("role", claims.get("role"));

            return point.proceed();
        } catch (Throwable throwable) {
            throw new AuthSecurityException("Token 不合法！");
        }
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
        Auth annotation = currentMethod.getAnnotation(Auth.class);
        String key = annotation.key();
        String value = annotation.value();
        log.info("【AuthAop 拦截的类名 = {}, 拦截的方法名 = {}, 拦截的方法参数 = {}】", className, methodName, args);
        log.info("【{} 方法注解 Auth 参数值 : key = {}, value = {}】", methodName, key, value);
    }

}
