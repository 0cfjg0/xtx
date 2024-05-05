package com.itheima.xiaotuxian.service.material;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.material.MaterialVideo;
import com.itheima.xiaotuxian.vo.material.*;

import java.util.List;

public interface MaterialVideoService extends IService<MaterialVideo> {
    /**
     * 上传文件至oss
     *
     * @param videoPath       视频路径
     * @param coverPath       封面路径
     * @param squareCoverPath 方形封面路径
     */
    VideoUploadResultVo uploadFile(String videoPath, String coverPath, String squareCoverPath);

    List<MaterialVideo> findAll(List<String> ids);

    /**
     * 获取视频或视频组分页数据
     *
     * @param query 查询条件
     * @return 分页数据
     */
    Page<VideoItemVo> findMaterialByPage(VideoPageQueryVo query);

    /**
     * 保存视频信息
     *
     * @param saveVo 视频信息
     * @return 操作结果
     */
    Boolean saveVideo(VideoSaveVo saveVo);

    /**
     * 根据id获取视频信息
     *
     * @param id 视频id
     * @return 视频信息
     */
    MaterialVideo findById(String id);
}
