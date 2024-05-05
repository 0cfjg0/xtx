package com.itheima.xiaotuxian.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.MaterialStatic;
import com.itheima.xiaotuxian.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
// import ws.schild.jave.EncoderException;
// import ws.schild.jave.MultimediaObject;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * @author: itheima
 * @Date: 2023/7/17 11:12 上午
 * @Description:
 */
@Slf4j
public class MaterialUtil {
    public static final String FILE_2_VIDEO = "video";
    public static final String FILE_2_PICTURE = "picture";
    public static final String FILE_2_MASK = "mask";
    public static final String FILE_AVATAR = "avatar";
    public static final String FILE_SIZE_UNIT_B = "B";
    public static final String FILE_SIZE_UNIT_K = "K";
    public static final String FILE_SIZE_UNIT_M = "M";
    public static final String FILE_SIZE_UNIT_G = "G";

    private MaterialUtil() {
    }

    public static LocalMaterial saveMask(String rootDirectory, MultipartFile attachment) {
        return saveToLocal(rootDirectory, FILE_2_MASK, attachment, 320, 320);
    }

    public static LocalMaterial savePicture(String rootDirectory, MultipartFile attachment) {
        checkFileSize(attachment.getSize(), 3, FILE_SIZE_UNIT_M);
        return saveToLocal(rootDirectory, FILE_2_PICTURE, attachment, null, null);
    }

    /**
     * base64转化成图片 base64ToImage
     * @param rootDirectory
     * @param file
     * @param suffix
     * @return
     */
    public static String save(String rootDirectory, String file, String suffix) {
        var destDir = rootDirectory + File.separator + FILE_2_PICTURE + File.separator + DateUtil.today();
        var files = file.split(",");
        //获取uuid文件名称
        var uuidFileName = IdUtil.randomUUID() + "." + suffix;
        var filePath = destDir + File.separator + uuidFileName;
        if (!FileUtil.exist(destDir)) {
            FileUtil.mkParentDirs(destDir);
            FileUtil.mkdir(destDir);

        }
        base64ToImage(files.length > 1 ? files[1] : file, filePath);
        return filePath;
    }

    public static LocalMaterial saveToLocal(String rootDirectory, String fileWay, MultipartFile attachment, Integer maxWidth, Integer maxHeight) {
        if (attachment == null) {
            throw new BusinessException(ErrorMessageEnum.FILE_EMPTY);
        }
        if (StrUtil.isEmpty(attachment.getOriginalFilename())) {
            throw new BusinessException(ErrorMessageEnum.FILE_EMPTY);
        }
        var destDir = rootDirectory + File.separator + fileWay + File.separator + DateUtil.today();
        var arLocalMaterial = new AtomicReference<LocalMaterial>();
        Optional.ofNullable(attachment.getOriginalFilename()).ifPresentOrElse(uploadFilePath -> {
            try {
                // 截取上传文件的文件名
                var uploadFileName = uploadFilePath.substring(uploadFilePath.lastIndexOf('\\') + 1);
                // 截取上传文件的后缀
                var uploadFileSuffix = uploadFileName.substring(uploadFileName.lastIndexOf('.') + 1);
                uploadFileName = uploadFileName.substring(0,uploadFileName.lastIndexOf('.'));
                Stream.of(checkFile(uploadFileSuffix, fileWay)).filter(Boolean.FALSE::equals).forEach(unMatch -> {
                    throw new BusinessException(ErrorMessageEnum.FILE_FORMAT_ERROR);
                });
                Stream.of(FILE_2_VIDEO.equals(fileWay)).filter(Boolean.FALSE::equals).forEach(unMatch -> {
                    try {
                        var bufferedImage = ImageIO.read(attachment.getInputStream());
                        // 超过宽度
                        Stream.of(maxWidth != null && maxWidth > 0 && bufferedImage.getWidth() > maxWidth).filter(Boolean.TRUE::equals).forEach(match -> {
                            throw new BusinessException(ErrorMessageEnum.FILE_FORMAT_ERROR);
                        });
                        // 超过高度
                        Stream.of(maxHeight != null && maxHeight > 0 && bufferedImage.getHeight() > maxHeight).filter(Boolean.TRUE::equals).forEach(match -> {
                            throw new BusinessException(ErrorMessageEnum.FILE_FORMAT_ERROR);
                        });
                    } catch (IOException e) {
                        throw new BusinessException(ErrorMessageEnum.FILE_IOEXCEPTION);
                    }

                });
                if (!FileUtil.exist(destDir)) {
                    FileUtil.mkParentDirs(destDir);
                    FileUtil.mkdir(destDir);

                }
                //获取uuid文件名称
                var uuidFileName = IdUtil.randomUUID() + "." + uploadFileSuffix;
                //创建目标文件
                var destFile = new File(destDir, uuidFileName);

                attachment.transferTo(destFile);
                arLocalMaterial.set(new LocalMaterial(destFile.getAbsolutePath(), uploadFileName, destFile.length() / 1024));
            } catch (IOException e) {
                log.error("文件上传错误",e);
                throw new BusinessException(ErrorMessageEnum.FILE_IOEXCEPTION);
            }
        }, () -> {
            throw new BusinessException(ErrorMessageEnum.FILE_IOEXCEPTION);
        });
        return arLocalMaterial.get();
    }

    public static LocalMaterial saveToLocal(String rootDirectory, String fileWay, MultipartFile attachment) {
        if (attachment == null || StrUtil.isEmpty(attachment.getOriginalFilename())) {
            throw new BusinessException(ErrorMessageEnum.FILE_EMPTY);
        }
        var arLocalMaterial = new AtomicReference<LocalMaterial>();
        var destDir = rootDirectory + File.separator + fileWay + File.separator + DateUtil.today();
        Optional.ofNullable(attachment.getOriginalFilename()).ifPresentOrElse(uploadFilePath -> {
            // 截取上传文件的文件名
            var uploadFileName = uploadFilePath.substring(uploadFilePath.lastIndexOf('\\') + 1);
            // 截取上传文件的后缀
            var uploadFileSuffix = uploadFileName.substring(uploadFileName.lastIndexOf('.') + 1);
            uploadFileName = uploadFileName.replace("." + uploadFileSuffix, "");
            Stream.of(checkFile(uploadFileSuffix, fileWay)).filter(Boolean.FALSE::equals).forEach(unMatch -> {
                throw new BusinessException(ErrorMessageEnum.FILE_FORMAT_ERROR);
            });
            if (!FileUtil.exist(destDir)) {
                FileUtil.mkParentDirs(destDir);
                FileUtil.mkdir(destDir);

            }
            //获取uuid文件名称
            var uuidFileName = IdUtil.randomUUID() + "." + uploadFileSuffix;
            //创建目标文件
            var destFile = new File(destDir, uuidFileName);
            try {
                attachment.transferTo(destFile);
            } catch (IOException e) {
                throw new BusinessException(ErrorMessageEnum.FILE_IOEXCEPTION);
            }
            arLocalMaterial.set(new LocalMaterial(destFile.getAbsolutePath(), uploadFileName, destFile.length() / 1024));
        }, () -> {
            throw new BusinessException(ErrorMessageEnum.FILE_IOEXCEPTION);
        });
        return arLocalMaterial.get();
    }

    /**
     * 检查文件类型是否正确
     *
     * @param uploadFileSuffix 当前文件类型
     * @return 是否正确
     */
    private static Boolean checkFile(String uploadFileSuffix, String fileWay) {
        var arr = FILE_2_VIDEO.equals(fileWay) ? MaterialStatic.getArrVideoExtension() : MaterialStatic.getArrPictureExtension();
        return arr.stream().anyMatch(extend -> extend.equals(uploadFileSuffix));
    }

    /**
     * 获取文件名称
     *
     * @param filePath 文件路径
     * @return 文件名称
     */
    public static String getFileName(String filePath) {
        return FileUtil.getName(filePath);
    }

    /**
     * 获取视频秒数
     *
     * @param fileUrl 视频链接
     * @return 视频秒数
     * @throws EncoderException
     */
    // public static Long videoTime(String fileUrl) {
    //     try {
    //         var source = new File(fileUrl);
    //         var instance = new MultimediaObject(source);
    //         var result = instance.getInfo();
    //         return result.getDuration() / 1000;
    //     } catch (EncoderException e) {
    //         throw new BusinessException(ErrorMessageEnum.READ_VIDEO_ERROR);
    //     }
    // }

    /**
     * 移除文件
     *
     * @param filePath 文件路径
     */
    public static void remove(String filePath) {
        FileUtil.del(filePath);
    }

    /**
     * 检查文件大小
     *
     * @param len  大小
     * @param size 对比数
     * @param unit 单位
     * @return 是否符合
     */
    public static boolean checkFileSize(Long len, int size, String unit) {
        var fileSize = 0d;
        if (FILE_SIZE_UNIT_B.equalsIgnoreCase(unit)) {
            fileSize = (double) len;
        } else if (FILE_SIZE_UNIT_K.equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1024;
        } else if (FILE_SIZE_UNIT_M.equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1048576;
        } else if (FILE_SIZE_UNIT_G.equalsIgnoreCase(unit)) {
            fileSize = (double) len / 1073741824;
        }
        return fileSize > size;
    }

    /**
     * 对字节数组字符串进行Base64解码并生成图片
     *
     * @param base64 图片Base64数据
     * @param path   图片路径
     * @return
     */
    public static boolean base64ToImage(String base64, String path) {// 对字节数组字符串进行Base64解码并生成图片
        if (base64 == null) { // 图像数据为空
            return false;
        }
        // JDK8以上
        var decoder = Base64.getDecoder();

        // Base64解码
        var bytes = decoder.decode(base64);
        for (var i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {// 调整异常数据
                bytes[i] += 256;
            }
        }
        // 生成图片
        try (var out = new FileOutputStream(path)){
            out.write(bytes);
            out.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocalMaterial {
        private String filePath;
        private String fileName;
        private Long fileSize;
    }
}
