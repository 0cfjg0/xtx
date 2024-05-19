package com.itheima.xiaotuxian.controller.member;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.RedisKeyStatic;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.member.UserMember;
import com.itheima.xiaotuxian.exception.AuthException;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.member.UserMemberService;
import com.itheima.xiaotuxian.util.ConvertUtil;
import com.itheima.xiaotuxian.util.JwtUtil;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.member.MobileLoginVo;
import com.itheima.xiaotuxian.vo.member.RegisterVo;
import com.itheima.xiaotuxian.vo.member.request.LoginSocialVo;
import com.itheima.xiaotuxian.vo.member.request.LoginVo;
import com.itheima.xiaotuxian.vo.member.response.LoginResultVo;
import com.itheima.xiaotuxian.vo.token.TokenRequestVo;

import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zsf
 * @date 2023年5月8日12:43:37
 */
@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    protected HttpServletRequest request;

    /**
     * 默认的用户头像信息
     */
    @Value("${account.avatar:}")
    private String avatar;

    /**
     * 登录
     *
     * @param vo 登录信息
     * @return 登录信息
     */
    @PostMapping
    public R<LoginResultVo> login(@RequestBody LoginVo vo) {

        LoginVo v = userMemberService.login(vo);
        UserMember userMember = userMemberService.select(vo);

        System.out.println(userMember);
        String token = userMemberService.getToken(userMember.getId(), userMember.getAccount(), userMember.getNickname());
        Claims claims = JwtUtil.getClaims(token);
        System.out.println("claims"+claims);
        LoginResultVo loginResultVo = convertMemberToLoginResult(userMember);
        return R.ok(loginResultVo);
    }


    /**
     * 登录-短信
     *
     * @param mobile 手机号
     * @return 验证码
     */
//    @PostMapping("/{mobile}")
//    public R<String> code(@PathVariable String mobile, HttpSession session){
//       String ss= userMemberService.code(mobile,session);
//
//        return R.ok(ss);
//    }

    /**
     * 手机验证码登录
     *
     * @param vo 登录信息
     * @return 登录信息
     */
    @PostMapping("/code")
    public R<LoginResultVo> loginByCode(@RequestBody @Valid MobileLoginVo vo) {
        userMemberService.checkLoginCode(vo.getMobile(), vo.getCode());
        var userMember = userMemberService
                .getOne(Wrappers.<UserMember>lambdaQuery().eq(UserMember::getMobile, vo.getMobile()));
        if (userMember == null) {
            throw new BusinessException(ErrorMessageEnum.MEMBER_DOES_NOT_EXIST);
        }
        var result = convertMemberToLoginResult(userMember);
        return R.ok(result);
    }

    /**
     * 三方登录
     *
     * @param socialVo 三方登录信息
     * @return 登录信息
     */
    @PostMapping("/social")
    public R<LoginResultVo> loginSocial(@RequestBody LoginSocialVo socialVo) {
        var userMember = userMemberService
                .getOne(Wrappers.<UserMember>lambdaQuery().eq(UserMember::getUnionId, socialVo.getUnionId()));
        LoginResultVo result;
        if (userMember == null) {
            // 三方登录，不成功走此处，PC前台处理错误信息2023-03-16
            throw new BusinessException(ErrorMessageEnum.SOCIAL_LOGIN_ERROR);
        }
        result = convertMemberToLoginResult(userMember);
        return R.ok(result);
    }

    /**
     * 将userMember信息转换成LoginResultVo
     *
     * @param userMember
     * @return
     */
    private LoginResultVo convertMemberToLoginResult(UserMember userMember) {
        var token = userMemberService.getToken(userMember.getId(), userMember.getAccount(), userMember.getNickname());
        var result = ConvertUtil.convertLoginInfo(userMember, token);
        // 为空的头像，设置默认的兔子头像
        if (StringUtils.isBlank(result.getAvatar())) {
            result.setAvatar(avatar);
        }
        return result;
    }

    /**
     * 三方登录_发送已有账号短信
     *
     * @param mobile 手机号
     * @return 验证码
     */
    @GetMapping("/social/code")
    public R<String> socialCode(@RequestParam(name = "mobile") String mobile) {
        if (!Validator.isMobile(mobile)) {
            throw new BusinessException(ErrorMessageEnum.MEMBER_MOBILE_FORMAT_INVALID);
        }
        int count = userMemberService.count(Wrappers.<UserMember>lambdaQuery().eq(UserMember::getMobile, mobile));
        if (count == 0) {
            throw new BusinessException(ErrorMessageEnum.MEMBER_DOES_NOT_EXIST);
        }
        userMemberService.sendSocialCode(mobile);
        return R.ok();
    }

    /**
     * 三方登录_账号绑定
     *
     * @param socialVo 绑定信息
     * @return 账号信息
     */
    @PostMapping("/social/bind")
    public R<LoginResultVo> socialBind(@RequestBody LoginSocialVo socialVo) {
        var userMember = userMemberService.socialBind(socialVo.getMobile(), socialVo.getUnionId(), socialVo.getCode());
        LoginResultVo result;
        if (userMember == null) {
            result = new LoginResultVo();
            result.setAvatar(avatar);
        } else {
            result = convertMemberToLoginResult(userMember);
        }
        return R.ok(result);
    }

    /**
     * 三方登录_账号解绑
     *
     * @param socialVo 接除绑定信息
     * @return 账号信息
     */
    @GetMapping("/social/unbind")
    public R<UserMember> socialUnbind(@RequestParam(name = "mobile") String mobile) {
        var userMember = userMemberService.getOne(Wrappers.<UserMember>lambdaQuery().eq(UserMember::getMobile, mobile));
        if (userMember == null) {
            throw new BusinessException(ErrorMessageEnum.MEMBER_DOES_NOT_EXIST);
        } else {
            userMember.setUnionId(null);
            userMemberService.unbind(userMember);
        }
        return R.ok(userMember);
    }

    /**
     * 三方登录-完善信息
     *
     * @param unionId unionId
     * @param vo      待完善数据
     * @return 登录信息
     */


    /**
     * 根据id和token信息重新刷新token
     *
     * @param tokenRequestVo
     * @return
     */
    @PutMapping("/refresh")
    public R<LoginResultVo> refreshToken(@RequestBody TokenRequestVo tokenRequestVo) {
        /**
         * 验证id是否存在
         * 1.1根据拿到的token信息，判断token是否过期，
         * 不过期检验token，获取的信息和传入的信息是否一致，一致，ok进行下一步,不一致，报错，不返回token
         * 1.2判断refreshtoken是否存在，
         * refreshtoken存在，根据refreshtoken获取新的token信息，同时清除原来的token信息，更新refreshtoken
         * 若refreshtoken 也不存在，返回token错误信息，重新登录
         *
         */
        var token = request.getHeader("Authorization").replace("Bearer ", "");

        var userMember = userMemberService.getOne(Wrappers.<UserMember>lambdaQuery()
                .eq(UserMember::getAccount, tokenRequestVo.getAccount())
                .or()
                .eq(UserMember::getMobile, tokenRequestVo.getAccount()));
        if (userMember == null) {
            throw new BusinessException(ErrorMessageEnum.MEMBER_DOES_NOT_EXIST);
        }

        var result = this.convertMemberToLoginResult(userMember);
        var id = result.getId();
        var account = result.getAccount();
        var nickname = result.getNickname();
        // 若token存在,即进行判断 2023年9月28日18:51:09 app测试修改
        // boolean expiredFlag = JwtUtil.isTokenExpired(token);
        var tokenRedis = redisTemplate.opsForValue()
                .get(RedisKeyStatic.KEY_PREFIX_TOKEN + tokenRequestVo.getId() + ":" + token);
        // token 不存在，（这里简化：同等于过期了），前台演示，传一个错误的token即可
        if (null == tokenRedis) {
            var refreshToken = redisTemplate.opsForValue()
                    .get(RedisKeyStatic.KEY_PREFIX_REFESH_TOKEN + tokenRequestVo.getId() + ":" + token);
            // if(StringUtils.isBlank(tokenRedis)){
            if (StringUtils.isNotBlank(refreshToken)) {
                // refreshtoken也过期了

                if (JwtUtil.isTokenExpired(refreshToken)) {
                    throw new AuthException(ErrorMessageEnum.REFRESH_TOKEN_ERROR);
                }
                var claims = JwtUtil.getClaims(refreshToken);
                var data = JSONUtil.parseObj(claims.get("sub"));
                String idTOken = data.getStr("id");
                // refreshtoken没有过期，返回新的token
                if (id.equals(idTOken)) {
                    var newToken = userMemberService.getToken(id, account, nickname);
                    // 原有的refrehtoken失效
                    result.setToken(newToken);
                } else {
                    // refreshtoken没有过期，入参中的id和refreshToken中解析的id不一致
                    throw new AuthException(ErrorMessageEnum.REFRESH_TOKEN_ERROR);
                }
            } else {
                throw new AuthException(ErrorMessageEnum.REFRESH_TOKEN_ERROR);
            }
            // }else{
            // // String tokenMillis=JwtUtil.getClaim(tokenRequestVo.getToken());
            // // result.setToken(tokenRedis);
            // throw new BusinessException(ErrorMessageEnum.MEMBER_DOES_NOT_EXIST);
            // }
        } else {
            // 正常情况不会存在这种情况
            // result.setToken(token);
            throw new AuthException(ErrorMessageEnum.REFRESH_TOKEN_ERROR);
        }
        return R.ok(result);
    }

    /**
     * @param {*}
     * @return {*}
     * @description:
     * @author: lbc
     */
    @GetMapping("/expireToken")
    public R<Boolean> expireToken(@RequestParam(name = "id") String id, @RequestParam(name = "token") String token) {
        String s = redisTemplate.opsForValue().get(RedisKeyStatic.KEY_PREFIX_TOKEN + id + ":" + token);
        log.info("aaaaaaaaaaaa" + s + token);
        Boolean expireFlag = redisTemplate.expire(RedisKeyStatic.KEY_PREFIX_TOKEN + id + ":" + token, 0,
                TimeUnit.SECONDS);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return R.ok(expireFlag);
    }

    /**
     * @param {*}
     * @return {*}
     * @description:
     * @author: lbc
     */
    @GetMapping("/expireRefreshToken")
    public R<Boolean> expireRefreshToken(@RequestParam(name = "id") String id,
                                         @RequestParam(name = "token") String token) {
        String s = redisTemplate.opsForValue().get(RedisKeyStatic.KEY_PREFIX_REFESH_TOKEN + id + ":" + token);
        Boolean expireFlag = redisTemplate.expire(RedisKeyStatic.KEY_PREFIX_REFESH_TOKEN + id + ":" + token, 0,
                TimeUnit.SECONDS);
        log.info("sss" + s);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return R.ok(expireFlag);
    }

}
