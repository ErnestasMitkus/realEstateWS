package com.mist.rews.op.helpers;

import com.mist.rews.services.xsd.realestate.LocationType;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.PersonNameAndCode;
import com.mist.rews.services.xsd.realestate.RealEstateDetails;
import com.mist.rews.services.xsd.realestate.RealEstateInformation;
import com.mist.rews.services.xsd.realestate.RealEstateRegistrationInformation;
import com.mist.rews.services.xsd.realestate.RealEstateRegistrationType;
import com.mist.rews.services.xsd.realestate.RealEstateType;

import static com.google.common.base.Strings.isNullOrEmpty;

public class RealEstateHelpers {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public static RealEstateType convertToRealEstateType(RealEstateRegistrationType realEstateRegistrationType) {
        return OBJECT_FACTORY.createRealEstateType()
            .withInformation(convertToRealEstateInformation(realEstateRegistrationType.getInformation()))
            .withDetails(realEstateRegistrationType.getDetails())
        ;
    }

    private static RealEstateInformation convertToRealEstateInformation(RealEstateRegistrationInformation information) {
        return OBJECT_FACTORY.createRealEstateInformation()
            .withId(null)
            .withOwner(information.getOwner())
            .withLocation(information.getLocation())
            .withEstateType(information.getEstateType())
            .withPrice(information.getPrice())
            .withRegistrationDate(null)
            .withConstructionDate(information.getConstructionDate())
        ;
    }

    public static boolean isEmpty(RealEstateType realEstate) {
        return realEstate == null || (
            isEmpty(realEstate.getInformation()) &&
            isEmpty(realEstate.getDetails())
        );
    }

    public static boolean isEmpty(RealEstateInformation realEstateInformation) {
        return realEstateInformation == null || (
            realEstateInformation.getId() == null &&
            isEmpty(realEstateInformation.getOwner()) &&
            isEmpty(realEstateInformation.getLocation()) &&
            realEstateInformation.getEstateType() == null &&
            realEstateInformation.getPrice() == null &&
            realEstateInformation.getRegistrationDate() == null &&
            realEstateInformation.getConstructionDate() == null
        );
    }

    public static boolean isEmpty(PersonNameAndCode personNameAndCode) {
        return personNameAndCode == null || (
            (personNameAndCode.getName() == null ||
                isNullOrEmpty(personNameAndCode.getName().getFirstName()) &&
                isNullOrEmpty(personNameAndCode.getName().getLastName())) &&
            personNameAndCode.getPersonCode() == null
        );
    }

    public static boolean isEmpty(LocationType location) {
        return location == null || (
            isNullOrEmpty(location.getCountry()) &&
            isNullOrEmpty(location.getCity()) &&
            isNullOrEmpty(location.getAddress())
        );
    }

    public static boolean isEmpty(RealEstateDetails realEstateDetails) {
        return realEstateDetails == null || (
            realEstateDetails.getArea() == null &&
            realEstateDetails.getCondition() == null &&
            realEstateDetails.getNumberOfRooms() == null &&
            realEstateDetails.getFloor() == null &&
            realEstateDetails.getNumberOfFloors() == null &&
            isNullOrEmpty(realEstateDetails.getDescription())
        );
    }
}
