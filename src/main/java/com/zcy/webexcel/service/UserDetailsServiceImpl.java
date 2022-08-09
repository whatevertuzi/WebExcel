package com.zcy.webexcel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zcy.webexcel.DaoSys.SysPermission;
import com.zcy.webexcel.DaoSys.mapper.SysPermissionMapper;
import com.zcy.webexcel.DaoSys.mapper.SysUser;
import com.zcy.webexcel.DaoSys.mapper.SysUserMapper;
import com.zcy.webexcel.DaoSys.pojo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || "".equals(username)) {
            throw new RuntimeException("用户不能为空");
        }
        //根据用户名查询用户
        SysUser sysUser = sysUserMapper.selectByName(username);
        if (sysUser == null) {
            throw new RuntimeException("用户不存在");
        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        List<SysPermission> sysPermissions;
        if (sysUser != null) {
            //获取该用户所拥有的权限
            sysPermissions = sysPermissionMapper.selectListByUser(sysUser.getId());
            // 声明用户授权
            sysPermissions.forEach(sysPermission -> {
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(sysPermission.getPermissionCode());
                grantedAuthorities.add(grantedAuthority);
            });
        }
        boolean sysUserEnabled;
        boolean sysUserAccountNonExpired;
        boolean sysUserAccountNonLocked;
        boolean sysUserCredentialsNonExpired;
        if (sysUser.getEnabled() == 1) {
            sysUserEnabled = true;
        } else {
            sysUserEnabled = false;
        }
        if (sysUser.getAccountNonExpired() == 1) {
            sysUserAccountNonExpired = true;
        } else {
            sysUserAccountNonExpired = false;
        }
        if (sysUser.getAccountNonLocked() == 1) {
            sysUserAccountNonLocked = true;
        } else {
            sysUserAccountNonLocked = false;
        }
        if (sysUser.getCredentialsNonExpired() == 1) {
            sysUserCredentialsNonExpired = true;
        } else {
            sysUserCredentialsNonExpired = false;
        }
        return new LoginUser(new User(sysUser.getAccount(), sysUser.getPassword(), sysUserEnabled, sysUserAccountNonExpired, sysUserAccountNonLocked, sysUserCredentialsNonExpired, grantedAuthorities), grantedAuthorities);
    }
}

