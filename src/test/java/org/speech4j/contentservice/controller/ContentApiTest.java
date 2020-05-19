package org.speech4j.contentservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.speech4j.contentservice.ContentServiceApplication;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.speech4j.contentservice.util.DataUtil.getListOfContents;
import static org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils.randomNumeric;


@AutoConfigureMockMvc
@SpringBootTest(classes = ContentServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContentApiTest extends AbstractContainerBaseTest {
    @Autowired
    private TestRestTemplate template;
    private HttpHeaders headers = new HttpHeaders();
    private HttpEntity<ContentRequestDto> request;
    private final String exceptionMessage = "Content not found!";
    private static List<ContentResponseDto> createdContents;


    @BeforeEach
    public void setUp() throws URISyntaxException{
        headers.setContentType(MediaType.APPLICATION_JSON);
        //Populating of db
        createdContents = populateDB(getListOfContents());
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void createContentTest_successFlow(
            Map<String, String> tenantIds,
            ContentRequestDto requestDto
    ) {
        final String url = "/api/tenants/" +  tenantIds.get("real") + "/contents";
        request = new HttpEntity<>(requestDto, headers);
        ResponseEntity<ContentResponseDto> response =
                this.template.exchange(url, HttpMethod.POST, request, ContentResponseDto.class);

        //Verify request succeed
        assertEquals(201, response.getStatusCodeValue());
        assertThat(requestDto).isEqualToIgnoringGivenFields(response.getBody(), "id");
        assertThat(response.getBody()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void createContentTest_unsuccessFlow( Map<String, String> tenantIds) {
        final String url =  "/api/tenants/" +  tenantIds.get("real") + "/contents";
        //Make entity null
        request = new HttpEntity<>(null, headers);
        ResponseEntity<ResponseMessageDto> response =
                this.template.exchange(url, HttpMethod.POST, request, ResponseMessageDto.class);

        //Verify this exception because of validation null entity can't be accepted by controller
        assertEquals(400, response.getStatusCodeValue());
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void createContentTestWithMissedRequiredField_unsuccessFlow(
            Map<String, String> tenantIds,
            ContentRequestDto requestDto
    ) {
        final String url = "/api/tenants/" +  tenantIds.get("real") + "/contents";
        requestDto.setTranscript(null);
        request = new HttpEntity<>(requestDto, headers);
        ResponseEntity<ResponseMessageDto> response =
                this.template.exchange(url, HttpMethod.POST, request, ResponseMessageDto.class);

        //Verify this exception because of validation missed field
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Validation failed for object='contentRequestDto'. Error count: 1", response.getBody().getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void createContentTestWitFakeTenantId_unsuccessFlow(
            Map<String, String> tenantIds,
            ContentRequestDto requestDto
    ) {
        final String url = "/api/tenants/" +  tenantIds.get("fake") + "/contents";
        request = new HttpEntity<>(requestDto, headers);
        ResponseEntity<ResponseMessageDto> response =
                this.template.exchange(url, HttpMethod.POST, request, ResponseMessageDto.class);

        //Verify this exception because of fake tenant id
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Could not open JPA EntityManager for transaction; " +
                "nested exception is org.speech4j.contentservice.exception.TenantNotFoundException: " +
                "Tenant with specified identifier [0] not found!", response.getBody().getMessage());
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void createContentTestWithNullPathVariable_unsuccessFlow(
            Map<String, String> tenantIds,
            ContentRequestDto requestDto
    ) {
        final String url = "/api/tenants/" +  tenantIds.get("null") + "/contents";
        request = new HttpEntity<>(requestDto, headers);
        ResponseEntity<ResponseMessageDto> response =
                this.template.exchange(url, HttpMethod.POST, request, ResponseMessageDto.class);
        //Verify this exception because of null tenant id
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Could not open JPA EntityManager for transaction; " +
                "nested exception is org.speech4j.contentservice.exception.TenantNotFoundException: " +
                "Tenant with specified identifier [null] not found!", response.getBody().getMessage());
    }


    @ParameterizedTest
    @MethodSource("provideTestData")
    public void findByIdTest_successFlow(
            Map<String, String> tenantIds,
            ContentRequestDto requestDto,
            Map<String, String> contentIds
    ) {
        final String url = "/api/tenants/" +  tenantIds.get("real") + "/contents/" + contentIds.get("real");
        ResponseEntity<ContentResponseDto> response
                = template.exchange(url, HttpMethod.GET, null, ContentResponseDto.class);

        //Verify request succeed
        assertEquals(200, response.getStatusCodeValue());
        assertThat(requestDto).isEqualToIgnoringGivenFields(response.getBody(), "id");
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void findByIdTest_unsuccessFlow(
            Map<String, String> tenantIds,
            ContentRequestDto requestDto,
            Map<String, String> contentIds
    ) {
        final String url = "/api/tenants/" +  tenantIds.get("real") + "/contents/" + contentIds.get("fake");
        ResponseEntity<ResponseMessageDto> response
                = template.exchange(url, HttpMethod.GET, null, ResponseMessageDto.class);

        //Verify request not succeed
        checkEntityNotFoundException(response);
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void updateContentTest_successFlow(
            Map<String, String> tenantIds,
            ContentRequestDto requestDto,
            Map<String, String> contentIds
    ) {
        final String url = "/api/tenants/" +  tenantIds.get("real") + "/contents/" + contentIds.get("real");
        requestDto.setTranscript("New test");
        request = new HttpEntity<>(requestDto, headers);
        ResponseEntity<ContentResponseDto> response =
                this.template.exchange(url, HttpMethod.PUT, request, ContentResponseDto.class);

        //Verify request succeed
        assertEquals(200, response.getStatusCodeValue());
        assertThat(requestDto).isEqualToIgnoringGivenFields(response.getBody(), "id");
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void updateContentTest_unsuccessFlow(
            Map<String, String> tenantIds,
            ContentRequestDto requestDto,
            Map<String, String> contentIds
    ) {
        final String url = "/api/tenants/" +  tenantIds.get("real") + "/contents/" + contentIds.get("fake");
        requestDto.setTranscript("New test");
        request = new HttpEntity<>(requestDto, headers);
        ResponseEntity<ResponseMessageDto> response =
                this.template.exchange(url, HttpMethod.PUT, request, ResponseMessageDto.class);

        //Verify request not succeed
        checkEntityNotFoundException(response);
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void deleteContent_successFlow(
            Map<String, String> tenantIds,
            ContentRequestDto requestDto,
            Map<String, String> contentIds
    ) {
        final String url = "/api/tenants/" +  tenantIds.get("real") + "/contents/" + contentIds.get("real");

        ResponseEntity<ResponseMessageDto> response
                = template.exchange(url, HttpMethod.DELETE, null, ResponseMessageDto.class);

        //Checking if entity was deleted
        assertEquals(204, response.getStatusCodeValue());

    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void deleteEntity_unsuccessFlow(
            Map<String, String> tenantIds,
            ContentRequestDto requestDto,
            Map<String, String> contentIds
    ) {
        final String url = "/api/tenants/" +  tenantIds.get("real") + "/contents/" + contentIds.get("fake");
        ResponseEntity<ResponseMessageDto> response
                = template.exchange(url, HttpMethod.DELETE, null, ResponseMessageDto.class);

        //Verify request isn't succeed
        checkEntityNotFoundException(response);
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void findByTagsTest_successFlow(Map<String, String> tenantIds) {
        String url = "/api/tenants/" +  tenantIds.get("real") + "/contents";
        URI uri = UriComponentsBuilder.fromPath(url)
                .queryParam("tagNames", "#nightcore, #music")
                .build().encode().toUri();
        ResponseEntity<PagedModel<ContentResponseDto>> response = template.exchange(uri,
                HttpMethod.GET, null, new ParameterizedTypeReference<PagedModel<ContentResponseDto>>(){});

        //Verify request succeed
        assertEquals(200, response.getStatusCodeValue());
        assertThat(response.getBody()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void findByTagsTest_unsuccessFlow(Map<String, String> tenantIds) {
        String url = "/api/tenants/" +  tenantIds.get("real") + "/contents";
        URI uri = UriComponentsBuilder.fromPath(url)
                .queryParam("tagNames", "#fakeTag")
                .build().encode().toUri();
        ResponseEntity<ResponseMessageDto> response = template.exchange(uri,
                HttpMethod.GET, null, ResponseMessageDto.class);

        //Verify request isn't succeed
        checkEntityNotFoundException(response);
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void findByTagsPageableTest_successFlowWhen200IsReceived(Map<String, String> tenantIds) {
        String url = "/api/tenants/" +  tenantIds.get("real") + "/contents";
        URI uri = UriComponentsBuilder.fromPath(url)
                .queryParam("tagNames", "#nightcore, #music")
                .queryParam("page", 0)
                .queryParam("size",2)
                .build().encode().toUri();

        ResponseEntity<PagedModel<ContentResponseDto>> response = template.exchange(uri,
                HttpMethod.GET, null , new ParameterizedTypeReference<PagedModel<ContentResponseDto>>(){});

        //Verify request succeed
        assertEquals(200, response.getStatusCodeValue());
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    public void findByTagsPageableTest_unsuccessFlowWhen404IsReceived(Map<String, String> tenantIds) {
        String url = "/api/tenants/" +  tenantIds.get("real") + "/contents";
        URI uri = UriComponentsBuilder.fromPath(url)
                .queryParam("tagNames", "#nightcore, #music")
                .queryParam("page", randomNumeric(5))
                .queryParam("size",1)
                .build().encode().toUri();
        ResponseEntity<ResponseMessageDto> response = template.exchange(uri,
                HttpMethod.GET, null, ResponseMessageDto.class);

        //Verify request isn't succeed
        checkEntityNotFoundException(response);
    }

    private void checkEntityNotFoundException(ResponseEntity<ResponseMessageDto> response){
        assertEquals(404, response.getStatusCodeValue());
        assertEquals(exceptionMessage, response.getBody().getMessage());
    }

    private List<ContentResponseDto> populateDB(List<ContentRequestDto> list) throws URISyntaxException {
        final String url = "/api/tenants/speech4j/contents";
        URI uri = new URI(url);
        List<ContentResponseDto> contentListResponse = new ArrayList<>();
        ResponseEntity<ContentResponseDto> response1 = template.postForEntity(uri, new HttpEntity<>(list.get(0), headers), ContentResponseDto.class);
        ResponseEntity<ContentResponseDto> response2 = template.postForEntity(uri, new HttpEntity<>(list.get(1), headers), ContentResponseDto.class);

        contentListResponse.addAll(Arrays.asList(response1.getBody(),response2.getBody()));
        return contentListResponse;
    }

    private static Stream<Arguments> provideTestData() {
        ContentRequestDto requestDto = getListOfContents().get(0);
        Map<String, String> tenantIds = new HashMap();
        tenantIds.put("real", "speech4j");
        tenantIds.put("fake", "0");
        tenantIds.put("null", "null");
        Map<String, String> contentIds = new HashMap();
        contentIds.put("real", createdContents.get(0).getContentGuid());
        contentIds.put("fake", "0");

        return Stream.of(
                Arguments.of(tenantIds, requestDto, contentIds)
        );
    }
}
