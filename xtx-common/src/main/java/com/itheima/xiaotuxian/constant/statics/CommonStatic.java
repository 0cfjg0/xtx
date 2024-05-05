package com.itheima.xiaotuxian.constant.statics;

public class CommonStatic {
    public static final String REQUEST_CLIENT_PC = "pc";
    public static final String REQUEST_CLIENT_APP = "app";
    public static final String REQUEST_CLIENT_MINIAPP = "miniapp";

    //1为pc，2为webapp，3为微信小程序，4为Android，5为ios 也对应了投放渠道
    public static final Integer SOURCE_REGISTER_PC = 1;
    public static final Integer SOURCE_REGISTER_APP = 2;
    public static final Integer SOURCE_REGISTER_WX_MINI = 3;
    public static final Integer SOURCE_REGISTER_WEBAPP = 4;

    //素材发布渠道
    public static final int MATERIAL_SHOW_PC = 1;//pc
    public static final int MATERIAL_SHOW_APP = 2;//app
    public static final int MATERIAL_SHOW_MINIAPP = 3 ;//miniapp小程序

    private CommonStatic() {
    }
}
