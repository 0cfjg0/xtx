package com.itheima.xiaotuxian.service.member.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.RedisKeyStatic;
import com.itheima.xiaotuxian.entity.member.UserMember;
import com.itheima.xiaotuxian.entity.member.UserMemberOpenInfo;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.mapper.member.UserMemberMapper;
import com.itheima.xiaotuxian.mapper.member.UserMemberOpenInfoMapper;
import com.itheima.xiaotuxian.service.member.UserMemberService;
import com.itheima.xiaotuxian.util.JwtUtil;
import com.itheima.xiaotuxian.util.SmsUtil;
import com.itheima.xiaotuxian.vo.member.RegisterVo;
import com.itheima.xiaotuxian.vo.member.request.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author zsf
 * @since 2023年5月9日16:49:13
 */
@Service
public class UserMemberServiceImpl extends ServiceImpl<UserMemberMapper, UserMember> implements UserMemberService {
    public static final String KEY_REGISTER_CODE = "KEY:REGISTER:CODE:";
    public static final String KEY_LOGIN_CODE = "KEY:LOGIN:CODE:";
    public static final String KEY_SOCIAL_CODE = "KEY:SOCIAL:CODE:";
    public static final String KEY_RESET_CODE = "KEY:RESET:CODE:";
    private static final String KEY_RESET_CAP = "KEY:RESET:CAP:";
    private static final String KEY_RESET_TICKET = "KEY:RESET:TICKET:";

    @Autowired
    private SmsUtil smsUtil;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserMemberOpenInfoMapper userMemberOpenInfoMapper;
    @Autowired
    private UserMemberMapper userMemberMapper;

    @Value("${sms.register.sign:}")
    private String registerSign;
    @Value("${sms.register.template-code:}")
    private String registerTemplateCode;
    @Value("${sms.login.sign:}")
    private String loginSign;
    @Value("${sms.login.template-code:}")
    private String loginTemplateCode;
    @Value("${sms.reset.sign:}")
    private String resetSign;
    @Value("${sms.reset.template-code:}")
    private String resetTemplateCode;
    @Value("${sms.social.sign:}")
    private String socialSign;
    @Value("${sms.social.template-code:}")
    private String socialTemplateCode;
    @Value("${sms.testCode:123456}")
    private String testCode;

    @Value("${token.ttlMillis:259200000}")
    private Long ttlMillis;
    @Value("${token.refreshTokenTimeout:1296000000}")
    private Long refreshTokenTimeout;



    @Override
    public Boolean sendRegisterCode(String mobile) {
        Stream.of(sendSms(KEY_REGISTER_CODE, mobile, registerTemplateCode, registerSign))
                .filter(Boolean.FALSE::equals)
                .forEach(noPass -> {
                    throw new BusinessException(ErrorMessageEnum.MEMBER_SEND_MESSAGE_FAILED);
                });
        return true;
    }
    @Override
    public Boolean sendLoginCode(String mobile) {
        Stream.of(sendSms(KEY_LOGIN_CODE, mobile, loginTemplateCode, loginSign))
                .filter(Boolean.FALSE::equals)
                .forEach(noPass -> {
                    throw new BusinessException(ErrorMessageEnum.MEMBER_CODE_SEND);
                });
        return true;
    }
//注册
    @Override
    public Boolean register(RegisterVo vo) {
       UserMember userMember=new UserMember();
//        UUID uniqueID = UUID.randomUUID();
//        String s = uniqueID.toString();
//        String uniqueID = UUID.randomUUID().toString();
//        Random r=new Random();
//        int n = r.nextInt(99999999);
//
//        userMember.setId("1609"+n);
        System.out.println("aaaaaaaaaaaaaaaaaaaaa"+userMember.getId());
        userMember.setCreator("1");
        userMember.setMobile(vo.getMobile());
        userMember.setAccount(vo.getAccount());
        userMember.setPassword(vo.getPassword());

        userMemberMapper.register(userMember);

        return true;
    }

    @Override
    public Boolean checkRegisterCode(String mobile, String code) {
        Stream.of(checkSmsCode(KEY_REGISTER_CODE, mobile, code))
                .filter(Boolean.FALSE::equals)
                .forEach(noPass -> {
                    throw new BusinessException(ErrorMessageEnum.MEMBER_CODE_INVALID);
                });
        return true;
    }


    @Override
    public Boolean sendSocialCode(String mobile) {
        Stream.of(sendSms(KEY_SOCIAL_CODE, mobile, socialTemplateCode, socialSign))
                .filter(Boolean.FALSE::equals)
                .forEach(noPass -> {
                    throw new BusinessException(ErrorMessageEnum.MEMBER_SEND_MESSAGE_FAILED);
                });
        return true;
    }

    @Override
    public Boolean checkLoginCode(String mobile, String code) {
        Stream.of(checkSmsCode(KEY_LOGIN_CODE, mobile, code))
                .filter(Boolean.FALSE::equals)
                .forEach(noPass -> {
                    throw new BusinessException(ErrorMessageEnum.MEMBER_CODE_INVALID);
                });
        return true;
    }

    @Override
    public Boolean sendResetCode(String mobile) {
        return sendSms(KEY_RESET_CODE, mobile, resetTemplateCode, resetSign);
    }


    @Override
    public UserMember socialBind(String mobile, String unionId, String code) {
    	// 判断三方登录信息qq,是否绑定过其他的手机号，如果没有，可以绑定当前的手机号
        var unionUserMember = getOne(Wrappers.<UserMember>lambdaQuery().eq(UserMember::getUnionId, unionId));
        if(null == unionUserMember || unionUserMember.getMobile().equals(mobile) ) {
        	// 查询用户信息
            var userMember = getOne(Wrappers.<UserMember>lambdaQuery().eq(UserMember::getMobile, mobile));

            if (userMember == null) {
                throw new BusinessException(ErrorMessageEnum.MEMBER_DOES_NOT_EXIST);
            }
            //校验验证码
            Stream.of(checkSmsCode(KEY_SOCIAL_CODE, mobile, code))
                    .filter(Boolean.FALSE::equals)
                    .forEach(noPass -> {
                        throw new BusinessException(ErrorMessageEnum.MEMBER_CODE_INVALID);
                    });
            //更新用户信息
            userMember.setUnionId(unionId);
            this.update(userMember);
            return userMember;

        }else {
        	throw new BusinessException(ErrorMessageEnum.MEMBER_BIND_USER_ERROR);
        }

    }





    @Override
    public void update(UserMember userMember) {
        var source = this.getById(userMember.getId());
        if (source == null) {
            throw new BusinessException(ErrorMessageEnum.MEMBER_DOES_NOT_EXIST);
        }
        BeanUtil.copyProperties(userMember,source);
        if (StrUtil.isNotEmpty(userMember.getNickname())) {
            source.setNickname(userMember.getNickname());
        }
        if (StrUtil.isNotEmpty(userMember.getGender())) {
            source.setGender(userMember.getGender());
        }
        if (userMember.getBirthday() != null) {
            source.setBirthday(userMember.getBirthday());
        }
        if (StrUtil.isNotEmpty(userMember.getCityCode())) {
            source.setCityCode(userMember.getCityCode());
        }
        if (StrUtil.isNotEmpty(userMember.getProvinceCode())) {
            source.setProvinceCode(userMember.getProvinceCode());
        }
        if (userMember.getProfession() != null) {
            source.setProfession(userMember.getProfession());
        }

        this.updateById(source);
    }








    /**
     * 检查短信验证码
     *
     * @param keyPrefix 缓存key前缀
     * @param mobile    手机号
     * @param code      验证码
     * @return 验证结果
     */
    private Boolean checkSmsCode(String keyPrefix, String mobile, String code) {
        var key = keyPrefix + mobile;
        checkRedisKey(key, Boolean.FALSE::equals, ErrorMessageEnum.MEMBER_CODE_EXPIRED);
        var cacheCode = redisTemplate.opsForValue().get(key);
        var isMatch = StrUtil.equals(code, cacheCode);
        if(isMatch){
            redisTemplate.delete(key);
        }
        return isMatch;
    }

    /**
     * 发送短信
     *
     * @param keyPrefix    缓存key前缀
     * @param mobile       手机号
     * @param templateCode 短信模板
     * @param sign         短信签名
     * @return 操作结果
     */
    private Boolean sendSms(String keyPrefix, String mobile, String templateCode, String sign) {
        var key = keyPrefix + mobile;
        checkRedisKey(key, Boolean.TRUE::equals, ErrorMessageEnum.MEMBER_CODE_SEND);
        var code = new Random().nextInt(899999) + 100000;
//        var code = testCode;
        redisTemplate.opsForValue().set(key, code + "", Duration.of(1, ChronoUnit.MINUTES));
        /**
         * TODO 为了防止后期大量学生调用短信接口产生费用，暂时注释掉此处代码
         * 短信功能可以使用
         */
                return smsUtil.sendSms(mobile, templateCode, String.format("{'code':%s}", code), sign);
//        return true;
    }

    /**
     * 检查缓存是否存在
     *
     * @param key       缓存key
     * @param predicate 条件
     * @param error     错误信息
     */
    private void checkRedisKey(String key, Predicate<Boolean> predicate, ErrorMessageEnum error) {
        Stream.of(redisTemplate.hasKey(key))
                .filter(predicate)
                .forEach(match -> {
                    throw new BusinessException(error);
                });
    }
    /**
    * @description: 获取token信息
    * @param {String} id
    * @param {String} account
    * @param {String} nickname
    * @return {*}
    * @author: lbc
    */
    @Override
    public String getToken(String id, String account, String nickname) {

        var token= JwtUtil.getToken(id,account,nickname,ttlMillis);
         //有效期为 3*24*60*60*1000  3天  259200000L 15天refreshtoken 1296000000L
        //  Long tokenTimeout = ttlMillis;
        //  Long refreshTokenTimeout = 1296000000L;
         //获取当前时间毫秒数
        long nowMillis = System.currentTimeMillis();
        //使用当前时间+ 超时时间=具体在哪个时间点超时
        long expMillis = nowMillis + ttlMillis;
        long refreshExpMillis = nowMillis + refreshTokenTimeout;
        var refreshToken= JwtUtil.getToken(id,account,nickname,refreshTokenTimeout);

        //存入redis
        this.redisTemplate.opsForValue().set(RedisKeyStatic.KEY_PREFIX_TOKEN + id + ":" + token, token, Duration.ofMillis(expMillis));
        this.redisTemplate.opsForValue().set(RedisKeyStatic.KEY_PREFIX_REFESH_TOKEN + id+":"+token, refreshToken, Duration.ofMillis(refreshExpMillis));
		return token;
	}




     /**
     * @description:
     * @param {UserMember} userMember
     * @return {*}
     * @author: lbc
     */
    @Override
    public void unbind(UserMember userMember) {
        int count = this.getBaseMapper().update(userMember, Wrappers.<UserMember>lambdaQuery()
                                                                    .eq(UserMember::getId,userMember.getId()));
        if(count < 1){
            throw new BusinessException(ErrorMessageEnum.FAILURE);
        }
    }

    @Override
    public UserMember findByOpenid(String openid) {
        UserMemberOpenInfo userOpneInfo = null;
        List<UserMemberOpenInfo> list = userMemberOpenInfoMapper.selectList(Wrappers.<UserMemberOpenInfo>lambdaQuery()
                                        .eq(UserMemberOpenInfo::getOpenId,openid)
                                        .eq(UserMemberOpenInfo::getIsDelete,0));
        if (list.size()>0){
            userOpneInfo = list.get(0);
        }

        if(null != userOpneInfo){
            UserMember userMember = this.getBaseMapper().selectById(userOpneInfo.getUserId());
            return userMember;
        }
        return null;

    }
//登入
    @Override
    public LoginVo login(LoginVo vo) {
      LoginVo v=  userMemberOpenInfoMapper.login(vo);

        return v;
    }

    @Override
    public UserMember select(LoginVo vo) {

        UserMember userMember=userMemberOpenInfoMapper.select(vo);
        return userMember;
    }

//    @Override
//    public String code(String mobile, HttpSession session) {
//        String s=userMemberOpenInfoMapper.code(mobile);
//        if (s==null){
//
//        }
//        return null;
//    }


}
