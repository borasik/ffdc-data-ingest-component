package com.finastra.ffdc.dataset.component;

import com.finastra.ffdc.dataset.component.services.DataIngestionService;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ffdcProducer extends DefaultProducer {        

    private ffdcEndpoint endpoint;
    private final DataIngestionService dataIngestionService;
    private static final Logger Logger = LoggerFactory.getLogger(ffdcProducer.class);

    public ffdcProducer(ffdcEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
        this.dataIngestionService = new DataIngestionService();        
    }

    public void process(Exchange exchange) throws Exception {    
        String environment = endpoint.environment;
        String id = endpoint.id;
        String secret = endpoint.secret;
        String dataSetId = endpoint.dataSetId;
        String fileName = endpoint.fileName;

        if(environment == null || environment.trim().isEmpty()){
            throw new Exception("Environment parameter missing");
        }

        if(id == null || id.trim().isEmpty()){
            throw new Exception("Id parameter missing");
        }

        if(secret == null || secret.trim().isEmpty()){
            throw new Exception("Secret parameter missing");
        }

        String accessToken = dataIngestionService.getAccessToken(environment, id, secret);

        if(accessToken == null || accessToken.trim().isEmpty()){
            throw new Exception("Can't Retrieve Access Token from: {}");
        }
        Logger.info(accessToken);

        String sas = dataIngestionService.getSasToken(dataSetId, fileName, accessToken, environment);

        Logger.info(sas);

        System.out.println(exchange.getIn().getBody());
    }

}
