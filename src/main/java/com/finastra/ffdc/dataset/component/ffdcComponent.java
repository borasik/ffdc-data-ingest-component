package com.finastra.ffdc.dataset.component;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;

import org.apache.camel.support.DefaultComponent;

@org.apache.camel.spi.annotations.Component("ffdc")
public class ffdcComponent extends DefaultComponent {
    
    private static final Logger Logger = LoggerFactory.getLogger(ffdcComponent.class);

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        Logger.info("uri: {}", uri);
        Logger.info("remaining: {}", remaining);
        Logger.info("parameters: {}", parameters);
        Endpoint endpoint = new ffdcEndpoint(uri, this);
        setProperties(endpoint, parameters);
        return endpoint;
    }
}
