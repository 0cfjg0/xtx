package com.itheima.xiaotuxian.entity.manager;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 后台管理-管理员
 */
@Data
@TableName(value = "manager_role")
public class ManagerRole extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 角色名称
     */
    private String roleName;
    /** 角色权限 */
     private String roleKey;

    /** 角色排序 */
     private String roleSort;

    /** 角色状态（0正常 1停用） */
     private String status;

     /** 备注*/
    private String remark;

    /**修改人*/
    private String updator;

}
