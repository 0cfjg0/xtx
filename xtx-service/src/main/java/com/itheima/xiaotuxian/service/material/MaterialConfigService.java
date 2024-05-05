package com.itheima.xiaotuxian.service.material;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.material.MaterialConfig;
import com.itheima.xiaotuxian.vo.material.PictureMaskVo;

public interface MaterialConfigService extends IService<MaterialConfig> {
    /**
     * 保存图片水印配置
     *
     * @param maskPath   水印图片存放地址
     * @param maskConfig 水印配置信息
     * @param opUser     操作人Id
     * @return 操作结果
     */
    Boolean saveMaskConfig(String maskPath, PictureMaskVo maskConfig, String opUser);

    /**
     * 获取图片水印配置
     *
     * @return 图片水印配置
     */
    PictureMaskVo getPictureMaskConfig();
}
