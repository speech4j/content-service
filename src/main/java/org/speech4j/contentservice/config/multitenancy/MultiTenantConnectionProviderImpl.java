package org.speech4j.contentservice.config.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.speech4j.contentservice.exception.InternalServerException;
import org.speech4j.contentservice.exception.TenantNotFoundException;
import org.speech4j.contentservice.migration.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.speech4j.contentservice.config.multitenancy.MultiTenantConstants.DEFAULT_TENANT_ID;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {
    private transient DataSource dataSource;
    private transient Logger logger = LoggerFactory.getLogger(MultiTenantConnectionProviderImpl.class);
    private TenantService tenantService;

    @Autowired
    public MultiTenantConnectionProviderImpl(DataSource dataSource, TenantService tenantService) {
        this.dataSource = dataSource;
        this.tenantService = tenantService;
    }


    public TenantService getTenantService() {
        return tenantService;
    }

    public DataSource getDataSource() {
        return dataSource;
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
                    tenantIdentifier != null
                    && !tenantIdentifier.equals(DEFAULT_TENANT_ID)
                    //Checking if specified tenant is in database even if this tenant will be created at runtime
                    && tenantService.getAllTenants(dataSource).contains(tenantIdentifier)
            ) {
                try (Statement ps = connection.createStatement()) {
                    connection.setSchema(tenantIdentifier);
                    logger.debug("DATABASE: Schema with id [{}] was successfully set as default!", tenantIdentifier);
                }

            } else if (tenantIdentifier.equals(DEFAULT_TENANT_ID)){
                connection.setSchema(DEFAULT_TENANT_ID);
            } else {
                throw new TenantNotFoundException("Tenant with specified identifier [" + tenantIdentifier + "] not found!");
            }

        }catch (SQLException e) {
            throw new InternalServerException("Error during the switching to schema: [ " + tenantIdentifier + "]");
        }

        return connection;
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try {
            connection.setSchema(DEFAULT_TENANT_ID);
        }catch ( SQLException e ) {
            throw new InternalServerException("Could not alter JDBC connection to specified schema [" + tenantIdentifier + "]");
        }finally {
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
}
