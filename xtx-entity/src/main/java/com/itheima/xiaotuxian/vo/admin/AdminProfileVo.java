package com.itheima.xiaotuxian.vo.admin;

import lombok.Data;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/11 5:56 下午
 * @Description:
 */
@Data
public class AdminProfileVo {
    /**
     * 管理员id
     */
    private String id;
    /**
     * 管理员名字
     */
    private String name;
    /**
     * 管理员账号
     */
    private String username;
    /**
     * 权限列表
     */
//    private List<AuthorityVo> authorities;

    /**
     * 用户角色信息
     */
    private List<String> roles;
    /**
     * 头像信息
     */
    private String avatar;


}
