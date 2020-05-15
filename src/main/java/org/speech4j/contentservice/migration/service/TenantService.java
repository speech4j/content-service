package org.speech4j.contentservice.migration.service;

import java.sql.SQLException;
import java.util.List;

public interface TenantService {
    List<String> getAllTenants() throws SQLException;
}
