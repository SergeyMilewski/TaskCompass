package com.sergey.compas.exeptions;

/**
 * Created by sergey on 14.11.17.
 */

public class MagneticSensorError extends Exception {

    private final String errorMsg;

    public MagneticSensorError(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
