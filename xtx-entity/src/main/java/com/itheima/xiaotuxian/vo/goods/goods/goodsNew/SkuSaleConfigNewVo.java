package com.itheima.xiaotuxian.vo.goods.goods.goodsNew;

import com.itheima.xiaotuxian.vo.material.PictureSimpleVo;
import lombok.Data;

@Data
public class SkuSaleConfigNewVo {
    /**
     * 值信息，[一层值,二层值]
     * { valueName: string, remark: string, valuePicture: {  url, id} }
     */
    private String valueName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 图片信息
     */
    private PictureSimpleVo valuePicture;
}
