package com.mist.rews.op;

import com.google.common.reflect.Reflection;
import com.mist.prws.services.xsd.personregistry.ObjectFactory;
import com.mist.rews.services.xsd.realestate.PersonNameAndCode;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.apache.camel.converter.jaxb.JaxbDataFormat;

import java.math.BigInteger;

public class PersonRegistryRouteBuilder extends RouteBuilder {

    private static final String SEARCH_PERSON_OPERATION_NAME = "SearchPersonOperation";
    private static final String SEARCH_PERSON_OPERATION = "urn:com:mist:prws:operations:SearchPersonOperation";
    private static final String TRANSFER_FUNDS_OPERATION_NAME = "TransferMoneyOperation";
    private static final String TRANSFER_FUNDS_OPERATION = "urn:com:mist:prws:operations:TransferMoneyOperation";
    private static final String PERSON_REGISTRY_OPERATION_NAMESPACE = "urn:com:mist:prws:services:wsdl:PersonRegistryService";

    private static final JaxbDataFormat PERSON_REGISTRY__DATA_FORMAT = new JaxbDataFormat(Reflection.getPackageName(com.mist.prws.services.xsd.personregistry.ObjectFactory.class));
    private static final String PERSON_REGISTRY_URI = "cxf:bean:personRegistryEndpoint";

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public static final String VALIDATE_PERSON_URI = "direct:validatePerson";
    public static final String CHARGE_FOR_OPERATION_URI = "direct:chargeForOperation";

    public static final String CHARGE_AMOUNT_HEADER = "ChargeAmount";

    private final BigInteger receiverId;

    public PersonRegistryRouteBuilder(long receiverId) {
        this.receiverId = BigInteger.valueOf(receiverId);
    }

    @Override
    public void configure() throws Exception {
        from(VALIDATE_PERSON_URI)
            .setHeader(CxfConstants.OPERATION_NAME, constant(SEARCH_PERSON_OPERATION_NAME))
            .setHeader(CxfConstants.OPERATION_NAMESPACE, constant(PERSON_REGISTRY_OPERATION_NAMESPACE))
            .setHeader("SOAPAction", constant(SEARCH_PERSON_OPERATION))

            .process(exchange -> {
                Object body = exchange.getIn().getBody();
                BigInteger personCode;
                if (body instanceof BigInteger) {
                    personCode = ((BigInteger) body);
                } else {
                    personCode = new BigInteger(""+body);
                }

                exchange.getIn().setBody(OBJECT_FACTORY.createSearchPerson()
                                            .withPersonCode(personCode));
            })

            .marshal(PERSON_REGISTRY__DATA_FORMAT)
            .to(PERSON_REGISTRY_URI)
            .unmarshal(PERSON_REGISTRY__DATA_FORMAT)
        ;

        from(CHARGE_FOR_OPERATION_URI)
            .setHeader(CxfConstants.OPERATION_NAME, constant(TRANSFER_FUNDS_OPERATION_NAME))
            .setHeader(CxfConstants.OPERATION_NAMESPACE, constant(PERSON_REGISTRY_OPERATION_NAMESPACE))
            .setHeader("SOAPAction", constant(TRANSFER_FUNDS_OPERATION))

            .process(exchange -> {
                BigInteger personCode = exchange.getIn().getBody(BigInteger.class);
                BigInteger amount = exchange.getIn().getHeader(CHARGE_AMOUNT_HEADER, BigInteger.class);
                exchange.getIn().setBody(OBJECT_FACTORY.createTransferMoney()
                                            .withPersonCode(personCode)
                                            .withBeneficiaryPersonCode(receiverId)
                                            .withAmount(amount));
            })

            .marshal(PERSON_REGISTRY__DATA_FORMAT)
            .to(PERSON_REGISTRY_URI)
            .unmarshal(PERSON_REGISTRY__DATA_FORMAT)
        ;
    }
}
