package com.itheima.xiaotuxian.vo.property;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itheima.xiaotuxian.vo.material.MaterialPictureVo;
import lombok.Data;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/12 1:59 下午
 * @Description:
 */
@Data
public class PropertyValueVo {
    private String id;
    /**
     * 父属性信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PropertyValueVo parent;
    /**
     * 值名称
     */
    private String valueName;
    /**
     * 值图片
     */
    private MaterialPictureVo valuePicture;
    /**
     * 属性值备注
     */
    private String remark;

    /**
     * 属性子值列表
     */
   @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<PropertyValueVo> children;

    /**
     * 属性信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PropertyVo property;
}
