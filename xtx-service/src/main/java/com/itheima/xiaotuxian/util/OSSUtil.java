package com.itheima.xiaotuxian.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.common.utils.IOUtils;
import com.aliyun.oss.model.GenericResult;
import com.aliyun.oss.model.ProcessObjectRequest;
import com.itheima.xiaotuxian.config.AliyunOssConfig;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.vo.material.PictureMaskVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Formatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class OSSUtil {
    @Autowired
    private OSS ossClient;
    @Value("${tmp.file-directory}")
    private String fileDirectory;
    @Autowired
    private AliyunOssConfig aliyunConfig;
    public static final String KEY_FILE_URL = "url";
    public static final String KEY_FILE_SIZE = "fileSize";

    /**
     * 上传图片至OSS
     *
     * @param filePath 文件路径
     * @return 上传后路径
     */
    public Map<String, Object> upload2Oss(String filePath) {
        var file = new File(filePath);
        filePath = filePath.replace(fileDirectory + File.separator, "").replace("\\", "/");
        if(isWinOS()){
            filePath = filePath.replace(":","");
        }
        var sourceKey = filePath;
        ossClient.putObject(aliyunConfig.getBucketName(), filePath, file);
        var ossObject = ossClient.getSimplifiedObjectMeta(aliyunConfig.getBucketName(), sourceKey);
        return CollUtil.zip(ListUtil.toList(KEY_FILE_URL, KEY_FILE_SIZE), ListUtil.toList(aliyunConfig.getUrlPrefix() + sourceKey, ossObject.getSize() / 1024));
    }

    /**
     * 复制图片使用，暂不使用
     * @param filePath
     * @param sourceKey
     * @return
     */
    public Map<String, Object> upload2Oss(String filePath,String sourceKey) {
        var file = new File(filePath);
        ossClient.putObject(aliyunConfig.getBucketName(), sourceKey, file);
        var ossObject = ossClient.getSimplifiedObjectMeta(aliyunConfig.getBucketName(), sourceKey);
        return CollUtil.zip(ListUtil.toList(KEY_FILE_URL, KEY_FILE_SIZE), ListUtil.toList(aliyunConfig.getUrlPrefix() + sourceKey, ossObject.getSize() / 1024));
    }
    /**
     * 上传图片至OSS
     *
     * @param is 输入流
     * @return 上传后路径
     */
    public Map<String, Object> upload2Oss(String sourceKey, InputStream is) {
        ossClient.putObject(aliyunConfig.getBucketName(), sourceKey, is);
        var ossObject = ossClient.getSimplifiedObjectMeta(aliyunConfig.getBucketName(), sourceKey);
        return CollUtil.zip(ListUtil.toList(KEY_FILE_URL, KEY_FILE_SIZE), ListUtil.toList(aliyunConfig.getUrlPrefix() + sourceKey, ossObject.getSize() / 1024));
    }

    /**
     * 图片添加水印
     *
     * @param url 原图片链接
     * @param vo  水印设置
     * @return 水印后图片
     */
    public Map<String, Object> mask2Oss(String url, PictureMaskVo vo) {
        var sourceKey = url.replace(aliyunConfig.getUrlPrefix(), "");
        vo.setMaskText(BinaryUtil.toBase64String(vo.getMaskText().getBytes()));
        var sbStyle = new StringBuilder();
        var styleFormatter = new Formatter(sbStyle);
        var styleTypeBuilder = new StringBuilder("image/watermark");
        if ("text".equals(vo.getMaskType())) {
            Optional.of(vo.getMaskText()).filter(StrUtil::isNotEmpty).ifPresent(mt -> styleTypeBuilder.append(",text_").append(mt));
            Optional.ofNullable(vo.getMaskTextType()).filter(StrUtil::isNotEmpty).ifPresent(mtt -> styleTypeBuilder.append(",type_").append(mtt));
            Optional.ofNullable(vo.getMaskTextSize()).ifPresent(mts -> styleTypeBuilder.append(",size_").append(mts));
            Optional.ofNullable(vo.getMaskTextColor()).filter(StrUtil::isNotEmpty).ifPresent(mtc -> styleTypeBuilder.append(",color_").append(mtc));
            Optional.ofNullable(vo.getTextMaskShadow()).ifPresent(ms -> styleTypeBuilder.append(",t_").append(ms));
            Optional.ofNullable(vo.getTextMaskSite()).filter(StrUtil::isNotEmpty).ifPresent(ms -> styleTypeBuilder.append(",g_").append(ms));
        } else {
            vo.setMaskFileUrl(vo.getMaskFileUrl().replace(aliyunConfig.getUrlPrefix(), ""));
            vo.setMaskFileUrl(BinaryUtil.toBase64String(vo.getMaskFileUrl().getBytes()));
            Optional.of(vo.getMaskFileUrl()).filter(StrUtil::isNotEmpty).ifPresentOrElse(mu -> styleTypeBuilder.append(",image_").append(mu), () -> {
                throw new BusinessException(ErrorMessageEnum.MATERIAL_MASK_URL_ERROR);
            });
            Optional.ofNullable(vo.getPictureMaskShadow()).ifPresent(ms -> styleTypeBuilder.append(",t_").append(ms));
            Optional.ofNullable(vo.getPictureMaskSite()).filter(StrUtil::isNotEmpty).ifPresent(ms -> styleTypeBuilder.append(",g_").append(ms));
        }

        var fileNames = url.split("/");
        var targetImage = url.replace(aliyunConfig.getUrlPrefix(), "").replace(fileNames[fileNames.length - 1], "") + "mask-" + fileNames[fileNames.length - 1];
        styleFormatter.format("%s|sys/saveas,o_%s,b_%s", styleTypeBuilder.toString(), BinaryUtil.toBase64String(targetImage.getBytes()), BinaryUtil.toBase64String(aliyunConfig.getBucketName().getBytes()));
        log.info("sbStyle:{}", sbStyle.toString());
        var request = new ProcessObjectRequest(aliyunConfig.getBucketName(), sourceKey, sbStyle.toString());
        var processResult = ossClient.processObject(request);
        styleFormatter.close();
        return readOssResult(processResult);
    }

    /**
     * 读取oss操作结果
     *
     * @param processResult oss操作结果
     * @return 解析后内容
     */
    public Map<String, Object> readOssResult(GenericResult processResult) {
        AtomicReference<String> json = new AtomicReference<>();
        Optional.ofNullable(processResult.getResponse())
                .filter(ResponseMessage::isSuccessful)
                .map(ResponseMessage::getContent)
                .ifPresent(content -> {
                    try {
                        json.set(IOUtils.readStreamAsString(processResult.getResponse().getContent(), "UTF-8"));
                    } catch (IOException ioException) {
                        throw new BusinessException(ErrorMessageEnum.FILE_IOEXCEPTION);
                    }
                });
        var resultMap = JSONUtil.toBean(json.get(), Map.class);
        resultMap.put("url", aliyunConfig.getUrlPrefix() + resultMap.get("object"));
        if (resultMap.containsKey(KEY_FILE_SIZE)) {
            resultMap.put(KEY_FILE_SIZE, Long.valueOf(resultMap.get(KEY_FILE_SIZE).toString()) / 1024);
        }
        return resultMap;
    }

    public AliyunOssConfig getAliyunConfig() {
        return aliyunConfig;
    }


    /**
     * 是否windows系统
     */
    public static boolean isWinOS() {
        boolean isWinOS = false;
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            String sharpOsName = osName.replaceAll("windows", "{windows}").replaceAll("^win([^a-z])", "{windows}$1")
                    .replaceAll("([^a-z])win([^a-z])", "$1{windows}$2");
            isWinOS = sharpOsName.contains("{windows}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isWinOS;
    }
}
