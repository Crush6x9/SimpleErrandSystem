package com.errand.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "评价统计信息")
public class EvaluationStats {
    @ApiModelProperty(value = "好评数")
    private Long goodReviews;

    @ApiModelProperty(value = "差评数")
    private Long badReviews;

    public EvaluationStats(Long goodReviews, Long badReviews) {
        this.goodReviews = goodReviews;
        this.badReviews = badReviews;
    }
}
