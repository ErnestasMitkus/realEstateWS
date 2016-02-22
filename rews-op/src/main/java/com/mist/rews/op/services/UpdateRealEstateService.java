package com.mist.rews.op.services;

import com.google.common.base.Optional;
import com.mist.rews.RealEstateDatabase;
import com.mist.rews.RealEstateFaults;
import com.mist.rews.op.helpers.RealEstateHelpers;
import com.mist.rews.services.xsd.realestate.FaultType;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.RealEstateType;
import com.mist.rews.services.xsd.realestate.RegisterRealEstate;
import com.mist.rews.services.xsd.realestate.RegisterRealEstateResponse;
import com.mist.rews.services.xsd.realestate.UpdateRealEstate;
import com.mist.rews.services.xsd.realestate.UpdateRealEstateResponse;
import org.apache.camel.Body;
import org.apache.camel.Handler;

import java.math.BigInteger;

public class UpdateRealEstateService implements RealEstateService {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private final RealEstateDatabase database;

    public UpdateRealEstateService(RealEstateDatabase database) {
        this.database = database;
    }

    @Handler
    public UpdateRealEstateResponse handle(UpdateRealEstate updateRealEstate) {
        BigInteger id = updateRealEstate.getId();
        Optional<RealEstateType> realEstate = database.findRealEstate(
            estate -> estate.getInformation().getId().equals(id));

        if (!realEstate.isPresent()) {
            RealEstateFaults.REAL_ESTATE_NOT_REGISTERED.throwException();
        }
        if (!RealEstateHelpers.isSame(realEstate.get().getInformation().getOwner(), updateRealEstate.getInformation().getOwner())) {
            RealEstateFaults.REAL_ESTATE_DIFFERENT_OWNER.throwException();
        }

        RealEstateType realEstateType = RealEstateHelpers.convertToRealEstateType(updateRealEstate);
        realEstateType.getInformation().setId(id);
        realEstateType.getInformation().setRegistrationDate(realEstate.get().getInformation().getRegistrationDate());
        database.updateRealEstate(id, realEstateType);

        return OBJECT_FACTORY.createUpdateRealEstateResponse();
    }

    public UpdateRealEstateResponse wrapFault(@Body FaultType fault) {
        return OBJECT_FACTORY.createUpdateRealEstateResponse()
            .withFault(fault);
    }

}
