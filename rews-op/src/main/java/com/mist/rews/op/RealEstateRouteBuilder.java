package com.mist.rews.op;

import com.google.common.reflect.Reflection;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;

import static com.mist.rews.op.Helpers.OPERATION_TYPE;
import static org.apache.camel.util.toolbox.AggregationStrategies.flexible;

public class RealEstateRouteBuilder extends RouteBuilder {

    private static final String MEDIATE_URI = "cxf:bean:reInMediatorEndpoint";
    private static final String RESOLVE_OPERATION_TYPE_URI = "direct:resolveOperationType";

    private static final JaxbDataFormat REAL_ESTATE_DATA_FORMAT = new JaxbDataFormat(Reflection.getPackageName(com.mist.rews.services.xsd.realestate.ObjectFactory.class));

    @Override
    public void configure() throws Exception {

        from(MEDIATE_URI)
            .setBody(xpath("/").nodeResult())
            .enrich(RESOLVE_OPERATION_TYPE_URI, flexible().storeInHeader(OPERATION_TYPE))
            .unmarshal(REAL_ESTATE_DATA_FORMAT)
        ;

        from(RESOLVE_OPERATION_TYPE_URI)
            .bean(Operations.class, "parseOperation")
        ;

    }
}
