package com.sergey.compas.dagger.components;

import com.sergey.compas.dagger.modules.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by sergey on 26.10.17.
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
}
