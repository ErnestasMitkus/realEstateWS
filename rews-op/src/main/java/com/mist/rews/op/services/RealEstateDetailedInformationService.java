package com.mist.rews.op.services;

import com.google.common.base.Optional;
import com.mist.rews.RealEstateDatabase;
import com.mist.rews.RealEstateFaults;
import com.mist.rews.services.xsd.realestate.FaultType;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.RealEstateDetailedInformation;
import com.mist.rews.services.xsd.realestate.RealEstateDetailedInformationResponse;
import com.mist.rews.services.xsd.realestate.RealEstateType;
import org.apache.camel.Handler;

public class RealEstateDetailedInformationService implements RealEstateService {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private final RealEstateDatabase database;

    public RealEstateDetailedInformationService(RealEstateDatabase database) {
        this.database = database;
    }

    @Handler
    public RealEstateDetailedInformationResponse handle(RealEstateDetailedInformation realEstateDetailedInformation) {
        Optional<RealEstateType> realEstate = database.findRealEstate(
            input -> input.getInformation().getId().equals(realEstateDetailedInformation.getId()));

        if (!realEstate.isPresent()) {
            RealEstateFaults.REAL_ESTATE_NOT_REGISTERED.throwException();
        }

        return OBJECT_FACTORY.createRealEstateDetailedInformationResponse()
            .withRealEstate(realEstate.get());
    }

    public RealEstateDetailedInformationResponse wrapFault(FaultType fault) {
        return OBJECT_FACTORY.createRealEstateDetailedInformationResponse()
            .withFault(fault);
    }

}
