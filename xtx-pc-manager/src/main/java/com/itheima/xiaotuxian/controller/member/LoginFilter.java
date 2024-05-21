package com.itheima.xiaotuxian.controller.member;


import com.alibaba.fastjson.JSONObject;

import com.itheima.xiaotuxian.util.JwtUtil;
import com.itheima.xiaotuxian.vo.R;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,//请求
                         ServletResponse servletResponse,//响应
                         FilterChain filterChain) throws IOException, ServletException {
//        强转
       HttpServletRequest hsr = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse= (HttpServletResponse) servletResponse;
        String url = hsr.getRequestURL().toString();
//        判断是否是登入界面,是就放行
        if (url.contains("login")){
            //        放行
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
//        不是,查看令牌
        String token = hsr.getHeader("token");//获取令牌
        if (token==null||token.equals("")){
//            没有令牌
            R result = R.error("NOT_LOGIN");
//            把result对象转换为JSON
            String s = JSONObject.toJSONString(result);
            httpServletResponse.getWriter().write(s);
            return;
        }

//        有 解析令牌
        try {
//            解析成功放行
            JwtUtil.getClaims(token);

        } catch (Exception e) {
//            解析失败,打印失败原因
            e.printStackTrace();
//            返回登入页面
            R result = R.error("NOT_LOGIN");
//            把result对象转换为JSON
            String s = JSONObject.toJSONString(result);
            httpServletResponse.getWriter().write(s);
            return;
        }
//        放行
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
