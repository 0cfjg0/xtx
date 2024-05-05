package com.itheima.xiaotuxian.service.material.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.MaterialStatic;
import com.itheima.xiaotuxian.entity.material.MaterialConfig;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.material.MaterialConfigMapper;
import com.itheima.xiaotuxian.service.material.MaterialConfigService;
import com.itheima.xiaotuxian.util.OSSUtil;
import com.itheima.xiaotuxian.vo.material.PictureMaskVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class MaterialConfigServiceImpl extends ServiceImpl<MaterialConfigMapper, MaterialConfig> implements MaterialConfigService {
    @Value("${tmp.file-directory}")
    private String fileDirectory;
    @Autowired
    private OSSUtil ossUtil;

    @Override
    public Boolean saveMaskConfig(String maskPath, PictureMaskVo maskConfig, String opUser) {
        var configs = new ArrayList<MaterialConfig>();
        //当水印文件存在时，上传水印文件,并更新配置项
        Optional.ofNullable(maskPath).ifPresent(mp -> {
            var uploadResult = ossUtil.upload2Oss(mp);
            configs.add(fillConfig(MaterialStatic.CONFIG_KEY_PICTURE_MASK_FILE_URL, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, uploadResult.get(OSSUtil.KEY_FILE_URL).toString(), opUser));
        });

        var configOptional = Optional.ofNullable(maskConfig);
        //处理水印类型
        var supportMaskTypes = new String[]{"picture", "text"};
        configOptional.map(PictureMaskVo::getMaskType)
                .filter(mt -> StrUtil.equalsAny(mt, supportMaskTypes))
                .ifPresentOrElse(mt -> configs.add(fillConfig(MaterialStatic.CONFIG_KEY_PICTURE_MASK_TYPE, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, mt, opUser))
                        , () -> {
                            throw new BusinessException(ErrorMessageEnum.MATERIAL_MASK_TYPE_ERROR);
                        });
        //处理水印文字
        configOptional.map(PictureMaskVo::getMaskText)
                .ifPresent(mt -> configs.add(fillConfig(MaterialStatic.CONFIG_KEY_PICTURE_MASK_TEXT, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, mt, opUser)));
        //处理水印文字字体
        var supportMaskTextTypes = new String[]{"d3F5LXplbmhlaQ", "d3F5LW1pY3JvaGVp", "ZmFuZ3poZW5nc2h1c29uZw", "ZmFuZ3poZW5na2FpdGk", "ZmFuZ3poZW5naGVpdGk", "ZmFuZ3poZW5nZmFuZ3Nvbmc"};
        configOptional.map(PictureMaskVo::getMaskTextType)
                .filter(mtt -> StrUtil.equalsAny(mtt, supportMaskTextTypes))
                .ifPresent(mtt -> configs.add(fillConfig(MaterialStatic.CONFIG_KEY_PICTURE_MASK_TEXT_TYPE, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, mtt, opUser)));
        //处理水印文字颜色
        configOptional.map(PictureMaskVo::getMaskTextColor)
                .ifPresent(color -> configs.add(fillConfig(MaterialStatic.CONFIG_KEY_PICTURE_MASK_TEXT_COLOR, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, color.replace("#",""), opUser)));
        //处理水印透明度
        configOptional.map(PictureMaskVo::getPictureMaskShadow)
                .filter(shadow -> maskConfig.getPictureMaskShadow() >= 0 && maskConfig.getPictureMaskShadow() <= 100)
                .ifPresent(shadow -> configs.add(fillConfig(MaterialStatic.CONFIG_KEY_PICTURE_MASK_SHADOW, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, shadow.toString(), opUser)));
        configOptional.map(PictureMaskVo::getTextMaskShadow)
                .filter(shadow -> maskConfig.getTextMaskShadow() >= 0 && maskConfig.getTextMaskShadow() <= 100)
                .ifPresent(shadow -> configs.add(fillConfig(MaterialStatic.CONFIG_KEY_TEXT_MASK_SHADOW, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, shadow.toString(), opUser)));
        //处理水印基准点
        var supportMaskSites = new String[]{"nw", "north", "ne", "west", "center", "east", "sw", "south", "se"};
        configOptional.map(PictureMaskVo::getPictureMaskSite)
                .filter(ms -> StrUtil.equalsAny(ms, supportMaskSites))
                .ifPresent(ms -> configs.add(fillConfig(MaterialStatic.CONFIG_KEY_PICTURE_MASK_SITE, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, ms, opUser)));
        configOptional.map(PictureMaskVo::getTextMaskSite)
                .filter(ms -> StrUtil.equalsAny(ms, supportMaskSites))
                .ifPresent(ms -> configs.add(fillConfig(MaterialStatic.CONFIG_KEY_TEXT_MASK_SITE, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, ms, opUser)));
        //处理水印横轴偏移量
        configOptional.map(PictureMaskVo::getPicturex)
                .ifPresent(x -> configs.add(fillConfig(MaterialStatic.CONFIG_KEY_PICTURE_MASK_X, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, x.toString(), opUser)));
        configOptional.map(PictureMaskVo::getTextx)
                .ifPresent(x -> configs.add(fillConfig(MaterialStatic.CONFIG_KEY_TEXT_MASK_X, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, x.toString(), opUser)));
        //处理水印纵轴偏移量
        configOptional.map(PictureMaskVo::getPicturey)
                .ifPresent(y -> configs.add(fillConfig(MaterialStatic.CONFIG_KEY_PICTURE_MASK_Y, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, y.toString(), opUser)));
        configOptional.map(PictureMaskVo::getTexty)
                .ifPresent(y -> configs.add(fillConfig(MaterialStatic.CONFIG_KEY_TEXT_MASK_Y, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, y.toString(), opUser)));
        //处理水印文字大小
        configOptional.map(PictureMaskVo::getMaskTextSize)
                .ifPresent(mts -> configs.add(fillConfig(MaterialStatic.CONFIG_KEY_PICTURE_MASK_TEXT_SIZE, MaterialStatic.CONFIG_TYPE_PICTURE_MASK, mts.toString(), opUser)));

        //操作数据
        if (CollUtil.isNotEmpty(configs)) {
            saveOrUpdateBatch(configs);
        }
        return true;
    }

    @Override
    public PictureMaskVo getPictureMaskConfig() {
        PictureMaskVo maskConfig = null;
        var configs = list(Wrappers
                .<MaterialConfig>lambdaQuery()
                .eq(MaterialConfig::getConfigType, MaterialStatic.CONFIG_TYPE_PICTURE_MASK)
        ).stream().collect(Collectors.toMap(MaterialConfig::getName, MaterialConfig::getValue));
        if (CollUtil.isNotEmpty(configs)) {
            maskConfig = new PictureMaskVo();
            maskConfig.setMaskType(configs.get(MaterialStatic.CONFIG_KEY_PICTURE_MASK_TYPE));
            maskConfig.setMaskText(configs.get(MaterialStatic.CONFIG_KEY_PICTURE_MASK_TEXT));
            maskConfig.setMaskTextType(configs.get(MaterialStatic.CONFIG_KEY_PICTURE_MASK_TEXT_TYPE));
            maskConfig.setMaskTextColor(configs.get(MaterialStatic.CONFIG_KEY_PICTURE_MASK_TEXT_COLOR));
            if (configs.containsKey(MaterialStatic.CONFIG_KEY_PICTURE_MASK_SHADOW)) {
                maskConfig.setPictureMaskShadow(Integer.valueOf(configs.get(MaterialStatic.CONFIG_KEY_PICTURE_MASK_SHADOW)));
            }
            if (configs.containsKey(MaterialStatic.CONFIG_KEY_TEXT_MASK_SHADOW)) {
                maskConfig.setTextMaskShadow(Integer.valueOf(configs.get(MaterialStatic.CONFIG_KEY_TEXT_MASK_SHADOW)));
            }
            if (configs.containsKey(MaterialStatic.CONFIG_KEY_PICTURE_MASK_SITE)) {
                maskConfig.setPictureMaskSite(configs.get(MaterialStatic.CONFIG_KEY_PICTURE_MASK_SITE));
            }
            if (configs.containsKey(MaterialStatic.CONFIG_KEY_TEXT_MASK_SITE)) {
                maskConfig.setTextMaskSite(configs.get(MaterialStatic.CONFIG_KEY_TEXT_MASK_SITE));
            }
            if (configs.containsKey(MaterialStatic.CONFIG_KEY_PICTURE_MASK_X)) {
                maskConfig.setPicturex(Integer.valueOf(configs.get(MaterialStatic.CONFIG_KEY_PICTURE_MASK_X)));
            }
            if (configs.containsKey(MaterialStatic.CONFIG_KEY_TEXT_MASK_X)) {
                maskConfig.setTextx(Integer.valueOf(configs.get(MaterialStatic.CONFIG_KEY_TEXT_MASK_X)));
            }
            if (configs.containsKey(MaterialStatic.CONFIG_KEY_PICTURE_MASK_Y)) {
                maskConfig.setPicturey(Integer.valueOf(configs.get(MaterialStatic.CONFIG_KEY_PICTURE_MASK_Y)));
            }
            if (configs.containsKey(MaterialStatic.CONFIG_KEY_TEXT_MASK_Y)) {
                maskConfig.setTexty(Integer.valueOf(configs.get(MaterialStatic.CONFIG_KEY_TEXT_MASK_Y)));
            }
            maskConfig.setMaskFileUrl(configs.get(MaterialStatic.CONFIG_KEY_PICTURE_MASK_FILE_URL));
            if (configs.containsKey(MaterialStatic.CONFIG_KEY_PICTURE_MASK_TEXT_SIZE)) {
                maskConfig.setMaskTextSize(Integer.valueOf(configs.get(MaterialStatic.CONFIG_KEY_PICTURE_MASK_TEXT_SIZE)));
            }
        } else {
            maskConfig = new PictureMaskVo();
            maskConfig.setMaskType("text");
        }

        return maskConfig;
    }

    /**
     * 创建配置
     *
     * @param name       配置名称
     * @param configType 配置类型
     * @param value      配置值
     * @param opUser     操作人
     * @return 配置信息
     */
    private MaterialConfig fillConfig(String name, String configType, String value, String opUser) {
        var config = getOne(Wrappers
                .<MaterialConfig>lambdaQuery()
                .eq(MaterialConfig::getName, name)
                .eq(MaterialConfig::getConfigType, configType)
        );
        config = Optional.ofNullable(config).orElse(createConfig(name, configType, opUser));
        config.setValue(value);
        return config;
    }

    private MaterialConfig createConfig(String name, String configType, String opUser) {
        var config = new MaterialConfig();
        config.setName(name);
        config.setConfigType(configType);
        config.setCreator(opUser);
        return config;
    }
}
