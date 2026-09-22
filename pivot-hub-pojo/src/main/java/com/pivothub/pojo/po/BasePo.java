package com.pivothub.pojo.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 基础实体类
 * @Author lhb
 * @CreateTime 2026/9/22 22:42
 */
@Data
public abstract class BasePo implements Serializable {
    /** 创建人uuid */
    private String createUserId;

    /** 更新人uuid */
    private String updateUserId;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
