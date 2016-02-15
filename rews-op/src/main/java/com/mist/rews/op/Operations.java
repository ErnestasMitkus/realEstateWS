package com.mist.rews.op;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mist.rews.op.services.RealEstateService;
import com.mist.rews.op.services.RegisterRealEstateService;
import org.apache.camel.Exchange;

import javax.xml.bind.JAXBContext;

import static com.mist.rews.op.Helpers.NAMESPACE_RE;

public enum Operations {

    REGISTER_REAL_ESTATE("RegisterRealEstate", RegisterRealEstateService.class),
    UNKNOWN("Unknown", null)
    ;

    private static JAXBContext JAXB_CONTEXT;

    private final String operationName;
    private final Class<? extends RealEstateService> serviceClass;

    Operations(String operationName, Class<? extends RealEstateService> serviceClass) {
        this.operationName = operationName;
        this.serviceClass = serviceClass;
    }

    public static Operations parseOperation(Exchange exchange) {
        Optional<Operations> optOperation = Iterables.tryFind(Lists.newArrayList(values()),
            input -> NAMESPACE_RE.xpath("/urn:" + input.operationName).matches(exchange));

        return optOperation.or(UNKNOWN);
    }

}
