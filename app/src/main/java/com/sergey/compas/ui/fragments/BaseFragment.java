package com.sergey.compas.ui.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

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


    public boolean checkPermission(OnPermissionResponse permissionResponse) {
        boolean isGranted;
        onPermissionResponse = permissionResponse;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionsList = new ArrayList<>();
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            int hasWriteStoragePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            isGranted = hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED;
            if (!isGranted) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            }
            return isGranted;
        } else {
            return true;
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
            }

        }
    }


}