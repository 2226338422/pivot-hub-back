package com.pivothub.business.map.typhoon.service;

import com.pivothub.pojo.vo.business.map.typhoon.TyphoonDetailVo;
import com.pivothub.pojo.vo.business.map.typhoon.TyphoonListVo;

import java.time.Instant;

/** 台风实况与预报查询。 */
public interface TyphoonService {
    /**
     * 查询指定时刻的活跃台风列表。
     * @param replayAt 回放时刻，为空时查询当前活跃台风
     * @return 活跃台风列表
     */
    TyphoonListVo list(Instant replayAt);

    /**
     * 查询指定台风在回放时刻的实况及当时已发布的最新预报。
     * @param typhoonId 台风标识
     * @param replayAt 回放时刻，为空时查询实时详情
     * @return 台风详情，暂无可用预报时预报字段为空
     */
    TyphoonDetailVo detail(String typhoonId, Instant replayAt);
}
