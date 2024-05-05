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
@TableName(value = "manager_admin")
public class ManagerAdmin extends AbstractBasePO  {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 管理员名称
     */
    private String name;
    /**
     * 账号
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    /**
     * 头像信息
     */
    private String avatar;
}
