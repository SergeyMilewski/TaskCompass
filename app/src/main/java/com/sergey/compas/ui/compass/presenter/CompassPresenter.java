package com.sergey.compas.ui.compass.presenter;

import android.content.SharedPreferences;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.sergey.compas.BasePresenter;
import com.sergey.compas.UI;
import com.sergey.compas.data.RxLocationEmitter;
import com.sergey.compas.data.RxSensorEmitter;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.sergey.compas.ui.map.presenter.MapPresenter.IS_SETTLED_POINT;
import static com.sergey.compas.ui.map.presenter.MapPresenter.LAT;
import static com.sergey.compas.ui.map.presenter.MapPresenter.LNG;

/**
 * Created by sergey on 15.11.17.
 */

public class CompassPresenter extends BasePresenter<CompassPresenter.CompassUI> {

    private final RxLocationEmitter rxLocationEmitter;
    private final RxSensorEmitter rxSensorEmitter;
    private final SharedPreferences sharedPreferences;
    private CompositeDisposable compositDisposable;


    @Inject
    public CompassPresenter(RxLocationEmitter rxLocationEmitter, RxSensorEmitter rxSensorEmitter, SharedPreferences sharedPreferences) {
        this.rxLocationEmitter = rxLocationEmitter;
        this.rxSensorEmitter = rxSensorEmitter;
        this.sharedPreferences = sharedPreferences;
    }


    @Override
    public void attachUI(CompassUI ui) {
        super.attachUI(ui);
        compositDisposable = new CompositeDisposable();
        compositDisposable.add(rxSensorEmitter.getFlowable().subscribe(ui::displayAzimuth, ui::displayNoSensorDialog));
    }

    @Override
    public void detacheUI() {
        super.detacheUI();
        if (compositDisposable != null) {
            compositDisposable.dispose();
        }
    }

    public void showCurrentLocationIfPermissionGranted() {
        compositDisposable.add(rxLocationEmitter.getFlowableForLocationUpdate().subscribe(loc -> {
            ui.displayCurrentLocation(loc);
            showTargetLocAndBaring(loc);
        }));
    }


    private void showTargetLocAndBaring(Location current) {
        if (sharedPreferences.getBoolean(IS_SETTLED_POINT, false)) {
            double lat = Double.longBitsToDouble(sharedPreferences.getLong(LAT, 0));
            double lng = Double.longBitsToDouble(sharedPreferences.getLong(LNG, 0));
            LatLng latLng = new LatLng(lat, lng);
            ui.displayDestinationLocation(latLng);
            setBearing(current, latLng);

        }
    }

    private void setBearing(Location currentLoc, LatLng targetLatLng) {
        Location targetLoc = new Location("marker");
        targetLoc.setLatitude(targetLatLng.latitude);
        targetLoc.setLongitude(targetLatLng.longitude);
        ui.displayBearing(currentLoc.bearingTo(targetLoc));

    }


    public void inputDestination(Location location) {
        sharedPreferences.edit().putLong(LNG, Double.doubleToLongBits(location.getLongitude())).apply();
        sharedPreferences.edit().putLong(LAT, Double.doubleToLongBits(location.getLatitude())).apply();
        sharedPreferences.edit().putBoolean(IS_SETTLED_POINT, true).apply();
        ui.displayDestinationLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    public interface CompassUI extends UI {
        void displayBearing(float bearing);

        void displayAzimuth(float azimuth);

        void displayCurrentLocation(Location location);

        void displayDestinationLocation(LatLng latLng);

        void displayNoSensorDialog(Throwable throwable);
    }
}
