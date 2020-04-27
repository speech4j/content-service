package org.speech4j.contentservice.config.multitenancy;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.speech4j.contentservice.config.multitenancy.MultiTenantConstants.DEFAULT_TENANT_ID;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.driver}")
    private String driverClassName;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;


    private void init(DataSource dataSource){
        try {
            final Connection connection = dataSource.getConnection();
            connection.createStatement().executeUpdate("CREATE SCHEMA IF NOT EXISTS " + DEFAULT_TENANT_ID);
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Bean
    public DataSource getDatasource() {
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setDriverClassName(driverClassName);
        dataSourceConfig.setJdbcUrl(url);
        dataSourceConfig.setUsername(username);
        dataSourceConfig.setPassword(password);
        dataSourceConfig.setMaximumPoolSize(5);

        DataSource dataSource = new HikariDataSource(dataSourceConfig);
        //Initializing of speech4j schema
        init(dataSource);

        return dataSource;
    }
}
