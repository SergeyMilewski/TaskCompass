package com.sergey.compas.ui.fragments;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sergey.compas.MyApplication;
import com.sergey.compas.R;
import com.sergey.compas.data.RxLocationEmitter;
import com.sergey.compas.data.RxSensorEmitter;
import com.sergey.compas.ui.MainActivity;
import com.sergey.compas.ui.view.CustomCompasView;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.sergey.compas.ui.fragments.MapFragment.IS_SETTLED_POINT;
import static com.sergey.compas.ui.fragments.MapFragment.LAT;
import static com.sergey.compas.ui.fragments.MapFragment.LNG;

/**
 * Created by sergey on 26.10.17.
 */

public class CompassFragment extends BaseFragment implements CoordinatesDialogFragment.Callback {

    private CustomCompasView customCompasView;
    private TextView currentLongitude, currentLatitude, targetLng, targetLat;
    private float bearing = 0f;
    private LatLng currentLatLng;

    private CompositeDisposable compositDisposable;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    RxSensorEmitter rxSensorEmitter;

    @Inject
    RxLocationEmitter rxLocationEmitter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getAppComponent().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.compas_fragment_layout, null);
        customCompasView = view.findViewById(R.id.compass_img);
        currentLongitude = view.findViewById(R.id.current_longitude);
        currentLatitude = view.findViewById(R.id.current_latitude);
        targetLng = view.findViewById(R.id.target_longitude);
        targetLat = view.findViewById(R.id.target_latitude);
        view.findViewById(R.id.input).setOnClickListener(v -> new CoordinatesDialogFragment().show(getChildFragmentManager(), CoordinatesDialogFragment.TAG));
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).stopRequest();
        if (compositDisposable != null) {
            compositDisposable.dispose();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        compositDisposable = new CompositeDisposable();

        compositDisposable.add(rxSensorEmitter.getFlowable()
                .subscribe(f -> customCompasView.setAzimuth(f), throwable ->
                        new AlertDialog.Builder(getActivity())
                                .setCancelable(false)
                                .setPositiveButton(R.string.ok, (dialog, which) -> {
                                    dialog.dismiss();
                                    getActivity().finish();
                                })
                                .setMessage(throwable.getMessage())
                                .setTitle("Error message")
                                .show()
                ));
        checkPermission(() -> compositDisposable.add(rxLocationEmitter.getFlowableForLocationUpdate()
                .subscribe(location -> setCurrentCoordinates(new LatLng(location.getLatitude(), location.getLongitude())))));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCoordinatesInputted(Location location) {
        sharedPreferences.edit().putLong(LNG, Double.doubleToLongBits(location.getLongitude())).apply();
        sharedPreferences.edit().putLong(LAT, Double.doubleToLongBits(location.getLatitude())).apply();
        sharedPreferences.edit().putBoolean(IS_SETTLED_POINT, true).apply();

    }


    private void setCurrentCoordinates(LatLng currentLatLng) {
        this.currentLatLng = currentLatLng;
        currentLongitude.setText(String.valueOf(currentLatLng.longitude));
        currentLatitude.setText(String.valueOf(currentLatLng.latitude));
        if (sharedPreferences.getBoolean(IS_SETTLED_POINT, false)) {
            double lat = Double.longBitsToDouble(sharedPreferences.getLong(LAT, 0));
            double lng = Double.longBitsToDouble(sharedPreferences.getLong(LNG, 0));
            targetLat.setText(String.valueOf(lat));
            targetLng.setText(String.valueOf(lng));
            setBearing(currentLatLng, new LatLng(lat, lng));

        }
    }

    private void setBearing(LatLng currentLatLng, LatLng targetLatLng) {
        Location currentLoc = new Location("current");
        Location targetLoc = new Location("marker");
        currentLoc.setLongitude(currentLatLng.longitude);
        currentLoc.setLatitude(currentLatLng.latitude);
        targetLoc.setLatitude(targetLatLng.latitude);
        targetLoc.setLongitude(targetLatLng.longitude);
        bearing = currentLoc.bearingTo(targetLoc);
        customCompasView.setBearing(bearing);

    }


    @Override
    public void setLocation(Location location) {
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (sharedPreferences.getBoolean(IS_SETTLED_POINT, false)) {
            double lat = Double.longBitsToDouble(sharedPreferences.getLong(LAT, 0));
            double lng = Double.longBitsToDouble(sharedPreferences.getLong(LNG, 0));
            Location targetLoc = new Location("target");
            targetLoc.setLongitude(lng);
            targetLoc.setLatitude(lat);
            bearing = location.bearingTo(targetLoc);
            customCompasView.setBearing(bearing);
        }
    }
}
