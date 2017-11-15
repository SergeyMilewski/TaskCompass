package com.sergey.compas.ui;


import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;

import com.sergey.compas.MyApplication;
import com.sergey.compas.R;
import com.sergey.compas.ui.compass.fragment.CompassFragment;
import com.sergey.compas.ui.map.fragment.MapFragment;

public class MainActivity extends AppCompatActivity {

    private final SparseArray<BaseFragment> fragmentArray = new SparseArray<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_main);
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

    private void replaceFragment(BaseFragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();

    }


}
