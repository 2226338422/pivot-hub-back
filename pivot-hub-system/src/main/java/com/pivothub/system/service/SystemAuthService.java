package com.pivothub.system.service;

import com.pivothub.pojo.dto.system.EmailLoginDto;
import com.pivothub.pojo.dto.system.TokenDto;
import com.pivothub.pojo.vo.TokenVo;

public interface SystemAuthService {
    TokenVo loginByEmail(EmailLoginDto dto);

    TokenVo refresh(TokenDto dto);
}
