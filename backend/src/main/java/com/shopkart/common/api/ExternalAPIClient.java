package com.shopkart.common.api;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ExternalAPIClient {

    private final RestClient restClient;

    public ExternalAPIClient() {
        this.restClient = RestClient.create();
    }

    public <T> T invokePostAPI(String url, Object payload, Class<T> responseType) {
        return restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(responseType);
    }
}
