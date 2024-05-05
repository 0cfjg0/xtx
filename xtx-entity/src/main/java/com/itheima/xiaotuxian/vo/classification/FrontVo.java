package com.itheima.xiaotuxian.vo.classification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.itheima.xiaotuxian.vo.material.PictureSimpleVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FrontVo {
    private String id;
    /**
     * 前台分类名称
     */
    private String name;
    /**
     * 分类图片
     */
    private PictureSimpleVo picture;
    /**
     * 父级分类
     */
    private FrontSimpleVo parent;
    /**
     * 关联关系集合
     */
    private List<FrontRelationVo> relations;
    /**
     * 排序，数值越大越靠前
     */
    private Integer sort;

    @JsonIgnore
    private Integer layer;

    /**
     * 前台类目关联的商品数量
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer goodsCount;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /**
     * 关联信息
     */
    private String relationInfo;
    /**
     * 子分类集合
     */
    private List<FrontVo> children;
    /**
     * 是否启用，0为启用、1为未启用
     */
    private Integer state;
}
