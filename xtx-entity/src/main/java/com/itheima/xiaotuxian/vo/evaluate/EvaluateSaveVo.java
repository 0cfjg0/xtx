package com.itheima.xiaotuxian.vo.evaluate;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class EvaluateSaveVo {
    /**
     * 是否匿名
     */
    private Boolean anonymous = true;
    /**
     * 评价信息集合
     */
    @NotEmpty(message = "评价信息不能为空")
    private List<EvaluateItemSaveVo> evaluates;
}