package com.sagatrading.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestClientService {
    @Autowired
    private RestTemplate restTemplate;

    public RestClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> ResponseEntity makeAPIRequest(String url, HttpMethod method, T reqBody) {
        HttpEntity<T> requestEntity = null;
        // Set the headers (if needed)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (!reqBody.equals(null)) {
            T requestData = reqBody;
            // Create the HTTP entity with payload and headers
            requestEntity = new HttpEntity<>(requestData, headers);
        }

        // Make the POST request
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                method,
                requestEntity,
                String.class
        );

        // Get the response
        return responseEntity;
    }
}