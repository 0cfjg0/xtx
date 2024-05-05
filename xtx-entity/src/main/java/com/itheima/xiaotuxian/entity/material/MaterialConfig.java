package com.itheima.xiaotuxian.entity.material;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

@Data
@TableName(value = "material_config")
public class MaterialConfig extends AbstractBasePO {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 配置项标识
     */
    private String name;
    /**
     * 配置项值
     */
    private String value;
    /**
     * 配置项类型
     */
    private String configType;
}
