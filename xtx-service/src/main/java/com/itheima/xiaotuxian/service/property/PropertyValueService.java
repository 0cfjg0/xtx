package com.itheima.xiaotuxian.service.property;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.property.PropertyValue;
import com.itheima.xiaotuxian.vo.property.PropertyVo;

import java.util.List;

public interface PropertyValueService extends IService<PropertyValue> {

    /**
     * 获取属性值列表
     *
     * @param mainIds 属性id集合
     * @return 属性值列表
     */
    List<PropertyValue> findAllValue(List<String> mainIds);

    /**
     * 获取全部属性与属性值列表
     *
     * @param groupId 属性组Id
     * @param state   状态,0为启用，1为禁用
     * @return 全部属性与属性值列表
     */
    List<PropertyVo> findAllPropertyAndValue(String groupId, Integer state);



    /**
     * 通过属性值名称获取属性值信息
     *
     * @param mainId    属性Id
     * @param valueName 属性值名称
     * @return 属性值信息
     */
    PropertyValue findPropertyValueByName(String mainId, String valueName);

    /**
     * 通过属性值Id获取属性值信息
     *
     * @param id 属性值id
     * @return 属性值信息
     */
    PropertyValue findPropertyValueById(String id);
}
