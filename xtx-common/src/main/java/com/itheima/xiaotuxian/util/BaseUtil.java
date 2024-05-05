package com.itheima.xiaotuxian.util;

import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.constant.statics.CommonStatic;
import com.itheima.xiaotuxian.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 基础controller使用的类
 */
@Slf4j
public class BaseUtil {
    public static Integer getShowClient(String client) {
        if(StringUtils.isBlank(client)){
            log.error("getShowClient入参为空，请检查入参");
            throw new BusinessException(ErrorMessageEnum.PARAMETER_ERROR);
        }
        if(CommonStatic.REQUEST_CLIENT_PC.equals(client)){
            return CommonStatic.MATERIAL_SHOW_PC;
        }
        if(CommonStatic.REQUEST_CLIENT_APP.equals(client)){
            return CommonStatic.MATERIAL_SHOW_APP;
        }
        if(CommonStatic.REQUEST_CLIENT_MINIAPP.equals(client)){
            return CommonStatic.MATERIAL_SHOW_MINIAPP;
        }
        log.error("没有匹配的客户端信息,client:"+client);
        return  -1;
    }

    public static String getClient(String sourceClient) {
        return  sourceClient == null ? CommonStatic.REQUEST_CLIENT_PC : sourceClient;
    }
}
