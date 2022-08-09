package com.zcy.webexcel.config;

import com.zcy.webexcel.Component.*;
import com.zcy.webexcel.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity	// 添加 security 过滤器
@EnableGlobalMethodSecurity(prePostEnabled = true)	// 启用方法级别的权限认证
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;

    // 权限不足错误信息处理，包含认证错误与鉴权错误处理
    private final JwtAuthError jwtAuthError;

    //权限拒绝处理逻辑
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtAuthError jwtAuthError, AccessDeniedHandlerImpl accessDeniedHandler) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthError = jwtAuthError;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    // jwt 校验过滤器，从 http 头部 Authorization 字段读取 token 并校验
    @Bean
    public JwtAuthenticationTokenFilter authFilter(){
        return new JwtAuthenticationTokenFilter();
    }

    /**
     * 密码明文加密方式配置
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 获取AuthenticationManager（认证管理器），登录时认证使用
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 基于 token，不需要 csrf
                .cors().and()
                .csrf().disable()
                // 基于 token，不需要 session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                // 设置 jwtAuthError 处理认证失败、鉴权失败
                .exceptionHandling().authenticationEntryPoint(jwtAuthError).accessDeniedHandler(accessDeniedHandler)
//                //登出之后删除cookie
//                .deleteCookies("JSESSIONID")
                .and()
                // 下面开始设置权限
                .authorizeRequests(authorize -> {
                            try {
                                authorize
                                        // 请求放开
                                        .antMatchers("/user/login").permitAll()
                                        .antMatchers("/user/logout").permitAll()
                                        .antMatchers("/getUser").hasAuthority("query_user")
                                        .antMatchers("/getdata").hasAuthority("query_data")
                                        .antMatchers("/export").hasAuthority("query_data")
//                                        .antMatchers("/**").permitAll()
                                        // 其他地址的访问均需验证权限
                                        .anyRequest().authenticated();

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                    }
                )
                // 添加 JWT 过滤器，JWT 过滤器在用户名密码认证过滤器之前
                .addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class)
                // 登入
                // 认证用户时用户信息加载配置，注入springAuthUserService
                .userDetailsService(userDetailsService)
                .build();
    }

    /**
     * 配置跨源访问(CORS)
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

}
