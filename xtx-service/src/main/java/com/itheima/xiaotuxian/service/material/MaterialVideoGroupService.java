package com.itheima.xiaotuxian.service.material;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.material.MaterialVideoGroup;

import java.util.List;

public interface MaterialVideoGroupService extends IService<MaterialVideoGroup> {
    /**
     * 保存图片组
     *
     * @param videoGroup 视频组信息
     * @param opUser     操作用户
     * @return 操作结果
     */
    Boolean saveGroup(MaterialVideoGroup videoGroup, String opUser);

    /**
     * 获取视频组
     *
     * @param state 状态，1为正常，2为回收站
     * @return 视频组集合
     */
    List<MaterialVideoGroup> findAll(Integer state);
}
