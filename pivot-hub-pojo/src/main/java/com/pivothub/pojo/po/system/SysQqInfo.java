package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * QQ用户信息实体（参考mall的qq_info表）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysQqInfo extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** QQ openid */
    private String openid;

    /** 性别 0-未知 1-男 2-女 */
    private String gender;

    /** 性别类型 */
    private Integer genderType;

    /** 昵称 */
    private String nickname;

    /** 头像大图 */
    private String figureurl2;

    /** 头像中图 */
    private String figureurl1;

    /** 头像 */
    private String figureurl;

    /** QQ头像 */
    private String figureurlQq;

    /** QQ头像1 */
    private String figureurlQq1;

    /** QQ头像2 */
    private String figureurlQq2;

    /** 状态 0-弃用 1-启用 */
    private Integer status;
}
