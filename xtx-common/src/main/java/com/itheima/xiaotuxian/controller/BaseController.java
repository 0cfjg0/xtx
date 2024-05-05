package com.itheima.xiaotuxian.controller;
/*
 * @author: lbc
 * @Date: 2023-06-13 20:57:28
 * @Descripttion:
 */


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.exception.AuthException;
import com.itheima.xiaotuxian.util.BaseUtil;
import com.itheima.xiaotuxian.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: itheima
 * @Date: 2023/7/11 6:04 下午
 * @Description:
 */
@Slf4j
@Component
public abstract class BaseController {
    @Resource
    protected HttpServletRequest request;

    // TODO Redis整合
    //@Autowired
    //private StringRedisTemplate redisTemplate;

    protected Boolean hasToken() {
        return  request.getHeader("Authorization") != null  && StringUtils.isNotBlank(request.getHeader("Authorization"));
    }

    protected String getTokenValue(String key) {
        //获取头部header 信息
        try {
            var token = request.getHeader("Authorization").replace("Bearer ", "");
            log.info("token:{}",token);
            var claims = JwtUtil.getClaims(token);
            var data = JSONUtil.parseObj(claims.get("sub"));
            return data.getStr(key);
        } catch (Exception e) {
            throw new AuthException(ErrorMessageEnum.TOKEN_ERROR);
        }
    }

    protected String getUserId() {
        var userId = getTokenValue("id");
        var token = request.getHeader("Authorization").replace("Bearer ", "");
        //String redisToken = redisTemplate.opsForValue().get(RedisKeyStatic.KEY_PREFIX_TOKEN + userId + ":" + token);
        // TODO 获取token
        String redisToken = "TOKEN";

        if(null == redisToken){
            throw new AuthException(ErrorMessageEnum.TOKEN_ERROR);
        }
        if (StrUtil.isEmpty(userId)) {
            throw new AuthException(ErrorMessageEnum.TOKEN_ERROR);
        }
        return userId;
    }

    protected String getClient() {
        var sourceClient = request.getHeader("source-client");
        return BaseUtil.getClient(sourceClient);
    }

    protected Integer getShowClient() {
        var client = getClient();
        return BaseUtil.getShowClient(client);
    }
}
