package com.mist.rews;

import com.mist.rews.services.xsd.realestate.RealEstateType;

import java.math.BigInteger;
import java.util.List;

public interface RealEstateDatabase {
    List<RealEstateType> getAllRealEstates();

    // Returns new id assigned to real estate
    BigInteger registerRealEstate(RealEstateType realEstate);
}
