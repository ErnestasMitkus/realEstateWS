package com.mist.rews;

import com.mist.rews.services.xsd.realestate.FaultType;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeProperty;

import java.math.BigInteger;

public enum RealEstateFaults {

    REGISTER_ESTATE__LOCATION_ALREADY_REGISTERED(1, "Real estate is already registered in that location."),
    REGISTER_ESTATE__LOCATION_ALREADY_REGISTERED_DIFFERENT_OWNER(2, "Real estate is already registered in that location with a different owner."),
    TRANSFER_ESTATE__ESTATE_NOT_REGISTERED(3, "Real estate with specified id is not registered."),
    TRANSFER_ESTATE__NOT_ESTATE_OWNER(4, "Cannot transfer estate, since you are not the current owner of the specified estate."),
    ;

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private final RealEstateFaultException exception;

    RealEstateFaults(int code, String message) {
        exception = new RealEstateFaultException(code, message);
    }

    public void throwException() {
        throw exception;
    }

    public static void throwUnknownException(String message) {
        throw new RealEstateFaultException(0, "Unknown error." + message);
    }

    public static FaultType transformToExceptionBody(@ExchangeProperty(Exchange.EXCEPTION_CAUGHT) RealEstateFaultException exception) {
        return OBJECT_FACTORY.createFaultType()
            .withErrorCode(exception.getCode())
            .withErrorMessage(exception.getMessage());
    }

    public static class RealEstateFaultException extends RuntimeException {
        private final BigInteger code;

        public RealEstateFaultException(int code, String message) {
            super(message);
            this.code = BigInteger.valueOf(code);
        }

        public BigInteger getCode() {
            return code;
        }
    }
}
