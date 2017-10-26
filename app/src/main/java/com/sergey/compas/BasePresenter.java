package com.sergey.compas;

import com.sergey.compas.ui.UI;

/**
 * Created by sergey on 26.10.17.
 */

public abstract class BasePresenter<T extends UI> {

    protected T ui;

    public void attacheUI(T ui) {
        this.ui = ui;
    }

    public void detachUI() {
        ui = null;
    }
}