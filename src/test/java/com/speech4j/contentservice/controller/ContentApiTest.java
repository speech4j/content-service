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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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