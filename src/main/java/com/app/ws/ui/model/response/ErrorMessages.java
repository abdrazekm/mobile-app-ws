package com.app.ws.ui.model.response;

public enum ErrorMessages {
    MISSING_REQUIRED_FIELDS("Missing Required Fields. Please check documentation for required fields"),
    RECORD_ALREADY_EXISTS("Record Already Exists"),
    INTERNAL_SERVER_ERROR("Internal Server Error"),
    NO_RECORD_FOUND("Record wih provided id is not found"),
    AUTHENTICATION_FAILED("Authentication Failed"),
    COULD_NOT_UPDATE_FIELD("Could not update field"),
    COULD_NOT_DELETE_FIELD("Could not delete field"),
    EMAIL_ADDRESS_NOT_VERIFIED("Email address not verified");

    private String errorMessage;

    ErrorMessages(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
