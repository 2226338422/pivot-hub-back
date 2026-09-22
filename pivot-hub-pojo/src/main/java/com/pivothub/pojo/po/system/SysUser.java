package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 用户实体
 * @Author lhb
 * @CreateTime 2026/9/22 22:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 用户编码 */
    private String code;


    /** 昵称 */
    private String nickname;


    /** 邮箱 */
    private String email;

    /** 头像URL */
    private String avatar;

    /** 角色ID */
    private String roleId;

    /** 状态 0-禁用 1-正常 */
    private Integer status;

    /** 备注 */
    private String remark;
}
