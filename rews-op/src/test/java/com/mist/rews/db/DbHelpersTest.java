package com.mist.rews.db;

import com.mist.rews.StringReader;
import com.mist.rews.services.xsd.realestate.EstateTypeEnumeration;
import com.mist.rews.services.xsd.realestate.LocationType;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.PersonNameAndCode;
import com.mist.rews.services.xsd.realestate.RealEstateCondition;
import com.mist.rews.services.xsd.realestate.RealEstateDetails;
import com.mist.rews.services.xsd.realestate.RealEstateInformation;
import com.mist.rews.services.xsd.realestate.RealEstateType;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

import static com.mist.rews.op.helpers.Helpers.currentDate;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class DbHelpersTest {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
    private static XMLGregorianCalendar TEST_DATE;
    static {
        try {
            TEST_DATE = DatatypeFactory.newInstance().newXMLGregorianCalendar(2015, 1, 1, 12, 0, 0, 0, 0);
        } catch(DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRealEstateToString() throws Exception {
        RealEstateType realEstate = OBJECT_FACTORY.createRealEstateType()
                .withInformation(OBJECT_FACTORY.createRealEstateInformation()
                .withId(new BigInteger("123"))
                .withOwner(OBJECT_FACTORY.createPersonNameAndCode()
                    .withName(OBJECT_FACTORY.createPersonName()
                        .withFirstName("FirstName")
                        .withLastName("LastName"))
                    .withPersonCode(new BigInteger("1234567890")))
                .withLocation(OBJECT_FACTORY.createLocationType()
                    .withCountry("TestCountry")
                    .withCity("TestCity")
                    .withAddress("Some Address g. 107-58"))
                .withEstateType(EstateTypeEnumeration.HOUSE)
                .withPrice(new BigInteger("5000"))
                .withRegistrationDate(TEST_DATE)
                .withConstructionDate(TEST_DATE))
            .withDetails(OBJECT_FACTORY.createRealEstateDetails()
                .withCondition(RealEstateCondition.NEEDS_REPAIR)
                .withArea(null)
                .withNumberOfRooms(null)
                .withFloor(new BigInteger("4"))
                .withNumberOfFloors(new BigInteger("5"))
                .withDescription("Some description HAHAHA"));

        assertThat(DbHelpers.toString(realEstate),
            is("123|FirstName|LastName|1234567890|TestCountry|TestCity|Some Address g. 107-58" +
                "|HOUSE|5000|2015-01-01T12:00:00.000Z|2015-01-01T12:00:00.000Z|NEEDS_REPAIR|||4|5|Some description HAHAHA"));
    }

    @Test
    public void testRealEstateInformationToString() throws Exception {
        RealEstateInformation realEstateInformation = OBJECT_FACTORY.createRealEstateInformation()
            .withId(new BigInteger("123"))
            .withOwner(OBJECT_FACTORY.createPersonNameAndCode()
                .withName(OBJECT_FACTORY.createPersonName()
                    .withFirstName("FirstName")
                    .withLastName("LastName"))
                .withPersonCode(new BigInteger("1234567890")))
            .withLocation(OBJECT_FACTORY.createLocationType()
                .withCountry("TestCountry")
                .withCity("TestCity")
                .withAddress("Some Address g. 107-58"))
            .withEstateType(EstateTypeEnumeration.HOUSE)
            .withPrice(new BigInteger("5000"))
            .withRegistrationDate(null)
            .withConstructionDate(TEST_DATE);

        assertThat(DbHelpers.toString(realEstateInformation),
            is("123|FirstName|LastName|1234567890|TestCountry|TestCity|Some Address g. 107-58" +
                "|HOUSE|5000||2015-01-01T12:00:00.000Z"));
    }

    @Test
    public void testPersonNameAndCodeToString() throws Exception {
        PersonNameAndCode personNameAndCode = OBJECT_FACTORY.createPersonNameAndCode()
            .withName(OBJECT_FACTORY.createPersonName()
                .withFirstName("FirstName")
                .withLastName("LastName"))
            .withPersonCode(new BigInteger("1234567890"));

        assertThat(DbHelpers.toString(personNameAndCode), is("FirstName|LastName|1234567890"));
    }

    @Test
    public void testLocationTypeToString() throws Exception {
        LocationType location = OBJECT_FACTORY.createLocationType()
            .withCountry("TestCountry")
            .withCity("TestCity")
            .withAddress("Some Address g. 107-58");

        assertThat(DbHelpers.toString(location), is("TestCountry|TestCity|Some Address g. 107-58"));
    }

    @Test
    public void testRealEstateDetailsToString() throws Exception {
        RealEstateDetails realEstateDetails = OBJECT_FACTORY.createRealEstateDetails()
            .withCondition(RealEstateCondition.NEW_CONSTRUCTION)
            .withArea(new BigInteger("215"))
            .withNumberOfRooms(null)
            .withFloor(new BigInteger("4"))
            .withNumberOfFloors(new BigInteger("5"))
            .withDescription("Some description HAHAHA");

        assertThat(DbHelpers.toString(realEstateDetails), is("NEW_CONSTRUCTION|215||4|5|Some description HAHAHA"));
    }

    @Test
    public void testStringToRealEstate() throws Exception {
        String string = "123|FirstName|LastName|1234567890|TestCountry|TestCity|Some Address g. 107-58" +
                "|HOUSE|5000|2015-01-01T12:00:00.000Z|2015-01-01T12:00:00.000Z|NEEDS_REPAIR|||4|5|Some description HAHAHA";
        RealEstateType realEstate = DbHelpers.toRealEstateType(new StringReader(string));

        assertNotNull(realEstate);
        assertNotNull(realEstate.getInformation());
        assertThat(realEstate.getInformation().getId(), is(new BigInteger("123")));
        assertNotNull(realEstate.getInformation().getOwner());
        assertThat(realEstate.getInformation().getOwner().getName().getFirstName(), is("FirstName"));
        assertThat(realEstate.getInformation().getOwner().getName().getLastName(), is("LastName"));
        assertThat(realEstate.getInformation().getOwner().getPersonCode(), is(new BigInteger("1234567890")));
        assertNotNull(realEstate.getInformation().getLocation());
        assertThat(realEstate.getInformation().getLocation().getCountry(), is("TestCountry"));
        assertThat(realEstate.getInformation().getLocation().getCity(), is("TestCity"));
        assertThat(realEstate.getInformation().getLocation().getAddress(), is("Some Address g. 107-58"));
        assertThat(realEstate.getInformation().getEstateType(), is(EstateTypeEnumeration.HOUSE));
        assertThat(realEstate.getInformation().getPrice(), is(new BigInteger("5000")));
        assertThat(realEstate.getInformation().getRegistrationDate(), is(TEST_DATE));
        assertThat(realEstate.getInformation().getConstructionDate(), is(TEST_DATE));
        assertNotNull(realEstate.getDetails());
        assertThat(realEstate.getDetails().getCondition(), is(RealEstateCondition.NEEDS_REPAIR));
        assertNull(realEstate.getDetails().getArea());
        assertNull(realEstate.getDetails().getNumberOfRooms());
        assertThat(realEstate.getDetails().getFloor(), is(new BigInteger("4")));
        assertThat(realEstate.getDetails().getNumberOfFloors(), is(new BigInteger("5")));
        assertThat(realEstate.getDetails().getDescription(), is("Some description HAHAHA"));
    }

    @Test
    public void testStringToRealEstateInformation() throws Exception {
        String string = "123|FirstName|LastName|1234567890|TestCountry|TestCity|Some Address g. 107-58" +
            "|HOUSE|5000||2015-01-01T12:00:00.000Z";
        RealEstateInformation realEstateInformation = DbHelpers.toRealEstateInformation(new StringReader(string));

        assertNotNull(realEstateInformation);
        assertThat(realEstateInformation.getId(), is(new BigInteger("123")));
        assertNotNull(realEstateInformation.getOwner());
        assertThat(realEstateInformation.getOwner().getName().getFirstName(), is("FirstName"));
        assertThat(realEstateInformation.getOwner().getName().getLastName(), is("LastName"));
        assertThat(realEstateInformation.getOwner().getPersonCode(), is(new BigInteger("1234567890")));
        assertNotNull(realEstateInformation.getLocation());
        assertThat(realEstateInformation.getLocation().getCountry(), is("TestCountry"));
        assertThat(realEstateInformation.getLocation().getCity(), is("TestCity"));
        assertThat(realEstateInformation.getLocation().getAddress(), is("Some Address g. 107-58"));
        assertThat(realEstateInformation.getEstateType(), is(EstateTypeEnumeration.HOUSE));
        assertThat(realEstateInformation.getPrice(), is(new BigInteger("5000")));
        assertNull(realEstateInformation.getRegistrationDate());
        assertThat(realEstateInformation.getConstructionDate(), is(TEST_DATE));
    }

    @Test
    public void testStringToPersonNameAndCode() throws Exception {
        String string = "FirstName|LastName|1234567890";
        PersonNameAndCode personNameAndCode = DbHelpers.toPersonNameAndCode(new StringReader(string));

        assertNotNull(personNameAndCode);
        assertNotNull(personNameAndCode.getName());
        assertThat(personNameAndCode.getName().getFirstName(), is("FirstName"));
        assertThat(personNameAndCode.getName().getLastName(), is("LastName"));
        assertThat(personNameAndCode.getPersonCode(), is(new BigInteger("1234567890")));
    }

    @Test
    public void testStringToLocation() throws Exception {
        String string = "TestCountry|TestCity|Some Address g. 107-58";
        LocationType location = DbHelpers.toLocation(new StringReader(string));

        assertNotNull(location);
        assertThat(location.getCountry(), is("TestCountry"));
        assertThat(location.getCity(), is("TestCity"));
        assertThat(location.getAddress(), is("Some Address g. 107-58"));
    }

    @Test
    public void testStringToRealEstateDetails() throws Exception {
        String string = "NEW_CONSTRUCTION|215||4|5|Some description HAHAHA";
        RealEstateDetails realEstateDetails = DbHelpers.toRealEstateDetails(new StringReader(string));

        assertNotNull(realEstateDetails);
        assertThat(realEstateDetails.getCondition(), is(RealEstateCondition.NEW_CONSTRUCTION));
        assertThat(realEstateDetails.getArea(), is(new BigInteger("215")));
        assertNull(realEstateDetails.getNumberOfRooms());
        assertThat(realEstateDetails.getFloor(), is(new BigInteger("4")));
        assertThat(realEstateDetails.getNumberOfFloors(), is(new BigInteger("5")));
        assertThat(realEstateDetails.getDescription(), is("Some description HAHAHA"));
    }

    @Test
    public void shouldNotContainDetails() throws Exception {
        String string = "123|FirstName|LastName|1234567890|TestCountry|TestCity|Some Address g. 107-58" +
                "|HOUSE|5000|2015-01-01T12:00:00.000Z|2015-01-01T12:00:00.000Z";
        RealEstateType realEstate = DbHelpers.toRealEstateType(new StringReader(string));
        assertNotNull(realEstate);
        assertNotNull(realEstate.getInformation());
        assertNull(realEstate.getDetails());
    }

}