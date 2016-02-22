package com.mist.rews.op.services;

import com.google.common.base.Optional;
import com.mist.rews.RealEstateDatabase;
import com.mist.rews.RealEstateFaults;
import com.mist.rews.op.helpers.RealEstateHelpers;
import com.mist.rews.services.xsd.realestate.FaultType;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.RealEstateType;
import com.mist.rews.services.xsd.realestate.TransferRealEstate;
import com.mist.rews.services.xsd.realestate.TransferRealEstateResponse;
import org.apache.camel.Body;
import org.apache.camel.Handler;

import java.math.BigInteger;

public class TransferRealEstateService implements RealEstateService {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private final RealEstateDatabase database;

    public TransferRealEstateService(RealEstateDatabase database) {
        this.database = database;
    }

    @Handler
    public TransferRealEstateResponse handle(TransferRealEstate transferRealEstate) {
        BigInteger id = transferRealEstate.getId();
        Optional<RealEstateType> realEstate = database.findRealEstate(
            estate -> estate.getInformation().getId().equals(id));

        if (!realEstate.isPresent()) {
            RealEstateFaults.TRANSFER_ESTATE__ESTATE_NOT_REGISTERED.throwException();
        }
        if (!RealEstateHelpers.isSame(realEstate.get().getInformation().getOwner(), transferRealEstate.getOwner())) {
            RealEstateFaults.TRANSFER_ESTATE__NOT_ESTATE_OWNER.throwException();
        }
        realEstate.get().getInformation().setOwner(transferRealEstate.getBeneficiary());
        database.updateRealEstate(id, realEstate.get());

        return OBJECT_FACTORY.createTransferRealEstateResponse();
    }

    public TransferRealEstateResponse wrapFault(@Body FaultType fault) {
        return OBJECT_FACTORY.createTransferRealEstateResponse()
            .withFault(fault);
    }

}
