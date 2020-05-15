package org.speech4j.contentservice.migration.service;

import java.util.List;

public interface MigrationService {
    void migrate(List<String> tenants);
}
