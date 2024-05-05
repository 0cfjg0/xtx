package com.itheima.xiaotuxian.vo.property;

import lombok.Data;

import java.util.List;

@Data
public class PropertyQueryVo {
    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 页尺寸
     */
    private Integer pageSize;
    /**
     * 属性组类型,1为关键属性，2为销售属性（规格属性），3为非关键属性，4为基本属性
     */
    private Integer propertyType;
    /**
     * 关键词查询类型，取值范围[1，2，3]  分别表示为 [组名、属性名称、属性值]
     */
    private Integer keywordType = 2;
    /**
     * 关键字
     */
    private String keyword;
    /**
     * 属性组id
     */
    private String groupId;
    /**
     * 属性组id集合
     */
    private List<String> groupIds;
}
