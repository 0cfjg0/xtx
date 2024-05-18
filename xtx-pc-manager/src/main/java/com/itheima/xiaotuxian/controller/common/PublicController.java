package com.itheima.xiaotuxian.controller.common;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.entity.member.UserMember;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.member.UserMemberService;
import com.itheima.xiaotuxian.util.OSSUtil;
import com.itheima.xiaotuxian.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RestController
public class PublicController {
    @Autowired
    private OSSUtil ossUtil;
    @Autowired
    private UserMemberService userMemberService;

    @PostMapping("/upload")
    public R<String> upload(@RequestParam(name = "file", required = false) MultipartFile file) {
        var arResult = new AtomicReference<Map<String, Object>>();
        Optional.ofNullable(file.getOriginalFilename()).ifPresentOrElse(fileName -> {
            var suffix = "";
            if (file.getOriginalFilename().contains(".")) {
                suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            }
            var key = "member/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "/" + IdUtil.fastUUID() + suffix;
            try {
                arResult.set(ossUtil.upload2Oss(key, file.getInputStream()));
                log.info("上传头像成功");
            } catch (IOException e) {
                throw new BusinessException(ErrorMessageEnum.FILE_IOEXCEPTION);
            }
        }, () -> {
            throw new BusinessException(ErrorMessageEnum.FILE_IOEXCEPTION);
        });
        return R.ok(arResult.get());
    }

    /**
     * 获取短信验证码-注册或登录-APP
     *
     * @param mobile 手机号
     * @param type   发送短信类型，取值范围[register、login]
     * @return 返回信息
     */
}
