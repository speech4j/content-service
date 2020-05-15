package org.speech4j.contentservice.migration.service.impl;

import liquibase.exception.LiquibaseException;
import lombok.extern.slf4j.Slf4j;
import org.speech4j.contentservice.exception.InternalServerException;
import org.speech4j.contentservice.migration.service.LiquibaseService;
import org.speech4j.contentservice.migration.service.MigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
public class MigrationServiceImpl implements MigrationService {
    @Value("${liquibase.master_changelog}")
    private String masterChangelogFile;

    private LiquibaseService liquibaseService;
    private DataSource dataSource;

    @Autowired
    public MigrationServiceImpl(LiquibaseService liquibaseService,
                                DataSource dataSource
    ) {
        this.liquibaseService = liquibaseService;
        this.dataSource = dataSource;
    }

    @Override
    public void migrate(List<String> tenants) {
        if (!tenants.isEmpty()) {
            tenants.forEach(tenant -> {
                try (Connection connection = dataSource.getConnection()) {
                    connection.setSchema(tenant);
                    log.info("DATABASE: Schema with id [{}] was successfully set as default!", tenant);
                    liquibaseService.updateSchema(connection, masterChangelogFile, tenant);
                } catch (SQLException | LiquibaseException e) {
                    throw new InternalServerException("Error during updating of database!");
                }
            });
        }
    }
}
