package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "评价请求")
public class EvaluationRequest {
    @ApiModelProperty(value = "评价", required = true)
    @NotBlank(message = "评价不能为空")
    private String review;

    public EvaluationRequest() {}

    public EvaluationRequest(String review) {
        this.review = review;
    }
}
