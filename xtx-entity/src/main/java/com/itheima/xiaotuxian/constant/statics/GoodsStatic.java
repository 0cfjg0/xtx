package com.itheima.xiaotuxian.constant.statics;


public class GoodsStatic {
    private GoodsStatic() {
    }

    //上架类型
    public static final int SHELF_TYPE_IMMEDIATELY = 1; //立即上架
    public static final int SHELF_TYPE_TIMING = 2; //定时上架
    public static final int SHELF_TYPE_STORE = 3; //仓库

    //库存减扣方式
    public static final int STYLE_DECREASE_STOCK_ORDER = 1; //拍下减库存
    public static final int STYLE_DECREASE_STOCK_PAY = 2; //付款减库存

    //支持换货
    public static final int EXCHANGE_SUPPORT = 0; //支持
    public static final int EXCHANGE_UN_SUPPORT = 1; //不支持

    //支持7天无理由退货
    public static final int NO_REASON_TO_RETURN_SUPPORT = 0; //支持
    public static final int NO_REASON_TO_RETURN_UN_SUPPORT = 1; //不支持

    //编辑状态
    public static final int EDIT_STATE_DRAFT = 0;//草稿
    public static final int EDIT_STATE_SUBMIT = 1;//提交



    //商品状态
    public static final int STATE_SELLING = 1; //出售中
    public static final int STATE_STORING = 2; //仓库中
    public static final int STATE_SOLD_OUT = 3; //已售罄
    public static final int STATE_RECYCLE = 4; //回收站
    public static final int STATE_HISTORY = 5; //历史宝贝

    //审核状态
    public static final int AUDIT_STATE_WAIT = 1; //待审核
    public static final int AUDIT_STATE_PASS = 2; //审核通过
    public static final int AUDIT_STATE_REJECT = 3; //驳回

    //消息队列
    //操作类型
    public static final String OP_TYPE_SAVE = "save";
    public static final String OP_TYPE_DELETE = "delete";
    public static final String OP_TYPE_AUDIT = "audit";
    public static final String OP_TYPE_FLUSH = "flush";
    //主题名称
    public static final String OP_TOPIC = "xtx_goods_op_topic";

}
