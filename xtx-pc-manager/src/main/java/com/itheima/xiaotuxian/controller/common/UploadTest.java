package com.itheima.xiaotuxian.controller.common;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.entity.material.MaterialPicture;
import com.itheima.xiaotuxian.service.material.MaterialPictureService;
import com.itheima.xiaotuxian.util.OSSConvertUtil;
import com.itheima.xiaotuxian.util.OSSUtil;
import com.itheima.xiaotuxian.vo.R;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/ceshi")
public class UploadTest {
    @Autowired
    OSSUtil ossUtil;
    @Autowired
    OSSConvertUtil ossConvertUtil;
    @Autowired
    MaterialPictureService materialPictureService;

    /**
     * 测试将网络图片上传到oss
     *
     * @return
     */
    @PostMapping("/testUploadToLocal")
    public String testUploadToLocal() {

        String url = ossConvertUtil.testUploadToLocal();
        log.info(url);
        return url;
    }

    /**
     * 测试上传图片
     *
     * @throws IOException
     */
    @PostMapping("/testSaveFileToOss")
    public R<Map<String, Object>> testSaveFileToOss() {
        Map<String, Object> ossResult = ossConvertUtil.covertToAliyunOss(
                "http://yjy-xiaotuxian-dev.oss-cn-beijing.aliyuncs.com/picture/2023-05-06/201516e3-25d0-48f5-bcee-7f0cafb14176.png");
        return R.ok(ossResult);
    }

    /**
     * 测试上传图片
     *
     * @throws IOException
     */
    @PostMapping("/testAllSaveFileToOss")
    public R convertYanxuanToOss() {
        // 全表转换的参数
        // int size = 10;
        // 读取严选的图片
        // int total = materialPictureService.count(Wrappers.<MaterialPicture>lambdaQuery()
        //         .like(MaterialPicture::getUrl, "yanxuan"));
        // int pageTotal = Math.round(total / size);
        // 测试使用数据
        int size = 5;
        int pageTotal = 1;
        // 计数器 转换成功的图片数量
        int count = 0;
        int currnet = 1;
        IPage<MaterialPicture> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(currnet, size);
        // 每次读取100条 间隔1秒去转换
        for (int i = 0; i < pageTotal; i++) {
            IPage<MaterialPicture> mPage = materialPictureService.page(page, Wrappers.<MaterialPicture>lambdaQuery()
                    .like(MaterialPicture::getUrl, "yanxuan")
                    .orderByDesc(MaterialPicture::getName));
            List<MaterialPicture> materialPictures = mPage.getRecords();
            for (int j = 0; j < materialPictures.size(); j++) {
                MaterialPicture materialPicture = materialPictures.get(j);
                MaterialPicture newMaterialPicture = new MaterialPicture();
                newMaterialPicture.setUrlBak(materialPicture.getUrl());
                // 上传oss
                Map<String, Object> ossResult = ossConvertUtil.covertToAliyunOss(materialPicture.getUrl());
                // 更新图片的地址
                // 合并地址
                newMaterialPicture.setConvertUrl((String) ossResult.get("url"));
                newMaterialPicture.setFileSize((Long)ossResult.get("fileSize"));
                newMaterialPicture.setUrl((String) ossResult.get("url"));
                newMaterialPicture.setId(materialPicture.getId());
                boolean updateFlag = materialPictureService.updateById(newMaterialPicture);
                if(!updateFlag){
                    log.error("当前的数据修改没有成功，数据为：materialPicture", JSON.toJSONString(materialPicture));
                }else{
                    count++;
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return R.ok(count);
    }
}
