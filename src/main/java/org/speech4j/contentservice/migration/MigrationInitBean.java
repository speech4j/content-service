package org.speech4j.contentservice.migration;

import lombok.extern.slf4j.Slf4j;
import org.speech4j.contentservice.config.multitenancy.MultiTenantConnectionProviderImpl;
import org.speech4j.contentservice.migration.service.MigrationService;
import org.speech4j.contentservice.migration.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.SQLException;


@ConditionalOnBean(MultiTenantConnectionProviderImpl.class)
@Component
@Slf4j
public class MigrationInitBean {
    private TenantService tenantService;
    private MigrationService migrationService;

    @Autowired
    public MigrationInitBean(
            TenantService tenantService,
            MigrationService migrationService) {
        this.tenantService = tenantService;
        this.migrationService = migrationService;
    }

    @PostConstruct
    public void init() throws SQLException{
        migrationService.migrate(tenantService.getAllTenants());
    }
}
