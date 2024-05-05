package com.itheima.xiaotuxian.service.material;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.xiaotuxian.entity.material.MaterialPicture;
import com.itheima.xiaotuxian.vo.material.PictureItemVo;
import com.itheima.xiaotuxian.vo.material.PicturePageQueryVo;
import com.itheima.xiaotuxian.vo.material.MaterialUploadResultVo;
import com.itheima.xiaotuxian.vo.material.PictureUploadVo;

import java.util.List;

public interface MaterialPictureService extends IService<MaterialPicture> {
    List<MaterialPicture> findAll(List<String> ids);

    /**
     * 获取图片或图片组分页数据
     *
     * @param query 查询条件
     * @return 分页数据
     */
    Page<PictureItemVo> findMaterialByPage(PicturePageQueryVo query);

    /**
     * 上传或替换图片
     *
     * @param picturePath 图片路径
     * @param filename    文件名称
     * @param picture     图片附带信息
     * @param opUser      操作人
     * @return 图片完整信息
     */
    MaterialUploadResultVo savePicture(String picturePath, String filename, PictureUploadVo picture, String opUser);

    String saveAvatar(String filePath);

    /**
     * 更新图片
     *
     * @param id          图片id
     * @param picturePath 图片路径
     * @param filename    文件名称
     * @return 操作结果
     */
    Boolean updatePicture(String id, String picturePath, String filename);

    /**
     * 复制图片
     *
     * @param id 图片Id
     * @return 操作结果
     */
    Boolean copyPicture(String id);

    /**
     * 适配手机
     *
     * @param id 图片Id
     * @return 操作结果
     */
    Boolean adaptPicture(String id);

    /**
     * 根据id获取图片信息
     *
     * @param id 图片id
     * @return 图片信息
     */
    MaterialPicture findById(String id);

    /**
     * 获取图片链接
     * @param id 图片id
     * @return 图片链接
     */
    String getPictureUrl(String id);
}
