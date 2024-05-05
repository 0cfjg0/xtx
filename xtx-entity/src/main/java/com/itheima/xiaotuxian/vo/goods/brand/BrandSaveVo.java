package com.itheima.xiaotuxian.vo.goods.brand;

import com.itheima.xiaotuxian.vo.classification.BackendSimpleVo;
import com.itheima.xiaotuxian.vo.material.PictureSimpleVo;
import lombok.Data;

import java.util.List;

@Data
public class BrandSaveVo {
    /**
     * id
     */
    private String id;
    /**
     * 品牌名称
     */
    private String name;
    /**
     * 英文品牌名
     */
    private String nameEn;
    /**
     * slogan
     */
    private String slogan;
    /**
     * 商标注册号
     */
    private String trademarkCode;
    /**
     * 品牌所有人
     */
    private String brandOwner;
    /**
     * 产地，多字段通过/分割
     */
    private String productionPlace;
    /**
     * 备注
     */
    private String description;
    /**
     * 品牌故事
     */
    private String brandStory;
    /**
     * 状态，0为可用，1为不可用
     */
    private Integer state;
    /**
     * 编辑状态，0为草稿，1为发布
     */
    private Integer editState;
    /**
     * logo信息
     */
    private PictureSimpleVo logo;
    /**
     * 关联类目集合
     */
    private List<BackendSimpleVo> backends;
    /**
     * 品牌大图
     */
    private PictureSimpleVo brandImage;
}
