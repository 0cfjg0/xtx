package com.itheima.xiaotuxian.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsUtil {
    @Value("${sms.region-id:}")
    private String regionId;
    @Value("${sms.accessKey:}")
    private String accessKey;
    @Value("${sms.secret:}")
    private String secret;

    private static final String ALIYUN_DOMAIN = "dysmsapi.aliyuncs.com";
    private static final String ALIYUN_VERSION = "2017-05-25";
    private static final String ALIYUN_SENDSMS = "SendSms";
    private static final String ALIYUN_SENDBATCHSMS = "SendBatchSms";

    /**
     * @param mobile        手机号
     * @param templateCode  模板code
     * @param templateParam 参数 {"code":"786523"}
     * @param signName      签名
     * @return
     */
    public Boolean sendSms(String mobile, String templateCode, String templateParam, String signName) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, secret);
        IAcsClient client = new DefaultAcsClient(profile);
        var request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(ALIYUN_DOMAIN);
        request.setSysVersion(ALIYUN_VERSION);
        request.setSysAction(ALIYUN_SENDSMS);
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", templateParam);
        request.putQueryParameter("SignName", signName);
        try {
            var response = client.getCommonResponse(request);
            var map = JSONUtil.parseObj(response.getData());
            if (map != null && StrUtil.equals("OK", map.get("Message").toString())) {
                return true;
            }
            log.info("map:{}", map);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param phoneNumberJson   手机号 ["手机号1","手机号2"]
     * @param templateCode      模板code
     * @param templateParamJson 参数 [{"name":"TemplateParamJson",{"name":"TemplateParamJson"}]
     * @param signNameJson      签名  ["签名1","签名2"]
     * @return
     */
    public Boolean sendBatchSms(String phoneNumberJson, String templateCode, String templateParamJson, String signNameJson) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKey, secret);
        IAcsClient client = new DefaultAcsClient(profile);
        var request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(ALIYUN_DOMAIN);
        request.setSysVersion(ALIYUN_VERSION);
        request.setSysAction(ALIYUN_SENDBATCHSMS);
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumberJson", phoneNumberJson);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParamJson", templateParamJson);
        request.putQueryParameter("SignNameJson", signNameJson);
        try {
            var response = client.getCommonResponse(request);
            var map = JSONUtil.parseObj(response.getData());
            if (map != null && StrUtil.equals("OK", map.get("Message").toString())) {
                return true;
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }
}
