package com.itheima.xiaotuxian.constant.statics;


public class KeywordStatic {
    private KeywordStatic() {
    }

    //关联条件类型
    //后台类目
    public static final int RELATION_TYPE_BACKEND = 1;
    //销售属性
    public static final int RELATION_TYPE_SALE_PROPERTY_GROUP = 2;
    //品牌
    public static final int RELATION_TYPE_BRAND = 3;

    //状态
    // 启用
    public static final int STATE_NORMAL = 0;
    // 禁用
    public static final int STATE_DISABLE = 1;

    //消息队列
    //操作类型
    public static final String OP_TYPE_SAVE = "save";
    public static final String OP_TYPE_DELETE = "delete";
    //主题名称
    public static final String OP_TOPIC="xtx_keyword_op_topic";

}
