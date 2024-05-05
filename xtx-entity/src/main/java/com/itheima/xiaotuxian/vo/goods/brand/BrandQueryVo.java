package com.itheima.xiaotuxian.vo.goods.brand;

import com.itheima.xiaotuxian.vo.classification.BackendSimpleVo;
import lombok.Data;

import java.util.List;

@Data
public class BrandQueryVo {
    /**
     * 页码
     */
    private Integer page;
    /**
     * 页尺寸
     */
    private Integer pageSize;
    /**
     * 名称关键字
     */
    private String name;
    /**
     * 产地
     */
    private String productionPlace;
    /**
     * 状态，0为可用，1为不可用
     */
    private Integer state;
    /**
     * 关联后台类目集合
     */
    private List<BackendSimpleVo> backends;
    /**
     * 后台类目Id
     */
    private String backendId;
    /**
     * 前台类目id
     */
    private String frontId;
    /**
     * 品牌id集合
     */
    private List<String> ids;
    /**
     * 编辑状态
     */
    private Integer editState;
    /**
     * 数量限制
     */
    private Integer limit;
    /**
     * 首字母
     */
    private String firstWord;
}
