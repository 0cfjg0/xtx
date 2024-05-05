package com.itheima.xiaotuxian.controller.member;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Validator;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.itheima.xiaotuxian.config.WxMiniPayConfig;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.controller.BaseController;
import com.itheima.xiaotuxian.entity.member.UserMember;
import com.itheima.xiaotuxian.entity.member.UserMemberOpenInfo;
import com.itheima.xiaotuxian.exception.BusinessException;
import com.itheima.xiaotuxian.service.member.UserMemberOpenInfoService;
import com.itheima.xiaotuxian.service.member.UserMemberService;
import com.itheima.xiaotuxian.util.ConvertUtil;
import com.itheima.xiaotuxian.util.UrlUtil;
import com.itheima.xiaotuxian.vo.R;
import com.itheima.xiaotuxian.vo.member.request.LoginWxMinRequestVo;
import com.itheima.xiaotuxian.vo.member.response.LoginWxMiniResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.*;

/*
 * @author: lbc
 * @Date: 2023-06-11 14:41:19
 * @Descripttion: 
 */
@RestController
@Slf4j
@RequestMapping("/login")
public class LoginWxMinController extends BaseController {
    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private UserMemberOpenInfoService userMemberOpenInfoService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private WxMiniPayConfig wxMiniConfig;
    private final String KEY_ALGORITHM = "AES";
    private final String ALGORITHM_STR = "AES/CBC/PKCS7Padding";
    private Key key;
    private Cipher cipher;
    /** 默认的用户头像信息 */
    @Value("${account.avatar:}")
    private String avatar;

    /**
     * 小程序登录_内测版
     * 小程序模拟登录
     * @param vo
     * @return
     */
    @ResponseBody
    @PostMapping("/wxMin/simple")
    public R<LoginWxMiniResultVo> wxMinLoginSimple(@RequestBody LoginWxMinRequestVo vo) {
        log.info("Start get SessionKey");
        
        log.info("phoneNumber" + vo.getPhoneNumber());
        if (StringUtils.isBlank(vo.getPhoneNumber())) {
            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
        }
        if (!Validator.isMobile(vo.getPhoneNumber())) {
            throw new BusinessException(ErrorMessageEnum.MEMBER_MOBILE_FORMAT_INVALID);
        }

        var result = new LoginWxMiniResultVo();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("phoneNumber", vo.getPhoneNumber());
        // phoneNumber = (String) vo.getPhoneNumber();
        log.info("明文的phoneNumber" + vo.getPhoneNumber());
         
            UserMember   userMember = userMemberService.getBaseMapper()
                    .selectOne(Wrappers.<UserMember>lambdaQuery().eq(UserMember::getMobile, map.get("phoneNumber")));
            if (userMember == null) {
                // 根据openid查询不到，当前手机号不存在用户信息
                UserMember user = new UserMember();
                user.setMobile((String) map.get("phoneNumber"));
//                user.setAccount("用户" + user.getMobile().substring(user.getMobile().length() - 6));
                user.setAccount("用户" + user.getMobile());
                //设置初始化密码 123456
                user.setPassword(SecureUtil.md5("123456"));
                user.setNickname("用户" + user.getMobile().substring(user.getMobile().length() - 6));
                user.setCreator(user.getMobile());
                // 注册来源，1为pc，2为webapp，3为微信小程序，4为Android，5为ios
                user.setSource(3);
                userMemberService.save(user);
                userMember = user;
                // 根据用户信息生成token信息
            } else {
                // 根据用户信息生成token信息
                // 根据openid查询不到，但是当前手机号存在用户信息
            }

        // 返回当前用户的token信息
        var token = userMemberService.getToken(userMember.getId(), userMember.getAccount(), userMember.getNickname());
        // ConvertUtil.convertLoginInfo(userMember, token);
        // 为空的头像，设置默认的兔子头像
        if (StringUtils.isBlank(userMember.getAvatar())) {
            result.setAvatar(avatar);
        }else{
            result.setAvatar(userMember.getAvatar());
        }
        result.setAccount(userMember.getAccount());
        result.setToken(token);
        result.setId(userMember.getId());
        result.setMobile(userMember.getMobile());
        result.setNickname(userMember.getNickname());
        return R.ok(result);
    }

    /**
     * 微信小程序获取手机号登录
     * @param vo
     * @return
     */
    @ResponseBody
    @PostMapping("/wxMin")
    public R<LoginWxMiniResultVo> wxMinLogin(@RequestBody LoginWxMinRequestVo vo) {
        log.info("Start get SessionKey");
        log.info("用户非敏感信息" + vo.getRawData());
        log.info("签名" + vo.getSignature());
        log.info("iv" + vo.getIv());
        log.info("encryptedData" + vo.getEncryptedData());
        log.info("phoneNumber" + vo.getPhoneNumber());
        if (StringUtils.isAllBlank(vo.getPhoneNumber(), vo.getEncryptedData(), vo.getIv())) {
            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
        }
        var result = new LoginWxMiniResultVo();
        Map<String, Object> map = new HashMap<String, Object>();

        JSONObject sessionKeyOpenId = getSessionKeyOrOpenId(vo.getCode());
        log.info("post请求获取的SessionAndopenId=" + sessionKeyOpenId);
        if(ObjectUtils.isNotEmpty(sessionKeyOpenId.get("errcode"))){
            // 请求微信端，获取openid错误
            throw new BusinessException(ErrorMessageEnum.MEMBER_WXMINI_OPENID_ERROR);
        }
        String openid = sessionKeyOpenId.getString("openid");
        String sessionKey = sessionKeyOpenId.getString("session_key");
        log.info("openid=" + openid + ",session_key=" + sessionKey);
        // JSONObject rawDataJson = JSON.parseObject(vo.getRawData());
        // uuid生成唯一key
        String skey = UUID.randomUUID().toString();
        // 把新的sessionKey和oppenid返回给小程序
        map.put("skey", skey);
        map.put("result", "0");
        // 根据openid查询skey是否存在
        String skey_redis = (String) redisTemplate.opsForValue().get(openid);
        if (StringUtils.isNotBlank(skey_redis)) {
            // 存在 删除 skey 重新生成skey 将skey返回
            redisTemplate.delete(skey_redis);
        }
        // 缓存一份新的
        JSONObject sessionObj = new JSONObject();
        sessionObj.put("openId", openid);
        sessionObj.put("sessionKey", sessionKey);
        redisTemplate.opsForValue().set(skey, sessionObj.toJSONString());
        redisTemplate.opsForValue().set(openid, skey);

        // String phoneNumber = "";
        if (StringUtils.isNotBlank(vo.getPhoneNumber())) {
            map.put("phoneNumber", vo.getPhoneNumber());
            // phoneNumber = (String) vo.getPhoneNumber();
            log.info("明文的phoneNumber" + vo.getPhoneNumber());

        } else {
            // 传了手机号的加密信息，解密手机号信息，并绑定openid和手机号的信息
            if (!StringUtils.isAnyBlank(vo.getEncryptedData(), sessionKey, vo.getIv())) {
                // JSONObject userInfo = getUserInfo(encryptedData, sessionKey, iv);
                String userInfoStr = decryptData(vo.getEncryptedData(), sessionKey, vo.getIv());
                JSONObject userInfo = JSON.parseObject(userInfoStr);

                log.info("根据解密算法获取的userInfo=" + userInfo);
                // userInfo.put("balance", user.getUbalance());
                map.put("phoneNumber", userInfo.get("phoneNumber"));
                // phoneNumber = (String) userInfo.get("phoneNumber");
            }
            // 没有传手机号的加密信息 但是根据openid，查到了关联的用户信息
            log.info("手机号的加密信息解密完成" + (String) map.get("phoneNumber"));
            // 根据用户信息，返回token
        }
        // 手机号获取后，保存手机号信息 和openId信息
        UserMember userMember = userMemberService.findByOpenid(openid);
        // 根据openid查询不到当前绑定的用户信息
        if (userMember == null) {
//            userMember = userMemberService.getBaseMapper()
//                    .selectOne(Wrappers.<UserMember>lambdaQuery().eq(UserMember::getMobile, map.get("phoneNumber")));
            List<UserMember> list = userMemberService.getBaseMapper()
                    .selectList(Wrappers.<UserMember>lambdaQuery().eq(UserMember::getMobile, map.get("phoneNumber")));
            if (list.size()>0){
                userMember = list.get(0);
            }
            if (userMember == null) {
                // 根据openid查询不到，当前手机号不存在用户信息
                UserMember user = new UserMember();
                user.setMobile((String) map.get("phoneNumber"));
                // user.setCreateTime(new Date());
                // user.setUpdateTime(new Date());
                user.setAccount("用户" + user.getMobile().substring(user.getMobile().length() - 6));
                user.setNickname("用户" + user.getMobile().substring(user.getMobile().length() - 6));
                user.setCreator(openid);
                //设置初始化密码 123456
                user.setPassword(SecureUtil.md5("123456"));
                // 注册来源，1为pc，2为webapp，3为微信小程序，4为Android，5为ios
                user.setSource(3);
                userMemberService.save(user);
                userMember = user;
                // 根据用户信息生成token信息
            } else {
                // 根据用户信息生成token信息
                // 根据openid查询不到，但是当前手机号存在用户信息
            }
            // TODO openid不一致的情况，可能是换了微信或者之前存的openid有错误，需要更新
            // 保存第三方的信息  
            UserMemberOpenInfo uOpenInfo = new UserMemberOpenInfo();
            uOpenInfo.setUserId(userMember.getId());
            uOpenInfo.setOpenId(openid);
            uOpenInfo.setCreator(openid);
            // 注册来源，1为pc，2为webapp，3为微信小程序，4为Android，5为ios
            uOpenInfo.setSource(3);
            userMemberOpenInfoService.saveOrUpdate(uOpenInfo,
                Wrappers.<UserMemberOpenInfo>lambdaQuery()
                  .eq(UserMemberOpenInfo::getOpenId,uOpenInfo.getOpenId())
                  .eq(UserMemberOpenInfo::getUserId,uOpenInfo.getUserId()));
        } else {
//            UserMember mobileUserMember = userMemberService.getBaseMapper().selectOne(Wrappers.<UserMember>lambdaQuery()
//                    .eq(UserMember::getMobile, map.get("phoneNumber")).eq(UserMember::getIsDelete, 0));
            UserMember mobileUserMember = null;
            List<UserMember> list = userMemberService.getBaseMapper().selectList(Wrappers.<UserMember>lambdaQuery()
                    .eq(UserMember::getMobile, map.get("phoneNumber")).eq(UserMember::getIsDelete, 0));
            if (list.size()>0){
                mobileUserMember =list.get(0);
            }
            if (null == mobileUserMember) {
                // 小程序使用了不同的手机号进行登录-把当前的openid的登录信息置为删除状态，重新绑定用户信息
                log.info("当前的用户数据异常，请联系管理员" + map.get("phoneNumber") + "openid:" + openid);
                // openid 存在，删除掉,
                userMemberOpenInfoService.updateOpenIdUser(openid);
                // 新增用户信息，
                UserMember user = new UserMember();
                user.setMobile((String) map.get("phoneNumber"));
                user.setAccount("用户" + user.getMobile().substring(user.getMobile().length() - 6));
                user.setNickname("用户" + user.getMobile().substring(user.getMobile().length() - 6));
                user.setCreator(openid);
                // 注册来源，1为pc，2为webapp，3为微信小程序，4为Android，5为ios
                user.setSource(3);
                //设置初始化密码 123456
                user.setPassword(SecureUtil.md5("123456"));
                userMemberService.save(user);
                // 新增小程序第三方信息
                // 保存第三方的信息
                UserMemberOpenInfo uOpenInfo = new UserMemberOpenInfo();
                uOpenInfo.setUserId(user.getId());
                uOpenInfo.setOpenId(openid);
                uOpenInfo.setCreator(openid);
                // 注册来源，1为pc，2为webapp，3为微信小程序，4为Android，5为ios
                uOpenInfo.setSource(3);
                uOpenInfo.setAvatar(user.getAvatar());
                userMemberOpenInfoService.save(uOpenInfo);
                // throw new BusinessException(ErrorMessageEnum.FAILURE);
            } else {
                if (userMember.getMobile().equals(mobileUserMember.getMobile())) {
                // 直接根据用户信息生成token
                } else {
                // 解除openid的关联关系
                userMemberOpenInfoService.updateOpenIdUser(openid);
                // 新建openId和手机号的关系
                // 保存第三方的信息
                UserMemberOpenInfo uOpenInfo = new UserMemberOpenInfo();
                uOpenInfo.setUserId(userMember.getId());
                uOpenInfo.setOpenId(openid);
                uOpenInfo.setCreator(openid);
                // 注册来源，1为pc，2为webapp，3为微信小程序，4为Android，5为ios
                uOpenInfo.setSource(3);
                uOpenInfo.setAvatar(mobileUserMember.getAvatar());
                uOpenInfo.setGender(mobileUserMember.getGender());
                uOpenInfo.setNickname(mobileUserMember.getNickname());
                uOpenInfo.setMobile(mobileUserMember.getMobile());
                userMemberOpenInfoService.save(uOpenInfo);
                // 根据用户信息，返回token
                }
            }
        }

        // 返回当前用户的token信息
        var token = userMemberService.getToken(userMember.getId(), userMember.getAccount(), userMember.getNickname());
        ConvertUtil.convertLoginInfo(userMember, token);
        // 为空的头像，设置默认的兔子头像
        if (StringUtils.isBlank(userMember.getAvatar())) {
            result.setAvatar(avatar);
        }else{
            result.setAvatar(userMember.getAvatar());
        }
        result.setAccount(userMember.getAccount());
        result.setToken(token);
        result.setId(userMember.getId());
        result.setMobile(userMember.getMobile());
        result.setNickname(userMember.getNickname());
        return R.ok(result);
    }
    /**
     * @description:
     * @param {UserMember} userMember
     * @param {String}     jwt
     * @return {*}
     * @author: lbc
     */
    public LoginWxMiniResultVo convertLoginInfo(UserMember userMember, String jwt) {
        var result = new LoginWxMiniResultVo();
        result.setId(userMember.getId());
        result.setMobile(userMember.getMobile());
        result.setToken(jwt);
        result.setAccount(userMember.getAccount());
        result.setAvatar(userMember.getAvatar());
        // result.setNickname(userMember.getNickname());
        // result.setGender(userMember.getGender());
        // if (userMember.getBirthday() != null) {
        // result.setBirthday(userMember.getBirthday().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        // }
        // result.setCityCode(userMember.getCityCode());
        // result.setProvinceCode(userMember.getProvinceCode());
        // result.setProfession(userMember.getProfession());
        return result;
    }

    public JSONObject getSessionKeyOrOpenId(String code) {
        // 微信端登录code
        String wxCode = code;
        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, String> requestUrlParam = new HashMap<String, String>();
        requestUrlParam.put("appid", wxMiniConfig.getAppId());// 小程序appId
        requestUrlParam.put("secret", wxMiniConfig.getAppSecret());
        requestUrlParam.put("js_code", wxCode);// 小程序端返回的code
        requestUrlParam.put("grant_type", "authorization_code");// 默认参数
        log.info("requestUrl:{},appid:{},secret:{},code:{}", requestUrl, wxMiniConfig.getAppId(),
                wxMiniConfig.getAppSecret(), wxCode);

        // 发送post请求读取调用微信接口获取openid用户唯一标识
        JSONObject jsonObject = JSON.parseObject(UrlUtil.sendPost(requestUrl, requestUrlParam));
        return jsonObject;
    }

    public JSONObject getUserInfo(String encryptedData, String sessionKey, String iv) {
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
        try {
            // 如果密钥不足16位，那么就补足. 这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                log.info("根据解密算法获取的userInfo=" + result);
                return JSON.parseObject(result);
            }
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchPaddingException e) {
            log.error(e.getMessage(), e);
        } catch (InvalidParameterSpecException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
            log.error(e.getMessage(), e);
        } catch (BadPaddingException e) {
            log.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            log.error(e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchProviderException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public String decryptData(String encryptDataB64, String sessionKeyB64, String ivB64) {
        return new String(
                decryptOfDiyIV(Base64.decode(encryptDataB64), Base64.decode(sessionKeyB64), Base64.decode(ivB64)));
    }

    private void init(byte[] keyBytes) {
        // 如果密钥不足16位，那么就补足. 这个if 中的内容很重要
        int base = 16;
        if (keyBytes.length % base != 0) {
            int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
            keyBytes = temp;
        }
        // 初始化
        Security.addProvider(new BouncyCastleProvider());
        // 转化成JAVA的密钥格式
        key = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        try {
            // 初始化cipher
            cipher = Cipher.getInstance(ALGORITHM_STR, "BC");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("dddd", e);
        }
    }

    /**
     * 解密方法
     *
     * @param encryptedData 要解密的字符串
     * @param keyBytes      解密密钥
     * @param ivs           自定义对称解密算法初始向量 iv
     * @return 解密后的字节数组
     */
    private byte[] decryptOfDiyIV(byte[] encryptedData, byte[] keyBytes, byte[] ivs) {
        byte[] encryptedText = null;
        init(keyBytes);
        try {
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(ivs));
            encryptedText = cipher.doFinal(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("失败", e);
        }
        return encryptedText;
    }
    // ————————————————
    // 版权声明：本文为CSDN博主「名字叫孙冉」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
    // 原文链接：https://blog.csdn.net/qq_36725282/article/details/89175592

}
