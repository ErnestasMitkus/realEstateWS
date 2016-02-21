package com.mist.rews.op.services;

import com.mist.rews.RealEstateDatabase;
import com.mist.rews.op.helpers.RealEstateHelpers;
import com.mist.rews.services.xsd.realestate.FaultType;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.RegisterRealEstate;
import com.mist.rews.services.xsd.realestate.RegisterRealEstateResponse;
import org.apache.camel.Body;
import org.apache.camel.Handler;

import java.math.BigInteger;

public class RegisterRealEstateService implements RealEstateService {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private final RealEstateDatabase database;

    public RegisterRealEstateService(RealEstateDatabase database) {
        this.database = database;
    }

    @Handler
    public RegisterRealEstateResponse handle(RegisterRealEstate registerRealEstate) {
        BigInteger id = database.registerRealEstate(
            RealEstateHelpers.convertToRealEstateType(registerRealEstate));

        return OBJECT_FACTORY
            .createRegisterRealEstateResponse()
            .withId(id);
    }

    public RegisterRealEstateResponse wrapFault(@Body FaultType fault) {
        return OBJECT_FACTORY.createRegisterRealEstateResponse()
            .withFault(fault);
    }

}
