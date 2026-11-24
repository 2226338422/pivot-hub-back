package com.pivothub.system.service;

import com.pivothub.pojo.dto.system.SystemConfigItemDto;
import com.pivothub.pojo.vo.system.SystemConfigVo;

import java.util.List;

public interface SystemConfigService {
    List<SystemConfigVo> listAuthorizedConfigs();

    void saveConfigs(List<SystemConfigItemDto> items);

    String getConfigValue(String key);
}
