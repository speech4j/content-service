package org.speech4j.contentservice.migration;

import liquibase.exception.LiquibaseException;
import org.speech4j.contentservice.config.multitenancy.MultiTenantConnectionProviderImpl;
import org.speech4j.contentservice.exception.InternalServerException;
import org.speech4j.contentservice.migration.service.LiquibaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;


@ConditionalOnBean(MultiTenantConnectionProviderImpl.class)
@Component
public class MigrationInitBean {
    @Value("${liquibase.master_changelog}")
    private String masterChangelogFile;

    private MultiTenantConnectionProviderImpl provider;
    private LiquibaseService liquibaseService;

    @Autowired
    public MigrationInitBean(MultiTenantConnectionProviderImpl provider,
                             LiquibaseService liquibaseService) {
        this.provider = provider;
        this.liquibaseService = liquibaseService;
    }

    @PostConstruct
    public void init() throws SQLException{
        Set<String> tenants = provider.getTenantService().getAllTenants(provider.getDataSource());

        if (!tenants.isEmpty()){
            tenants.forEach(tenant->{

                try(Connection connection = provider.getConnection(tenant)) {
                    liquibaseService.updateSchema(connection, masterChangelogFile, tenant);
                } catch (SQLException| LiquibaseException e) {
                    throw new InternalServerException("Error during updating of database!");
                }

            });

        }

    }
}
