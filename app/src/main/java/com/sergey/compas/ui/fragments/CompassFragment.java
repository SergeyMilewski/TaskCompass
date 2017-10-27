package com.sergey.compas.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.sergey.compas.R;

/**
 * Created by sergey on 26.10.17.
 */

public class CompassFragment extends BaseFragment {

    private ImageView compassView;

    private float currentDegree;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.compas_fragment_layout, null);
        compassView = view.findViewById(R.id.compass_img);
        return view;
    }

    private void rotateImg(float degree) {
        RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(210);
        ra.setFillAfter(true);
        compassView.startAnimation(ra);
        currentDegree = -degree;
    }

}
