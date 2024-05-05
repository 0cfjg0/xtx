package com.itheima.xiaotuxian.vo.property.propertyNew;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itheima.xiaotuxian.vo.property.PropertySimpleVo;
import lombok.Data;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/12 1:23 下午
 * @Description:
 */
@Data
public class PropertyGroupNewVo {
    private String id;
    /**
     * 属性组名称
     */
    private String name;


}
