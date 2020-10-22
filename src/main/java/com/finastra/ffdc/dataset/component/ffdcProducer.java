package com.finastra.ffdc.dataset.component;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ffdcProducer extends DefaultProducer {    
    private ffdcEndpoint endpoint;

    private static final Logger Logger = LoggerFactory.getLogger(ffdcProducer.class);

    public ffdcProducer(ffdcEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        Logger.info(this.endpoint.password);
        System.out.println(exchange.getIn().getBody());    
    }

}
