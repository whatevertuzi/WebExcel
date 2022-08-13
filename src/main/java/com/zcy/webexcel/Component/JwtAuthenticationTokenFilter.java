package com.zcy.webexcel.Component;

import com.zcy.webexcel.DaoSys.pojo.SysPermission;
import com.zcy.webexcel.DaoSys.mapper.SysPermissionMapper;
import com.zcy.webexcel.DaoSys.mapper.SysUserMapper;
import com.zcy.webexcel.DaoSys.pojo.LoginUser;
import com.zcy.webexcel.Utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private JwtAuthError jwtAuthError;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            //放行
            filterChain.doFilter(request, response);
            return;
        }
        //解析token
        String userAccount;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userAccount = claims.getSubject();
        } catch (ExpiredJwtException e) {
            e.printStackTrace();
//            result = ResultTool.fail(ResultCode.USER_TOKEN_ILLEGAL);
            SecurityContextHolder.clearContext();
            AccountExpiredException accountExpiredException = new AccountExpiredException("tokenExpired");
            this.jwtAuthError.commence(request, response, accountExpiredException);
            return;

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            AccountExpiredException accountExpiredException = new AccountExpiredException("token");
            this.jwtAuthError.commence(request, response, accountExpiredException);
            return;
        }
        //从redis中获取用户信息
        String redisKey = "login:" + userAccount;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        if(Objects.isNull(loginUser)){
            SecurityContextHolder.clearContext();
            AccountExpiredException accountExpiredException = new AccountExpiredException("tokenExpired");
            this.jwtAuthError.commence(request, response, accountExpiredException);
            return;
//            throw new RuntimeException("用户未登录");
        }
        //存入SecurityContextHolder
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        List<SysPermission> sysPermissions;
        //获取该用户所拥有的权限
        Integer userId = sysUserMapper.selectIdByAccount(loginUser.getUsername()).getId();
        sysPermissions = sysPermissionMapper.selectListByUser(userId);
        // 声明用户授权
        sysPermissions.forEach(sysPermission -> {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(sysPermission.getPermissionCode());
            grantedAuthorities.add(grantedAuthority);
        });
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser,null,grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        //放行
        filterChain.doFilter(request, response);
    }
}
