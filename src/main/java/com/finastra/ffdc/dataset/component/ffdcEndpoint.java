package com.finastra.ffdc.dataset.component;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;

/**
 * ffdc component which does bla bla.
 *
 * TODO: Update one line description above what the component does.
 */
@UriEndpoint(firstVersion = "1.0-SNAPSHOT", scheme = "ffdc", title = "ffdc", syntax="ffdc:name",
             consumerClass = ffdcConsumer.class, label = "custom")
public class ffdcEndpoint extends DefaultEndpoint {
    @UriPath @Metadata(required = true)
    private String name;
    @UriParam(defaultValue = "10")
    private int option = 10;

    public String password;

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    private static final Logger Logger = LoggerFactory.getLogger(ffdcEndpoint.class);

    public ffdcEndpoint() {
    }

    public ffdcEndpoint(String uri, ffdcComponent component) {
        super(uri, component);
    }

    public Producer createProducer() throws Exception {
        Logger.info("createProducer");
        return new ffdcProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        Logger.info("createConsumer");
        Consumer consumer = new ffdcConsumer(this, processor);
        configureConsumer(consumer);
        return consumer;
    }

    /**
     * Some description of this option, and what it does
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Some description of this option, and what it does
     */
    public void setOption(int option) {
        this.option = option;
    }

    public int getOption() {
        return option;
    }

    public ExecutorService createExecutor() {
        // TODO: Delete me when you implementy your custom component
        return getCamelContext().getExecutorServiceManager().newSingleThreadExecutor(this, "ffdcConsumer");
    }
}
