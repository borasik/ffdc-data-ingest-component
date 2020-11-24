package com.finastra.ffdc.dataset.component;

import java.util.Map;
import org.apache.camel.Endpoint;

import org.apache.camel.support.DefaultComponent;
import org.springframework.beans.factory.annotation.Autowired;

import com.finastra.ffdc.dataset.component.services.DataIngestionService;

@org.apache.camel.spi.annotations.Component("ffdc")
public class ffdcComponent extends DefaultComponent {

    @Autowired
    DataIngestionService dataIngestionService;    

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {             
        Endpoint endpoint = new ffdcEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
