package com.itheima.xiaotuxian.service.material.impl;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.MaterialStatic;
import com.itheima.xiaotuxian.constant.statics.RedisKeyStatic;
import com.itheima.xiaotuxian.entity.material.MaterialPicture;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.material.MaterialPictureMapper;
import com.itheima.xiaotuxian.service.material.MaterialConfigService;
import com.itheima.xiaotuxian.service.material.MaterialPictureService;
import com.itheima.xiaotuxian.util.OSSUtil;
import com.itheima.xiaotuxian.vo.material.MaterialUploadResultVo;
import com.itheima.xiaotuxian.vo.material.PictureItemVo;
import com.itheima.xiaotuxian.vo.material.PicturePageQueryVo;
import com.itheima.xiaotuxian.vo.material.PictureUploadVo;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

import static com.itheima.xiaotuxian.util.MaterialUtil.FILE_2_PICTURE;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:35 下午
 * @Description:
 */
@Slf4j
@Service
public class MaterialPictureServiceImpl extends ServiceImpl<MaterialPictureMapper, MaterialPicture> implements MaterialPictureService {
    @Autowired
    private MaterialConfigService configService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Value("${tmp.file-directory}")
    private String fileDirectory;
    @Autowired
    private OSSUtil ossUtil;


    @Override
    public List<MaterialPicture> findAll(List<String> ids) {
        var lambdaQueryChainWrapper = lambdaQuery();
        if (CollUtil.isNotEmpty(ids)) {
            lambdaQueryChainWrapper.in(MaterialPicture::getId, ids);
        }
        return lambdaQueryChainWrapper.list();
    }

    @Override
    public Page<PictureItemVo> findMaterialByPage(PicturePageQueryVo query) {
        var ipage = new Page<PictureItemVo>(query.getPage() == null ? 1 : query.getPage(), query.getPageSize() == null ? 10 : query.getPageSize());
        if(StringUtils.isBlank(query.getSortName())){
            if(StringUtils.isNoneBlank(query.getSortMethod())){
                log.error("查询图片分页信息入参错误，入参："+ JSON.toJSONString(query));
                throw  new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
            }
            // 无排序入参时，增加默认值
            query.setSortName("updateTime");
            query.setSortMethod(SortOrder.DESC.toString());
        }
        return baseMapper.findByPage(ipage, query);
    }

    @Transactional
    @Override
    public Boolean updatePicture(String id, String picturePath, String filename) {
        var pictureEntity = new MaterialPicture();
        Optional.ofNullable(picturePath).ifPresent(path -> {
            try {
                File file = new File(path);
                var image = ImageIO.read(file);
                var uploadResult = ossUtil.upload2Oss(path);
                pictureEntity.setUrl(uploadResult.get("url").toString());
                pictureEntity.setFileSize(FileUtil.size(file) / 1024);
                pictureEntity.setWidth(image.getWidth());
                pictureEntity.setHeight(image.getHeight());
            } catch (IOException e) {
                throw new BusinessException(ErrorMessageEnum.READ_PICTURE_ERROR);
            }
        });
        pictureEntity.setId(id);
        pictureEntity.setName(filename);
        updateById(pictureEntity);
        //更新缓存
        writePictureCache(pictureEntity.getId());
        FileUtil.del(picturePath);
        return true;
    }

    @Override
    public Boolean copyPicture(String id) {
        Optional.ofNullable(getById(id)).ifPresentOrElse(origin -> {
//            var dir = fileDirectory + File.separator + "temp";
//            if (!FileUtil.exist(dir)) {
//                FileUtil.mkdir(dir);
//            }
//            HttpUtil.downloadFile(origin.getUrl(), FileUtil.file(dir));
//            var strs = origin.getUrl().replace(ossUtil.getAliyunConfig().getUrlPrefix(), "").split("/");
//            var fileName = strs[strs.length - 1];
//            var suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
//            var uuidFileName = IdUtil.randomUUID()+"copy" + "." + suffix;
//            log.info(dir + File.separator + fileName);
//            var uploadResult = ossUtil.upload2Oss(dir + File.separator+fileName,FILE_2_PICTURE + "/"+ DateUtil.today() +"/"+ uuidFileName);
//            log.info(JSONUtil.toJsonStr(uploadResult));

            var picture = BeanUtil.toBean(origin, MaterialPicture.class);
            picture.setId(null);
            picture.setName(origin.getName() + "_copy");
//            picture.setUrl(uploadResult.get(OSSUtil.KEY_FILE_URL).toString());
            save(picture);
            writePictureCache(picture.getId());
        }, () -> {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        });
        return true;
    }

    @Override
    public Boolean adaptPicture(String id) {
        Optional.ofNullable(getById(id)).ifPresentOrElse(picture -> {
            var dir = fileDirectory + File.separator + "temp";
            if (!FileUtil.exist(dir)) {
                FileUtil.mkdir(dir);
            }
            HttpUtil.downloadFile(picture.getUrl(), FileUtil.file(dir));
            var strs = picture.getUrl().replace(ossUtil.getAliyunConfig().getUrlPrefix(), "").split("/");
            var fileName = strs[strs.length - 1];
            var realPath = adjustWidth(MaterialStatic.WIDTH_TYPE_MOBILE, dir + File.separator + fileName);
            var uploadResult = ossUtil.upload2Oss(realPath);
            picture.setUrl(uploadResult.get("url").toString());
            picture.setDisplay(MaterialStatic.DISPLAY_PHONE);
            picture.setId(null);
            picture.setName(String.format("%s_%s_%s", picture.getName(), RandomUtil.randomString(6), "_手机"));
            var bi = ImgUtil.read(dir + File.separator + fileName);
            picture.setWidth(bi.getWidth());
            picture.setHeight(bi.getHeight());
            save(picture);
            writePictureCache(picture.getId());
            FileUtil.del(dir + File.separator + fileName);
        }, () -> {
            throw new BusinessException(ErrorMessageEnum.OBJECT_DOES_NOT_EXIST);
        });
        return true;
    }

    @Override
    public MaterialPicture findById(String id) {
        var cacheStr = redisTemplate.opsForValue().get(RedisKeyStatic.KEY_PREFIX_PICTURE + id);
        MaterialPicture picture = null;
        if (StrUtil.isNotEmpty(cacheStr)) {
            picture = JSONUtil.toBean(cacheStr, MaterialPicture.class);
        } else {
            picture = writePictureCache(id);
        }
        return picture;
    }

    // @Cacheable(value = "xiaotuxian",key = "methodName +#root.args[0]")
    @Override
    public String getPictureUrl(String id) {
        AtomicReference<String> result = new AtomicReference<>();
        Optional.ofNullable(this.getById(id)).ifPresent(picture -> result.set(picture.getUrl()));
        return result.get();
    }

    @Transactional
    @Override
    public MaterialUploadResultVo savePicture(String picturePath, String filename, PictureUploadVo picture, String opUser) {
        String realPath;
        // 宽度调整
        realPath = adjustWidth(picture.getStyleWidth(), picturePath);
        var image = ImgUtil.read(realPath);
        //上传
        var upload2OssResult = ossUtil.upload2Oss(realPath);
        var ossPictureUrl = upload2OssResult.get("url").toString();
        var ossMap = new HashMap<String, Object>();
        ossMap.putAll(upload2OssResult);
        // 水印
        if (picture.getIsMask() != null && picture.getIsMask()) {
            var config = configService.getPictureMaskConfig();
            if (config != null) {
                ossMap.putAll(ossUtil.mask2Oss(ossPictureUrl, config));
            }
        }
        var pictureEntity = new MaterialPicture();
        if (StrUtil.isNotEmpty(picture.getId())) {
            pictureEntity.setId(picture.getId());
        } else {
            pictureEntity.setCreator(opUser);
            pictureEntity.setDisplay(picture.getStyleWidth() != null && picture.getStyleWidth() == -1 ? MaterialStatic.DISPLAY_PHONE : MaterialStatic.DISPLAY_PC);
        }
        pictureEntity.setUrl(ossMap.containsKey(OSSUtil.KEY_FILE_URL) ? ossMap.get(OSSUtil.KEY_FILE_URL).toString() : ossPictureUrl);
        pictureEntity.setFileSize(ossMap.containsKey(OSSUtil.KEY_FILE_SIZE) ? Long.valueOf(ossMap.get(OSSUtil.KEY_FILE_SIZE).toString()) : null);
        pictureEntity.setHeight(image.getHeight());
        pictureEntity.setWidth(image.getWidth());
        pictureEntity.setState(MaterialStatic.STATE_MATERIAL_NORMAL);
        pictureEntity.setAuditState(MaterialStatic.AUDIT_STATE_PASS);
        Optional.ofNullable(picture.getGroupId()).ifPresent(groupId->{
            pictureEntity.setGroupId(groupId);
            if ("-1".equals(groupId)){
                pictureEntity.setGroupId("0");
            }
        });
        if (StrUtil.isEmpty(picture.getId())) {
            pictureEntity.setName(filename);
        }
        saveOrUpdate(pictureEntity);
        // 移除本地暂存文件
        FileUtil.del(picturePath);
        //处理返回值
        return fillUploadResult(writePictureCache(pictureEntity.getId()));
    }

    @Override
    public String saveAvatar(String filePath) {
        return ossUtil.upload2Oss(filePath).get("url").toString();
    }

    /**
     * 填充上传返回值
     *
     * @param picture 图片信息
     * @return 上传返回值
     */
    private MaterialUploadResultVo fillUploadResult(MaterialPicture picture) {
        var resultVo = BeanUtil.toBean(picture, MaterialUploadResultVo.class);
        resultVo.setMessage("上传成功");
        return resultVo;
    }

    /**
     * 调整图片宽度
     *
     * @param styleWidth  宽度样式 -1为手机图片 -2为800px -3为640px 正数为自定义宽度
     * @param picturePath 图片地址
     * @return 调整后图片地址
     */
    private String adjustWidth(Integer styleWidth, String picturePath) {
        var styleWidthValid = styleWidth != null && styleWidth != 0;
        var picturePathValid = StrUtil.isNotEmpty(picturePath);
        if (styleWidthValid && picturePathValid) {
            // 获取图片信息
            try {
                var bufferedImage = ImageIO.read(new File(picturePath));
                var width = bufferedImage.getWidth();
                var height = bufferedImage.getHeight();
                var builder = Thumbnails.of(picturePath);
                switch (styleWidth) {
                    case MaterialStatic.WIDTH_TYPE_MOBILE:
                        // 图片宽度区间[480,1242] 高度区间[0,1546],超出区间则等比缩放至合理数值
                        if (height > 1546) {
                            height = 1546;
                        }
                        if (width < 480) {
                            width = 480;
                        } else if (width > 1242) {
                            width = 1242;
                        }
                        break;
                    case MaterialStatic.WIDTH_TYPE_800:
                        // 图片宽度区间[0,800] 超出区间则等比缩放至合理数值
                        if (width > 800) {
                            width = 800;
                        }
                        break;
                    case MaterialStatic.WIDTH_TYPE_640:
                        // 图片宽度区间[0,640] 超出区间则等比缩放至合理数值
                        if (width > 640) {
                            width = 640;
                        }
                        break;
                    default:
                        // 图片宽度区间[0,customize] 超出区间则等比缩放至合理数值
                        if (styleWidth > 0 && width > styleWidth) {
                            width = styleWidth;
                        }
                        break;
                }
                builder.size(width, height).outputQuality(1F).keepAspectRatio(true).toFile(picturePath);
            } catch (IOException ioe) {
                log.error("savePicture failed:{}", ioe.getMessage());
                throw new BusinessException(ErrorMessageEnum.READ_PICTURE_ERROR);
            }
        }
        return picturePath;
    }

    /**
     * 写入图片缓存
     *
     * @param id 图片Id
     */
    private MaterialPicture writePictureCache(String id) {
        //写缓存
        var finalPictureEntity = getById(id);
        Optional.ofNullable(finalPictureEntity)
                .ifPresent(picture -> redisTemplate.opsForValue()
                        .set(RedisKeyStatic.KEY_PREFIX_PICTURE + picture.getId()
                                , JSONUtil.toJsonStr(picture)
                                , Duration.ofDays(7)));
        return finalPictureEntity;
    }
}
