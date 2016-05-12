package com.mist.rews.config;

import com.mist.rews.security.WssCallbackHandler;

import java.util.Properties;

public class Configuration {

    public Properties wsaProperties() {
        Properties props = new Properties();

        props.setProperty("replyTo", "http://www.w3.org/2005/08/addressing/anonymous");

        return props;
    }

    public Properties wssProperties() {
        Properties props = new Properties();

        props.put("ws-security.encryption.properties", encryptionProperties());
        props.put("ws-security.signature.properties", signatureProperties());

        props.setProperty("ws-security.trustStore.path", "/usr/local/tomcat/conf/tls.jks");
        props.setProperty("ws-security.password", "changeit");
        props.put("ws-security.callback-handler", new WssCallbackHandler(props));
        props.setProperty("ws-security.encryption.username", "client");
        props.setProperty("ws-security.signature.username", "server");

        return props;
    }

    private static Properties encryptionProperties() {
        Properties props = new Properties();

        props.setProperty("org.apache.ws.security.crypto.merlin.keystore.file", "../RealEstateServer.jks");
        props.setProperty("org.apache.ws.security.crypto.merlin.keystore.password", "changeit");
        props.setProperty("org.apache.ws.security.crypto.merlin.keystore.type", "jks");
        props.setProperty("org.apache.ws.security.crypto.provider", "org.apache.ws.security.components.crypto.Merlin");

        return props;
    }

    private static Properties signatureProperties() {
        Properties props = new Properties();

        props.setProperty("org.apache.ws.security.crypto.merlin.keystore.file", "../RealEstateServer.jks");
        props.setProperty("org.apache.ws.security.crypto.merlin.keystore.password", "changeit");
        props.setProperty("org.apache.ws.security.crypto.merlin.keystore.type", "jks");
        props.setProperty("org.apache.ws.security.crypto.provider", "org.apache.ws.security.components.crypto.Merlin");

        return props;
    }
}
