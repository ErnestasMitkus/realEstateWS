package com.mist.rews.op;

import com.google.common.reflect.Reflection;
import com.mist.rews.RealEstateFaults;
import com.mist.rews.services.xsd.realestate.FaultType;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;

import static com.mist.rews.op.helpers.Helpers.OPERATION_TYPE;
import static org.apache.camel.util.toolbox.AggregationStrategies.flexible;

public class RealEstateRouteBuilder extends RouteBuilder {

    private static final String AUTH_MEDIATE_URI = "cxf:bean:reAuthInMediatorEndpoint";
    private static final String MEDIATE_URI = "cxf:bean:reInMediatorEndpoint";
    private static final String RESOLVE_OPERATION_TYPE_URI = "direct:resolveOperationType";
    private static final String SEND_TO_SERVICE_CLASS_URI = "direct:sendToServiceClass";
    private static final String WRAP_FAULT_URI = "direct:wrapFault";

    private static final JaxbDataFormat REAL_ESTATE_DATA_FORMAT = new JaxbDataFormat(Reflection.getPackageName(com.mist.rews.services.xsd.realestate.ObjectFactory.class));

    @Override
    public void configure() throws Exception {

        onException(RealEstateFaults.RealEstateFaultException.class)
            .handled(true)
            .maximumRedeliveries(0)
            .bean(RealEstateFaults.class, "transformToExceptionBody")
            .to(WRAP_FAULT_URI)
        ;

        from(AUTH_MEDIATE_URI)
            .setBody(xpath("/").nodeResult())
            .enrich(RESOLVE_OPERATION_TYPE_URI, flexible().storeInHeader(OPERATION_TYPE))
            .unmarshal(REAL_ESTATE_DATA_FORMAT)
            .to(SEND_TO_SERVICE_CLASS_URI)
        ;

        from(MEDIATE_URI)
            .setBody(xpath("/").nodeResult())
            .enrich(RESOLVE_OPERATION_TYPE_URI, flexible().storeInHeader(OPERATION_TYPE))
            .unmarshal(REAL_ESTATE_DATA_FORMAT)
            .to(SEND_TO_SERVICE_CLASS_URI)
        ;

        from(RESOLVE_OPERATION_TYPE_URI)
            .bean(Operations.class, "parseOperation")
        ;

        from(SEND_TO_SERVICE_CLASS_URI)
            .choice()
                .when(header(OPERATION_TYPE).isEqualTo(Operations.REGISTER_REAL_ESTATE))
                    .bean("registerRealEstateService")
                .when(header(OPERATION_TYPE).isEqualTo(Operations.TRANSFER_REAL_ESTATE))
                    .bean("transferRealEstateService")
                .when(header(OPERATION_TYPE).isEqualTo(Operations.UNREGISTER_REAL_ESTATE))
                    .bean("unregisterRealEstateService")
                .when(header(OPERATION_TYPE).isEqualTo(Operations.UPDATE_REAL_ESTATE))
                    .bean("updateRealEstateService")
                .when(header(OPERATION_TYPE).isEqualTo(Operations.LIST_ESTATES))
                    .bean("listEstatesService")
                .otherwise()
                    .throwException(new RuntimeException("Unexpected operation type."))
            .end()
        ;

        from(WRAP_FAULT_URI)
            .validate(body().isInstanceOf(FaultType.class))
            .validate(header(OPERATION_TYPE).isNotNull())
            .choice()
                .when(header(OPERATION_TYPE).isEqualTo(Operations.REGISTER_REAL_ESTATE))
                    .bean("registerRealEstateService", "wrapFault")
                .when(header(OPERATION_TYPE).isEqualTo(Operations.TRANSFER_REAL_ESTATE))
                    .bean("transferRealEstateService", "wrapFault")
                .when(header(OPERATION_TYPE).isEqualTo(Operations.UNREGISTER_REAL_ESTATE))
                    .bean("unregisterRealEstateService", "wrapFault")
                .when(header(OPERATION_TYPE).isEqualTo(Operations.UPDATE_REAL_ESTATE))
                    .bean("updateRealEstateService", "wrapFault")
                .otherwise()
                    .throwException(new RuntimeException("Unexpected operation type."))
            .end()
        ;

    }
}
