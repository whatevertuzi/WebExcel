package com.zcy.webexcel.config;

import com.zcy.webexcel.Component.*;
import com.zcy.webexcel.service.impl.UserDetailsServiceImpl;
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
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().authenticationEntryPoint(jwtAuthError).accessDeniedHandler(accessDeniedHandler)
                .and()
                .authorizeRequests(authorize -> {
                            try {
                                authorize
                                        .antMatchers("/user/login").permitAll()
                                        .antMatchers("/user/logout").permitAll()
                                        .antMatchers("/getUser").hasAuthority("query_user")
                                        .antMatchers("/itgroup/**").hasAuthority("query_it")
                                        .antMatchers("/booking/**").hasAuthority("query_booking")
                                        .anyRequest().authenticated();

                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                    }
                )
                .addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class)
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
