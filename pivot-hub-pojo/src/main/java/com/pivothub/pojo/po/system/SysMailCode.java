package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 邮箱验证码实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMailCode extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 邮箱地址 */
    private String email;

    /** 验证码 */
    private String code;

    /** 用途 register-注册 login-登录 reset-重置密码 */
    private String scene;

    /** 是否已使用 0-未使用 1-已使用 */
    private Integer used;

    /** 过期时间 */
    private Date expireTime;
}
