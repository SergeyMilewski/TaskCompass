package com.sergey.compas.exeptions;

/**
 * Created by sergey on 15.11.17.
 */

public class LastLocationError extends Exception {

    private String msg;

    public LastLocationError(String msg) {
        this.msg = msg;
    }
}
