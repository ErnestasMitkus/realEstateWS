package com.mist.rews.security;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.JAXWSAConstants;

import java.util.Map;
import java.util.Properties;

public class WsaHeaderManager {
    private static final String REPLY_TO = "replyTo";

    private final Properties props;

    public WsaHeaderManager(java.util.Properties props) {
        this.props = Preconditions.checkNotNull(props);
    }

    public void add(@org.apache.camel.Properties Map<String, Object> exchangeProperties) {
        AddressingProperties maps = new AddressingProperties();
        boolean present = false;
        if (!Strings.isNullOrEmpty(getReplyTo())) {
            EndpointReferenceType ref = new EndpointReferenceType();
            AttributedURIType add = new AttributedURIType();
            add.setValue(getReplyTo());
            ref.setAddress(add);
            maps.setReplyTo(ref);
            maps.setFaultTo(ref);
            present = true;
        }
        if (present) {
            exchangeProperties.put(JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES, maps);
        }
    }

    private String getReplyTo() {
        return props.getProperty(REPLY_TO);
    }
}
