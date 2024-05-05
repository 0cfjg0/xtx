package com.itheima.xiaotuxian.service.material.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.config.AliyunOssConfig;
import com.itheima.xiaotuxian.constant.statics.MaterialStatic;
import com.itheima.xiaotuxian.constant.statics.RedisKeyStatic;
import com.itheima.xiaotuxian.entity.material.MaterialVideo;
import com.itheima.xiaotuxian.mapper.material.MaterialVideoMapper;
import com.itheima.xiaotuxian.service.material.MaterialVideoService;
import com.itheima.xiaotuxian.util.OSSUtil;
import com.itheima.xiaotuxian.vo.material.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * @author: itheima
 * @Date: 2023/7/11 3:36 下午
 * @Description:
 */
@Slf4j
@Service
public class MaterialVideoServiceImpl extends ServiceImpl<MaterialVideoMapper, MaterialVideo> implements MaterialVideoService {
    @Autowired
    private OSSUtil ossUtil;
    @Autowired
    private AliyunOssConfig aliyunConfig;
    @Value("${tmp.file-directory}")
    private String fileDirectory;
    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public VideoUploadResultVo uploadFile(String videoPath, String coverPath, String squareCoverPath) {
        // 上传文件
        var videoUrl = ossUtil.upload2Oss(videoPath).get("url").toString();
        var coverUrl = ossUtil.upload2Oss(coverPath).get("url").toString();
        var squareCoverUrl = ossUtil.upload2Oss(squareCoverPath).get("url").toString();
        // 移除本地文件
        FileUtil.del(videoPath);
        FileUtil.del(coverPath);
        FileUtil.del(squareCoverPath);
        return new VideoUploadResultVo(videoUrl, coverUrl, squareCoverUrl);
    }

    @Override
    public List<MaterialVideo> findAll(List<String> ids) {
        var lambdaQueryChainWrapper = lambdaQuery();
        if (CollUtil.isNotEmpty(ids)) {
            lambdaQueryChainWrapper.in(MaterialVideo::getId, ids);
        }
        return lambdaQueryChainWrapper.list();
    }

    @Override
    public Page<VideoItemVo> findMaterialByPage(VideoPageQueryVo query) {
        var ipage = new Page<VideoItemVo>(query.getPage(), query.getPageSize());
        return baseMapper.findByPage(ipage, query);
    }

    @Transactional
    @Override
    public Boolean saveVideo(VideoSaveVo saveVo) {
        Optional.ofNullable(saveVo.getLocalCoverPath()).filter(StrUtil::isNotEmpty).ifPresent(localCoverPath ->
                saveVo.setCoverImg(ossUtil.upload2Oss(localCoverPath).get("url").toString()));
        Optional.ofNullable(saveVo.getLocalSquareCoverPath()).filter(StrUtil::isNotEmpty).ifPresent(localSquareCoverPath ->
                saveVo.setSquareCoverImg(ossUtil.upload2Oss(localSquareCoverPath).get("url").toString()));
        var entity = BeanUtil.toBean(saveVo, MaterialVideo.class);
        if (StrUtil.isEmpty(entity.getId())) {
            entity.setAuditState(MaterialStatic.AUDIT_STATE_PASS);
            entity.setCreator(saveVo.getOpUser());
        }
        Optional.ofNullable(saveVo.getGroupId()).ifPresent(groupId -> {
            entity.setGroupId(groupId);
            if ("-1".equals(groupId)) {
                entity.setGroupId("0");
            }
        });
        saveOrUpdate(entity);
        return true;
    }

    @Override
    public MaterialVideo findById(String id) {
        var cacheStr = redisTemplate.opsForValue().get(RedisKeyStatic.KEY_PREFIX_VIDEO + id);
        MaterialVideo video = null;
        if (StrUtil.isNotEmpty(cacheStr)) {
            video = JSONUtil.toBean(cacheStr, MaterialVideo.class);
        } else {
            video = writeVideoCache(id);
        }
        return video;
    }

    /**
     * 写入视频缓存
     *
     * @param id 视频Id
     */
    private MaterialVideo writeVideoCache(String id) {
        //写缓存
        var finalVideoEntity = getById(id);
        Optional.ofNullable(finalVideoEntity)
                .ifPresent(video -> redisTemplate.opsForValue()
                        .set(RedisKeyStatic.KEY_PREFIX_VIDEO + video.getId()
                                , JSONUtil.toJsonStr(video)
                                , Duration.ofDays(7)));
        return finalVideoEntity;
    }
}
