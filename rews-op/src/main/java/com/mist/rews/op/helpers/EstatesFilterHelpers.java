package com.mist.rews.op.helpers;

import com.google.common.base.Predicate;
import com.mist.rews.services.xsd.realestate.AddressType;
import com.mist.rews.services.xsd.realestate.ConstructionFromToDateType;
import com.mist.rews.services.xsd.realestate.EstateTypeEnumeration;
import com.mist.rews.services.xsd.realestate.LocationType;
import com.mist.rews.services.xsd.realestate.OptionalAddressType;
import com.mist.rews.services.xsd.realestate.OptionalLocationType;
import com.mist.rews.services.xsd.realestate.PersonNameAndCode;
import com.mist.rews.services.xsd.realestate.PersonOptionalNameOrCode;
import com.mist.rews.services.xsd.realestate.PriceType;
import com.mist.rews.services.xsd.realestate.RealEstateType;
import com.mist.rews.services.xsd.realestate.RegistrationFromToDateType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

public class EstatesFilterHelpers {

    public static Predicate<RealEstateType> predicateFilter(final PersonOptionalNameOrCode filterField) {
        if (filterField == null) {
            return input -> true;
        }
        return input -> {
            PersonNameAndCode owner = input.getInformation().getOwner();
            return (filterField.getName() == null ||
                   (filterField.getName().getFirstName() == null && filterField.getName().getLastName() == null) || (
                        owner.getName().getFirstName().equals(filterField.getName().getFirstName()) &&
                        owner.getName().getLastName().equals(filterField.getName().getLastName())))
                    && (filterField.getPersonCode() == null ||
                        owner.getPersonCode().equals(filterField.getPersonCode()));
        };
    }

    public static Predicate<RealEstateType> predicateFilter(final OptionalLocationType filterField) {
        if (filterField == null) {
            return input -> true;
        }
        return input -> {
            LocationType location = input.getInformation().getLocation();
            return (filterField.getCountry() == null || filterField.getCountry().equals(location.getCountry())) &&
                (filterField.getCity() == null || filterField.getCity().equals(location.getCity())) &&
                (filterField.getAddress() == null || predicateFilter(filterField.getAddress()).apply(input));
        };
    }

    public static Predicate<RealEstateType> predicateFilter(final OptionalAddressType filterField) {
        if (filterField == null) {
            return input -> true;
        }
        return input -> {
            AddressType address = input.getInformation().getLocation().getAddress();
            return (filterField.getStreetName() == null || filterField.getStreetName().equals(address.getStreetName())) &&
                (filterField.getHouseNo() == null || filterField.getHouseNo().equals(address.getHouseNo())) &&
                (filterField.getFlatNo() == null || filterField.getFlatNo().equals(address.getFlatNo()));
        };
    }

    public static Predicate<RealEstateType> predicateFilter(final EstateTypeEnumeration filterField) {
        if (filterField == null) {
            return input -> true;
        }
        return input ->
            filterField.equals(input.getInformation().getEstateType());
    }

    public static Predicate<RealEstateType> predicateFilter(final PriceType filterField) {
        if (filterField == null) {
            return input -> true;
        }
        return input -> bigIntegerBetween(input.getInformation().getPrice(), filterField.getFromPrice(), filterField.getToPrice());
    }

    public static Predicate<RealEstateType> predicateFilter(final RegistrationFromToDateType filterField) {
        if (filterField == null) {
            return input -> true;
        }
        return input -> dateBetween(input.getInformation().getRegistrationDate(), filterField.getFromDate(), filterField.getToDate());
    }

    public static Predicate<RealEstateType> predicateFilter(final ConstructionFromToDateType filterField) {
        if (filterField == null) {
            return input -> true;
        }
        return input -> dateBetween(input.getInformation().getRegistrationDate(), filterField.getFromDate(), filterField.getToDate());
    }

    public static boolean bigIntegerBetween(BigInteger value, BigInteger from, BigInteger to) {
        return (from == null || value.compareTo(from) >= 0) &&
            (to == null || value.compareTo(to) <= 0);
    }

    public static boolean dateBetween(XMLGregorianCalendar date, XMLGregorianCalendar from, XMLGregorianCalendar to) {
        return (from == null || date.compare(from) >= 0) &&
            (to == null || date.compare(to) <= 0);
    }

}
