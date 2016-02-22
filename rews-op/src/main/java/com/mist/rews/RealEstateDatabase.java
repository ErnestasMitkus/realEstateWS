package com.mist.rews;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.mist.rews.services.xsd.realestate.RealEstateType;

import java.math.BigInteger;
import java.util.List;

public interface RealEstateDatabase {
    List<RealEstateType> getAllRealEstates();

    Optional<RealEstateType> findRealEstate(Predicate<RealEstateType> predicate);

    void updateRealEstate(BigInteger id, RealEstateType realEstate);

    // Returns new id assigned to real estate
    BigInteger registerRealEstate(RealEstateType realEstate);
}
