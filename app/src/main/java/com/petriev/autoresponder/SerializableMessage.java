package com.petriev.autoresponder;

import java.io.Serializable;

/**
 * Created by evgenii on 05.11.16.
 */

public class SerializableMessage implements Serializable {

    private final String address;

    private final boolean isEmail;

    private final String text;

    private final String serviceCenterAddress;

    public SerializableMessage(final String address, final boolean isEmail, final String text, final String serviceCenterAddress) {
        this.address = address;
        this.isEmail = isEmail;
        this.text = text;
        this.serviceCenterAddress = serviceCenterAddress;
    }

    public String getServiceCenterAddress() {
        return serviceCenterAddress;
    }

    public String getText() {
        return text;
    }

    public String getAddress() {
        return address;
    }

    public boolean isEmail() {
        return isEmail;
    }
}
