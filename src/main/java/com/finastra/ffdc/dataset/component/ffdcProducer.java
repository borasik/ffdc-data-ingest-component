package com.finastra.ffdc.dataset.component;

import com.finastra.ffdc.dataset.component.services.DataIngestionService;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;

import com.finastra.ffdc.dataset.component.dtos.SasResponse;

public class ffdcProducer extends DefaultProducer {

    private ffdcEndpoint endpoint;
    private final DataIngestionService dataIngestionService;

    public ffdcProducer(ffdcEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
        this.dataIngestionService = new DataIngestionService();
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());
        String environment = endpoint.environment;
        String id = endpoint.id;
        String secret = endpoint.secret;
        String dataSetId = endpoint.dataSetId;
        String fileName = endpoint.fileName;

        if (environment == null || environment.trim().isEmpty()) {
            throw new Exception("Environment parameter missing");
        }

        if (id == null || id.trim().isEmpty()) {
            throw new Exception("Id parameter missing");
        }

        if (secret == null || secret.trim().isEmpty()) {
            throw new Exception("Secret parameter missing");
        }

        String accessToken = dataIngestionService.getAccessToken(environment, id, secret);

        if (accessToken == null || accessToken.trim().isEmpty()) {
            throw new Exception("Failed to Retrieve Access Token");
        }        

        SasResponse sasResponse = dataIngestionService.getSasToken(dataSetId, fileName, accessToken, environment);

        if (sasResponse.blobUrl == null || sasResponse.blobUrl.trim().isEmpty() || sasResponse.singleUseToken == null || sasResponse.singleUseToken.trim().isEmpty()) {
            throw new Exception("Failed to Retrieve SAS Token");
        }
                
        String body = exchange.getIn().getBody().toString();

        if (body == null || body.trim().isEmpty()) {
            throw new Exception("Failed to Ingest Data, Body can't be Empty");
        }        

        dataIngestionService.ingestBlob(body, sasResponse);       

        dataIngestionService.closeIngestionJob(sasResponse.jobId, environment, accessToken);
    }

}
