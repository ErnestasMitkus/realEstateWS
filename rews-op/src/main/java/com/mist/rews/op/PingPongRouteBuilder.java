package com.mist.rews.op;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class PingPongRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("quartz2://periodicRequest?trigger.repeatInterval=1000")
            .log(LoggingLevel.WARN, "Hello")
        ;
    }
}
