package com.mist.rews.op.helpers;

import com.mist.rews.services.xsd.realestate.LocationType;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.PersonNameAndCode;
import com.mist.rews.services.xsd.realestate.RealEstateDetails;
import com.mist.rews.services.xsd.realestate.RealEstateInformation;
import com.mist.rews.services.xsd.realestate.RealEstateType;
import org.junit.Test;

import static org.junit.Assert.*;

public class RealEstateHelpersTest {

    public static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    @Test
    public void testIsEmpty() throws Exception {
        assertTrue(RealEstateHelpers.isEmpty((RealEstateType) null));
        assertTrue(RealEstateHelpers.isEmpty(OBJECT_FACTORY.createRealEstateType()));
    }

    @Test
    public void testIsEmpty1() throws Exception {
        assertTrue(RealEstateHelpers.isEmpty((RealEstateInformation) null));
        assertTrue(RealEstateHelpers.isEmpty(OBJECT_FACTORY.createRealEstateInformation()));
    }

    @Test
    public void testIsEmpty2() throws Exception {
        assertTrue(RealEstateHelpers.isEmpty((PersonNameAndCode) null));
        assertTrue(RealEstateHelpers.isEmpty(OBJECT_FACTORY.createPersonNameAndCode()));
        assertFalse(RealEstateHelpers.isEmpty(OBJECT_FACTORY.createPersonNameAndCode()
            .withName(OBJECT_FACTORY.createPersonName().withLastName("TestLastName"))));
    }

    @Test
    public void testIsEmpty3() throws Exception {
        assertTrue(RealEstateHelpers.isEmpty((LocationType) null));
        assertTrue(RealEstateHelpers.isEmpty(OBJECT_FACTORY.createLocationType()));
        assertFalse(RealEstateHelpers.isEmpty(OBJECT_FACTORY.createLocationType().withCity("TestCity")));
    }

    @Test
    public void testIsRealEstateDetailsEmpty() throws Exception {
        assertTrue(RealEstateHelpers.isEmpty((RealEstateDetails) null));
        assertTrue(RealEstateHelpers.isEmpty(OBJECT_FACTORY.createRealEstateDetails()));
        assertTrue(RealEstateHelpers.isEmpty(OBJECT_FACTORY.createRealEstateDetails().withDescription("")));
        assertFalse(RealEstateHelpers.isEmpty(OBJECT_FACTORY.createRealEstateDetails().withDescription("!")));
    }
}