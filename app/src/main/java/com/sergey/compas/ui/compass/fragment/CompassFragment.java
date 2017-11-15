package com.sergey.compas.ui.compass.fragment;

import android.app.AlertDialog;
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
import com.sergey.compas.ui.BaseFragment;
import com.sergey.compas.ui.compass.presenter.CompassPresenter;
import com.sergey.compas.ui.dialog.CoordinatesDialogFragment;
import com.sergey.compas.ui.view.CustomCompasView;

import javax.inject.Inject;

/**
 * Created by sergey on 26.10.17.
 */

public class CompassFragment extends BaseFragment implements CoordinatesDialogFragment.Callback, CompassPresenter.CompassUI {

    private CustomCompasView customCompasView;
    private TextView currentLongitude, currentLatitude, targetLng, targetLat;


    @Inject
    CompassPresenter presenter;


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
        presenter.detacheUI();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.attachUI(this);
        checkPermission(() -> presenter.showCurrentLocationIfPermissionGranted());
    }

    @Override
    public void onCoordinatesInputted(Location location) {
        presenter.inputDestination(location);

    }

    @Override
    public void displayCurrentLocation(Location location) {
        currentLongitude.setText(String.valueOf(location.getLongitude()));
        currentLatitude.setText(String.valueOf(location.getLatitude()));
    }

    @Override
    public void displayDestinationLocation(LatLng latLng) {
        targetLat.setText(String.valueOf(latLng.latitude));
        targetLng.setText(String.valueOf(latLng.longitude));
    }

    @Override
    public void displayBearing(float bearing) {
        customCompasView.setBearing(bearing);
    }

    @Override
    public void displayAzimuth(float azimuth) {
        customCompasView.setAzimuth(azimuth);
    }

    @Override
    public void displayNoSensorDialog(Throwable throwable) {
        new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    getActivity().finish();
                })
                .setMessage(throwable.getMessage())
                .setTitle("Error message")
                .show();
    }

}
