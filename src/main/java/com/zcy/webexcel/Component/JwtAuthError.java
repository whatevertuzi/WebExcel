package com.zcy.webexcel.Component;


import com.alibaba.fastjson.JSON;
import com.zcy.webexcel.DaoSys.vo.JsonResult;
import com.zcy.webexcel.DaoSys.vo.ResultCode;
import com.zcy.webexcel.DaoSys.vo.ResultTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthError implements AuthenticationEntryPoint {
    private static final Logger LOGGER = LogManager.getLogger(JwtAuthError.class);
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        //返回json数据
        LOGGER.warn("登陆失败:"+e.getMessage()+"\n"+"IP:"+request.getHeader("Origin"));
        JsonResult<ResultCode> result;
        if (e instanceof AccountExpiredException) {
            if (e.getMessage().equals("token")){
                //token非法
                result = ResultTool.fail(ResultCode.USER_TOKEN_ILLEGAL);
            } else if (e.getMessage().equals("tokenExpired")) {
                //token已失效
                result = ResultTool.fail(ResultCode.USER_ACCOUNT_EXPIRED);
            } else {
                //账号过期
                result = ResultTool.fail(ResultCode.USER_ACCOUNT_EXPIRED);
            }
        } else if (e instanceof BadCredentialsException) {
            //密码错误
            result = ResultTool.fail(ResultCode.USER_CREDENTIALS_ERROR);
        } else if (e instanceof CredentialsExpiredException) {
            //密码过期
            result = ResultTool.fail(ResultCode.USER_CREDENTIALS_EXPIRED);
        } else if (e instanceof DisabledException) {
            //账号不可用
            result = ResultTool.fail(ResultCode.USER_ACCOUNT_DISABLE);
        } else if (e instanceof LockedException) {
            //账号锁定
            result = ResultTool.fail(ResultCode.USER_ACCOUNT_LOCKED);
        } else if (e instanceof InternalAuthenticationServiceException) {
            //用户不存在
            result = ResultTool.fail(ResultCode.USER_ACCOUNT_NOT_EXIST);
        } else if (e instanceof InsufficientAuthenticationException) {
            //用户未登陆
            result = ResultTool.fail(ResultCode.USER_NOT_LOGIN);
        } else{
            //其他错误
            result = ResultTool.fail(ResultCode.COMMON_FAIL);
        }
        //处理编码方式，防止中文乱码的情况
        response.setContentType("text/json;charset=utf-8");
        //塞到HttpServletResponse中返回给前台
        response.getWriter().write(JSON.toJSONString(result));
    }
}
