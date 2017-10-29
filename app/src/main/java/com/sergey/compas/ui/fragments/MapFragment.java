package com.sergey.compas.ui.fragments;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sergey.compas.MyApplication;
import com.sergey.compas.R;
import com.sergey.compas.ui.MainActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Created by smilevkiy on 27.10.17.
 */

public class MapFragment extends BaseFragment implements OnMapReadyCallback {

    private MapView mapView;
    private Marker targetLocation;
    public final static String LNG = "longitude";
    public final static String LAT = "latitude";
    public final static String IS_SETTLED_POINT = "is_settled_point";


    @Inject
    SharedPreferences sharedPreferences;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment_layout, container,
                false);

        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);
        return view;
    }


    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        mapView.onStart();
        super.onStart();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();

    }

    @Override
    public void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        checkPermission(() -> this.googleMap.setMyLocationEnabled(true));
        ((MainActivity) getActivity()).setCurrentLocation();
        this.googleMap.setOnMapLongClickListener(this::putMarker);
        if (sharedPreferences.getBoolean(IS_SETTLED_POINT, false)) {
            double lng = Double.longBitsToDouble(sharedPreferences.getLong(LNG, 0));
            double lat = Double.longBitsToDouble(sharedPreferences.getLong(LAT, 0));
            putMarker(new LatLng(lat, lng));
        }

    }


    private void putMarker(LatLng markerLocation) {
        if (targetLocation != null) {
            targetLocation.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions().position(markerLocation).title("TARGET");
        targetLocation = googleMap.addMarker(markerOptions);
        savePoint(markerLocation);

    }

    @Override
    public void setLocation(Location location) {
        //do nothing
    }

    private void savePoint(LatLng latLng) {
        sharedPreferences.edit().putLong(LNG, Double.doubleToLongBits(latLng.longitude)).apply();
        sharedPreferences.edit().putLong(LAT, Double.doubleToLongBits(latLng.latitude)).apply();
        sharedPreferences.edit().putBoolean(IS_SETTLED_POINT, true).apply();
    }

}
