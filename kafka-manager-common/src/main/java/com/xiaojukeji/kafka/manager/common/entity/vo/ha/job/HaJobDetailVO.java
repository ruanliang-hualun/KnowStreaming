package com.xiaojukeji.kafka.manager.common.entity.vo.ha.job;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Job详情")
public class HaJobDetailVO {
    @ApiModelProperty(value = "Topic名称")
    private String topicName;

    @ApiModelProperty(value="主物理集群ID")
    private Long activeClusterPhyId;

    @ApiModelProperty(value="主物理集群名称")
    private String activeClusterPhyName;

    @ApiModelProperty(value="备物理集群ID")
    private Long standbyClusterPhyId;

    @ApiModelProperty(value="备物理集群名称")
    private String standbyClusterPhyName;

    @ApiModelProperty(value="Lag和")
    private Long sumLag;

    @ApiModelProperty(value="状态")
    private Integer status;

    @ApiModelProperty(value="超时时间配置")
    private Long timeoutUnitSecConfig;
}
