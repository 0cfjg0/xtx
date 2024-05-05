package com.itheima.xiaotuxian.service.property.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.entity.property.PropertyMain;
import com.itheima.xiaotuxian.entity.property.PropertyValue;
import com.itheima.xiaotuxian.mapper.property.PropertyValueMapper;
import com.itheima.xiaotuxian.service.property.PropertyGroupService;
import com.itheima.xiaotuxian.service.property.PropertyMainService;
import com.itheima.xiaotuxian.service.property.PropertyValueService;
import com.itheima.xiaotuxian.vo.property.PropertyValueVo;
import com.itheima.xiaotuxian.vo.property.PropertyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author itheima
 * @Date: 2023/7/11 3:39 下午
 * @Description:
 */
@Service
public class PropertyValueServiceImpl extends ServiceImpl<PropertyValueMapper, PropertyValue> implements PropertyValueService {
    @Autowired
    private PropertyValueMapper propertyValueMapper;
    @Autowired
    private PropertyMainService mainService;
    @Autowired
    private PropertyGroupService propertyGroupService;

    @Override
    public List<PropertyValue> findAllValue(List<String> mainIds) {
        var results = new ArrayList<PropertyValue>();
        if (CollUtil.isNotEmpty(mainIds)) {
            results.addAll(propertyValueMapper.selectList(Wrappers.<PropertyValue>lambdaQuery().in(PropertyValue::getMainId, mainIds).orderByAsc(PropertyValue::getCreateTime)));
        }
        return results;
    }

    /**
     * 根据属性组id和状态获取所有的属性和值
     * @param groupId 属性组Id
     * @param state   状态,0为启用，1为禁用
     * @return
     */
    @Override
    public List<PropertyVo> findAllPropertyAndValue(String groupId, Integer state) {
        var propertyVos = new ArrayList<PropertyVo>();
        Optional.ofNullable(mainService.list(Wrappers.<PropertyMain>lambdaQuery()
                .eq(StrUtil.isNotEmpty(groupId), PropertyMain::getGroupId, groupId)
                .eq(null != state, PropertyMain::getState, state)
                .orderByDesc(PropertyMain::getId)
                .orderByDesc(PropertyMain::getUpdateTime)
        ))
                .filter(CollUtil::isNotEmpty)
                .ifPresent(propertyMains ->
                        propertyMains.forEach(propertyMain -> {
                            var propertyVo = BeanUtil.toBean(propertyMain, PropertyVo.class);
                            // 处理属性组信息
                            Optional.ofNullable(propertyMain.getGroupId()).filter(StrUtil::isNotEmpty).flatMap(gid ->
                                    Optional.ofNullable(propertyGroupService.getById(gid))).ifPresent(group ->
                                    propertyVo.setGroupName(group.getName()));
                            // 处理别名
                            Optional.ofNullable(propertyMain.getAlias())
                                    .filter(StrUtil::isNotEmpty)
                                    .ifPresent(alias -> propertyVo.setAlias(Arrays.asList(alias.split(",")).stream().map(s -> (s.trim())).collect(Collectors.toList())));
                            // 处理值录入方式
                            Optional.ofNullable(propertyMain.getInputType())
                                    .filter(StrUtil::isNotEmpty)
                                    .ifPresent(inputTypes -> propertyVo.setInputType(Arrays.asList(inputTypes.split(",")).stream().map(s -> (s.trim())).collect(Collectors.toList())));
                            // 处理值
                            Optional.ofNullable(propertyValueMapper
                                                        .selectList(Wrappers.<PropertyValue>lambdaQuery()
                                                                            .eq(PropertyValue::getMainId, propertyMain.getId())
                                                                             .orderByAsc(PropertyValue::getId)
                                                                             .orderByAsc(PropertyValue::getCreateTime)
                            )).filter(CollUtil::isNotEmpty)
                                    .ifPresent(allPropertyValues -> {
                                        // 将所有的属性值放在一起
//                                        var pvMap = allPropertyValues
//                                                .stream()
//                                                .filter(propertyValue -> "0".equals(propertyValue.getPid()))
//                                                .map(propertyValue -> {
//                                                    var pvVo = BeanUtil.toBean(propertyValue, PropertyValueVo.class);
//                                                    Optional.ofNullable(propertyMain.getLayer())
//                                                            .filter(layer -> 2 == layer)
//                                                            .ifPresent(layer -> pvVo.setChildren(new ArrayList<>()));
//                                                    return pvVo;
//                                                })
//                                                .collect(Collectors.toMap(PropertyValueVo::getId, valueVo -> valueVo));
//                                        Optional.ofNullable(propertyMain.getLayer())
//                                                .filter(layer -> 2 == layer)
//                                                .ifPresent(layer ->
//                                                        allPropertyValues.stream()
//                                                                .filter(subValue -> !"0".equals(subValue.getPid()))
//                                                                .forEach(subValue ->
//                                                                        Optional.ofNullable(pvMap.get(subValue.getPid()))
//                                                                                .ifPresent(pvVo ->
//                                                                                        pvVo.getChildren().add(BeanUtil.toBean(subValue, PropertyValueVo.class))
//                                                                                )
//                                                                )
//                                                );
//                                        propertyVo.setValues(pvMap.values().stream().collect(Collectors.toList()));
                                        // 父级   --->子级
                                        List<PropertyValueVo> values = allPropertyValues.stream().filter(propertyValue -> "0".equals(propertyValue.getPid()))
                                                    .map(propertyValue -> {
                                                        var pvVo = BeanUtil.toBean(propertyValue, PropertyValueVo.class);
                                                        Optional.ofNullable(propertyMain.getLayer())
                                                                .filter(layer -> 2 == layer)
                                                                .ifPresent(layer -> pvVo.setChildren(null));
                                                        return pvVo;
                                                    }).collect(Collectors.toList());
                                        Map<String, List<PropertyValue>> collect = allPropertyValues.stream().filter(value -> value.getPid() != null)
                                                    .collect(Collectors.groupingBy(PropertyValue::getPid));
                                        if (collect != null && collect.size() > 0) {
                                                values.stream().forEach(value->{
                                                    List childrens = collect.get(value.getId());
                                                    if(CollectionUtils.isNotEmpty(childrens)){
                                                        value.setChildren(childrens);
                                                    }
                                                });
                                            }
                                        propertyVo.setValues(values);
                                    });

                            // 处理子值录入方式
                            Optional.ofNullable(propertyMain.getLayer())
                                    .filter(layer -> 2 == layer)
                                    .ifPresent(layer ->
                                            Optional.ofNullable(propertyMain.getSubValueInputType())
                                                    .filter(StrUtil::isNotEmpty)
                                                    .ifPresent(subValueInputTypes -> propertyVo.setSubValueInputType(Arrays.asList(subValueInputTypes.split(",")).stream().map(s -> (s.trim())).collect(Collectors.toList())))
                                    );
                            propertyVos.add(propertyVo);
                        })
                );
        return propertyVos;
    }



    @Override
    public PropertyValue findPropertyValueByName(String mainId, String valueName) {
        if (StrUtil.isEmpty(mainId) || StrUtil.isEmpty(valueName)) {
            return null;
        }
        return propertyValueMapper.selectOne(Wrappers.<PropertyValue>lambdaQuery().eq(PropertyValue::getMainId, mainId).eq(PropertyValue::getValueName, valueName));
    }

    @Override
    public PropertyValue findPropertyValueById(String id) {
        return propertyValueMapper.selectById(id);
    }


}
