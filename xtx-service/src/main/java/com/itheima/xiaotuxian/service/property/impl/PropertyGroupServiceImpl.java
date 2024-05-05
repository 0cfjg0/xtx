package com.itheima.xiaotuxian.service.property.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.PropertyStatic;
import com.itheima.xiaotuxian.entity.classification.ClassificationBackendPropertyGroup;
import com.itheima.xiaotuxian.entity.property.PropertyGroup;
import com.itheima.xiaotuxian.entity.property.PropertyMain;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.property.PropertyGroupMapper;
import com.itheima.xiaotuxian.mapper.property.PropertyMainMapper;
import com.itheima.xiaotuxian.service.classification.ClassificationBackendService;
import com.itheima.xiaotuxian.service.property.PropertyGroupService;
import com.itheima.xiaotuxian.vo.property.PropertyGroupQueryVo;
import com.itheima.xiaotuxian.vo.property.PropertyGroupVo;
import com.itheima.xiaotuxian.vo.property.PropertySimpleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:39 下午
 * @Description:
 */
@Service
public class PropertyGroupServiceImpl extends ServiceImpl<PropertyGroupMapper, PropertyGroup> implements PropertyGroupService {
    @Autowired
    private PropertyMainMapper propertyMainMapper;
    @Autowired
    private ClassificationBackendService backendService;

    @Override
    public Boolean saveGroup(PropertyGroup propertyGroup, String opUser) {
        String id = propertyGroup.getId();
        Optional.ofNullable(propertyGroup.getName()).ifPresent(name -> {
            var sameName = getOne(Wrappers.<PropertyGroup>lambdaQuery().eq(PropertyGroup::getName, name));
            Optional.ofNullable(sameName).ifPresent(sameEntity -> {
                if (StrUtil.isEmpty(id) || !id.equals(sameName.getId())) {
                    throw new BusinessException(ErrorMessageEnum.OBJECT_DUPLICATE_NAME);
                }
            });
        });
        if (StrUtil.isNotEmpty(id)) {
            var sourceEntity = getById(id);
            if (sourceEntity == null) {
                throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
            }
        } else {
            propertyGroup.setCreator(opUser);
        }
        saveOrUpdate(propertyGroup);
        return true;
    }

    @Override
    public boolean isUsed(String id) {
        var ab = new AtomicBoolean(false);
        Optional.ofNullable(getById(id)).ifPresent(group -> ab.set(group.getState() == PropertyStatic.STATE_NORMAL));
        var hasProperty = propertyMainMapper.selectCount(Wrappers.<PropertyMain>lambdaQuery().eq(PropertyMain::getGroupId, id)) > 0;
        return ab.get() && hasProperty;
    }

    @Override
    public List<PropertyGroup> findAll(PropertyGroupQueryVo queryVo) {
        return baseMapper.findAll(queryVo);
    }
    @Override
    public List<PropertyGroupVo> findAllVo(PropertyGroupQueryVo queryVo) {
        return baseMapper.findAll(queryVo).stream()
                .map(group -> {
                    var groupVo = BeanUtil.toBean(group, PropertyGroupVo.class);
                    Optional.ofNullable(propertyMainMapper.selectList(Wrappers
                                    .<PropertyMain>lambdaQuery()
                                    .eq(PropertyMain::getGroupId, group.getId())
                                    .eq(PropertyMain::getState, PropertyStatic.STATE_NORMAL)
                            )
                    ).ifPresent(properties ->
                            groupVo.setProperties(properties
                                    .stream()
                                    .map(property -> BeanUtil.toBean(property, PropertySimpleVo.class))
                                    .collect(Collectors.toList())
                            ));
                    return groupVo;
                })
                .collect(Collectors.toList());
    }
    @Override
    public Page<PropertyGroup> findByPage(PropertyGroupQueryVo queryVo) {
        Page<PropertyGroup> pageResult = new Page<>(queryVo.getPage() == null ? 1 : queryVo.getPage(), queryVo.getPageSize() == null ? 10 : queryVo.getPageSize());
        return page(pageResult, Wrappers.<PropertyGroup>lambdaQuery()
                .like(StrUtil.isNotEmpty(queryVo.getId()), PropertyGroup::getId, queryVo.getId())
                .like(StrUtil.isNotEmpty(queryVo.getName()), PropertyGroup::getName, queryVo.getName())
                .eq(queryVo.getState() != null && queryVo.getState() >= 0, PropertyGroup::getState, queryVo.getState())
                .orderByDesc(PropertyGroup::getUpdateTime)
        );

    }

    /**
     * 获取属性组（携带属性）列表
     * @param queryVo 查询条件
     * @return
     */
    @Override
    public List<PropertyGroupVo> findAllAndProperty(PropertyGroupQueryVo queryVo) {
        return list(Wrappers.<PropertyGroup>lambdaQuery()
                .eq(queryVo.getPropertyType() != null, PropertyGroup::getPropertyType, queryVo.getPropertyType())
                .eq(PropertyGroup::getState, PropertyStatic.STATE_NORMAL)
                .in(CollUtil.isNotEmpty(queryVo.getIds()), PropertyGroup::getId, queryVo.getIds())
            ).stream()
                .map(group -> {
                    var groupVo = BeanUtil.toBean(group, PropertyGroupVo.class);
                    Optional.ofNullable(propertyMainMapper.selectList(Wrappers
                                    .<PropertyMain>lambdaQuery()
                                    .eq(PropertyMain::getGroupId, group.getId())
                                    .eq(PropertyMain::getState, PropertyStatic.STATE_NORMAL)
                            )
                    ).ifPresent(properties ->
                            groupVo.setProperties(properties
                                    .stream()
                                    .map(property -> BeanUtil.toBean(property, PropertySimpleVo.class))
                                    .collect(Collectors.toList())
                            ));
                    return groupVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PropertyGroup findGroupByName(String groupName) {
        if (StrUtil.isEmpty(groupName)) {
            return null;
        }
        return getOne(Wrappers.<PropertyGroup>lambdaQuery().eq(PropertyGroup::getName, groupName));
    }


    /**
     * 获取属性组信息--根据后台类目id和属性基础
     *
     * @param backendId    后台分类Id
     * @param propertyType 属性组类型
     * @return 属性组信息
     */
    @Override
    public List<PropertyGroupVo> getPropertyGroups(String backendId, Integer propertyType) {
        var resultList = new ArrayList<PropertyGroupVo>();
        Optional.ofNullable(backendService.findAllBackendPropertyGroup(backendId, propertyType, null))
                .ifPresent(backendPropertyGroups -> Optional.of(backendPropertyGroups.stream()
                        .map(ClassificationBackendPropertyGroup::getPropertyGroupId)
                        .collect(Collectors.toList())).filter(CollUtil::isNotEmpty).ifPresent(propertyGroupIds -> {
                    var propertyGroupQueryVo = new PropertyGroupQueryVo();
                    propertyGroupQueryVo.setPropertyType(propertyType);
                    propertyGroupQueryVo.setIds(propertyGroupIds);
                    Optional.ofNullable(this.findAllAndProperty(propertyGroupQueryVo)).ifPresent(resultList::addAll);
                }));
        return resultList;
    }

}
