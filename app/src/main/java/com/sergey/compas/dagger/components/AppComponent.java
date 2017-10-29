package com.sergey.compas.dagger.components;

import com.sergey.compas.dagger.modules.AppModule;
import com.sergey.compas.ui.MainActivity;
import com.sergey.compas.ui.fragments.CompassFragment;
import com.sergey.compas.ui.fragments.MapFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by sergey on 26.10.17.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);

    void inject(MapFragment mapFragment);

    void inject(CompassFragment compassFragment);
}
