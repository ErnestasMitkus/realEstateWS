package com.mist.rews.db;

import com.mist.rews.StringReader;
import com.mist.rews.op.helpers.RealEstateHelpers;
import com.mist.rews.services.xsd.realestate.LocationType;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.PersonNameAndCode;
import com.mist.rews.services.xsd.realestate.RealEstateDetails;
import com.mist.rews.services.xsd.realestate.RealEstateInformation;
import com.mist.rews.services.xsd.realestate.RealEstateType;

import java.io.IOException;

import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.mist.rews.op.helpers.StringResolveHelpers.toBigInteger;
import static com.mist.rews.op.helpers.StringResolveHelpers.toEstateTypeEnumeration;
import static com.mist.rews.op.helpers.StringResolveHelpers.toRealEstateCondition;
import static com.mist.rews.op.helpers.StringResolveHelpers.toXmlGregorianCalendar;


public class DbHelpers {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
    private static final String DELIMITER = "|";
    private static final String MARKER = "**";

    public static String toString(RealEstateType realEstate) {
        StringBuilder sb = new StringBuilder();
        append(sb, toString(realEstate.getInformation()));
        append(sb, toString(realEstate.getDetails()));
        return toString(sb);
    }

    public static String toString(RealEstateInformation realEstateInformation) {
        StringBuilder sb = new StringBuilder();
        append(sb, realEstateInformation.getId());
        append(sb, toString(realEstateInformation.getOwner()));
        append(sb, toString(realEstateInformation.getLocation()));
        append(sb, realEstateInformation.getEstateType());
        append(sb, realEstateInformation.getPrice());
        append(sb, realEstateInformation.getRegistrationDate());
        append(sb, realEstateInformation.getConstructionDate());
        return toString(sb);
    }

    public static String toString(PersonNameAndCode personNameAndCode) {
        if (personNameAndCode == null) {
            personNameAndCode = OBJECT_FACTORY.createPersonNameAndCode();
        }
        if (personNameAndCode.getName() == null) {
            personNameAndCode.setName(OBJECT_FACTORY.createPersonName());
        }

        StringBuilder sb = new StringBuilder();
        append(sb, personNameAndCode.getName().getFirstName());
        append(sb, personNameAndCode.getName().getLastName());
        append(sb, personNameAndCode.getPersonCode());
        return toString(sb);
    }

    public static String toString(LocationType location) {
        if (location == null) {
            location = OBJECT_FACTORY.createLocationType();
        }
        StringBuilder sb = new StringBuilder();
        append(sb, location.getCountry());
        append(sb, location.getCity());
        append(sb, location.getAddress());
        return toString(sb);
    }

    public static String toString(RealEstateDetails realEstateDetails) {
        if (realEstateDetails == null) {
            realEstateDetails = OBJECT_FACTORY.createRealEstateDetails();
        }
        StringBuilder sb = new StringBuilder();
        append(sb, realEstateDetails.getCondition());
        append(sb, realEstateDetails.getArea());
        append(sb, realEstateDetails.getNumberOfRooms());
        append(sb, realEstateDetails.getFloor());
        append(sb, realEstateDetails.getNumberOfFloors());
        append(sb, realEstateDetails.getDescription());
        return toString(sb);
    }

    public static StringBuilder append(StringBuilder sb, Object object) {
        if (sb.length() > 0) {
            sb.append(DELIMITER);
        }
        if (object != null) {
            return sb.append(object.toString());
        }
        if (sb.length() == 0) {
            sb.append(MARKER);
        }
        return sb;
    }

    private static String toString(StringBuilder sb) {
        String str = sb.toString();
        if (str.startsWith(MARKER)) {
            return str.substring(MARKER.length());
        }
        return str;
    }

    public static RealEstateType toRealEstateType(StringReader str) {
        RealEstateType realEstate = OBJECT_FACTORY.createRealEstateType()
            .withInformation(toRealEstateInformation(str))
            .withDetails(toRealEstateDetails(str));

        if (RealEstateHelpers.isEmpty(realEstate)) {
            return null;
        }
        return realEstate;
    }

    public static RealEstateInformation toRealEstateInformation(StringReader str) {
        RealEstateInformation realEstateInformation = OBJECT_FACTORY.createRealEstateInformation()
            .withId(toBigInteger(resolve(str)))
            .withOwner(toPersonNameAndCode(str))
            .withLocation(toLocation(str))
            .withEstateType(toEstateTypeEnumeration(resolve(str)))
            .withPrice(toBigInteger(resolve(str)))
            .withRegistrationDate(toXmlGregorianCalendar(resolve(str)))
            .withConstructionDate(toXmlGregorianCalendar(resolve(str)));

        if (RealEstateHelpers.isEmpty(realEstateInformation)) {
            return null;
        }
        return realEstateInformation;
    }

    public static PersonNameAndCode toPersonNameAndCode(StringReader str) {
        PersonNameAndCode personNameAndCode = OBJECT_FACTORY.createPersonNameAndCode()
            .withName(OBJECT_FACTORY.createPersonName()
                .withFirstName(emptyToNull(resolve(str)))
                .withLastName(emptyToNull(resolve(str))))
            .withPersonCode(toBigInteger(resolve(str)));

        if (RealEstateHelpers.isEmpty(personNameAndCode)) {
            return null;
        }
        return personNameAndCode;
    }

    public static LocationType toLocation(StringReader str) {
        LocationType locationType = OBJECT_FACTORY.createLocationType()
            .withCountry(emptyToNull(resolve(str)))
            .withCity(emptyToNull(resolve(str)))
            .withAddress(emptyToNull(resolve(str)));

        if (RealEstateHelpers.isEmpty(locationType)) {
            return null;
        }
        return locationType;
    }

    public static RealEstateDetails toRealEstateDetails(StringReader str) {
        RealEstateDetails realEstateDetails = OBJECT_FACTORY.createRealEstateDetails()
            .withCondition(toRealEstateCondition(resolve(str)))
            .withArea(toBigInteger(resolve(str)))
            .withNumberOfRooms(toBigInteger(resolve(str)))
            .withFloor(toBigInteger(resolve(str)))
            .withNumberOfFloors(toBigInteger(resolve(str)))
            .withDescription(emptyToNull(resolve(str)));

        if (RealEstateHelpers.isEmpty(realEstateDetails)) {
            return null;
        }
        return realEstateDetails;
    }

    public static String resolve(StringReader str) {
        if (!str.hasNext()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try {
            if (DELIMITER.equals(str.peekCh())) {
                str.read();
            }
            while(str.hasNext() && !DELIMITER.equals(str.peekCh())) {
                sb.append((char) str.read());
            }
        } catch (IOException ignored) {
        }
        return sb.toString();
    }
}
