package com.example.DeaneryProject.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailVerificationResponse {

    @JsonProperty("isValid")
    private boolean isValid;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}