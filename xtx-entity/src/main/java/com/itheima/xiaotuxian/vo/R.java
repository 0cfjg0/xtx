package com.itheima.xiaotuxian.vo;
/*
 * @author: lbc
 * @Date: 2023-06-13 20:57:26
 * @Descripttion: 
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itheima.xiaotuxian.vo.member.CartVo;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回数据
 */
@Data
@NoArgsConstructor
public class R<T>{
    public static final String SUCCESS = "操作成功";
    public static final String ERROR = "操作失败";
    //@JsonIgnore
    private String code;//返回状态码
    private String msg;//返回信息
    private T result;//返回数据

    public R(T data) {
        this.msg = SUCCESS;
        this.result = data;
        this.code = "1";
    }

    public R(String message) {
        this.msg = message;
        this.code = "1";
    }

    public R(T data, String message) {
        this.msg = message;
        this.result = data;
    }
    public R(T data, String message, String code) {
        this.msg = message;
        this.result = data;
        this.code = code;
    }

    public static R ok() {
        return new R(SUCCESS);
    }

    public static <T> R ok(T data, String message) {
        return new R(data, message);
    }

    public static <T> R ok(T data) {
        return new R(data);
    }


    public static <T> R error(T data) {
        return new R(data,ERROR,"-1");
    }

}
