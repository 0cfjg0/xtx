package com.itheima.xiaotuxian.constant.statics;

public class RedisKeyStatic {
    private RedisKeyStatic(){}
    //素材缓存前缀
    public static final String KEY_PREFIX_PICTURE = "m:picture:";
    public static final String KEY_PREFIX_VIDEO = "m:video:";
    //商品缓存前缀
    public static final String KEY_PREFIX_SPU = "g:spu:";
    //登陆使用的token
    public static final String KEY_PREFIX_TOKEN = "jwt:token:";
    //登陆使用的token
    public static final String KEY_PREFIX_REFESH_TOKEN = "jwt:refreshToken:";
}
