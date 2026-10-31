package com.pivothub.business.map.typhoon.service;

import com.pivothub.pojo.vo.business.map.typhoon.TyphoonDetailVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonListVo;

/** 台风实况与预报查询。 */
public interface TyphoonService {
    TyphoonListVo list();
    TyphoonDetailVo detail(String typhoonId);
}
