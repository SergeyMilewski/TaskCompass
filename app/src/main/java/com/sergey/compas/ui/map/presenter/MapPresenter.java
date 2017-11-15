package com.sergey.compas.ui.map.presenter;

import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.sergey.compas.BasePresenter;
import com.sergey.compas.UI;
import com.sergey.compas.data.RxLocationEmitter;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;

/**
 * Created by sergey on 15.11.17.
 */

public class MapPresenter extends BasePresenter<MapPresenter.mapUI> {

    public final static String LNG = "longitude";
    public final static String LAT = "latitude";
    public final static String IS_SETTLED_POINT = "is_settled_point";

    private final RxLocationEmitter rxLocationEmitter;
    private Disposable disposable;
    private final SharedPreferences sharedPreferences;

    @Inject
    public MapPresenter(RxLocationEmitter rxLocationEmitter, SharedPreferences sharedPreferences) {
        this.rxLocationEmitter = rxLocationEmitter;
        this.sharedPreferences = sharedPreferences;
    }


    @Override
    public void attachUI(mapUI ui) {
        super.attachUI(ui);
    }


    @Override
    public void detacheUI() {
        super.detacheUI();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public void zooIfPermissionGranted() {
        disposable = rxLocationEmitter.getLastLocation().subscribe(location ->
                ui.zoomToPosition(new LatLng(location.getLatitude(), location.getLongitude())));
    }

    public void savePoint(LatLng latLng) {
        sharedPreferences.edit().putLong(LNG, Double.doubleToLongBits(latLng.longitude)).apply();
        sharedPreferences.edit().putLong(LAT, Double.doubleToLongBits(latLng.latitude)).apply();
        sharedPreferences.edit().putBoolean(IS_SETTLED_POINT, true).apply();
    }

    public void putSavedMarker() {
        if (sharedPreferences.getBoolean(IS_SETTLED_POINT, false)) {
            double lng = Double.longBitsToDouble(sharedPreferences.getLong(LNG, 0));
            double lat = Double.longBitsToDouble(sharedPreferences.getLong(LAT, 0));
            ui.showSavedMarker(lat, lng);
        }
    }

    public interface mapUI extends UI {

        void showSavedMarker(double lat, double lng);

        void zoomToPosition(LatLng latLng);
    }
}
