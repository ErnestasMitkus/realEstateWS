package com.mist.rews.op.helpers;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mist.rews.services.xsd.realestate.EstateTypeEnumeration;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.RealEstateType;
import org.junit.Before;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.math.BigInteger;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class EstatesFilterHelpersTest {

    private static XMLGregorianCalendar DATE1;
    private static XMLGregorianCalendar DATE2;
    private static XMLGregorianCalendar DATE3;

    private static final BigInteger INT1 = new BigInteger("5");
    private static final BigInteger INT2 = new BigInteger("10");
    private static final BigInteger INT3 = new BigInteger("100");
    
    static {
        try {
            DATE1 = DatatypeFactory.newInstance().newXMLGregorianCalendar("2015-02-02");
            DATE2 = DatatypeFactory.newInstance().newXMLGregorianCalendar("2015-04-02");
            DATE3 = DatatypeFactory.newInstance().newXMLGregorianCalendar("2016-01-01");
        } catch(DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private static final List<RealEstateType> ESTATES = Lists.newArrayList(
        OBJECT_FACTORY.createRealEstateType()
            .withInformation(OBJECT_FACTORY.createRealEstateInformation()
                .withId(new BigInteger("1"))
                .withOwner(
                    OBJECT_FACTORY.createPersonNameAndCode()
                        .withName(OBJECT_FACTORY.createPersonName()
                            .withFirstName("FirstName")
                            .withLastName("LastName"))
                        .withPersonCode(new BigInteger("12345")))
                .withLocation(
                    OBJECT_FACTORY.createLocationType()
                        .withCity("TestCity")
                        .withCity("TestCountry")
                        .withCity("TestAddress"))
                .withEstateType(EstateTypeEnumeration.HOUSE)
                .withPrice(new BigInteger("15"))
                .withRegistrationDate(DATE2)
                .withConstructionDate(DATE1)
            ),
        OBJECT_FACTORY.createRealEstateType()
            .withInformation(OBJECT_FACTORY.createRealEstateInformation()
                .withId(new BigInteger("2"))
                .withOwner(
                    OBJECT_FACTORY.createPersonNameAndCode()
                        .withName(OBJECT_FACTORY.createPersonName()
                            .withFirstName("FirstName")
                            .withLastName("LastName"))
                        .withPersonCode(new BigInteger("12345")))
                .withLocation(
                    OBJECT_FACTORY.createLocationType()
                        .withCity("TestCity")
                        .withCity("TestCountry")
                        .withCity("TestAddress2"))
                .withEstateType(EstateTypeEnumeration.FLAT)
                .withPrice(new BigInteger("10"))
                .withRegistrationDate(DATE3)
                .withConstructionDate(DATE1)
            ),
        OBJECT_FACTORY.createRealEstateType()
            .withInformation(OBJECT_FACTORY.createRealEstateInformation()
                .withId(new BigInteger("3"))
                .withOwner(
                    OBJECT_FACTORY.createPersonNameAndCode()
                        .withName(OBJECT_FACTORY.createPersonName()
                            .withFirstName("OtherFirstName")
                            .withLastName("OtherLastName"))
                        .withPersonCode(new BigInteger("1234567")))
                .withLocation(
                    OBJECT_FACTORY.createLocationType()
                        .withCity("TestCity")
                        .withCity("TestCountry2")
                        .withCity("TestAddress"))
                .withEstateType(EstateTypeEnumeration.HOUSE)
                .withPrice(new BigInteger("20"))
                .withRegistrationDate(DATE3)
                .withConstructionDate(DATE2)
            )
    );

    @Test
    public void testFilterOwnerType() throws Exception {
        List<RealEstateType> result = applyPredicate(ESTATES, predicateForOwner("OtherFirstName", "OtherLastName", null));
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getInformation().getId(), is(new BigInteger("3")));

        result = applyPredicate(ESTATES, predicateForOwner(null, null, 12345));
        assertThat(result.size(), is(2));
        assertThat(result.get(0).getInformation().getId(), is(new BigInteger("1")));
        assertThat(result.get(1).getInformation().getId(), is(new BigInteger("2")));

    }

    private Predicate<RealEstateType> predicateForOwner(String firstName, String lastName, Integer personCode) {
        return EstatesFilterHelpers.predicateFilter(OBJECT_FACTORY.createPersonOptionalNameOrCode()
            .withName(OBJECT_FACTORY.createOptionalPersonName()
                .withFirstName(firstName)
                .withLastName(lastName))
            .withPersonCode(personCode == null ? null : new BigInteger("" + personCode)));
    }

    private List<RealEstateType> applyPredicate(List<RealEstateType> list, Predicate<RealEstateType> predicate) {
        return Lists.newArrayList(Iterables.filter(list, predicate));
    }

    @Test
    public void testBigIntegerBetween() throws Exception {
        assertTrue(EstatesFilterHelpers.bigIntegerBetween(INT1, null, null));

        assertTrue(EstatesFilterHelpers.bigIntegerBetween(INT1, null, INT3));
        assertFalse(EstatesFilterHelpers.bigIntegerBetween(INT1, INT2, null));

        assertTrue(EstatesFilterHelpers.bigIntegerBetween(INT2, INT1, INT3));

        assertFalse(EstatesFilterHelpers.bigIntegerBetween(INT1, INT2, INT3));
        assertFalse(EstatesFilterHelpers.bigIntegerBetween(INT3, INT1, INT2));
    }

    @Test
    public void testDateBetween() throws Exception {
        assertTrue(EstatesFilterHelpers.dateBetween(DATE1, null, null));

        assertTrue(EstatesFilterHelpers.dateBetween(DATE1, null, DATE3));
        assertFalse(EstatesFilterHelpers.dateBetween(DATE1, DATE2, null));

        assertTrue(EstatesFilterHelpers.dateBetween(DATE2, DATE1, DATE3));

        assertFalse(EstatesFilterHelpers.dateBetween(DATE1, DATE2, DATE3));
        assertFalse(EstatesFilterHelpers.dateBetween(DATE3, DATE1, DATE2));
    }
}