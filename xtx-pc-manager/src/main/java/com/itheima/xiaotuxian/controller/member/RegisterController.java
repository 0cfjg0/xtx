package com.itheima.xiaotuxian.controller.member;

import javax.validation.Valid;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.entity.member.UserMember;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.member.UserMemberService;
import com.itheima.xiaotuxian.util.ConvertUtil;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.member.CheckResultVo;
import com.itheima.xiaotuxian.vo.member.RegisterVo;
import com.itheima.xiaotuxian.vo.member.response.LoginResultVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.lang.Validator;
import lombok.extern.slf4j.Slf4j;

/*
 * @author: lbc
 * @Date: 2023-06-13 20:57:28
 * @Descripttion:
 */
@Slf4j
@RestController
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    private UserMemberService userMemberService;


    /**
     * 校验用户名唯一
     *
     * @param account 用户名
     * @return 是否唯一
     */
    @GetMapping("/check")
    public R<CheckResultVo> accountCheck(@RequestParam(name = "account") String account) {
        int count = userMemberService.count(Wrappers.<UserMember>lambdaQuery()
                .eq(UserMember::getAccount, account)
                .or()
                .eq(UserMember::getMobile, account));
        var resultVo = new CheckResultVo(count > 0);
        return R.ok(resultVo);
    }

    /**
     * 注册
     *
     * @param vo 注册信息
     * @return 登录信息
     */
    @PostMapping
    public R<String> register(@RequestBody RegisterVo vo) {
//        if (Boolean.FALSE.equals(userMemberService.checkRegisterCode(vo.getMobile(), vo.getCode()))) {
//            throw new BusinessException(ErrorMessageEnum.MEMBER_CODE_INVALID);
//        }
      userMemberService.register(vo);
        return R.ok();
    }


    /**
     * 注册-短信
     *
     * @param mobile 手机号
     * @return 验证码
     */
    @GetMapping("/code")
    public R<String> code(@RequestParam(name = "mobile") String mobile) {
        if (!Validator.isMobile(mobile)) {
            throw new BusinessException(ErrorMessageEnum.MEMBER_MOBILE_FORMAT_INVALID);
        }
        int count = userMemberService.count(Wrappers.<UserMember>lambdaQuery().eq(UserMember::getMobile, mobile));
        if (count > 0) {
            throw new BusinessException(ErrorMessageEnum.MEMBER_EXIST);
        }
        userMemberService.sendRegisterCode(mobile);
        return R.ok();
    }

    /**
     * 注册-验证手机验证码-APP
     *
     * @return 返回信息
     */
    @GetMapping("/code/check")
    public R<CheckResultVo> checkCode(@RequestParam(name = "mobile") String mobile, @RequestParam(name = "code") String code) {
        if (Boolean.FALSE.equals(userMemberService.checkRegisterCode(mobile, code))) {
            throw new BusinessException(ErrorMessageEnum.MEMBER_CODE_INVALID);
        }
        var resultVo = new CheckResultVo(true);
        return R.ok(resultVo);
    }
}
