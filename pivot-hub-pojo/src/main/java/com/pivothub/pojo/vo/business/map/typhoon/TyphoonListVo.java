package com.pivothub.pojo.vo.business.map.typhoon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "活跃台风列表及数据状态")
public class TyphoonListVo {
    @Schema(description = "活跃台风")
    private List<TyphoonSummaryVo> typhoons;

    @Schema(description = "数据来源")
    private String source;

    @Schema(description = "来源页面")
    private String sourceUrl;

    @Schema(description = "来源更新时间，未知时为空")
    private String sourceUpdatedAt;

    @Schema(description = "最近成功抓取时间，ISO8601")
    private String fetchedAt;

    @Schema(description = "是否使用过期缓存")
    private boolean stale;
}
