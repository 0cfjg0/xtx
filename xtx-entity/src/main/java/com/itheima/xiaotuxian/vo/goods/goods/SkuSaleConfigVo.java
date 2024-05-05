package com.itheima.xiaotuxian.vo.goods.goods;

import com.itheima.xiaotuxian.vo.material.PictureSimpleVo;
import lombok.Data;

import java.util.List;

@Data
public class SkuSaleConfigVo {
    /**
     * 值信息，[一层值,二层值]
     */
    private List<String> values;
    /**
     * 备注
     */
    private String remark;
    /**
     * 图片信息
     */
    private PictureSimpleVo img;
}
