//package com.zcy.webexcel.config;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.SqlSessionTemplate;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//
//import javax.sql.DataSource;
//
///**
// * @program:
// * @description: CRS/report数据库配置
// * @author: zcy
// * @create: 2022/07/20
// **/
//@Configuration
//@MapperScan(basePackages = "com.zcy.webexcel.DaoCrs.mapper", sqlSessionFactoryRef = "oneSqlSessionFactory")
//public class DataSourceCrsConfig {
//    @Value("${spring.datasource.one.driver-class-name}")
//    String driverClass;
//    @Value("${spring.datasource.one.url}")
//    String url;
//    @Value("${spring.datasource.one.username}")
//    String userName;
//    @Value("${spring.datasource.one.password}")
//    String passWord;
//    @Value("${spring.datasource.one.type}")
//    String type;
//    @Value("${spring.datasource.one.initialSize}")
//    Integer initialSize;
//    @Value("${spring.datasource.one.maxActive}")
//    Integer maxActive;
//
//
//    @Primary
//    @Bean(name = "oneDataSource")
//    @ConfigurationProperties("spring.datasource.one")
//    public DataSource masterDataSource() {
//        DruidDataSource dataSource = new DruidDataSource();
//        dataSource.setDriverClassName(driverClass);
//        dataSource.setUrl(url);
//        dataSource.setUsername(userName);
//        dataSource.setPassword(passWord);
//        dataSource.setDbType(type);
//        dataSource.setInitialSize(initialSize);
//        dataSource.setInitialSize(maxActive);
//        return dataSource;
//    }
//
//    @Bean(name = "oneSqlSessionFactory")
//    public SqlSessionFactory sqlSessionFactory(@Qualifier("oneDataSource") DataSource dataSource) throws Exception {
//        final MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("mapper/*.xml"));
//        return sqlSessionFactoryBean.getObject();
//    }
//    @Bean(name = "oneSqlSessionTemplate")
//    public SqlSessionTemplate sqlSessionFactoryTemplate(@Qualifier("oneSqlSessionFactory")SqlSessionFactory sqlSessionFactory ) throws Exception {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//}
//
