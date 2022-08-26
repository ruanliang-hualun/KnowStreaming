package com.xiaojukeji.know.streaming.km.common.bean.dto.metrices;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author didi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "topic指标查询信息")
public class MetricsTopicDTO extends MetricDTO {

    @ApiModelProperty("topic名称")
    private List<String> topics;
}
