package org.speech4j.contentservice.s3;

import lombok.extern.slf4j.Slf4j;
import org.speech4j.contentservice.dto.response.ConfigDto;
import org.speech4j.contentservice.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static java.util.Objects.isNull;

@Component
@Slf4j
public class TenantClient {
    @Value(value = "${remote.tenant-service.url}")
    private String remoteServiceURL;
    private RestTemplate template;

    public TenantClient() {
        this.template = new RestTemplate();
    }

    public List<ConfigDto> getAllConfigByTenantId(String tenantId) {
        String url = remoteServiceURL + tenantId + "/configs";
        ResponseEntity<List<ConfigDto>> response =
                template.exchange(url, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<ConfigDto>>(){});

        log.debug("CONTENT-SERVICE: Configs with [ tenantId: {}] were successfully got from tenant-service!", tenantId);

        if (!isNull(response.getBody()) && !response.getBody().isEmpty()){
            return response.getBody();
        }else {
            throw new EntityNotFoundException("Config not found!");
        }

    }
}
