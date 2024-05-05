package com.itheima.xiaotuxian.vo.evaluate;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class EvaluateItemSaveVo {
    /**
     * 商品id
     */
    @NotBlank(message = "商品id不能为空")
    private String id;
    /**
     * skuId
     */
    @NotBlank(message = "sku id不能为空")
    private String skuId;
    /**
     * 评分，取值范围0-5
     */
    @NotNull(message = "评分不能为空")
    private BigDecimal score;
    /**
     * 评价正文
     */
    private String content;
    /**
     * 印象标签集合
     */
    private List<String> tags;
    /**
     * 图片或视频链接集合
     */
    private List<String> pictures;
}