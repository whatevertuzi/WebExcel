package com.zcy.webexcel.service.impl;

import com.zcy.webexcel.DaoSys.SysPermission;
import com.zcy.webexcel.DaoSys.mapper.SysPermissionMapper;
import com.zcy.webexcel.DaoSys.pojo.SysUser;
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
    private final SysUserMapper sysUserMapper;
    private final SysPermissionMapper sysPermissionMapper;

    public UserDetailsServiceImpl(SysUserMapper sysUserMapper, SysPermissionMapper sysPermissionMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysPermissionMapper = sysPermissionMapper;
    }

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
        //获取该用户所拥有的权限
        sysPermissions = sysPermissionMapper.selectListByUser(sysUser.getId());
        // 声明用户授权
        sysPermissions.forEach(sysPermission -> {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(sysPermission.getPermissionCode());
            grantedAuthorities.add(grantedAuthority);
        });
        boolean sysUserEnabled;
        boolean sysUserAccountNonExpired;
        boolean sysUserAccountNonLocked;
        boolean sysUserCredentialsNonExpired;
        sysUserEnabled = sysUser.getEnabled() == 1;
        sysUserAccountNonExpired = sysUser.getAccountNonExpired() == 1;
        sysUserAccountNonLocked = sysUser.getAccountNonLocked() == 1;
        sysUserCredentialsNonExpired = sysUser.getCredentialsNonExpired() == 1;
        return new LoginUser(new User(sysUser.getAccount(), sysUser.getPassword(), sysUserEnabled, sysUserAccountNonExpired, sysUserAccountNonLocked, sysUserCredentialsNonExpired, grantedAuthorities), grantedAuthorities);
    }
}

