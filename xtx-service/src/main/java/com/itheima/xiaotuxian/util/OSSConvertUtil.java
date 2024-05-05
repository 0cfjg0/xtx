package com.itheima.xiaotuxian.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.exception.BusinessException;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 将爬虫的图片转到我们的阿里云oss上
 */
@Component
@Slf4j
public class OSSConvertUtil {
    @Resource
    OSSUtil ossUtil;

    /**
     * 
     * @param strUrl
     * @return
     */
    public InputStream getInputStreamByUrlSimple(String strUrl) {
        // 文件访问路径
        String url = "";
        try (InputStream intstream = new URL(url).openStream()) {
            return intstream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Object> covertToAliyunOss(String strUrl) {
        int index = strUrl.lastIndexOf("/");
        if (index > 0) {
            InputStream inputStream = this.getInputStreamByUrl(strUrl);
            String name = "";
            name = strUrl.substring(index+1);
            return ossUtil.upload2Oss("spider/" + name, inputStream);
        } else {
            log.error("strUrl 异常,strUrl is {}", strUrl);
            throw new BusinessException(ErrorMessageEnum.UPLOAD_URL_ERROR);
        }
    }

    public InputStream getInputStreamByUrl(String strUrl) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(strUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20 * 1000);
            // 从HTTP响应消息获取状态码
            int code = conn.getResponseCode();
            if (code == 200) {
                final ByteArrayOutputStream output = new ByteArrayOutputStream();
                org.apache.tomcat.util.http.fileupload.IOUtils.copy(conn.getInputStream(), output);
                return new ByteArrayInputStream(output.toByteArray());
            }else{
                log.error("conn.getResponseCode()() 异常,code is {}", code);

                throw new BusinessException(ErrorMessageEnum.UPLOAD_URL_CONNECT_ERROR);
            }
        } catch (Exception e) {
            log.error("getInputStreamByUrl 异常,exception is {}", e);
            throw new BusinessException(ErrorMessageEnum.UPLOAD_URL_ERROR);
        } finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 测试上传，将网络图片转到本地
     * 
     * @return
     */
    public String testUploadToLocal() {
        try {
            // 网络图片资源的url（可以把这个放参数中动态获取）
            String picUrl = "https://yanxuan.nosdn.127.net/403ef629810368c9b3e0e6fd863ebb4e.jpg";
            // 获取原文件名
            String fileName = picUrl.substring(picUrl.lastIndexOf("/") + 1);
            // 创建URL对象，参数传递一个String类型的URL解析地址
            URL url = new URL(picUrl);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            // 从HTTP响应消息获取状态码
            int code = huc.getResponseCode();
            if (code == 200) {
                // 获取输入流
                InputStream ips = huc.getInputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((len = ips.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.close();
                return uploadFileByBytes(bos.toByteArray(), fileName);
            }
            return "dddddddddddddddddddddddddd";
        } catch (Exception e) {
            return "cccccccccccccccccccccccccccccc";
        }
    }

    private String uploadFileByBytes(byte[] bytes, String fileName) throws Exception {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] < 0) {
                bytes[i] += 256;
            }
        }
        String realPath = System.getProperty("user.dir") + File.separator + "upload" + File.separator;
        // 文件路径
        String url = realPath + fileName;
        // 判断文件路径是否存在，如果没有则新建文件夹
        File files = new File(realPath);
        if (!files.exists()) {
            files.mkdirs();
        }
        // 把文件写入到指定路径下
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(url, false))) {
            out.write(bytes);
        }
        return url;
    }
}
