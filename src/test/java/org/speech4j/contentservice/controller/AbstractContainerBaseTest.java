package org.speech4j.contentservice.controller;

import com.amazonaws.services.kinesisvideo.model.APIName;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.speech4j.contentservice.dto.response.ApiName;
import org.speech4j.contentservice.dto.response.ConfigDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AbstractContainerBaseTest {
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
        new AbstractContainerBaseTest().mockRemoteService();
    }

    @Test
    void isRunningContainer(){
        assertTrue(postgreSQLContainer.isRunning());
        String a = System.getProperty("aws.accessKey");
    }

    private void mockRemoteService() {
        //Setting of default credentials to aws S3
        Map<String, Object> credentials = new HashMap<>();
        credentials.put("bucket_name", System.getenv("TEST_AWS_BUCKET_NAME"));
        credentials.put("access_key", System.getenv("TEST_AWS_ACCESS_KEY"));
        credentials.put("secret_key", System.getenv("TEST_AWS_SECRET_KEY"));
        credentials.put("endpoint_url", System.getenv("TEST_AWS_ENDPOINT_URL"));

        ConfigDto config =  new ConfigDto();
        config.setApiName(ApiName.AWS);
        config.setCredentials(credentials);

        List<ConfigDto> configs = new ArrayList<>();
        configs.add(config);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(configs, JsonNode.class);

        //Mocking remote service
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8082));
        wireMockServer.start();
        wireMockServer.stubFor(get(urlEqualTo("/tenants/speech4j/configs"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withJsonBody(node)
                ));
    }
}
