package com.itheima.xiaotuxian.constant.statics;

import java.util.Arrays;
import java.util.List;

public class MaterialStatic {
    private MaterialStatic() {
    }
    
    //素材状态
    // 正常
    public static final int STATE_MATERIAL_NORMAL = 1;
    // 回收站
    public static final int STATE_MATERIAL_TRASH = 2;
    //最大素材组层级
    public static final int GROUP_MAX_LAYER = 2;

    // 操作类型
    // 素材组
    public static final int OP_TYPE_GROUP = 1;
    // 素材
    public static final int OP_TYPE_ELEMENT = 2;

    //宽度调整
    //手机图片
    public static final int WIDTH_TYPE_MOBILE = -1;
    public static final int WIDTH_TYPE_800 = -2;
    public static final int WIDTH_TYPE_640 = -3;

    //展示渠道
    //手机
    public static final int DISPLAY_PHONE = 1;
    //PC
    public static final int DISPLAY_PC = 2;

    //审核状态
    //待审核
    public static final int AUDIT_STATE_WAIT = 1;
    //审核通过
    public static final int AUDIT_STATE_PASS = 2;
    //审核不通过
    public static final int AUDIT_STATE_BLOCK = 3;
    //待人工审核
    public static final int AUDIT_STATE_MANUAL = 4;

    //水印配置-键标
    public static final String CONFIG_TYPE_PICTURE_MASK = "picture-mask";
    public static final String CONFIG_KEY_PICTURE_MASK_TYPE = "picture-mask-type";
    public static final String CONFIG_KEY_PICTURE_MASK_TEXT = "picture-mask-text";
    public static final String CONFIG_KEY_PICTURE_MASK_TEXT_TYPE = "picture-mask-text-type";
    public static final String CONFIG_KEY_PICTURE_MASK_TEXT_COLOR = "picture-mask-text-color";
    public static final String CONFIG_KEY_PICTURE_MASK_TEXT_SIZE = "picture-mask-text-size";
    public static final String CONFIG_KEY_PICTURE_MASK_FILE_URL = "picture-mask-file-url";
    public static final String CONFIG_KEY_PICTURE_MASK_SHADOW = "picture-mask-shadow";
    public static final String CONFIG_KEY_PICTURE_MASK_SITE = "picture-mask-site";
    public static final String CONFIG_KEY_PICTURE_MASK_X = "picture-mask-x";
    public static final String CONFIG_KEY_PICTURE_MASK_Y = "picture-mask-y";
    public static final String CONFIG_KEY_TEXT_MASK_SHADOW = "text-mask-shadow";
    public static final String CONFIG_KEY_TEXT_MASK_SITE = "text-mask-site";
    public static final String CONFIG_KEY_TEXT_MASK_X = "text-mask-x";
    public static final String CONFIG_KEY_TEXT_MASK_Y = "text-mask-y";

    protected static List<String> arrVideoExtension = Arrays.asList("wmv", "avi", "mpg", "mpeg", "3gp", "mov", "mp4", "flv", "f4v", "m2t", "mts", "rmvb", "vob", "mkv");
    protected static List<String> arrPictureExtension = Arrays.asList("jpg", "jpeg", "mpg", "gif", "png");

    public static List<String> getArrVideoExtension() {
        return arrVideoExtension;
    }

    public static List<String> getArrPictureExtension() {
        return arrPictureExtension;
    }
}
