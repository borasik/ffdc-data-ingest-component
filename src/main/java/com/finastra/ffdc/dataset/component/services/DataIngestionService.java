package com.finastra.ffdc.dataset.component.services;

import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import java.text.MessageFormat;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finastra.ffdc.dataset.component.dtos.SasResponse;
import com.finastra.ffdc.dataset.component.dtos.TokenDto;
import com.google.gson.Gson;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataIngestionService {

    private static final Logger Logger = LoggerFactory.getLogger(DataIngestionService.class);

    public String getAccessToken(String environment, String id, String secret)
            throws JsonMappingException, JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        String body = MessageFormat.format("grant_type=client_credentials&scope=openid&client_id={0}&client_secret={1}",
                id, secret);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("application", "x-www-form-urlencoded"));

        HttpEntity<Object> entity = new HttpEntity<Object>(body, requestHeaders);

        ResponseEntity<String> response = restTemplate.exchange(
                MessageFormat.format("https://api.{0}.fusionfabric.cloud/login/v1/sandbox/oidc/token", environment),
                HttpMethod.POST, entity, String.class);

        TokenDto tokenDto = mapper.readValue(response.getBody(), TokenDto.class);        

        return tokenDto.access_token;
    }

    public SasResponse getSasToken(String dataSetId, String fileName, String token, String environment) {

        RestTemplate restTemplate = new RestTemplate();

        HashMap<String, String> body = new HashMap<String, String>();
        body.put("dataSetId", dataSetId);
        body.put("protocol", "ADLv2");
        body.put("fileName", fileName);

        String json = new Gson().toJson(body);        
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(new MediaType("application", "json"));
        requestHeaders.set("Authorization", MessageFormat.format("Bearer {0}", token));
        HttpEntity<Object> entity = new HttpEntity<Object>(json, requestHeaders);

        ResponseEntity<String> accessToken = restTemplate.exchange(
                MessageFormat.format("https://api.{0}.fusionfabric.cloud/ingest/v1/ingestions", environment),
                HttpMethod.POST, entity, String.class);

        SasResponse sasResponse = new Gson().fromJson(accessToken.getBody(), SasResponse.class);        
        return sasResponse;
    }

    public void ingestBlob(String blob, SasResponse sasResponse) throws URISyntaxException {        
        
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders requestHeaders = new HttpHeaders();        
        requestHeaders.set("Content-Type", "application/json");
        requestHeaders.set("x-ms-blob-type", "BlockBlob");
        HttpEntity<Object> entity = new HttpEntity<Object>(blob, requestHeaders);        
        URI Uri = new URI(sasResponse.blobUrl + "?" + sasResponse.singleUseToken);
        Logger.info("Ingesting Data to: {}", sasResponse.blobUrl);
        ResponseEntity<Object> response = restTemplate.exchange(Uri, HttpMethod.PUT, entity, null);                
    }

    public void closeIngestionJob(String ingestionJob, String environment, String token) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();

        HashMap<String, String> body = new HashMap<String, String>();
        body.put("status", "COMPLETE_UPLOAD");
        String jsonBody = new Gson().toJson(body);

        HttpHeaders requestHeaders = new HttpHeaders();        
        requestHeaders.set("Content-Type", "application/json");
        requestHeaders.set("Authorization", MessageFormat.format("Bearer {0}", token));
        HttpEntity<Object> entity = new HttpEntity<Object>(jsonBody, requestHeaders);     

        URI Uri = new URI(MessageFormat.format("https://api.{0}.fusionfabric.cloud/ingest/v1/ingestions/{1}/status", environment, ingestionJob));
        Logger.info("Closing Ingestion Job #: {}", ingestionJob);

        ResponseEntity<Object> response = restTemplate.exchange(Uri, HttpMethod.PUT, entity, null);    
    }
}
