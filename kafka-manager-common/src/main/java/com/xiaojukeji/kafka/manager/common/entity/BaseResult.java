package com.xiaojukeji.kafka.manager.common.entity;

import com.xiaojukeji.kafka.manager.common.constant.Constant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class BaseResult implements Serializable {
    private static final long serialVersionUID = -5771016784021901099L;

    @ApiModelProperty(value = "信息", example = "成功")
    protected String message;

    @ApiModelProperty(value = "状态", example = "0")
    protected int code;

    public boolean successful() {
        return !this.failed();
    }

    public boolean failed() {
        return !Constant.SUCCESS.equals(code);
    }
}
