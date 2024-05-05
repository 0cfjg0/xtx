package com.itheima.xiaotuxian.entity.manager;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 后台管理-权限
 */
@Data
@TableName(value = "manager_authority")
public class ManagerAuthority extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 权限名称
     */
    private String name;
    /**
     * 权限编码
     */
    private String code;
    /**
     * 约束范围,1为前端权限，2为后端权限，3为数据
     */
    private Integer boundRange;
    /**
     * 父权限id
     */
    private String pid;
    /**
     * 菜单URL
     */
    private String url;
    /**
     * 类型  1：菜单   2：按钮
     */
    private Integer type;
    /**
     * 菜单图标
     */
    private String icon;
    /**
     * 排序
     */
    private Integer orderNum;
}
