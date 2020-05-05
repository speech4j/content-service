package org.speech4j.contentservice.migration.service;

import liquibase.exception.LiquibaseException;

import java.sql.Connection;

public interface LiquibaseService {
    void updateSchema(Connection connection, String changelogFile, String persistentTenant) throws LiquibaseException;
}
