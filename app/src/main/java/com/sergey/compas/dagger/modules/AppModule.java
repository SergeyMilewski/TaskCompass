package com.sergey.compas.dagger.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sergey on 26.10.17.
 */

@Module
public class AppModule {
    private Application mApplication;
    private Context mContext;

    public AppModule(Application mApplication) {
        this.mApplication = mApplication;
        mContext = mApplication.getApplicationContext();

    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }


    @Provides
    @Singleton
    Context provideApplicationContext() {
        return mContext;
    }
}
