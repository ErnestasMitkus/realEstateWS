package com.mist.rews.op.services;

import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.RegisterRealEstate;
import com.mist.rews.services.xsd.realestate.RegisterRealEstateResponse;

import java.math.BigInteger;

public class RegisterRealEstateService implements RealEstateService {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public RegisterRealEstateResponse handle(RegisterRealEstate registerRealEstate) {
        return OBJECT_FACTORY
            .createRegisterRealEstateResponse()
            .withId(BigInteger.ONE);
    }

}
