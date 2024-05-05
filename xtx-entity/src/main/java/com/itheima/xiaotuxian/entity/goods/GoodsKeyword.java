package com.itheima.xiaotuxian.entity.goods;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.itheima.xiaotuxian.entity.AbstractBasePO;
import lombok.Data;

/**
 * @author: itheima
 * @Date: 2023/7/11 10:06 上午
 * @Description: 商品-关键字
 */
@Data
@TableName(value = "goods_keyword")
public class GoodsKeyword extends AbstractBasePO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 联想词集,以,分割
     */
    private String associateWords;
    /**
     * 关键词
     */
    private String title;
    /**
     * 状态，0为开启，1为关闭
     */
    private Integer state;
}
