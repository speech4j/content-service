package org.speech4j.contentservice.migration;

import liquibase.exception.LiquibaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.speech4j.contentservice.config.multitenancy.DataSourceConfig;
import org.speech4j.contentservice.config.multitenancy.MultiTenantConnectionProviderImpl;
import org.speech4j.contentservice.migration.service.LiquibaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


@ConditionalOnBean(MultiTenantConnectionProviderImpl.class)
@Component
public class MigrationInitBean {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${liquibase.master_changelog}")
    private String masterChangelogFile;

    private DataSource dataSource;
    private MultiTenantConnectionProviderImpl multiTenantConnectionProvider;
    private LiquibaseService liquibaseService;

    @Autowired
    public MigrationInitBean(DataSource dataSource,
                             MultiTenantConnectionProviderImpl multiTenantConnectionProvider,
                             LiquibaseService liquibaseService) {
        this.dataSource = dataSource;
        this.multiTenantConnectionProvider = multiTenantConnectionProvider;
        this.liquibaseService = liquibaseService;
    }

    @PostConstruct
    public void init(){
        List<String> tenants = getAllTenants();

        if (!tenants.isEmpty()){
            tenants.forEach(tenant->{

                try(Connection connection = multiTenantConnectionProvider.getConnection(tenant)) {
                    liquibaseService.updateSchema(connection, masterChangelogFile, tenant);

                } catch (SQLException | LiquibaseException e) {
                    e.printStackTrace();
                }

            });

        }

    }


    private List<String> getAllTenants(){
        List<String> tenants = new ArrayList<>();

        try (final Connection connection = dataSource.getConnection()){
            connection.setSchema("metadata");
            try(Statement statement = connection.createStatement()) {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM tenants");
                while (resultSet.next()){
                    tenants.add(resultSet.getString("id"));
                }

            }
        }catch (SQLException e){
            logger.debug(e.getMessage());
        }

        return tenants;
    }
}
