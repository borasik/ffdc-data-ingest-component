package com.finastra.ffdc.dataset.component;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ffdcComponentTest extends CamelTestSupport {

    private final EventBusHelper eventBusHelper = EventBusHelper.getInstance();

    @Test
    public void testffdc() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(5);

        // Trigger events to subscribers
        simulateEventTrigger();

        mock.await();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("timer://foo?fixedRate=true&period=1000&repeatCount=1")
                .process(exchange -> exchange.getIn().setBody(
                    "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
                ))             
                // from("file://C:/Users/ab5645/Downloads/IMG-155533.jpg")
                  .to("ffdc://bar?environment=lobdev&id=1988806a-8113-4cba-ab81-22b5eada6d99&secret=5a13861f-f1fd-441f-9683-72cee3f9b99e&dataSetId=trades-v1-27a8371b-9317-43ed-82c0-39835cf1ec03&fileName=2019-02-13T23:00:00.000Z.json")
                  .to("mock:result");
            }
        };
    }

    private void simulateEventTrigger() {
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                final Date now = new Date();
                // publish events to the event bus
                eventBusHelper.publish(now);
            }
        };

        new Timer().scheduleAtFixedRate(task, 1000L, 1000L);
    }
}
