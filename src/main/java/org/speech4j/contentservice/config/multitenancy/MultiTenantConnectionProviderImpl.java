package org.speech4j.contentservice.config.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.speech4j.contentservice.exception.InternalServerException;
import org.speech4j.contentservice.exception.TenantNotFoundException;
import org.speech4j.contentservice.migration.service.MigrationService;
import org.speech4j.contentservice.migration.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.speech4j.contentservice.config.multitenancy.MultiTenantConstants.DEFAULT_TENANT_ID;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {
    private transient DataSource dataSource;
    private transient Logger logger = LoggerFactory.getLogger(MultiTenantConnectionProviderImpl.class);
    private transient TenantService tenantService;
    private transient MigrationService migrationService;
    private List<String> initialTenants;

    @Autowired
    public MultiTenantConnectionProviderImpl(DataSource dataSource,
                                             TenantService tenantService,
                                             MigrationService migrationService) {
        this.dataSource = dataSource;
        this.tenantService = tenantService;
        this.migrationService = migrationService;
    }

    @PostConstruct
    public void init() throws SQLException{
        initialTenants = tenantService.getAllTenants();
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
    public Connection getConnection(String tenantName) throws SQLException {
        final Connection connection = getAnyConnection();
        try {
            //Checking if specified tenant is in database
            if (initialTenants.contains(tenantName)) {
                connection.setSchema(tenantName);
                logger.debug("DATABASE: Schema with id [{}] was successfully set as default!", tenantName);
            }
            //Checking if specified tenant is in database even if this tenant will be created at runtime
            else if (getNewRuntimeTenants(tenantService.getAllTenants()).contains(tenantName)) {
                migrationService.migrate(getNewRuntimeTenants(tenantService.getAllTenants()));
                connection.setSchema(tenantName);
                logger.debug("DATABASE: Schema with id [{}] was successfully set as default!", tenantName);
            }
            //Case if tenant is fake
            else {
                throw new TenantNotFoundException("Tenant with specified identifier [" + tenantName + "] not found!");
            }
        } catch (SQLException e) {
            throw new InternalServerException("Error during the switching to schema: [ " + tenantName + "]");
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
        tenants.removeAll(initialTenants);
        return tenants;
    }
}
