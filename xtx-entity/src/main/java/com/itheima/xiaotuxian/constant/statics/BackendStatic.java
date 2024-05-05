package com.itheima.xiaotuxian.constant.statics;


public class BackendStatic {
    private BackendStatic() {
    }

    //最大后台类目层级
    public static final int MAX_LAYER = 3;

    //状态
    // 启用
    public static final int STATE_NORMAL = 0;
    // 禁用
    public static final int STATE_DISABLE = 1;
    // 自身关联数据-本级
    public static final int RELATION_SOURCE_SELF = 1;
    // 父级关联数据
    public static final int RELATION_SOURCE_PARENT = 2;
}
