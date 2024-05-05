package com.itheima.xiaotuxian.vo.goods.property;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itheima.xiaotuxian.vo.property.PropertyGroupVo;
import lombok.Data;

import java.util.List;

/**
 * @author: itheima
 * @Date: 2023/7/12 1:59 下午
 * @Description:
 */
@Data
public class GoodsPropertyNewVo {
    private String id;
    /**
     * id集合
     */
    @JsonIgnore
    private List<String> ids;
    /**
     * 属性名称
     */
    private String name;

    /**
     * 属性别名
     */
    @JsonIgnore
    private List<String> alias;
    /**
     * 值的层级，最少1层
     */
    @JsonIgnore
    private Integer layer;
    /**
     * 值录入方式，manual为手工、list为列表选择，多值以逗号进行分割
     */
    @JsonIgnore
    private List<String> inputType;
    /**
     * 是否必填，0为必填，1为非必填
     */
    @JsonIgnore
    private Integer required;
    /**
     * 是否备注，0为备注，1为不备注
     */

    private Integer isRemark;
    /**
     * 值选择类型，1为单选，2为多选
     */
    @JsonIgnore
    private Integer valueChooseType;
    /**
     * 是否被搜索到
     */
    @JsonIgnore
    private Integer searchEnable;
    /**
     * 属性组类型,1为关键属性，2为销售属性（规格属性），3为非关键属性，4为基本属性
     */
    @JsonIgnore
    private Integer propertyType;
    /**
     * 状态,0为启用，1为禁用
     */
    @JsonIgnore
    private Integer state;
    /**
     * 前端样式-值与子值排列方式,1为上下排列，2为左右排列
     */
    @JsonIgnore
    private Integer styleValueLayout;
    /**
     * 前端样式-图片大小，1为48*48  2为12*12
     */
    @JsonIgnore
    private Integer styleImageSize;
    /**
     * 编辑状态，0为草稿，1为发布
     */
    @JsonIgnore
    private Integer editState;
    /**
     * 值选择类型，1为单选，2为多选
     */
    @JsonIgnore
    private Integer subValueChooseType;
    /**
     * 子值录入方式，manual为手工、list为列表选择，多值以逗号进行分割
     */
    @JsonIgnore
    private List<String> subValueInputType;
    /**
     * 值和子值是否传图，0为否，1为是
     */
    private Integer valueHasPicture;
    /**
     * 属性组id
     */
    @JsonIgnore
    private PropertyGroupVo group;
    /**
     * 属性组名称
     */
    private String groupName;

    /**
     * 属性值列表
     */
    private List<GoodsPropertyValueNewVo> values;
}
