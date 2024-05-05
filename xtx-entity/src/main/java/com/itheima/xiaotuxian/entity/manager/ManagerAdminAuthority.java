package com.itheima.xiaotuxian.entity.manager;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 后台管理-管理员与权限关联
 */
@Data
@TableName(value = "manager_admin_authority")
public class ManagerAdminAuthority extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 管理员id
     */
    private String adminId;
    /**
     * 权限id
     */
    private String authorityId;
}
