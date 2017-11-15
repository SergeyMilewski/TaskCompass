package com.sergey.compas.exeptions;

/**
 * Created by sergey on 14.11.17.
 */

public class AccelerometerSensorError extends Exception {

    private String errMsg;

    public AccelerometerSensorError(String errMsg) {
        this.errMsg = errMsg;
    }
}
