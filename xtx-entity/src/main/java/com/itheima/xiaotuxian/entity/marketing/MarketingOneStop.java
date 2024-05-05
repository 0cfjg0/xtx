package com.itheima.xiaotuxian.entity.marketing;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

@Data
@TableName(value = "marketing_one_stop")
public class MarketingOneStop extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 专场名称
     */
    private String name;
    /**
     * 副标题
     */
    private String summary;
    /**
     * 专场图片
     */
    private String picture;
}
