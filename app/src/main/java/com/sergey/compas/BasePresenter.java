package com.sergey.compas;

/**
 * Created by sergey on 15.11.17.
 */

public abstract class BasePresenter<T extends UI> {

    protected T ui;

    public void attachUI(T ui) {
        this.ui = ui;
    }

    public void detacheUI() {
        ui = null;
    }
}
