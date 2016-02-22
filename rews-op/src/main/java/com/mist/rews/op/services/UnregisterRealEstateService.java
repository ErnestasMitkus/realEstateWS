package com.mist.rews.op.services;

import com.google.common.base.Optional;
import com.mist.rews.RealEstateDatabase;
import com.mist.rews.RealEstateFaults;
import com.mist.rews.op.helpers.RealEstateHelpers;
import com.mist.rews.services.xsd.realestate.FaultType;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.RealEstateType;
import com.mist.rews.services.xsd.realestate.UnregisterRealEstate;
import com.mist.rews.services.xsd.realestate.UnregisterRealEstateResponse;
import org.apache.camel.Body;
import org.apache.camel.Handler;

import java.math.BigInteger;

public class UnregisterRealEstateService implements RealEstateService {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private final RealEstateDatabase database;

    public UnregisterRealEstateService(RealEstateDatabase database) {
        this.database = database;
    }

    @Handler
    public UnregisterRealEstateResponse handle(UnregisterRealEstate unregisterRealEstate) {
        BigInteger id = unregisterRealEstate.getId();
        Optional<RealEstateType> realEstate = database.findRealEstate(
            estate -> estate.getInformation().getId().equals(id));

        if (!realEstate.isPresent()) {
            RealEstateFaults.REAL_ESTATE_NOT_REGISTERED.throwException();
        }
        if (!RealEstateHelpers.isSame(realEstate.get().getInformation().getOwner(), unregisterRealEstate.getOwner())) {
            RealEstateFaults.REAL_ESTATE_DIFFERENT_OWNER.throwException();
        }
        database.updateRealEstate(id, null);

        return OBJECT_FACTORY.createUnregisterRealEstateResponse();
    }

    public UnregisterRealEstateResponse wrapFault(@Body FaultType fault) {
        return OBJECT_FACTORY.createUnregisterRealEstateResponse()
            .withFault(fault);
    }

}
