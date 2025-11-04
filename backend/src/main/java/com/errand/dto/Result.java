package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "统一响应")
public class Result<DataType> {
    @ApiModelProperty(value = "状态码")
    private Integer code;

    @ApiModelProperty(value = "消息")
    private String message;

    @ApiModelProperty(value = "数据")
    private DataType data;

    public Result() {}

    public Result(Integer code, String message, DataType data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功静态方法
    public static <DataType> Result<DataType> success(String message, DataType data) {
        return new Result<>(200, message, data);
    }

    public static Result success(String message) {
        return new Result<>(200, message, null);
    }

    // 错误静态方法
    public static Result error(String message) {
        return new Result<>(400, message, null);
    }

    public static Result error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
