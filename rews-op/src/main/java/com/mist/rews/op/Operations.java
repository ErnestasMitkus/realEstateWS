package com.mist.rews.op;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mist.rews.op.services.RealEstateService;
import com.mist.rews.op.services.RegisterRealEstateService;
import com.mist.rews.op.services.TransferRealEstateService;
import com.mist.rews.op.services.UnregisterRealEstateService;
import com.mist.rews.op.services.UpdateRealEstateService;
import org.apache.camel.Exchange;

import javax.xml.bind.JAXBContext;

import static com.mist.rews.op.helpers.Helpers.NAMESPACE_RE;

public enum Operations {

    REGISTER_REAL_ESTATE("RegisterRealEstate", RegisterRealEstateService.class),
    TRANSFER_REAL_ESTATE("TransferRealEstate", TransferRealEstateService.class),
    UNREGISTER_REAL_ESTATE("UnregisterRealEstate", UnregisterRealEstateService.class),
    UPDATE_REAL_ESTATE("UpdateRealEstate", UpdateRealEstateService.class),
    UNKNOWN("Unknown", null);

    private final String operationName;
    private final Class<? extends RealEstateService> serviceClass;

    Operations(String operationName, Class<? extends RealEstateService> serviceClass) {
        this.operationName = operationName;
        this.serviceClass = serviceClass;
    }

    public Class<? extends RealEstateService> getServiceClass() {
        return serviceClass;
    }

    public static Operations parseOperation(Exchange exchange) {
        Optional<Operations> optOperation = Iterables.tryFind(Lists.newArrayList(values()),
            input -> NAMESPACE_RE.xpath("/urn:" + input.operationName).matches(exchange));

        return optOperation.or(UNKNOWN);
    }

}
