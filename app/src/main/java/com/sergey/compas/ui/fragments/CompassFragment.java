package com.sergey.compas.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.sergey.compas.ui.MainActivity;
import com.sergey.compas.ui.view.CustomCompasView;

import javax.inject.Inject;

import static com.sergey.compas.ui.fragments.MapFragment.IS_SETTLED_POINT;
import static com.sergey.compas.ui.fragments.MapFragment.LAT;
import static com.sergey.compas.ui.fragments.MapFragment.LNG;

/**
 * Created by sergey on 26.10.17.
 */

public class CompassFragment extends BaseFragment implements SensorEventListener, CoordinatesDialogFragment.Callback {

    private CustomCompasView customCompasView;
    private TextView currentLongitude, currentLatitude, targetLng, targetLat;

    private SensorManager sensorManager;
    private Sensor msensor;
    private Sensor gsensor;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float azimuth = 0f;
    private float bearing = 0f;
    private LatLng currentLatLng;


    @Inject
    SharedPreferences sharedPreferences;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getAppComponent().inject(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.compas_fragment_layout, null);
        //  compassView = view.findViewById(R.id.compass_img);
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
    }

    @Override
    public void onStart() {
        super.onStart();
        registerSensors();
    }

    private void registerSensors() {
        sensorManager.registerListener(this, gsensor,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,
                SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onCoordinatesInputted(Location location) {
        sharedPreferences.edit().putLong(LNG, Double.doubleToLongBits(location.getLongitude())).apply();
        sharedPreferences.edit().putLong(LAT, Double.doubleToLongBits(location.getLatitude())).apply();
        sharedPreferences.edit().putBoolean(IS_SETTLED_POINT, true).apply();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final MainActivity mainActivity = (MainActivity) getActivity();
        checkPermission(mainActivity::setCurrentLocation);
        setUpSensors();

    }

    private void setUpSensors() {
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (currentLatLng != null) {
            setCurrentCoordinates(currentLatLng);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic[0] = alpha * geomagnetic[0] + (1 - alpha) * event.values[0];
            geomagnetic[1] = alpha * geomagnetic[1] + (1 - alpha) * event.values[1];
            geomagnetic[2] = alpha * geomagnetic[2] + (1 - alpha) * event.values[2];

        }
        float R[] = new float[9];
        float I[] = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
        if (success) {
            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);
            azimuth = (float) Math.toDegrees(-orientation[0]);
            azimuth = azimuth < 0 ? azimuth + 360 : azimuth;
            customCompasView.setAzimuth(azimuth);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing
    }

    public void setCurrentCoordinates(LatLng currentLatLng) {
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
