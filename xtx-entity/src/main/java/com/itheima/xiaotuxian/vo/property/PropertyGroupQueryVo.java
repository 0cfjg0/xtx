package com.itheima.xiaotuxian.vo.property;

import com.itheima.xiaotuxian.vo.classification.BackendSimpleVo;
import lombok.Data;

import java.util.List;

@Data
public class PropertyGroupQueryVo {
    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 页尺寸
     */
    private Integer pageSize;
    /**
     * 属性组Id
     */
    private String id;
    /**
     * 属性组名称
     */
    private String name;
    /**
     * 属性组状态
     */
    private Integer state;
    /**
     * 属性组类型,1为关键属性，2为销售属性（规格属性），3为非关键属性，4为基本属性，未传该属性为获取全部
     */
    private Integer propertyType;
    /**
     * id集合
     */
    private List<String> ids;
    /**
     * 后台类目Id
     */
    private String backendId;
    /**
     * 后台类目id 2023-04-26
     */
    private List<BackendSimpleVo> backends;
    /**
     * 前台类目id
     */
    private String frontId;
}
