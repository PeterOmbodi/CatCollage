package com.peterombodi.catcollage.presentation.screen.activityMain;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.peterombodi.catcollage.R;
import com.peterombodi.catcollage.presentation.screen.fragmentCreateCollage.CreateCollageFragment;


public class MainActivity extends AppCompatActivity {

    private static final String TAG_CREATE_COLLAGE_FRAGMENT = "TAG_CREATE_COLLAGE_FRAGMENT";

    //implements IMainActivity.IView

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            commitCreateCollageFragment();
        }
    }

    private void commitCreateCollageFragment() {
        CreateCollageFragment createCollageFragment = new CreateCollageFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(TAG_CREATE_COLLAGE_FRAGMENT)
                .replace(R.id.fragment_container, createCollageFragment, TAG_CREATE_COLLAGE_FRAGMENT)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }
}
