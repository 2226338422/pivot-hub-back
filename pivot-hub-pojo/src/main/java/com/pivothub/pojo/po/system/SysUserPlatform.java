package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户平台账户实体（QQ/微信绑定等）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserPlatform extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    private String userId;

    /** QQ开放平台openid（AES加密） */
    private String qqOpenId;

    /** QQ绑定状态 0-未绑定 1-已绑定 */
    private Integer qqStatus;

    /** 微信开放平台openid（AES加密） */
    private String wxOpenId;

    /** 微信绑定状态 0-未绑定 1-已绑定 */
    private Integer wxStatus;

    /**  Mall AES加密key（用于业务关联） */
    private String mallAes;

    /** Mall状态 0-禁用 1-正常 */
    private Integer mallStatus;
}
