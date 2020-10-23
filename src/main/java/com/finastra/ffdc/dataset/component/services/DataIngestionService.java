package com.finastra.ffdc.dataset.component.services;

import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finastra.ffdc.dataset.component.dtos.TokenDto;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataIngestionService {

    private static final Logger Logger = LoggerFactory.getLogger(DataIngestionService.class);

    public String getAccessToken(String environment, String id, String secret)
            throws JsonMappingException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        String body = MessageFormat.format("grant_type=client_credentials&scope=openid&client_id={0}&client_secret={1}", id, secret);      
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("application", "x-www-form-urlencoded"));

        HttpEntity<Object> entity = new HttpEntity<Object>(body, requestHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
            MessageFormat.format("https://api.{0}.fusionfabric.cloud/login/v1/sandbox/oidc/token", environment), HttpMethod.POST, entity,
            String.class);        

       TokenDto tokenDto = mapper.readValue(response.getBody(), TokenDto.class);

        Logger.info("TOEKN: " + tokenDto.access_token);

        return tokenDto.access_token;
    }


    public String getSasToken(String dataSetId, String fileName, String token, String environment) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("dataSetId", dataSetId);
        body.add("protocol", "ADLv2");
        body.add("fileName", fileName);

        Logger.info("token {}", token);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("application", "json"));
        requestHeaders.set("Authorization", MessageFormat.format("Bearer {0}", token));
        HttpEntity<Object> entity = new HttpEntity<Object>(body.toString(), requestHeaders);

        ResponseEntity<String> accessToken = restTemplate.exchange(
            MessageFormat.format("https://api.{0}.fusionfabric.cloud/ingest/v1/ingestions", environment), HttpMethod.POST, entity,
                String.class);

        return accessToken.getBody();
    }

}
