package com.itheima.xiaotuxian.exception;

import cn.hutool.core.collection.CollUtil;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import com.itheima.xiaotuxian.resp.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * @author: dongxing
 * @date ：Created in 2020/6/3
 * @description ：
 * @version: 1.0
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse> handleExcepting(BusinessException e) {
        log.error(e.getMessage(), e);
        //业务异常返回400 的状态码
        return ResponseEntity.status(cn.hutool.http.HttpStatus.HTTP_BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CommonResponse(e.getErrorMessage()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<CommonResponse> handleExcepting(AuthException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CommonResponse(e.getErrorMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse> handleExcepting(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        var messages = e.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CommonResponse(ErrorMessageEnum.PARAMETER_ERROR.getCode(), CollUtil.join(messages, ",")));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleExcepting(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CommonResponse(ErrorMessageEnum.UN_KNOW,e.getMessage()));
    }

    /**
     * 登录异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<CommonResponse> handleExcepting(LoginException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(e.getStatus()).contentType(MediaType.APPLICATION_JSON).body(new CommonResponse(ErrorMessageEnum.UNAUTHORIZED));
    }

    /**
     * Get请求参数错误时的处理
     */
    @ResponseBody
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<CommonResponse>  handleNoHandlerFoundException(Exception e){
//        addExceptionLog(e,request);
        log.error("路径不存在：{}", e.toString());
//        return new ApiFinalResult(404, "服务器资源异常", "路径不存在");
        return ResponseEntity.status(404).contentType(MediaType.APPLICATION_JSON).body(new CommonResponse(ErrorMessageEnum.NOTFUND));
    }
}
