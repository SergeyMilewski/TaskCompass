package com.sergey.compas.data;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sergey.compas.exeptions.LastLocationError;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Created by sergey on 09.11.17.
 */

public class RxLocationEmitter {
    private LocationRequest locationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback locationCallback;
    private OnSuccessListener<Location> onSuccessListener;

    @Inject
    public RxLocationEmitter(Context context) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    private void setupLocationUpdateRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(10)); //10 seconds
        locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(5)); //5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @SuppressWarnings("MissingPermission")
    public Observable<Location> getLastLocation() {
        return Observable.create(e -> {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(onSuccessListener = location -> {
                if (location == null) {
                    e.onError(new LastLocationError("Can't determine location"));
                    return;
                }
                e.onNext(location);
                e.onComplete();
            });
            e.setDisposable(new Disposable() {
                @Override
                public void dispose() {
                    onSuccessListener = null;
                }

                @Override
                public boolean isDisposed() {
                    return onSuccessListener == null;
                }
            });
        });
    }


    @SuppressWarnings("MissingPermission")
    public Flowable<Location> getFlowableForLocationUpdate() {
        return Flowable.create(e -> {
            setupLocationUpdateRequest();
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            e.onNext(location);
                        }
                    }
                }
            }, Looper.myLooper());

            e.setDisposable(new Disposable() {
                @Override
                public void dispose() {
                    if (locationCallback != null) {
                        mFusedLocationClient.removeLocationUpdates(locationCallback);
                        locationCallback = null;
                    }
                }

                @Override
                public boolean isDisposed() {
                    return locationCallback == null;
                }
            });
        }, BackpressureStrategy.LATEST);
    }
}
