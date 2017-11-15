package com.sergey.compas.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.sergey.compas.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergey on 26.10.17.
 */

public abstract class BaseFragment extends Fragment {
    public final static int REQUEST_LOCATION_PERMISSION = 555;

    public interface OnPermissionResponse {
        void onResponse();
    }

    private OnPermissionResponse onPermissionResponse;
    private List<String> permissionsList;
    protected GoogleMap googleMap;


    public void checkPermission(OnPermissionResponse permissionResponse) {
        onPermissionResponse = permissionResponse;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionsList = new ArrayList<>();
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasLocation = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasLocation == PackageManager.PERMISSION_GRANTED) {
                onPermissionResponse.onResponse();
                return;
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            onPermissionResponse.onResponse();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    permissionsList.remove(permissions[i]);
                }
            }
            if (permissionsList.size() == 0) {
                if (onPermissionResponse != null) {
                    onPermissionResponse.onResponse();
                }
            } else {
                showPermissionErrorDialog();
            }

        }
    }

    protected void zoomToUserPosition(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    private void showPermissionErrorDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.dialog_loc_label))
                .setMessage(getString(R.string.restriction_location_message))
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}