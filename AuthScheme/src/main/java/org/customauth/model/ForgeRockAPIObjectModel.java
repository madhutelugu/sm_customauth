package org.customauth.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ForgeRockAPIObjectModel implements Serializable {

    private String authId;

    private List<Callback> callbacks;

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public List<Callback> getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(List<Callback> callbacks) {
        this.callbacks = callbacks;
    }
}
