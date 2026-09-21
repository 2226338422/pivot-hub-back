package com.pivothub.pojo.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类
 */
@Data
public abstract class BasePo implements Serializable {
    /** 创建人uuid */
    private String createUserId;

    /** 更新人uuid */
    private String updateUserId;

    private Date createTime;
    private Date updateTime;
}
