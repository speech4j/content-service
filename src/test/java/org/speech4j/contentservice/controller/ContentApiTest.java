package org.speech4j.contentservice.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.speech4j.contentservice.ContentServiceApplication;
import org.speech4j.contentservice.dto.TagDto;
import org.speech4j.contentservice.dto.handler.ResponseMessageDto;
import org.speech4j.contentservice.dto.request.ContentRequestDto;
import org.speech4j.contentservice.dto.response.ContentResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.speech4j.contentservice.util.DataUtil.getListOfContents;
import static org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils.randomNumeric;


@SpringBootTest(classes = ContentServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ContentApiTest extends AbstractContainerBaseTest {
    @Autowired
    private TestRestTemplate template;

    private HttpHeaders headers = new HttpHeaders();
    private HttpEntity<ContentRequestDto> request;
    private ContentRequestDto testContent;

    private final String exceptionMessage = "Content not found!";
    private List<ContentRequestDto> contentList;
    private List<ContentResponseDto> contentListResponse;
    private String tenantId = "speech4j";
    private String contentId;

    private WireMockServer wireMockServer;

    @BeforeEach
    public void setUp() throws URISyntaxException{
        //Mocking the remote service
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8082));
        wireMockServer.start();
        mockRemoteService();

        headers.setContentType(MediaType.APPLICATION_JSON);
        //Initializing of test content
        testContent = new ContentRequestDto();
        testContent.setContentUrl("https://www.youtube.com/watch?v=LCDd433SdJE");
        testContent.setTags(Arrays.asList(new TagDto("#nightcore"), new TagDto("#nightcoresong")));
        testContent.setTranscript(
                        "I went down to the crossroads, fell down on my knees\n" +
                        "Down to the crossroads fell down on my knees\n" +
                        "Asked the Lord above for mercy, " +
                        "Take me, if you please");

        request = new HttpEntity<>(testContent, headers);

        //Populating of db
        contentList = getListOfContents();
        contentListResponse = populateDB(contentList);
        contentId = contentListResponse.get(0).getContentGuid();
    }

    @AfterEach
    public void noMoreWireMock() {
        wireMockServer.stop();
        wireMockServer = null;
    }

    @Test
    public void createContentTest_successFlow() {
        final String url = "/api/tenants/" +  tenantId + "/contents";

        ResponseEntity<ContentResponseDto> response =
                this.template.exchange(url, HttpMethod.POST, request, ContentResponseDto.class);

        //Verify request succeed
        assertEquals(201, response.getStatusCodeValue());
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void createContentTest_unsuccessFlow() {
        final String url =  "/api/tenants/" +  tenantId + "/contents";

        //Make entity null
        request = new HttpEntity<>(null, headers);

        ResponseEntity<ResponseMessageDto> response =
                this.template.exchange(url, HttpMethod.POST, request, ResponseMessageDto.class);

        //Verify this exception because of validation null entity can't be accepted by controller
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createContentTestWithMissedRequiredField_unsuccessFlow() {
        final String url = "/api/tenants/" +  tenantId + "/contents";

        testContent.setTranscript(null);
        request = new HttpEntity<>(testContent, headers);

        ResponseEntity<ResponseMessageDto> response =
                this.template.exchange(url, HttpMethod.POST, request, ResponseMessageDto.class);

        //Verify this exception because of validation missed field
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Validation failed for object='contentRequestDto'. Error count: 1", response.getBody().getMessage());
    }

    @Test
    public void createContentTestWitFakeTenantId_unsuccessFlow() {
        final String url = "/api/tenants/" +  0 + "/contents";

        request = new HttpEntity<>(testContent, headers);

        ResponseEntity<ResponseMessageDto> response =
                this.template.exchange(url, HttpMethod.POST, request, ResponseMessageDto.class);

        //Verify this exception because of validation missed field
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Could not open JPA EntityManager for transaction; " +
                "nested exception is org.speech4j.contentservice.exception.TenantNotFoundException: " +
                "Tenant with specified identifier [0] not found!", response.getBody().getMessage());
    }


    @Test
    public void findByIdTest_successFlow() {
        ResponseEntity<ContentResponseDto> response
                = template.exchange("/api/tenants/" +  tenantId + "/contents/" + contentId, HttpMethod.GET, null, ContentResponseDto.class);

        //Verify request succeed
        assertEquals(200, response.getStatusCodeValue());
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void findByIdTest_unsuccessFlow() {
        ResponseEntity<ResponseMessageDto> response
                = template.exchange("/api/tenants/" +  tenantId + "/contents/" + 0, HttpMethod.GET, null, ResponseMessageDto.class);

        //Verify request not succeed
        checkEntityNotFoundException(response);
    }

    @Test
    public void updateContentTest_successFlow() {
        final String url = "/api/tenants/" +  tenantId + "/contents/" + contentId;

        testContent.setTranscript("New test");
        request = new HttpEntity<>(testContent, headers);

        ResponseEntity<ContentResponseDto> response =
                this.template.exchange(url, HttpMethod.PUT, request, ContentResponseDto.class);

        //Verify request succeed
        assertEquals(200, response.getStatusCodeValue());
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void updateContentTest_unsuccessFlow() {
        final String url = "/api/tenants/" +  tenantId + "/contents/" + 0;

        testContent.setTranscript("New test");
        request = new HttpEntity<>(testContent, headers);

        ResponseEntity<ResponseMessageDto> response =
                this.template.exchange(url, HttpMethod.PUT, request, ResponseMessageDto.class);

        //Verify request not succeed
        checkEntityNotFoundException(response);
    }

    @Test
    public void deleteContent_successFlow() {
        final String url = "/api/tenants/" +  tenantId + "/contents/" + contentId;

        ResponseEntity<ResponseMessageDto> response
                = template.exchange(url, HttpMethod.DELETE, null, ResponseMessageDto.class);

        //Checking if entity was deleted
        assertEquals(204, response.getStatusCodeValue());

    }

    @Test
    public void deleteEntity_unsuccessFlow() {
        final String url = "/api/tenants/" +  tenantId + "/contents/" + 0;

        ResponseEntity<ResponseMessageDto> response
                = template.exchange(url, HttpMethod.DELETE, null, ResponseMessageDto.class);

        //Verify request isn't succeed
        checkEntityNotFoundException(response);
    }

    @Test
    public void findByTagsTest_successFlow() {
        String url = "/api/tenants/" +  tenantId + "/contents";
        URI uri = UriComponentsBuilder.fromPath(url)
                .queryParam("tagNames", "#nightcore, #music")
                .build().encode().toUri();

        ResponseEntity<PagedModel<ContentResponseDto>> response = template.exchange(
                uri,
                HttpMethod.GET, null, new ParameterizedTypeReference<PagedModel<ContentResponseDto>>(){});

        //Verify request succeed
        assertEquals(200, response.getStatusCodeValue());
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void findByTagsTest_unsuccessFlow() {
        String url = "/api/tenants/" +  tenantId + "/contents";
        URI uri = UriComponentsBuilder.fromPath(url)
                .queryParam("tagNames", "#fakeTag")
                .build().encode().toUri();

        ResponseEntity<ResponseMessageDto> response = template.exchange(
                uri,
                HttpMethod.GET, null, ResponseMessageDto.class);

        //Verify request isn't succeed
        checkEntityNotFoundException(response);
    }

    @Test
    public void findByTagsPageableTest_successFlowWhen200IsReceived() {
        String url = "/api/tenants/" +  tenantId + "/contents";
        URI uri = UriComponentsBuilder.fromPath(url)
                .queryParam("tagNames", "#nightcore, #music")
                .queryParam("page", 0)
                .queryParam("size",2)
                .build().encode().toUri();

        ResponseEntity<PagedModel<ContentResponseDto>> response = template.exchange(
                uri,
                HttpMethod.GET, null , new ParameterizedTypeReference<PagedModel<ContentResponseDto>>(){});

        //Verify request succeed
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void findByTagsPageableTest_unsuccessFlowWhen404IsReceived() {
        String url = "/api/tenants/" +  tenantId + "/contents";
        URI uri = UriComponentsBuilder.fromPath(url)
                .queryParam("tagNames", "#nightcore, #music")
                .queryParam("page", randomNumeric(5))
                .queryParam("size",1)
                .build().encode().toUri();

        ResponseEntity<ResponseMessageDto> response = template.exchange(
                uri,
                HttpMethod.GET, null, ResponseMessageDto.class);

        //Verify request isn't succeed
        checkEntityNotFoundException(response);
    }

    private void assertExpectedContentResource(ContentResponseDto content) {
        assertThat(content.hasLink("self")).isTrue();
    }

    private void checkEntityNotFoundException(ResponseEntity<ResponseMessageDto> response){
        assertEquals(404, response.getStatusCodeValue());
        assertEquals(exceptionMessage, response.getBody().getMessage());
    }

    private List<ContentResponseDto> populateDB(List<ContentRequestDto> list) throws URISyntaxException {
        final String url = "/api/tenants/" + tenantId + "/contents";
        URI uri = new URI(url);
        List<ContentResponseDto> contentListResponse = new ArrayList<>();
        ResponseEntity<ContentResponseDto> response1 = template.postForEntity(uri, new HttpEntity<>(list.get(0), headers), ContentResponseDto.class);
        ResponseEntity<ContentResponseDto> response2 = template.postForEntity(uri, new HttpEntity<>(list.get(1), headers), ContentResponseDto.class);

        contentListResponse.add(response1.getBody());
        contentListResponse.add(response2.getBody());

        return contentListResponse;
    }

    private void mockRemoteService() {
        wireMockServer.stubFor(get(urlEqualTo("/tenants/" + tenantId + "/configs"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("response.json")
                ));
    }

}
