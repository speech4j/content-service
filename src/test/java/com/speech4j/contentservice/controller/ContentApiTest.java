package com.speech4j.contentservice.controller;

import com.speech4j.contentservice.ContentServiceApplication;
import com.speech4j.contentservice.dto.handler.ResponseMessageDto;
import com.speech4j.contentservice.dto.request.ContentRequestDto;
import com.speech4j.contentservice.dto.request.TagDto;
import com.speech4j.contentservice.dto.response.ContentResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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
import java.util.Set;

import static com.speech4j.contentservice.util.DataUtil.getListOfContents;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = ContentServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContentApiTest extends AbstractContainerBaseTest {
    @Autowired
    private TestRestTemplate template;

    private HttpHeaders headers;
    private HttpEntity<ContentRequestDto> request;
    private ContentRequestDto testContent;

    private final String exceptionMessage = "Content not found!";
    private List<ContentRequestDto> contentList;
    private List<ContentResponseDto> contentListResponse;

    private String tenantId = "111";
    private String contentId;

    @BeforeEach
    void setUp() throws URISyntaxException {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //Initializing of test content
        testContent = new ContentRequestDto();
        testContent.setContentUrl("https://www.youtube.com/watch?v=LCDd433SdJE");
        testContent.setTags(Arrays.asList(new TagDto("#nightcore"), new TagDto("#nightcoresong")));
        testContent.setTranscript(
                        "I went down to the crossroads, fell down on my knees\n" +
                        "Down to the crossroads fell down on my knees\n" +
                        "Asked the Lord above for mercy, \"Take me, if you please\"");

        request = new HttpEntity<>(testContent, headers);

        //Populating of db
        contentList = getListOfContents();
        contentListResponse = populateDB(contentList);
        contentId = contentListResponse.get(0).getContentGuid();
    }

    @Test
    public void createEntityTest_successFlow() {
        final String url = "/api/tenants/" +  tenantId + "/contents";

        ResponseEntity<ContentResponseDto> response =
                this.template.exchange(url, HttpMethod.POST, request, ContentResponseDto.class);

        //Verify request succeed
        assertEquals(201, response.getStatusCodeValue());
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void createEntityTest_unsuccessFlow() {
        final String url =  "/api/tenants/" +  tenantId + "/contents";

        //Make entity null
        request = new HttpEntity<>(null, headers);

        ResponseEntity<ResponseMessageDto> response =
                this.template.exchange(url, HttpMethod.POST, request, ResponseMessageDto.class);

        //Verify this exception because of validation null entity can't be accepted by controller
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createEntityTestWithMissedRequiredField_unsuccessFlow() {
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
    public void findByIdTest_successFlow() {
        request = new HttpEntity<>(headers);
        ResponseEntity<ContentResponseDto> response
                = template.exchange("/api/tenants/" +  tenantId + "/contents/" + contentId, HttpMethod.GET, request, ContentResponseDto.class);

        System.out.println(response.getBody());

        //Verify request succeed
        assertEquals(200, response.getStatusCodeValue());
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    public void findByIdTest_unsuccessFlow() {
        request = new HttpEntity<>(headers);
        ResponseEntity<ResponseMessageDto> response
                = template.exchange("/api/tenants/" +  tenantId + "/contents/" + 0, HttpMethod.GET, request, ResponseMessageDto.class);

        //Verify request not succeed
        checkEntityNotFoundException(response);
    }

    @Test
    public void updateEntityTest_successFlow() {
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
    public void updateEntityTest_unsuccessFlow() {
        final String url = "/api/tenants/" +  tenantId + "/contents/" + 0;

        testContent.setTranscript("New test");
        request = new HttpEntity<>(testContent, headers);

        ResponseEntity<ResponseMessageDto> response =
                this.template.exchange(url, HttpMethod.PUT, request, ResponseMessageDto.class);

        //Verify request not succeed
        checkEntityNotFoundException(response);
    }

    @Test
    public void deleteEntity_successFlow() {
        final String url = "/api/tenants/" +  tenantId + "/contents/" + contentId;

        request = new HttpEntity<>(headers);
        ResponseEntity<ResponseMessageDto> response
                = template.exchange(url, HttpMethod.DELETE, request, ResponseMessageDto.class);

        //Checking if entity was deleted
        assertEquals(204, response.getStatusCodeValue());

    }

    @Test
    public void deleteEntity_unsuccessFlow() {
        final String url = "/api/tenants/" +  tenantId + "/contents/" + 0;

        request = new HttpEntity<>(headers);
        ResponseEntity<ResponseMessageDto> response
                = template.exchange(url, HttpMethod.DELETE, request, ResponseMessageDto.class);

        //Verify request isn't succeed
        checkEntityNotFoundException(response);
    }

    @Test
    public void findByTagsTest_successFlow() {
        request = new HttpEntity<>(headers);
        String url = "/api/tenants/" +  tenantId + "/contents";
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(url)
                .queryParam("tagNames", "#nightcore, #music");

        ResponseEntity<Set> response = template.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET, request, Set.class);

        //Verify request succeed
        assertEquals(200, response.getStatusCodeValue());
        assertThat(response.getBody()).isNotNull();
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void findByTagsTest_unsuccessFlow() {
        request = new HttpEntity<>(headers);
        String url = "/api/tenants/" +  tenantId + "/contents";
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(url)
                .queryParam("tagNames", "#fakeTag");

        ResponseEntity<ResponseMessageDto> response = template.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET, request, ResponseMessageDto.class);

        //Verify request isn't succeed
        checkEntityNotFoundException(response);
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

}
