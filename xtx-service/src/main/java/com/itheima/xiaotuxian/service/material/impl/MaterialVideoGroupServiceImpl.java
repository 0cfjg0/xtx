package com.itheima.xiaotuxian.service.material.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.MaterialStatic;
import com.itheima.xiaotuxian.entity.material.MaterialVideoGroup;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.material.MaterialVideoGroupMapper;
import com.itheima.xiaotuxian.service.material.MaterialVideoGroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:35 下午
 * @Description:
 */
@Service
public class MaterialVideoGroupServiceImpl extends ServiceImpl<MaterialVideoGroupMapper, MaterialVideoGroup> implements MaterialVideoGroupService {
    @Override
    public Boolean saveGroup(MaterialVideoGroup videoGroup, String opUser) {
        var pid = StrUtil.isEmpty(videoGroup.getPid()) ? "0" : videoGroup.getPid();
        if (StrUtil.isEmpty(videoGroup.getId())) {
            videoGroup.setCreator(opUser);
        } else {
            var source = getById(videoGroup.getId());
            if (source == null) {
                throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
            }
        }
        //校验数据有效性
        // 1.同层不允许重名
        checkName(videoGroup.getId(), pid, videoGroup.getName());
        // 2.校验层级并填充
        videoGroup.setLayer(getLayer(pid));
        return saveOrUpdate(videoGroup);
    }

    @Override
    public List<MaterialVideoGroup> findAll(Integer state) {
        return list(Wrappers
                .<MaterialVideoGroup>lambdaQuery()
                .eq(state != null, MaterialVideoGroup::getState, state)
        );
    }

    /**
     * 检查名称是否可用
     *
     * @param id   当前操作对象id
     * @param pid  当前操作对象父id
     * @param name 待检查名称
     */
    private void checkName(String id, String pid, String name) {
        Optional.ofNullable(getOne(Wrappers
                .<MaterialVideoGroup>lambdaQuery()
                .eq(MaterialVideoGroup::getPid, pid)
                .eq(MaterialVideoGroup::getName, name)
                .ne(StrUtil.isNotEmpty(id), MaterialVideoGroup::getId, id)))
                .ifPresent(sameEntity -> {
                    Stream.of(sameEntity.getState()).filter(state -> MaterialStatic.STATE_MATERIAL_NORMAL == state).forEach(state -> {
                        throw new BusinessException(ErrorMessageEnum.MATERIAL_GROUP_DUPLICATE);
                    });
                    Stream.of(sameEntity.getState()).filter(state -> MaterialStatic.STATE_MATERIAL_TRASH == state).forEach(state -> {
                        throw new BusinessException(ErrorMessageEnum.MATERIAL_GROUP_DUPLICATE_TRASH);
                    });
                });
    }

    /**
     * 检验层级并获取可用层级
     *
     * @param pid 父级id
     * @return 可用层级
     */
    private Integer getLayer(String pid) {
        var layer = 1;
        if (!"0".equals(pid)) {
            var parent = getById(pid);
            if (parent == null) {
                throw new BusinessException(ErrorMessageEnum.MATERIAL_GROUP_PARENT_INVALID);
            }
            layer = parent.getLayer() + 1;
        }
        if (layer > MaterialStatic.GROUP_MAX_LAYER) {
            throw new BusinessException(ErrorMessageEnum.MATERIAL_GROUP_OUT_OF_LAYER);
        }
        return layer;
    }
}
