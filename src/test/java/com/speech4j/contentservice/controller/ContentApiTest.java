package com.speech4j.contentservice.controller;

import com.speech4j.contentservice.ContentServiceApplication;
import com.speech4j.contentservice.dto.request.ContentRequestDto;
import com.speech4j.contentservice.dto.response.ContentResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.speech4j.contentservice.util.DataUtil.getListOfContents;

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

    private String tenantId;

    @BeforeEach
    void setUp() throws URISyntaxException {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //Initializing of test user
        testContent = new ContentRequestDto();

        request = new HttpEntity<>(testContent, headers);

        //Populating of db
        contentList = getListOfContents();
        populateDB(contentList);
    }


    private void populateDB(List<ContentRequestDto> list) throws URISyntaxException {
        final String url = "/api/tenants/" + tenantId + "/contents";
        URI uri = new URI(url);

        ResponseEntity<ContentResponseDto> response1 = template.postForEntity(uri, new HttpEntity<>(list.get(0), headers), ContentResponseDto.class);
        ResponseEntity<ContentResponseDto> response2 = template.postForEntity(uri, new HttpEntity<>(list.get(1), headers), ContentResponseDto.class);
    }

}
