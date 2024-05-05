package com.itheima.xiaotuxian.constant.enums;

public enum ErrorMessageEnum {
    // 公共
    UNAUTHORIZED("401", "未登录"),
    NOAUTHORIZED("403", "没有权限"),
    NOTFUND("404", "未匹配到当前路径"),
    REFRESH_TOKEN_ERROR("401001", "token刷新失败"),

    SOCIAL_LOGIN_ERROR("501", "三方登录失败"),
    SUCCESS("200", "操作成功"),
    UN_KNOW("10001", "服务器内部错误"),
    FAILURE("10002", "操作失败"),
    OBJECT_DUPLICATE_NAME("10003", "对象名称重复"),
    OBJECT_DOES_NOT_EXIST("10004", "对象不存在"),
    OBJECT_ID_INVALID("10005", "无效的对象id"),
    OBJECT_USED("10006", "对象正在被使用"),
    OBJECT_EMPTY("10007", "待操作对象不能为空"),
    OBJECT_OPERATOR_EMPTY("10008", "操作对象不能为空"),
    OBJECT_STATE_NOT_NULL("10009", "状态不能为空"),
    OBJECT_STATE_UN_SUPPORT("10010", "该状态不被支持"),
    FILE_IOEXCEPTION("10011", "保存文件发生错误"),
    FILE_UPLOAD("10012", "文件上传失败"),
    FILE_EMPTY("100013", "文件不能为空"),
    FILE_FORMAT_ERROR("10014", "上传文件格式或尺寸不正确"),
    READ_VIDEO_ERROR("10015", "视频文件读取失败"),
    READ_PICTURE_ERROR("10016", "图片文件读取失败"),
    PARAMETER_ERROR("10017", "参数错误"),
    CHAR_CONVERT_ERROR("10018", "字符串转换错误"),
    TOKEN_ERROR("10019", "token校验失败"),
    FILE_LARGER("10020", "超大文件，请检查后上传"),
    REDIRECT_ERROR("10021", "跳转时发生错误"),
    UPLOAD_URL_ERROR("10022", "上传文件的url错误"),
    UPLOAD_URL_CONNECT_ERROR("10023", "网络错误:url练测错误"),
    // 素材
    MATERIAL_GROUP_DUPLICATE("11001", "组名不能重复"),
    MATERIAL_GROUP_DUPLICATE_TRASH("11002", "回收站存在同名组"),
    MATERIAL_GROUP_PARENT_INVALID("11003", "无效的上级分组"),
    MATERIAL_GROUP_OUT_OF_LAYER("11004", "最多只能3层"),
    MATERIAL_OPERATOR_TYPE_ERROR("11005", "操作对象类型错误"),
    MATERIAL_MASK_TYPE_ERROR("11006", "水印类型错误"),
    MATERIAL_MASK_URL_ERROR("11007", "图片水印无效"),
    // 属性
    PROPERTY_GROUP_NOT_NULL("12001", "属性组不能为空"),
    // 后台分类
    CLASSIFICATION_BACKEND_MAX_LAYER("13001", "后台类目最多3层"),
    // 前台分类
    CLASSIFICATION_FRONT_MAX_LAYER("14001", "前台类目最多3层"),
    // 搜索
    ES_INIT_FAILED("15001", "初始化索引失败"),
    ES_INSERT_FAILED("15002", "保存数据失败"),
    ES_DELETE_FAILED("15003", "删除数据失败"),
    ES_DICT_FAILED("15004", "获取自定义词典失败"),
    ES_SEARCHING_FAIL("15005", "搜索时发生异常"),
    // 商品
    GOODS_INVALID_BACKEND("16001", "未配置有效的后台分类"),
    GOODS_INVALID_BRAND("16002", "未配置有效的品牌"),
    GOODS_INVALID_STATE("16003", "商品状态有误，无法审核"),
    GOODS_AUDIT_UN_SUPPORT("16004", "审核操作类型不被支持"),
    GOODS_REJECT_DESCRIPTION_NOT_NULL("16005", "驳回说明不能为空"),
    GOODS_DUPLICATE_SPU_CODE("16006", "SpuCode已存在"),
    GOODS_DUPLICATE_SKU_CODE("16007", "SkuCode已存在"),
    // 用户
    MEMBER_DOES_NOT_EXIST("17001", "用户不存在"),
    MEMBER_EXIST("17002", "用户已存在"),
    QQ_MEMBER_EXIST("17013", "QQ已绑定,请直接登录"),
    MEMBER_PASSWORD_INVALID("17003", "无效的密码"),
    MEMBER_MOBILE_FORMAT_INVALID("17004", "手机号格式错误"),
    MEMBER_SEND_MESSAGE_FAILED("17005", "发送短信失败"),
    MEMBER_CODE_INVALID("17006", "验证码错误"),
    MEMBER_CODE_EXPIRED("17007", "验证码过期"),
    MEMBER_CODE_SEND("17008", "验证码已发送"),
    MEMBER_TICKET_DOES_NOT_EXIST("17009", "验证票据不存在"),
    MEMBER_MAXIMUM_ITEM_ERROR("17010", "最多只能添加10个"),
    MEMBER_WXMINI_OPENID_ERROR("17011", "微信服务端获取openid错误"),
    MEMBER_BIND_USER_ERROR("17012", "重复绑定用户信息，请使用QQ登录"),
    MEMBER_PAY_VALID_ERROR("17013", "当前支付对象未进行授权登录"),
    // 订单
    ORDER_GOODS_NOT_NULL("18001", "商品不能为空"),
    ORDER_PAY_TIMEOUT_OR_DONE("18002", "订单已支付或支付超时"),
    ORDER_PAY_TYPE_VALID("18003", "订单支付类型错误"),
    ORDER_PAY_CONTAINS_VALID_GOODS("18004", "支付失败！存在失效商品"),
    ORDER_NO_PRIVILEGE("18005", "没有操作权限"),
    ORDER_ADDRESS_NOT_NULL("18006", "地址不能为空"),
    ORDER_ADDRESS_VALID("18007", "地址有误"),
    ORDER_GOODS_ALL_VALID("18008", "无有效商品"),
    ORDER_STATE_VALID("18009", "订单状态错误"),
    ORDER_PAY_STATE_VALID("18010", "订单支付状态错误"),
    ORDER_PAY_VERIFY_FAILURE("18011", "支付校验错误"),
    ORDER_PAY_QUERY_FAILURE("18012", "交易查询失败"),
    ORDER_PAY_FAILURE("18013", "订单支付失败！第三方支付出现异常"),
    // 管理员
    ADMIN_USERNAME_CANNOT_NULL("19001", "账号不能为空"),
    ADMIN_PASSWORD_CANNOT_NULL("19002", "密码不能为空"),
    ;

    private String code;
    private String message;

    ErrorMessageEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
