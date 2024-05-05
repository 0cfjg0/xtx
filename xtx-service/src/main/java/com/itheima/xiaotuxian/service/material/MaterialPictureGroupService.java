package com.itheima.xiaotuxian.service.material;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.material.MaterialPictureGroup;

import java.util.List;

public interface MaterialPictureGroupService extends IService<MaterialPictureGroup> {
    /**
     * 保存图片组
     *
     * @param pictureGroup 图片组信息
     * @param opUser       操作用户
     * @return 操作结果
     */
    Boolean saveGroup(MaterialPictureGroup pictureGroup, String opUser);

    /**
     * 获取图片组
     *
     * @param state 状态，1为正常，2为回收站
     * @return 图片组集合
     */
    List<MaterialPictureGroup> findAll(Integer state);
}
