package com.zcy.webexcel.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @program:
 * @description: 本地数据库配置
 * @author: zcy
 * @create: 2022/07/20
 **/
@Configuration
@MapperScan(basePackages = "com.zcy.webexcel.DaoSys.mapper" ,sqlSessionFactoryRef = "twoSqlSessionFactory")
public class DataSourceSysConfig {

    @Value("${spring.datasource.two.driver-class-name}")
    String driverClass;
    @Value("${spring.datasource.two.url}")
    String url;
    @Value("${spring.datasource.two.username}")
    String userName;
    @Value("${spring.datasource.two.password}")
    String passWord;

    @Bean(name = "twoDataSource")
    @ConfigurationProperties("spring.datasource.two")
    public DataSource masterDataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(passWord);
        return dataSource;
    }

    @Bean(name = "twoSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("twoDataSource") DataSource dataSource) throws Exception {
        final MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "twoSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionFactoryTemplate(@Qualifier("twoSqlSessionFactory")SqlSessionFactory sqlSessionFactory ) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}


