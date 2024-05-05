package com.itheima.xiaotuxian.util;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.exception.AuthException;

import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @version : 1.0
 * @author : zwk
 * @data : Create in 2020-06-04
 * @description :
 */
public class JwtUtil {
    private JwtUtil() {
    }

    //有效期为 3*24*60*60*1000  3天  259200000L
    public static final Long JWT_TTL = 259200000L;  
    //设置秘钥明文
    public static final String JWT_KEY = "xiaotuxian";

    public static String createJWT(String id, String userNmae, String name, Long ttlMillis) {

        //指定签名中使用的加密算法hs256
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        //获取当前时间毫秒数
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //如果传入的超时时间参数为空, 使用默认1小时作为超时时间
        if (ttlMillis == null) {
            ttlMillis = JwtUtil.JWT_TTL;
        }
        //使用当前时间+ 超时时间=具体在哪个时间点超时
        long expMillis = nowMillis + ttlMillis;
        //在这个时间超时
        Date expDate = new Date(expMillis);
        //将传入的id与userName转换为json
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("username", userNmae);
        map.put("name", name);
        String s = JSONUtil.toJsonStr(map);

        //指定加密的钥匙是什么
        SecretKey secretKey = generalKey();

        JwtBuilder builder = Jwts.builder()
                .setSubject(s)//设置json:id和userName
                .setIssuedAt(now)//签发时间
                .signWith(signatureAlgorithm, secretKey)//使用hs256加密对称算法签名,第二个是秘钥
                .setExpiration(expDate);//设置过期时间
        return builder.compact();
    }

    /**
     * 生成加密后的秘钥 secretKey
     *
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    /**
     * 根据secretKey和token信息获取
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims getClaims(String jwt) {
        SecretKey secretKey = generalKey();
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwt)
                    .getBody();
        }catch (Exception e){
            throw new AuthException(ErrorMessageEnum.TOKEN_ERROR);
        }
    }

    /**
     * 根据id account nickname 生成token
     * @param id
     * @param account
     * @param nickname
     * @return
     */
    public static String getToken(String id, String account, String nickname,Long ttlMillis) {
        var jwt = "";
        try {
            jwt = JwtUtil.createJWT(id, account, nickname, ttlMillis);
        } catch (Exception e) {
            throw new AuthException(ErrorMessageEnum.TOKEN_ERROR);
        }
        return jwt;
    }
    /**
    * token是否过期
    * @return true：过期
    */
    public static boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }

     /**
     * 判断token是否已经失效
     */
    public static boolean isTokenExpired(String token) {
        Date expiredDate = null;
        try {
            Claims claims = getClaims(token);
            expiredDate = claims.getExpiration();
        } catch (Exception e) {
            return true;
        }
        return expiredDate.before(new Date());
    }

}
