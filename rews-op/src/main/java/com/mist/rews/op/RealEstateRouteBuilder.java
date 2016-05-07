package com.mist.rews.op;

import com.google.common.reflect.Reflection;
import com.mist.rews.RealEstateFaults;
import com.mist.rews.op.helpers.Helpers;
import com.mist.rews.services.xsd.realestate.FaultType;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.cxf.binding.soap.SoapFault;

import javax.xml.namespace.QName;

import java.math.BigInteger;

import static com.mist.rews.RealEstateFaults.PERSON_REGISTRY_NON_EXISTING_BENEFICIARY;
import static com.mist.rews.RealEstateFaults.PERSON_REGISTRY_NON_EXISTING_PERSON;
import static com.mist.rews.op.PersonRegistryRouteBuilder.CHARGE_AMOUNT_HEADER;
import static com.mist.rews.op.PersonRegistryRouteBuilder.CHARGE_FOR_OPERATION_URI;
import static com.mist.rews.op.PersonRegistryRouteBuilder.VALIDATE_PERSON_URI;
import static com.mist.rews.op.helpers.Helpers.OPERATION_TYPE;
import static org.apache.camel.util.toolbox.AggregationStrategies.flexible;

public class RealEstateRouteBuilder extends RouteBuilder {

    private static final String AUTH_MEDIATE_URI = "cxf:bean:reAuthInMediatorEndpoint";
    private static final String MEDIATE_URI = "cxf:bean:reInMediatorEndpoint";
    private static final String RESOLVE_OPERATION_TYPE_URI = "direct:resolveOperationType";
    private static final String SEND_TO_SERVICE_CLASS_URI = "direct:sendToServiceClass";
    private static final String TEST_PERSON_AUTHENTICATION_URI = "direct:testPersonAuthentication";
    private static final String CHARGE_PERSON_FOR_OPERATION_URI = "direct:chargePersonForOperation";

    private static final String REQUEST_BODY_HEADER = "RequestBody";
    private static final String BENEFICIARY_CHECK_HEADER = "BeneficiaryCheck";

    private static final JaxbDataFormat REAL_ESTATE_DATA_FORMAT = new JaxbDataFormat(Reflection.getPackageName(com.mist.rews.services.xsd.realestate.ObjectFactory.class));

    private static final BigInteger CHARGE_AMOUNT_REGISTER_REAL_ESTATE = new BigInteger("100");
    private static final BigInteger CHARGE_AMOUNT_TRANSFER_REAL_ESTATE = new BigInteger("30");
    private static final BigInteger CHARGE_AMOUNT_UNREGISTER_REAL_ESTATE = new BigInteger("10");
    private static final BigInteger CHARGE_AMOUNT_UPDATE_REAL_ESTATE = new BigInteger("10");

    @Override
    public void configure() throws Exception {
        onException(RealEstateFaults.RealEstateFaultException.class)
            .handled(true)
            .maximumRedeliveries(0)
            .bean(RealEstateFaults.class, "transformToExceptionBody")
            .process(exchange -> {
                FaultType fault = exchange.getIn().getBody(FaultType.class);
                exchange.getOut().setFault(true);
                exchange.getOut().setBody(
                    new SoapFault(fault.getErrorMessage(), SoapFault.FAULT_CODE_SERVER));
            })
        ;

        from(AUTH_MEDIATE_URI)
            .setBody(xpath("/").nodeResult())
            .enrich(RESOLVE_OPERATION_TYPE_URI, flexible().storeInHeader(OPERATION_TYPE))
            .unmarshal(REAL_ESTATE_DATA_FORMAT)
            .to(TEST_PERSON_AUTHENTICATION_URI)
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
                    .to(CHARGE_PERSON_FOR_OPERATION_URI)
                    .bean("registerRealEstateService")
                .when(header(OPERATION_TYPE).isEqualTo(Operations.TRANSFER_REAL_ESTATE))
                    .to(CHARGE_PERSON_FOR_OPERATION_URI)
                    .bean("transferRealEstateService")
                .when(header(OPERATION_TYPE).isEqualTo(Operations.UNREGISTER_REAL_ESTATE))
                    .to(CHARGE_PERSON_FOR_OPERATION_URI)
                    .bean("unregisterRealEstateService")
                .when(header(OPERATION_TYPE).isEqualTo(Operations.UPDATE_REAL_ESTATE))
                    .to(CHARGE_PERSON_FOR_OPERATION_URI)
                    .bean("updateRealEstateService")
                .when(header(OPERATION_TYPE).isEqualTo(Operations.LIST_ESTATES))
                    .bean("listEstatesService")
                .when(header(OPERATION_TYPE).isEqualTo(Operations.REAL_ESTATE_DETAILED_INFORMATION))
                    .bean("realEstateDetailedInformationService")
                .otherwise()
                    .throwException(new RuntimeException("Unexpected operation type."))
            .end()
        ;

        from(TEST_PERSON_AUTHENTICATION_URI)
            .setHeader(REQUEST_BODY_HEADER, body())
            .process(Helpers::backupHeaders)
            .removeHeader(BENEFICIARY_CHECK_HEADER)
            .choice()
                .when(header(OPERATION_TYPE).isEqualTo(Operations.REGISTER_REAL_ESTATE))
                    .setBody(simple("${body.information.owner.personCode}"))
                .when(header(OPERATION_TYPE).isEqualTo(Operations.TRANSFER_REAL_ESTATE))
                    .setBody(simple("${body.owner.personCode}"))
                    .setHeader(BENEFICIARY_CHECK_HEADER, simple("${body.beneficiary.personCode}"))
                .when(header(OPERATION_TYPE).isEqualTo(Operations.UNREGISTER_REAL_ESTATE))
                    .setBody(simple("${body.owner.personCode}"))
                .when(header(OPERATION_TYPE).isEqualTo(Operations.UPDATE_REAL_ESTATE))
                    .setBody(simple("${body.information.owner.personCode}"))
                .otherwise()
                    .throwException(new RuntimeException("Unexpected operation type."))
            .end()

            .to(VALIDATE_PERSON_URI)
            .filter(header(BENEFICIARY_CHECK_HEADER).isNotNull())
                .setBody(header(BENEFICIARY_CHECK_HEADER))
                .to(VALIDATE_PERSON_URI)
            .end()

            .process(Helpers::restoreHeaders)
            .setBody(header(REQUEST_BODY_HEADER))
        ;

        from(CHARGE_PERSON_FOR_OPERATION_URI)
            .setHeader(REQUEST_BODY_HEADER, body())
            .process(Helpers::backupHeaders)

            .choice()
                .when(header(OPERATION_TYPE).isEqualTo(Operations.REGISTER_REAL_ESTATE))
                    .setHeader(CHARGE_AMOUNT_HEADER, constant(CHARGE_AMOUNT_REGISTER_REAL_ESTATE))
                    .setBody(simple("${body.information.owner.personCode}"))
                .when(header(OPERATION_TYPE).isEqualTo(Operations.TRANSFER_REAL_ESTATE))
                    .setHeader(CHARGE_AMOUNT_HEADER, constant(CHARGE_AMOUNT_TRANSFER_REAL_ESTATE))
                    .setBody(simple("${body.owner.personCode}"))
                .when(header(OPERATION_TYPE).isEqualTo(Operations.UNREGISTER_REAL_ESTATE))
                    .setHeader(CHARGE_AMOUNT_HEADER, constant(CHARGE_AMOUNT_UNREGISTER_REAL_ESTATE))
                    .setBody(simple("${body.owner.personCode}"))
                .when(header(OPERATION_TYPE).isEqualTo(Operations.UPDATE_REAL_ESTATE))
                    .setHeader(CHARGE_AMOUNT_HEADER, constant(CHARGE_AMOUNT_UPDATE_REAL_ESTATE))
                    .setBody(simple("${body.information.owner.personCode}"))
                .otherwise()
                    .throwException(new RuntimeException("Unexpected operation type."))
            .end()

            .log(LoggingLevel.INFO, "Charging for operation. Amount: ${header."+CHARGE_AMOUNT_HEADER+"}")

            .to(CHARGE_FOR_OPERATION_URI)

            .process(Helpers::restoreHeaders)
            .setBody(header(REQUEST_BODY_HEADER))
        ;

    }
}
