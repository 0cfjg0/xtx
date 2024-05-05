package com.itheima.xiaotuxian.vo.goods.brand;

import com.itheima.xiaotuxian.vo.classification.BackendSimpleVo;
import lombok.Data;

import java.util.List;

@Data
public class BrandVo {
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
     * 产地
     */
    private String productionPlace;
    /**
     * 备注
     */
    private String description;
    /**
     * 状态，0为可用，1为不可用
     */
    private Integer state;
    /**
     * logo
     */
    private String logo;
    /**
     * 关联类目集合
     */
    private List<BackendSimpleVo> backends;
}
