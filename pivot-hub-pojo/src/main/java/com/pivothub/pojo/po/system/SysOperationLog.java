package com.pivothub.pojo.po.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.pivothub.pojo.po.BasePo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 操作日志实体
 * @Author lhb
 * @CreateTime 2026/9/22 22:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysOperationLog extends BasePo {
    @TableId(type = IdType.INPUT)
    private String uuid;

    /** 用户uuid */
    private String userId;

    /** 用户名 */
    private String username;

    /** 操作模块 */
    private String title;

    /** 操作类型 add-新增 delete-删除 update-修改 query-查询 */
    private String method;

    /** 请求方法名 */
    private String requestMethod;

    /** 请求 URL */
    private String requestUrl;

    /** 请求参数（JSON） */
    private String requestParams;

    /** 响应结果（JSON） */
    private String responseResult;

    /** 消耗时间（毫秒） */
    private Long costTime;

    /** 操作 IP */
    private String ip;

    /** IP 属地 */
    private String location;

    /** 浏览器 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 状态 0-失败 1-成功 */
    private Integer status;

    /** 异常信息 */
    private String errorMsg;
}
