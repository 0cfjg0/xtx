package com.itheima.xiaotuxian.entity.marketing;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

@Data
@TableName(value = "marketing_topic_classification")
public class MarketingTopicClassification extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 图片
     */
    private String icon;
}
