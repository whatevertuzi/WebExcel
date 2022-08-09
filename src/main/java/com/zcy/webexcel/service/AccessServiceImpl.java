package com.zcy.webexcel.service;

import com.zcy.webexcel.Component.RedisCache;
import com.zcy.webexcel.DaoSys.mapper.SysUser;
import com.zcy.webexcel.DaoSys.mapper.SysUserMapper;
import com.zcy.webexcel.DaoSys.pojo.LoginUser;
import com.zcy.webexcel.DaoSys.vo.JsonResult;
import com.zcy.webexcel.DaoSys.vo.ResultCode;
import com.zcy.webexcel.DaoSys.vo.ResultTool;
import com.zcy.webexcel.Utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AccessServiceImpl implements AccessService {

    private static final Logger log = LogManager.getLogger(AccessServiceImpl.class);
    private final AuthenticationManager authenticationManager;

    private final RedisCache redisCache;

    private final SysUserMapper sysUserMapper;

    public AccessServiceImpl(AuthenticationManager authenticationManager, RedisCache redisCache, SysUserMapper sysUserMapper) {
        this.authenticationManager = authenticationManager;
        this.redisCache = redisCache;
        this.sysUserMapper = sysUserMapper;
    }

    @Override
    public JsonResult login(SysUser user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getAccount(),user.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        if(!authentication.isAuthenticated()){
            return ResultTool.fail(ResultCode.USER_CREDENTIALS_ERROR);
        }
        //使用userid生成token
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userId = loginUser.getUser().getUsername();
        String jwt = JwtUtil.createJWT(userId,3600000L);// 60秒 * 60分钟 *1000毫秒  一个小时
        //authenticate存入redis
        redisCache.setCacheObject("login:"+userId,loginUser);
        //把token响应给前端
        LoginUser userDetails = (LoginUser) authentication.getPrincipal();
        SysUser sysUser = sysUserMapper.selectByName(userDetails.getUsername());
        sysUser.setLastLoginTime(new Date());
        sysUser.setUpdateTime(new Date());
        sysUser.setUpdateUser(sysUser.getId());
        sysUserMapper.updateById(sysUser);
        log.info(sysUser.getAccount()+"登陆了系统");
        return ResultTool.success(jwt);
    }

    @Override
    public JsonResult logout(String token) throws Exception {
        String userid;
        try{
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
            redisCache.deleteObject("login:"+userid);
        } catch (SignatureException e){
            return ResultTool.fail(ResultCode.USER_TOKEN_ILLEGAL);
        }
        log.info(userid+"退出了系统");
        return ResultTool.success();
    }
}
