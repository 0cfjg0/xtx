package com.itheima.xiaotuxian.aspect;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志切面类
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    /**
     * ..表示包及子包 该方法代表controller层的所有方法
     * 路径需要根据自己项目定义
     */
    @Pointcut("execution(public * com.itheima.xiaotuxian.controller..*.*(..))" +
            "|| execution(public * com.itheima.xiaotuxian.exception..*.*(..))")
    public void controllerMethod() {
    }

    //    原文链接：https://blog.csdn.net/weixin_43818327/article/details/121286563
    public Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 方法环绕执行
     *
     * @param joinPoint
     * @throws Throwable
     * @throws Exception
     */
    @Around("controllerMethod()")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        // IP地址
        String ipAddr = getRemoteHost(request);
        String url = request.getRequestURL().toString();
        var header = getHeadersInfo(request);
        String reqParam = preHandle(joinPoint, request);
        log.info("请求源IP:【{}】,请求URL:【{}】,请求参数:【{}】", ipAddr, url, reqParam);

        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
//            e.printStackTrace();
            log.error("请求源IP:【{}】,请求头:【{}】,请求URL:【{}】,报错信息:【{}】", ipAddr,header, url, e.getMessage());
            throw e;
        }
        String respParam = postHandle(result);
        log.info("请求源IP:【{}】,请求URL:【{}】,返回参数:【{}】", ipAddr, url, respParam);
        return result;
    }

    /**
     * 返回数据
     *
     * @param retVal
     * @return
     */
    private String postHandle(Object retVal) {
        if (null == retVal) {
            return "";
        }
        return JSON.toJSONString(retVal);
    }

    /**
     * 获取目标主机的ip
     *
     * @param request
     * @return
     */
    private String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    private String preHandle(ProceedingJoinPoint joinPoint, HttpServletRequest request) {

        StringBuilder reqParam = new StringBuilder();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        java.lang.reflect.Method targetMethod = methodSignature.getMethod();

        Object[] args = joinPoint.getArgs();

        Annotation[][] parameterAnnotations = targetMethod.getParameterAnnotations();
        int i = 0;
        for (Annotation[] annotations : parameterAnnotations) {
            for1: for (Annotation annotation : annotations) {
                if (annotation instanceof RequestParam) {
                    if(args[i] instanceof MultipartFile){
                        reqParam.append(((RequestParam) annotation).name()).append(":").append(((MultipartFile)args[i]).getOriginalFilename())
                                .append(",");
                    }else{
                        reqParam.append(((RequestParam) annotation).name()).append(":").append(JSON.toJSONString(args[i]))
                                .append(",");
                    }

                    i++;
                    break for1;
                } else if (annotation instanceof PathVariable) {
                    reqParam.append(JSON.toJSONString(args[i])).append(",");
                    i++;
                    break for1;
                } else if (annotation instanceof RequestBody) {
                    reqParam.append(JSON.toJSONString(args[i])).append(",");
                    i++;
                    break for1;
                }
            }
        }
        return reqParam.toString();
    }

}
