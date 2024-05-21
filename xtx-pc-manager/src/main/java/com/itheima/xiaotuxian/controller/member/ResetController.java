package com.itheima.xiaotuxian.controller.member;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.entity.member.UserMember;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.member.UserMemberService;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.member.ResetVo;
import com.itheima.xiaotuxian.vo.member.response.ResetResultVo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/reset")
public class ResetController {
    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @GetMapping("/mobile/check")
  public R<String> reset(@RequestParam(name = "mobile") String mobile,@RequestParam (name = "code") String code){
        

        return R.ok();
    }


}
