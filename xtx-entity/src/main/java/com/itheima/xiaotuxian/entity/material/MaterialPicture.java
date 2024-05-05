package com.itheima.xiaotuxian.entity.material;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 素材-图片
 */
@Data
@TableName(value = "material_picture")
public class MaterialPicture extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 图片名称
     */
    private String name;
    /**
     * 图片宽度，单位px
     */
    private Integer width;
    /**
     * 图片高度，单位px
     */
    private Integer height;
    /**
     * 所属图片组id
     */
    private String groupId;
    /**
     * 图片链接
     */
    private String url;
    /**
     * 严选图片地址转换后的oss地址
     */
    private String convertUrl;
    /**
     * 对要修改的url备份
     */
    private String urlBak;
    /**
     * 文件大小，单位k
     */
    private Long fileSize;
    /**
     * 状态，1为正常，2为回收站
     */
    private Integer state;
    /**
     * 图片审核状态，1为待审核，2为审核通过（即正常），3为审核不通过，4为待人工审核
     */
    private Integer auditState;
    /**
     * 展示渠道，1为手机，2为PC
     */
    private Integer display;
}
