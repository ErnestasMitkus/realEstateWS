package com.mist.rews.op.helpers;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.mist.rews.RealEstateFaults;
import com.mist.rews.services.xsd.realestate.LocationType;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.PersonNameAndCode;
import com.mist.rews.services.xsd.realestate.RealEstateDetails;
import com.mist.rews.services.xsd.realestate.RealEstateInformation;
import com.mist.rews.services.xsd.realestate.RealEstateRegistrationInformation;
import com.mist.rews.services.xsd.realestate.RealEstateRegistrationType;
import com.mist.rews.services.xsd.realestate.RealEstateType;
import com.mist.rews.services.xsd.realestate.UpdateRealEstate;

import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class RealEstateHelpers {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    public static RealEstateType convertToRealEstateType(RealEstateRegistrationType realEstateRegistrationType) {
        return OBJECT_FACTORY.createRealEstateType()
            .withInformation(convertToRealEstateInformation(realEstateRegistrationType.getInformation()))
            .withDetails(realEstateRegistrationType.getDetails())
        ;
    }

    public static RealEstateType convertToRealEstateType(UpdateRealEstate updateRealEstate) {
        return OBJECT_FACTORY.createRealEstateType()
            .withInformation(convertToRealEstateInformation(updateRealEstate.getInformation()))
            .withDetails(updateRealEstate.getDetails())
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

    public static Optional<RealEstateType> findSame(RealEstateType estate, List<RealEstateType> estateList) {
        return Iterables.tryFind(estateList,
            estateType -> isSame(estate.getInformation(), estateType.getInformation()));
    }

    public static boolean isSame(RealEstateInformation info1, RealEstateInformation info2) {
        return info1.equals(info2) || (
            isSame(info1.getLocation(), info2.getLocation())
        );
    }

    public static boolean isSame(LocationType loc1, LocationType loc2) {
        return loc1.equals(loc2) || (
            loc1.getCountry().equals(loc2.getCountry()) &&
            loc1.getCity().equals(loc2.getCity()) &&
            loc1.getAddress().equals(loc2.getAddress())
        );
    }

    public static boolean isSame(PersonNameAndCode person1, PersonNameAndCode person2) {
        return person1.equals(person2) || (
            person1.getName() != null && person2.getName() != null &&
            person1.getName().getFirstName().equals(person2.getName().getFirstName()) &&
            person1.getName().getLastName().equals(person2.getName().getLastName()) &&
            person1.getPersonCode().equals(person2.getPersonCode())
        );
    }

    public static void resolveRegisterException(RealEstateType realEstate, RealEstateType match) {
        if (isSame(realEstate.getInformation().getLocation(), match.getInformation().getLocation())) {
            if (!isSame(realEstate.getInformation().getOwner(), match.getInformation().getOwner())) {
                RealEstateFaults.REAL_ESTATE_LOCATION_ALREADY_REGISTERED_DIFFERENT_OWNER.throwException();
            } else {
                RealEstateFaults.REAL_ESTATE_LOCATION_ALREADY_REGISTERED.throwException();
            }
        }
        RealEstateFaults.throwUnknownException("Failed to register real estate.");
    }
}
