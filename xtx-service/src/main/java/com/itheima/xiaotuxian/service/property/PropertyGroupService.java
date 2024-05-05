package com.itheima.xiaotuxian.service.property;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.property.PropertyGroup;
import com.itheima.xiaotuxian.vo.property.PropertyGroupQueryVo;
import com.itheima.xiaotuxian.vo.property.PropertyGroupVo;

import java.util.List;

public interface PropertyGroupService extends IService<PropertyGroup> {
    /**
     * 保存属性组
     *
     * @param propertyGroup 属性组信息
     * @return 操作结果
     */
    Boolean saveGroup(PropertyGroup propertyGroup, String opUser);

    /**
     * 是否被引用
     *
     * @return 是否被引用
     */
    boolean isUsed(String id);

    /**
     * 根据条件获取属性组列表
     *
     * @param queryVo 查询条件
     * @return 属性组列表
     */
    List<PropertyGroup> findAll(PropertyGroupQueryVo queryVo);

    /**
     *
     * @param propertyGroupQueryVo
     * @return
     */
    List<PropertyGroupVo> findAllVo(PropertyGroupQueryVo propertyGroupQueryVo);

    /**
     * 获取属性组分页数据
     *
     * @param queryVo 查询条件
     * @return 属性组分页数据
     */
    Page<PropertyGroup> findByPage(PropertyGroupQueryVo queryVo);

    /**
     * 获取属性组（携带属性）列表
     *
     * @param queryVo 查询条件
     * @return 属性组列表
     */
    List<PropertyGroupVo> findAllAndProperty(PropertyGroupQueryVo queryVo);

    /**
     * 通过属性组名称获取属性组信息
     *
     * @param groupName 属性组名称
     * @return 属性组信息
     */
    PropertyGroup findGroupByName(String groupName);

    /**
     * 获取属性组信息--根据后台类目id和属性基础
     *
     * @param backendId    后台分类Id
     * @param propertyType 属性组类型
     * @return 属性组信息
     */
    List<PropertyGroupVo> getPropertyGroups(String backendId, Integer propertyType);

}
