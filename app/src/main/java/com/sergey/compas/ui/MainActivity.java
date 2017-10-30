package com.sergey.compas.ui;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.sergey.compas.MyApplication;
import com.sergey.compas.R;
import com.sergey.compas.ui.fragments.BaseFragment;
import com.sergey.compas.ui.fragments.CompassFragment;
import com.sergey.compas.ui.fragments.MapFragment;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final SparseArray<BaseFragment> fragmentArray = new SparseArray<>();
    private GoogleApiClient googleApiClient;
    private BaseFragment baseFragment;
    private LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_main);
        buildClient();
        fragmentArray.put(R.id.action_one, new CompassFragment());
        fragmentArray.put(R.id.action_two, new MapFragment());
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        replaceFragment(fragmentArray.get(R.id.action_one));

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_one:
                    replaceFragment(fragmentArray.get(R.id.action_one));
                    return true;
                case R.id.action_two:
                    replaceFragment(fragmentArray.get(R.id.action_two));
                    return true;
                default:
                    return false;
            }
        });

    }


    private void buildClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        baseFragment.setLocation(location);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setCurrentLocation();

    }


    public void setCurrentLocation() {
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
            return;
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);
            requestLocationUpdate();
            if(lastLocation == null){
                return;
            }
            if (isShouldZoom(lastLocation)) {
                baseFragment.zoomToUserPosition(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
            }
            if (baseFragment instanceof CompassFragment) {
                CompassFragment compassFragment = (CompassFragment) baseFragment;
                compassFragment.setCurrentCoordinates(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
            }
        }
    }

    @SuppressWarnings({"MissingPermission"}) //we already have asked permissions from fragment
    public void requestLocationUpdate() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(10)); //10 seconds
        locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(5)); //5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    public void stopRequest() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRequest();
    }

    private boolean isShouldZoom(Location location) {
        return location != null && baseFragment != null && baseFragment instanceof MapFragment;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        googleApiClient.disconnect();
    }

    private void replaceFragment(BaseFragment fragment) {
        this.baseFragment = fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();

    }


}
