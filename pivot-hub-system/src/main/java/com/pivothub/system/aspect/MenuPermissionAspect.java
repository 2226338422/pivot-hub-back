package com.pivothub.system.aspect;

import com.pivothub.common.exception.SystemException;
import com.pivothub.common.util.TLUtil;
import com.pivothub.pojo.po.system.SysUser;
import com.pivothub.system.annotation.MenuPermission;
import com.pivothub.system.mapper.SysMenuMapper;
import com.pivothub.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
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

import static com.pivothub.commoncore.constants.system.RoleManagementConstants.ENABLED;

/** 在带菜单权限注解的控制器入口统一校验当前用户权限。 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
@RequiredArgsConstructor
public class MenuPermissionAspect {
    private final SysMenuMapper menuMapper;
    private final SysUserMapper userMapper;

    @Around("execution(public * com.pivothub.system.controller..*(..)) && "
            + "(@within(com.pivothub.system.annotation.MenuPermission) "
            + "|| @annotation(com.pivothub.system.annotation.MenuPermission))")
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
            throw new SystemException(500, "菜单权限配置不能为空");
        }
        String operatorId = TLUtil.get(TLUtil.USER_ID);
        if (!StringUtils.hasText(operatorId)) {
            throw new SystemException(400, "操作人不能为空");
        }
        SysUser user = userMapper.selectById(operatorId);
        if (user == null || !ENABLED.equals(user.getStatus())) {
            throw new SystemException(403, "用户不存在或已禁用");
        }
        if (menuMapper.countUserMenuAccess(operatorId, permission.value().trim()) == 0) {
            throw new SystemException(403, "无对应菜单访问权限");
        }
        return joinPoint.proceed();
    }
}
