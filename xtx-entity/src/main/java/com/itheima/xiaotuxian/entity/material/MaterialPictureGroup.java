package com.itheima.xiaotuxian.entity.material;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 素材-图片组
 */
@Data
@TableName(value = "material_picture_group")
public class MaterialPictureGroup extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 所属图片组id
     */
    private String pid;
    /**
     * 状态，1为正常，2为回收站
     */
    private Integer state;
    /**
     * 层级，从1开始
     */
    private Integer layer;
}
