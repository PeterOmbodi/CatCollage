package com.peterombodi.catcollage.presentation.screen.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.peterombodi.catcollage.R;
import com.peterombodi.catcollage.presentation.screen.collage_create.CollageFragment;
import com.peterombodi.catcollage.presentation.screen.collage_create.CollageFragment_;

import org.androidannotations.annotations.EActivity;

@SuppressLint("Registered")
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private static final String TAG_CREATE_COLLAGE_FRAGMENT = "TAG_CREATE_COLLAGE_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            commitCreateCollageFragment();
        }
    }

    private void commitCreateCollageFragment() {
        CollageFragment_ createCollageFragment = new CollageFragment_();
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(TAG_CREATE_COLLAGE_FRAGMENT)
                .replace(R.id.fragment_container, createCollageFragment, TAG_CREATE_COLLAGE_FRAGMENT)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

}
