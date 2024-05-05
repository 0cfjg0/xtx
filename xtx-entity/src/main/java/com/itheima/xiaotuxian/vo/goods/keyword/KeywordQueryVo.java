package com.itheima.xiaotuxian.vo.goods.keyword;

import lombok.Data;

import java.util.List;

@Data
public class KeywordQueryVo {
    /**
     * id
     */
    private String id;
    /**
     * id集合
     */
    private List<String> ids;
    /**
     * 后台类目id
     */
    private String backendId;
    /**
     * 后台类目id集合
     */
    private List<String> backendIds;
    /**
     * 品牌id
     */
    private String brandId;
    /**
     * 品牌id集合
     */
    private List<String> brandIds;
    /**
     * 属性组id
     */
    private String propertyGroupId;
    /**
     * 属性组id集合
     */
    private List<String> propertyGroupIds;
}
