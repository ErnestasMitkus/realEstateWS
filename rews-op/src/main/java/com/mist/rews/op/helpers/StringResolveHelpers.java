package com.mist.rews.op.helpers;

import com.google.common.base.Strings;
import com.mist.rews.services.xsd.realestate.EstateTypeEnumeration;
import com.mist.rews.services.xsd.realestate.RealEstateCondition;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

public class StringResolveHelpers {

    public static BigInteger toBigInteger(String str) {
        try {
            return new BigInteger(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(String str) {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static RealEstateCondition toRealEstateCondition(String str) {
        try {
            return RealEstateCondition.valueOf(Strings.nullToEmpty(str));
        } catch (Exception e) {
            return null;
        }
    }

    public static EstateTypeEnumeration toEstateTypeEnumeration(String str) {
        try {
            return EstateTypeEnumeration.valueOf(Strings.nullToEmpty(str));
        } catch (Exception e) {
            return null;
        }
    }

}
