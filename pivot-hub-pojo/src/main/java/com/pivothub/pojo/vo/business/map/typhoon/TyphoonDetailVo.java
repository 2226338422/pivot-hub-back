package com.pivothub.pojo.vo.business.map.typhoon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "台风实况和预报详情及数据状态")
public class TyphoonDetailVo {
    @Schema(description = "基本信息")
    private TyphoonSummaryVo typhoon;

    @Schema(description = "按时间升序的实况点")
    private List<TyphoonObservationVo> observations;

    @Schema(description = "最新观测对应的预报，无数据为空")
    private TyphoonForecastVo forecast;

    @Schema(description = "数据来源")
    private String source;

    @Schema(description = "来源页面")
    private String sourceUrl;

    @Schema(description = "来源更新时间，ISO8601")
    private String sourceUpdatedAt;

    @Schema(description = "最近成功抓取时间，ISO8601")
    private String fetchedAt;

    @Schema(description = "是否使用过期缓存")
    private boolean stale;
}
