package org.speech4j.contentservice.migration.service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Set;

public interface TenantService {
    Set<String> getAllTenants(DataSource dataSource) throws SQLException;
}
