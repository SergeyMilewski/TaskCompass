package com.sergey.compas;

import android.app.Application;

import com.sergey.compas.dagger.components.AppComponent;
import com.sergey.compas.dagger.components.DaggerAppComponent;
import com.sergey.compas.dagger.modules.AppModule;

/**
 * Created by sergey on 26.10.17.
 */

public class MyApplication extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}