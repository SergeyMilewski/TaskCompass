package com.sergey.compas.ui.fragments;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sergey.compas.R;

/**
 * Created by sergey on 29.10.17.
 */

public class CoordinatesDialogFragment extends DialogFragment {
    public interface Callback {
        void onCoordinatesInputted(Location location);
    }

    public static final String TAG = CoordinatesDialogFragment.class.getSimpleName();

    protected EditText latitude;


    protected EditText longitude;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.input_dialog_title)
                .setView(createDialogView())
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null);
        AlertDialog dialog = builder.create();
        initializeDialog(dialog);
        return dialog;
    }

    private void passResult() {
        Location location = new Location("");
        location.setLatitude(Float.parseFloat(latitude.getText().toString()));
        location.setLongitude(Float.parseFloat(longitude.getText().toString()));

        getParent().onCoordinatesInputted(location);
    }

    private Callback getParent() {
        return (Callback) getParentFragment();
    }

    private boolean validateCoordinates() {
        boolean valid = validateLatitude(latitude.getText().toString())
                && validateLongitude(longitude.getText().toString());

        if (!valid)
            Toast.makeText(getContext(), getString(R.string.incorrect_value), Toast.LENGTH_SHORT).show();

        return valid;
    }

    private View createDialogView() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.dialog_coordinates_layout, null);
        latitude = view.findViewById(R.id.coordinates_lat);
        longitude = view.findViewById(R.id.coordinates_long);
        return view;
    }

    private void initializeDialog(AlertDialog dialog) {
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialog.setOnShowListener(d -> {

            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {

                if (validateCoordinates()) {
                    passResult();
                    dismiss();
                }

            });
        });
    }

    public static boolean validateLatitude(String lat) {
        try {
            Float f = Float.parseFloat(lat);
            return f >= -90 && f <= 90;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

    public static boolean validateLongitude(String longitude) {
        try {
            Float f = Float.parseFloat(longitude);
            return f >= -180 && f <= 180;
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
    }

}
