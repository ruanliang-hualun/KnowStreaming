package com.xiaojukeji.kafka.manager.common.entity.dto.rd;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zengqiao
 * @date 20/5/4
 */
@Data
@ApiModel(description="App关联Topic信息")
public class AppRelateTopicsDTO {
    @NotNull(message = "clusterPhyId不允许为NULL")
    @ApiModelProperty(value="物理集群ID")
    private Long clusterPhyId;

    @NotNull(message = "filterTopicNameList不允许为NULL")
    @ApiModelProperty(value="过滤的Topic列表")
    private List<String> filterTopicNameList;
}