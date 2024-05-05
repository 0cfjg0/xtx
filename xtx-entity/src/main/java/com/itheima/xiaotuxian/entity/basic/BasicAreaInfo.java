package com.itheima.xiaotuxian.entity.basic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: bc.lv
 * @Date: 2023年3月10日16:30:35
 * @Description: 地区信息表-基于2020国家数据
 */
@Data
@TableName("basic_area_info")
public class BasicAreaInfo implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 父级id
     */
    private String parentId;
    /**
     * 名称-当前级别的名称
     */
    private String name;
    /**
     * 所有级联名称
     */
    private String mergerName;
    /**
     * 简称-名称
     */
    private String shortName;
    /**
     * 简称-所有级联名称
     */
    private String mergerShortName;
    /**
     * 级别
     */
    private String levelType;
    /**
     * 市区号-电话区号
     */
    private String cityCode;
    /**
     * 邮政编码
     */
    private String zipCode;
    /**
     * 拼音
     */
    private String pinyin;
    /**
     * 简拼
     */
    private String jianpin;
    /**
     * 首字母
     */
    private String firstChar;
    /**
     * 经度
     */
    private BigDecimal lng;
    /**
     * 维度
     */
    private BigDecimal lat;
    /**
     * 备注
     */
    private String remarks;



}
