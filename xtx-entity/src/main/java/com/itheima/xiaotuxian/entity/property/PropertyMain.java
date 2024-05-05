package com.itheima.xiaotuxian.entity.property;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 属性-属性主体
 */
@Data
@TableName(value = "property_main")
public class PropertyMain extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 属性名称
     */
    private String name;
    /**
     * 属性别名，多个，以半角逗号分割
     */
    private String alias;
    /**
     * 值的层级，最少1层
     */
    private Integer layer;
    /**
     * 值录入方式，manual为手工、list为列表选择，多值以逗号进行分割
     */
    private String inputType;
    /**
     * 是否必填，0为必填，1为非必填
     */
    private Integer required;
    /**
     * 是否备注，0为备注，1为不备注
     */
    private Integer isRemark;
    /**
     * 值选择类型，1为单选，2为多选
     */
    private Integer valueChooseType;
    /**
     * 是否被搜索到
     */
    private Integer searchEnable;
    /**
     * 属性组类型,1为关键属性，2为销售属性（规格属性），3为非关键属性，4为基本属性
     */
    private Integer propertyType;
    /**
     * 状态,0为启用，1为禁用
     */
    private Integer state;
    /**
     * 前端样式-值与子值排列方式,1为上下排列，2为左右排列
     */
    private Integer styleValueLayout;
    /**
     * 前端样式-图片大小，1为48*48  2为12*12
     */
    private Integer styleImageSize;
    /**
     * 编辑状态，0为草稿，1为发布
     */
    private Integer editState;
    /**
     * 子值选择类型，1为单选，2为多选
     */
    private Integer subValueChooseType;
    /**
     * 子值录入方式，manual为手工、list为列表选择，多值以逗号进行分割
     */
    private String subValueInputType;
    /**
     * 值是否传图，0为否，1为是
     */
    private Integer valueHasPicture;
    /**
     * 属性组id
     */
    private String groupId;
}
