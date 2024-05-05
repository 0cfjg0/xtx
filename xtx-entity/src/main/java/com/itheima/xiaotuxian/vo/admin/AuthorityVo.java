package com.itheima.xiaotuxian.vo.admin;

import lombok.Data;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/11 5:57 下午
 * @Description:
 */
@Data
public class AuthorityVo {
    /**
     * 权限id
     */
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
     * 子权限集合
     */
    private List<AuthorityVo> children;
}
