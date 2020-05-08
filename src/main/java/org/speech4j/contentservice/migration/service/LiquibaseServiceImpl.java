package org.speech4j.contentservice.migration.service;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
@Slf4j
public class LiquibaseServiceImpl implements LiquibaseService {
    private SpringLiquibase springLiquibase;

    @Autowired
    public LiquibaseServiceImpl(SpringLiquibase springLiquibase) {
        this.springLiquibase = springLiquibase;
    }


    public void updateSchema(Connection connection, String changelogFile, String persistentTenant) throws LiquibaseException {
        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));
        database.setLiquibaseSchemaName(persistentTenant);
        database.setDefaultSchemaName(persistentTenant);
        log.debug("LIQUIBASE: Schema with id [{}] was successfully set as default!", persistentTenant);
        ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor(getClass().getClassLoader());

        try (Liquibase liquibase = new Liquibase(changelogFile, resourceAccessor, database)) {
            liquibase.update(springLiquibase.getContexts());
            log.debug("LIQUIBASE: Schema was successfully updated");
        } catch (Exception e) {
            throw new LiquibaseException("Error during the effort to update schema!");
        }
    }
}
