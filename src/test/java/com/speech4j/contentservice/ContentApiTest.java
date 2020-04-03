package com.speech4j.contentservice;

import com.speech4j.contentservice.controller.AbstractContainerBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(classes = ContentServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContentApiTest extends AbstractContainerBaseTest {

    @Autowired
    private TestRestTemplate template;

}
