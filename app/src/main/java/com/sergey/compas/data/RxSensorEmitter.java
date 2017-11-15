package com.sergey.compas.data;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.sergey.compas.exeptions.AccelerometerSensorError;
import com.sergey.compas.exeptions.MagneticSensorError;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.disposables.Disposable;

/**
 * Created by sergey on 09.11.17.
 */

public class RxSensorEmitter {

    private final SensorManager sensorManager;
    private final Sensor msensor;
    private final Sensor gsensor;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private Context context;

    private SensorEventListener sensorEventListener;

    @Inject
    public RxSensorEmitter(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public Flowable<Float> getFlowable() {
        return Flowable.create((FlowableOnSubscribe<Float>) e -> {
            if (msensor == null) {
                e.onError(new MagneticSensorError("Device doesn't have magnetic sensor"));
            }
            if (gsensor == null) {
                e.onError(new AccelerometerSensorError("Device doesn't have accelerometer sensor "));
            }

            sensorManager.registerListener(sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    Float calculatedValue = calculateOutPutAzimuth(event);
                    if (calculatedValue != null) {
                        e.onNext(calculatedValue);
                    }
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            }, msensor, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(sensorEventListener, gsensor, SensorManager.SENSOR_DELAY_GAME);
            e.setDisposable(new Disposable() {
                @Override
                public void dispose() {
                    if (sensorEventListener != null) {
                        sensorManager.unregisterListener(sensorEventListener);
                        sensorEventListener = null;
                    }
                    Log.d("Sergey", "on dispose sensor");
                }

                @Override
                public boolean isDisposed() {
                    return sensorEventListener == null;
                }
            });
        }, BackpressureStrategy.LATEST);
    }

    private Float calculateOutPutAzimuth(SensorEvent event) {
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
        float I[] = new float[9];
        float R[] = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
        if (success) {
            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);
            float azimuth = (float) Math.toDegrees(-orientation[0]);
            Log.d("Sergey", "emmit " + (azimuth < 0 ? azimuth + 360 : azimuth));
            return azimuth < 0 ? azimuth + 360 : azimuth;
        }
        Log.d("Sergey", "old value");
        return null;
    }

}
