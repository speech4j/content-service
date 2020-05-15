package org.speech4j.contentservice.config.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.speech4j.contentservice.exception.InternalServerException;
import org.speech4j.contentservice.exception.TenantNotFoundException;
import org.speech4j.contentservice.migration.MigrationInitBean;
import org.speech4j.contentservice.migration.service.MigrationService;
import org.speech4j.contentservice.migration.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static java.util.Objects.isNull;
import static org.speech4j.contentservice.config.multitenancy.MultiTenantConstants.DEFAULT_TENANT_ID;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {
    private transient DataSource dataSource;
    private transient Logger logger = LoggerFactory.getLogger(MultiTenantConnectionProviderImpl.class);
    private transient TenantService tenantService;
    private transient MigrationService migrationService;

    @Autowired
    public MultiTenantConnectionProviderImpl(DataSource dataSource,
                                             TenantService tenantService,
                                             MigrationService migrationService) {
        this.dataSource = dataSource;
        this.tenantService = tenantService;
        this.migrationService = migrationService;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        final Connection connection = getAnyConnection();
        try {
            if (
                    !isNull(tenantIdentifier)
                    //Checking if specified tenant is in database
                    && MigrationInitBean.getInitialTenants().contains(tenantIdentifier)
            ) {
                connection.setSchema(tenantIdentifier);
                logger.debug("DATABASE: Schema with id [{}] was successfully set as default!", tenantIdentifier);
            } else if (
                    !isNull(tenantIdentifier)
                    //Checking if specified tenant is in database even if this tenant will be created at runtime
                    && getNewRuntimeTenants(tenantService.getAllTenants()).contains(tenantIdentifier)
            ) {
                migrationService.migrate(getNewRuntimeTenants(tenantService.getAllTenants()));
                connection.setSchema(tenantIdentifier);
                logger.debug("DATABASE: Schema with id [{}] was successfully set as default!", tenantIdentifier);
            } else {
                throw new TenantNotFoundException("Tenant with specified identifier [" + tenantIdentifier + "] not found!");
            }

        } catch (SQLException e) {
            throw new InternalServerException("Error during the switching to schema: [ " + tenantIdentifier + "]");
        }

        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try {
            connection.setSchema(DEFAULT_TENANT_ID);
        } catch (SQLException e) {
            throw new InternalServerException("Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]");
        } finally {
            connection.close();
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return true;
    }

    private List<String> getNewRuntimeTenants(List<String> tenants) {
        List<String> initialTenants = MigrationInitBean.getInitialTenants();
        tenants.removeAll(initialTenants);
        return tenants;
    }
}
