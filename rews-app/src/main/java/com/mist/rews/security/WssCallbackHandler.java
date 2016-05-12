package com.mist.rews.security;

import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.Properties;

public class WssCallbackHandler implements CallbackHandler {

    public static final String SIGNATURE_PASSWORD = "ws-security.signature.password";
    private static final String FALLBACK_PASSWORD = SecurityConstants.PASSWORD;

    private Properties props;

    private static final Logger log = LoggerFactory.getLogger(WssCallbackHandler.class);

    public WssCallbackHandler(Properties properties) {
        this.props = properties;
    }

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (!(callbacks[i] instanceof WSPasswordCallback)) {
                throw new IllegalStateException("Unsupported password callback " + callbacks[i] +
                        ". Must be " + PasswordCallback.class);
            }

            WSPasswordCallback cb = (WSPasswordCallback) callbacks[i];
            String passwordProperty;
            switch (cb.getUsage()) {
                case WSPasswordCallback.DECRYPT:
                    passwordProperty = FALLBACK_PASSWORD;
                    break;
                case WSPasswordCallback.SIGNATURE:
                    passwordProperty = SIGNATURE_PASSWORD;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown WSS password callback usage: " + cb.getUsage());
            }

            String password = getPassword(passwordProperty);
            boolean found = false;
            if (password != null) {
                cb.setPassword(password);
                found = true;
            } else if (!FALLBACK_PASSWORD.equals(passwordProperty)) {
                password = getPassword(FALLBACK_PASSWORD);
                if (password != null) {
                    cb.setPassword(password);
                    found = true;
                }
            }
            if (!found) {
                log.warn("Unknown WSS password for " + cb.getIdentifier() + " with usage " + cb.getUsage());
            }
        }
    }

    private String getPassword(String propertyName) {
        Object password = props.get(propertyName);

        if (password != null && !(password instanceof String)) {
            throw new IllegalStateException("Password in property " + propertyName
                    + " must be a string. It's " + password.getClass() + "now.");
        }
        return (String) password;
    }
}
