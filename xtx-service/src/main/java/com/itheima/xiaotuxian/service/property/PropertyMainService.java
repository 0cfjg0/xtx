package com.itheima.xiaotuxian.service.property;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.property.PropertyMain;
import com.itheima.xiaotuxian.vo.property.PropertyMainVo;
import com.itheima.xiaotuxian.vo.property.PropertyQueryVo;
import com.itheima.xiaotuxian.vo.property.PropertyVo;
import com.itheima.xiaotuxian.vo.property.propertyNew.PropertyNewVo;

import java.util.List;

public interface PropertyMainService extends IService<PropertyMain> {
    /**
     * 根据条件获取属性信息列表
     *
     * @param queryVo 查询条件
     * @return 属性信息列表
     */
    List<PropertyMain> findAll(PropertyQueryVo queryVo);

    /**
     * 获取属性分页数据
     *
     * @param queryVo 查询条件
     * @return 属性分页数据
     */
    Page<PropertyMain> findByPage(PropertyQueryVo queryVo);

    /**
     * 保存属性
     *
     * @param propertyVo 属性信息
     * @param opUser     操作人
     * @return 操作结果
     */
    Boolean savePropertyMain(PropertyVo propertyVo, String opUser);

    /**
     * 新版本的保存修改属性，简化了很多字段
     * @param propertyNewVo
     * @param opUser
     * @return
     */
    Boolean savePropertyMainNew(PropertyNewVo propertyNewVo, String opUser);

    /**
     * 移动属性组
     *
     * @param propertyVo 属性信息
     * @return 操作结果
     */
    Boolean changGroup(PropertyVo propertyVo);

    /**
     * 属性启、禁用
     *
     * @param id    属性Id
     * @param state 状态,0为启用，1为禁用
     * @return 操作结果
     */
    Boolean saveState(String id, Integer state);



    /**
     * 通过属性名称获取属性信息
     *
     * @param groupId  属性组Id
     * @param mainName 属性名称
     * @return 属性信息
     */
    PropertyMain findPropertyByMainName(String groupId, String mainName);


    List<PropertyMainVo> findMainAndValueByGroupId(String groupId, Integer stateNormal);
}
