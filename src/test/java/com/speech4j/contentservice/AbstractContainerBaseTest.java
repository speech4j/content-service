package com.speech4j.contentservice;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class AbstractContainerBaseTest {
    static PostgreSQLContainer postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer()
                .withPassword("postgres")
                .withUsername("postgres")
                .withDatabaseName("tenant_db");
        postgreSQLContainer.start();

        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
    }

    @Test
    void isRunningContainer(){
        assertTrue(postgreSQLContainer.isRunning());
    }
}
