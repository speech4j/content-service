package org.speech4j.contentservice.migration;

import liquibase.exception.LiquibaseException;
import lombok.extern.slf4j.Slf4j;
import org.speech4j.contentservice.config.multitenancy.MultiTenantConnectionProviderImpl;
import org.speech4j.contentservice.exception.InternalServerException;
import org.speech4j.contentservice.migration.service.LiquibaseService;
import org.speech4j.contentservice.migration.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;


@ConditionalOnBean(MultiTenantConnectionProviderImpl.class)
@Component
@Slf4j
public class MigrationInitBean {
    @Value("${liquibase.master_changelog}")
    private String masterChangelogFile;

    private LiquibaseService liquibaseService;
    private TenantService tenantService;
    private DataSource dataSource;

    @Autowired
    public MigrationInitBean(LiquibaseService liquibaseService,
                             TenantService tenantService,
                             DataSource dataSource) {
        this.liquibaseService = liquibaseService;
        this.tenantService = tenantService;
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void init() throws SQLException{
        Set<String> tenants = tenantService.getAllTenants(dataSource);

        if (!tenants.isEmpty()){
            tenants.forEach(tenant->{

                try(Connection connection = dataSource.getConnection()) {
                    connection.setSchema(tenant);
                    log.info("DATABASE: Schema with id [{}] was successfully set as default!", tenant);

                    liquibaseService.updateSchema(connection, masterChangelogFile, tenant);

                } catch (SQLException| LiquibaseException e) {
                    throw new InternalServerException("Error during updating of database!");
                }

            });

        }

    }
}
