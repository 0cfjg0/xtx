package com.itheima.xiaotuxian.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.itheima.xiaotuxian.constant.enums.ErrorMessageEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class CommonResponse implements Serializable {
    private String message = "";
    private String msg = "";
    private String code = "";
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String data ;

    public CommonResponse(ErrorMessageEnum errorEnum) {
        this.msg = errorEnum.getMessage();
        this.message = errorEnum.getMessage();
        this.code = errorEnum.getCode();
    }

    public CommonResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
        this.message = msg;
    }
    public CommonResponse(ErrorMessageEnum errorEnum, String data) {
        this.msg = errorEnum.getMessage();
        this.message = errorEnum.getMessage();
        this.code = errorEnum.getCode();
        this.data = data;
    }

    public CommonResponse() {
    }
}
