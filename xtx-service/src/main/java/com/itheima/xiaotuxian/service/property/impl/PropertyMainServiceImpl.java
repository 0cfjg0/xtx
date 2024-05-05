package com.itheima.xiaotuxian.service.property.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.PropertyStatic;
import com.itheima.xiaotuxian.entity.property.PropertyMain;
import com.itheima.xiaotuxian.entity.property.PropertyValue;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.property.PropertyMainMapper;
import com.itheima.xiaotuxian.mapper.property.PropertyValueMapper;
import com.itheima.xiaotuxian.service.property.PropertyMainService;
import com.itheima.xiaotuxian.service.property.PropertyValueService;
import com.itheima.xiaotuxian.vo.property.*;
import com.itheima.xiaotuxian.vo.property.propertyNew.PropertyGroupNewVo;
import com.itheima.xiaotuxian.vo.property.propertyNew.PropertyNewVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : itheima
 * @Date: 2023/7/11 3:39 下午
 * @Description:
 */
@Service
public class PropertyMainServiceImpl extends ServiceImpl<PropertyMainMapper, PropertyMain> implements PropertyMainService {
    @Autowired
    private PropertyValueMapper propertyValueMapper;
    @Autowired
    private PropertyValueService propertyValueService;

    /**
     * 根据不同的查询条件查询所有属性名信息
     * @param queryVo 查询条件
     * @return
     */
    @Override
    public List<PropertyMain> findAll(PropertyQueryVo queryVo) {
        var lambdaQueryChainWrapper = this.lambdaQuery();
        if (StrUtil.isNotEmpty(queryVo.getGroupId())) {
            lambdaQueryChainWrapper.eq(PropertyMain::getGroupId, queryVo.getGroupId());
        }
        if (CollUtil.isNotEmpty(queryVo.getGroupIds())) {
            lambdaQueryChainWrapper.in(PropertyMain::getGroupId, queryVo.getGroupIds());
        }
        if (queryVo.getPropertyType() != null && queryVo.getPropertyType() != 0) {
            lambdaQueryChainWrapper.eq(PropertyMain::getPropertyType, queryVo.getPropertyType());
        }
        lambdaQueryChainWrapper.eq(PropertyMain::getState, PropertyStatic.STATE_NORMAL);
        return lambdaQueryChainWrapper.list();
    }

    @Override
    public Page<PropertyMain> findByPage(PropertyQueryVo queryVo) {
        var pageResult = new Page<PropertyMain>(queryVo.getPage() == null ? 1 : queryVo.getPage(), queryVo.getPageSize() == null ? 10 : queryVo.getPageSize());
        return this.baseMapper.findByPage(pageResult, queryVo);
    }

    /**
     * 保存属性和属性值
     * @param propertyVO
     * @param opUser     操作人
     * @return
     */
    @Transactional(rollbackFor=Exception.class)
    @Override
    public Boolean savePropertyMain(PropertyVo propertyVO, String opUser) {
        //保存属性主体信息
        var propertyMain = BeanUtil.toBean(propertyVO, PropertyMain.class);
        propertyMain.setCreator(opUser);

        Optional.ofNullable(propertyMain.getId()).ifPresentOrElse(id ->
                        Optional.ofNullable(getById(id))
                                .ifPresentOrElse(property ->
                                                propertyNameDuplicate(propertyVO.getGroup() !=null ?propertyVO.getGroup().getId():null,propertyVO.getName(), property.getId())
                                , () -> {
                                    throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                                })
                , () -> {
                    propertyNameDuplicate(propertyVO.getGroup() !=null ?propertyVO.getGroup().getId():null,propertyVO.getName(), null);
                });
        //处理别名
        Optional.ofNullable(propertyVO.getAlias()).ifPresent(alias -> propertyMain.setAlias(CollUtil.join(alias, ",")));
        //处理值录入方式
        Optional.ofNullable(propertyVO.getInputType()).ifPresent(inputTypes -> propertyMain.setInputType(CollUtil.join(inputTypes, ",")));
        //处理子值录入方式
        Optional.ofNullable(propertyVO.getSubValueInputType()).ifPresent(subValueInputTypes -> propertyMain.setSubValueInputType(CollUtil.join(subValueInputTypes, ",")));
        //处理属性组信息
        Optional.ofNullable(propertyVO.getGroup()).map(PropertyGroupVo::getId).ifPresent(propertyMain::setGroupId);
        //保存属性主体信息
        saveOrUpdate(propertyMain);
        //保存属性值信息
        saveOrUpdateValues(propertyVO.getValues(),propertyMain.getId(),opUser);
        return true;
    }

    /**
     *新版本保存属性和保存属性值
     * @param propertyNewVo
     * @param opUser
     * @return
     */
    @Transactional(rollbackFor=Exception.class)
    @Override
    public Boolean savePropertyMainNew(PropertyNewVo propertyNewVo, String opUser) {
        //保存属性主体信息
        var propertyMain = BeanUtil.toBean(propertyNewVo, PropertyMain.class);
        propertyMain.setCreator(opUser);
        Optional.ofNullable(propertyMain.getId()).ifPresentOrElse(id ->
                        Optional.ofNullable(getById(id))
                                .ifPresentOrElse(property ->
                                        propertyNameDuplicate(propertyNewVo.getGroup() !=null ?propertyNewVo.getGroup().getId():null,propertyNewVo.getName(), property.getId())
                                , () -> {
                                    throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                                })
                , () -> {
                    propertyNameDuplicate(propertyNewVo.getGroup() !=null ?propertyNewVo.getGroup().getId():null,propertyNewVo.getName(), null);
                });
        //处理别名
//        Optional.ofNullable(propertyNewVo.getAlias()).ifPresent(alias -> propertyMain.setAlias(CollUtil.join(alias, ",")));
        propertyMain.setAlias(propertyNewVo.getAlias());
        //处理值录入方式
        propertyMain.setInputType("manual");
        //处理子值录入方式
        propertyMain.setSubValueInputType("manual");
        // 是否必填 默认是0
        propertyMain.setRequired(0);
        // 默认不搜索
        propertyMain.setSearchEnable(0);

        //处理属性组信息
        Optional.ofNullable(propertyNewVo.getGroup()).map(PropertyGroupNewVo::getId).ifPresent(propertyMain::setGroupId);
        //保存属性主体信息
        saveOrUpdate(propertyMain);
        //保存属性值信息
        if(CollectionUtils.isNotEmpty(propertyNewVo.getValues())){
            List<PropertyValueVo> values = propertyNewVo.getValues().stream().map(value->BeanUtil.toBean(value,PropertyValueVo.class)).collect(Collectors.toList());
            saveOrUpdateValues(values,propertyMain.getId(),opUser);
        }
        return true;
    }

    /**
     * 保存属性值信息
     * @param values
     * @param id
     */
    private void saveOrUpdateValues(List<PropertyValueVo> values,String id, String opUser) {
        Optional.ofNullable(values).ifPresent(propertyValueVos -> {
            var originalPropertyValueIds = propertyValueMapper.selectList(Wrappers
                    .<PropertyValue>lambdaQuery()
                    .eq(PropertyValue::getMainId, id)
            ).stream().map(PropertyValue::getId).collect(Collectors.toList());
            //获取当前待操作属性值已存在id列表，用于差集计算
            var tempValueIds = new HashSet<String>();
            propertyValueVos.forEach(propertyValueVo -> {
                var value = BeanUtil.toBean(propertyValueVo, PropertyValue.class);
                value.setMainId(id);
                Optional.ofNullable(value.getId()).ifPresentOrElse(valueId -> {
                    // 新增属性值
                    tempValueIds.add(value.getId());
                    // 修改
                    propertyValueMapper.updateById(value);
                }, () -> {
                    value.setCreator(opUser);
                    // 新增
                    propertyValueMapper.insert(value);
                });
            });
            //移除无效数据
            Optional.ofNullable(CollUtil.disjunction(CollUtil.intersection(originalPropertyValueIds, tempValueIds), originalPropertyValueIds))
                    .filter(CollUtil::isNotEmpty)
                    .ifPresent(ids -> propertyValueMapper.deleteBatchIds(ids));
        });

    }

    @Transactional(rollbackFor=Exception.class)
    @Override
    public Boolean changGroup(PropertyVo propertyVo) {
        var groupId = new AtomicReference<String>();
        var updates = new ArrayList<PropertyMain>();
        Optional.ofNullable(propertyVo.getGroup()).ifPresentOrElse(group ->
                        Optional.ofNullable(group.getId()).ifPresentOrElse(groupId::set, () -> {
                            throw new BusinessException(ErrorMessageEnum.PROPERTY_GROUP_NOT_NULL);
                        })
                , () -> {
                    throw new BusinessException(ErrorMessageEnum.PROPERTY_GROUP_NOT_NULL);
                });
        Optional.ofNullable(propertyVo.getIds()).ifPresentOrElse(propertyIds ->
                        propertyIds.forEach(propertyId -> {
                            var property = new PropertyMain();
                            property.setGroupId(groupId.get());
                            property.setId(propertyId);
                            updates.add(property);
                        })
                , () -> {
                    throw new BusinessException(ErrorMessageEnum.OBJECT_EMPTY);
                });
        updateBatchById(updates);
        return true;
    }

    @Override
    public Boolean saveState(String id, Integer state) {
        var property = new PropertyMain();
        Optional.ofNullable(id).ifPresentOrElse(propertyId ->
                        Optional.ofNullable(getById(propertyId))
                                .map(PropertyMain::getId)
                                .ifPresentOrElse(
                                        property::setId, () -> {
                                            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
                                        })
                , () -> {
                    throw new BusinessException(ErrorMessageEnum.OBJECT_EMPTY);
                });
        Optional.ofNullable(state).filter(propertyState ->
                CollUtil.contains(ListUtil.list(false, PropertyStatic.STATE_NORMAL, PropertyStatic.STATE_DISABLE), propertyState))
                .ifPresentOrElse(property::setState, () -> {
                    throw new BusinessException(ErrorMessageEnum.OBJECT_STATE_NOT_NULL);
                });
        updateById(property);
        return true;
    }

    @Override
    public PropertyMain findPropertyByMainName(String groupId, String mainName) {
        if (StrUtil.isEmpty(groupId) || StrUtil.isEmpty(mainName)) {
            return null;
        }
        return getOne(Wrappers.<PropertyMain>lambdaQuery().eq(PropertyMain::getGroupId, groupId).eq(PropertyMain::getName, mainName));
    }

    @Override
    public List<PropertyMainVo> findMainAndValueByGroupId(String groupId, Integer state) {
        var propertyVos = new ArrayList<PropertyMainVo>();
        Optional.ofNullable(this.list(Wrappers.<PropertyMain>lambdaQuery()
                .eq(StrUtil.isNotEmpty(groupId), PropertyMain::getGroupId, groupId)
                .eq(null != state, PropertyMain::getState, state)
                .orderByDesc(PropertyMain::getUpdateTime)
        ))
                .filter(CollUtil::isNotEmpty)
                .ifPresent(propertyMains ->
                        propertyMains.forEach(propertyMain -> {
                            var propertyVo = BeanUtil.toBean(propertyMain, PropertyMainVo.class);
                            // 处理值
                            Optional.ofNullable(
                                    propertyValueService.list(Wrappers.<PropertyValue>lambdaQuery()
                                                                    .eq(PropertyValue::getMainId, propertyMain.getId())
                                                                    .orderByAsc(PropertyValue::getCreateTime))
                            ).filter(CollUtil::isNotEmpty)
                                    .ifPresent(propertyValues -> {
                                        var pvMap = propertyValues.stream()
                                                .filter(propertyValue -> "0".equals(propertyValue.getPid()))
                                                .map(propertyValue -> {
                                                    var pvVo = BeanUtil.toBean(propertyValue, PropertySimpleVo.class);
                                                    pvVo.setName(propertyValue.getValueName());
                                                    return pvVo;
                                                }).collect(Collectors.toMap(PropertySimpleVo::getId, valueVo -> valueVo));

                                        propertyVo.setProperties(pvMap.values().stream().collect(Collectors.toList()));
                                    });

                            // 处理子值录入方式 TODO 子值暂不处理 2023-04-27
//                            Optional.ofNullable(propertyMain.getLayer())
//                                    .filter(layer -> 2 == layer)
//                                    .ifPresent(layer ->
//                                            Optional.ofNullable(propertyMain.getSubValueInputType())
//                                                    .filter(StrUtil::isNotEmpty)
//                                                    .ifPresent(subValueInputTypes -> propertyVo.setSubValueInputType(Arrays.asList(subValueInputTypes.split(",")).stream().map(s -> (s.trim())).collect(Collectors.toList())))
//                                    );
                            propertyVos.add(propertyVo);
                        })
                );
        return propertyVos;
    }


    /**
     * 检查属性名称是否重复
     *
     * @param groupId 属性组id
     * @param propertyName 属性名称
     * @param curId      当前属性Id
     */
    private void propertyNameDuplicate(String groupId,String propertyName, String curId) {
        var queryWrapper = Wrappers.<PropertyMain>lambdaQuery();
        Optional.ofNullable(groupId).ifPresent(gid -> queryWrapper.eq(PropertyMain::getGroupId, gid));
        Optional.ofNullable(curId)
                .ifPresent(id -> queryWrapper.ne(PropertyMain::getId, id));
        Optional.ofNullable(propertyName)
                .filter(StrUtil::isNotEmpty)
                .ifPresent(name -> queryWrapper.eq(PropertyMain::getName, name));
        Stream.of(count(queryWrapper))
                .filter(count -> count > 0)
                .forEach(count -> {
                    throw new BusinessException(ErrorMessageEnum.OBJECT_DUPLICATE_NAME);
        });
    }
}
