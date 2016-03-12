package com.mist.rews.op.helpers;

import com.google.common.base.Throwables;
import org.apache.camel.builder.xml.Namespaces;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Helpers {

    public static final Namespaces NAMESPACE_RE = new Namespaces("urn", "urn:com:mist:rews:services:xsd:realestate");

    public static final String OPERATION_TYPE = "OperationTypeHeader";


    public static XMLGregorianCalendar currentDate() {
        return convertToXmlDate(Calendar.getInstance().getTime());
    }

    public static XMLGregorianCalendar convertToXmlDate(Date date) {
        if (date != null) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            try {
                XMLGregorianCalendar xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
                xmlCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
                xmlCalendar.setTime(DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);

                return xmlCalendar;
            } catch (DatatypeConfigurationException dce) {
                throw Throwables.propagate(dce);
            }
        } else {
            return null;
        }
    }

}
