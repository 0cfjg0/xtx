package com.itheima.xiaotuxian.vo.material;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class MaterialOperatorVo {
    /**
     * 操作对象id
     */
    @NotBlank(message = "id不能为空")
    private String id;
    /**
     * 对象类型，1为目录（即图片组或视频组），2为素材（即图片或视频）
     */
    @Max(value = 2, message = "类型错误")
    @Min(value = 1, message = "类型错误")
    private Integer itemType;
}
