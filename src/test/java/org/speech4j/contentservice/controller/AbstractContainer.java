package org.speech4j.contentservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.speech4j.contentservice.dto.response.ApiName;
import org.speech4j.contentservice.dto.response.ConfigDto;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

@ActiveProfiles("test")
@AutoConfigureMockMvc
class AbstractContainer {
    private static PostgreSQLContainer postgreSQLContainer;
    private static WireMockServer wireMockServer;

    static {
        postgreSQLContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres:12.2")
                .withPassword("postgres")
                .withUsername("postgres")
                .withDatabaseName("tenant_db")
                .withInitScript("data/init_data.sql");

        postgreSQLContainer.start();
        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        new AbstractContainer().mockRemoteService();
    }

    private void mockRemoteService() {
        //Setting of default credentials to aws S3
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("bucket_name", System.getenv("TEST_AWS_BUCKET_NAME"));
        credentials.put("access_key", System.getenv("AWS_ACCESS_KEY"));
        credentials.put("secret_key", System.getenv("AWS_SECRET_KEY"));
        credentials.put("endpoint_url", System.getenv("TEST_AWS_ENDPOINT_URL"));

        ConfigDto config =  new ConfigDto();
        config.setApiName(ApiName.AWS);
        config.setCredentials(credentials);

        JsonNode node = new ObjectMapper()
                .convertValue(Arrays.asList(config), JsonNode.class);
        //Mocking remote service
        wireMockServer = new WireMockServer(WireMockConfiguration.options()
                .port(Integer.parseInt(System.getenv("TENANT-SERVICE_PORT"))));
        wireMockServer.start();
        wireMockServer.stubFor(get(urlEqualTo("/tenants/speech4j/configs"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withJsonBody(node)
                ));
    }
}
