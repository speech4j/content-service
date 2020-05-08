package org.speech4j.contentservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class AbstractContainerBaseTest {
    static PostgreSQLContainer postgreSQLContainer;

    static {
        postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres:12.2")
                .withPassword("postgres")
                .withUsername("postgres")
                .withDatabaseName("tenant_db")
                .withInitScript("data/init_data.sql");

        postgreSQLContainer.start();

        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
    }

    @Test
    void isRunningContainer(){
        assertTrue(postgreSQLContainer.isRunning());
    }
}
