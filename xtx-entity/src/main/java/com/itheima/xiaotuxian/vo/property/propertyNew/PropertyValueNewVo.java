package com.itheima.xiaotuxian.vo.property.propertyNew;

import com.itheima.xiaotuxian.vo.material.MaterialPictureVo;
import lombok.Data;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/12 1:59 下午
 * @Description:
 */
@Data
public class PropertyValueNewVo {
    private String id;

    /**
     * 值名称
     */
    private String valueName;
    /**
     * 属性值备注
     */
    private String remark;

    /**
     * 值名称
     */
//    private String valuePicture;
    /**
     * 值图片
     */
//    private MaterialPictureVo valuePicture;
}
