package com.itheima.xiaotuxian.vo.goods.goods;

import com.itheima.xiaotuxian.vo.material.PictureSimpleVo;
import lombok.Data;

import java.util.List;

@Data
public class GoodsPropertyValueSaveVo {
    /**
     * 属性组名称
     */
    private String propertyGroupName;
    /**
     * 属性名称
     */
    private String propertyMainName;
    /**
     * 属性值名称集合
     */
    private List<String> propertyValueName;
    /**
     * 父属性值名称
     */
    private String parentValueName;
    /**
     * 属性值备注
     */
    private String propertyValueDescription;
    /**
     * 属性值图片
     */
    private PictureSimpleVo propertyValuePicture;
}
