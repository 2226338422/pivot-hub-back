package com.pivothub.business.aspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.pivothub.business.annotation.MenuPermission;
import com.pivothub.common.exception.BusinessException;
import com.pivothub.common.feign.SystemMenuClient;
import com.pivothub.common.util.TLUtil;
import com.pivothub.pojo.dto.system.feign.CheckMenuAccessDto;
import feign.FeignException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/** 在业务控制器入口检查已认证用户的菜单权限。 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class MenuPermissionAspect {
    @Autowired
    @Lazy
    private SystemMenuClient menuClient;

    @Around("execution(public * com.pivothub.business..controller..*(..)) && "
            + "(@within(com.pivothub.business.annotation.MenuPermission) "
            + "|| @annotation(com.pivothub.business.annotation.MenuPermission))")
    public Object checkMenuPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> targetClass = AopUtils.getTargetClass(joinPoint.getTarget());
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = BridgeMethodResolver.findBridgedMethod(
                AopUtils.getMostSpecificMethod(signature.getMethod(), targetClass));
        MenuPermission permission = AnnotatedElementUtils.findMergedAnnotation(method, MenuPermission.class);
        if (permission == null) {
            permission = AnnotatedElementUtils.findMergedAnnotation(signature.getMethod(), MenuPermission.class);
        }
        if (permission == null) {
            permission = AnnotatedElementUtils.findMergedAnnotation(targetClass, MenuPermission.class);
        }
        if (permission == null || !StringUtils.hasText(permission.value())) {
            throw new BusinessException(500, "菜单权限配置不能为空");
        }
        String userId = TLUtil.get(TLUtil.USER_ID);
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(500, "请求身份上下文不可用");
        }
        CheckMenuAccessDto dto = new CheckMenuAccessDto();
        dto.setUserId(userId);
        dto.setMenuCode(permission.value().trim());
        Boolean allowed;
        try {
            allowed = menuClient.checkAccess(dto);
        } catch (FeignException ex) {
            if (ex.status() == 403) throw new BusinessException(403, "无对应菜单访问权限");
            throw new BusinessException(503, "菜单服务暂时不可用，请稍后重试");
        } catch (Exception ex) {
            throw new BusinessException(503, "菜单服务暂时不可用，请稍后重试");
        }
        if (allowed == null) throw new BusinessException(503, "菜单服务暂时不可用，请稍后重试");
        if (!allowed) throw new BusinessException(403, "无对应菜单访问权限");
        return joinPoint.proceed();
    }
}
