package com.peterombodi.catcollage.presentation.screen.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.peterombodi.catcollage.BuildConfig;
import com.peterombodi.catcollage.R;
import com.peterombodi.catcollage.presentation.screen.collage_create.CollageFragment_;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import io.reactivex.disposables.CompositeDisposable;

@SuppressLint("Registered")
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    private static final String TAG_CREATE_COLLAGE_FRAGMENT = "TAG_CREATE_COLLAGE_FRAGMENT";
    private static final int PERMISSIONS_SETTINGS_RESULT_CODE = 501;
    private static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private RxPermissions rxPermissions;
    private CompositeDisposable compositeSubscriptions;

    @ViewById
    protected FrameLayout fragment_container;

    @AfterViews
    protected void initUI() {
        commitCreateCollageFragment();
    }

    @AfterViews
    void initRxPermissions() {
        rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(BuildConfig.DEBUG);
        compositeSubscriptions = new CompositeDisposable();
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

    public void checkPermission() {
        if (rxPermissions == null) return;
        if (!rxPermissions.isGranted(PERMISSION_WRITE_EXTERNAL_STORAGE))
            requestPermission();
    }

    @SuppressLint("CheckResult")
    private void requestPermission() {
        compositeSubscriptions.add(
                rxPermissions
                        .requestEach(PERMISSION_WRITE_EXTERNAL_STORAGE)
                        .subscribe(this::onPermissionChecked));
    }

    @OnActivityResult(PERMISSIONS_SETTINGS_RESULT_CODE)
    void resultFromPermissionsSettings() {
        checkPermission();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1)
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscriptions.clear();
    }

    public RxPermissions getRxPermissions() {
        return rxPermissions;
    }

    public void onPermissionChecked(Permission permission) {
        if (!permission.granted)
            if (permission.shouldShowRequestPermissionRationale) {
                displayInfoAboutPermission();
            } else {
                displayInfoAboutGrantPermissionManually();
            }
    }

    private void displayInfoAboutPermission() {
        Snackbar snackbar = Snackbar.make(fragment_container, R.string.msg_permission_rationale, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.permission_grant, v -> requestPermission())
                .setDuration(4423);
        fixTextStyleInSnackbar(snackbar);
        snackbar.show();
    }

    private void displayInfoAboutGrantPermissionManually() {
        Snackbar snackbar = Snackbar.make(fragment_container, R.string.msg_store_permission_not_granted, Snackbar.LENGTH_LONG)
                .setAction(R.string.permission_settings, v -> {
                    openApplicationSettings();
                    Toast.makeText(this,
                            R.string.msg_location_permission_on_tips,
                            Toast.LENGTH_SHORT)
                            .show();
                });
        fixTextStyleInSnackbar(snackbar);
        snackbar.show();
    }

    private void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSIONS_SETTINGS_RESULT_CODE);
    }

    private void fixTextStyleInSnackbar(final Snackbar snackbar) {
        TextView tv = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        TextView tvAction = snackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
        tv.setMaxLines(6);
        tv.setMinLines(2);
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tvAction.setTypeface(tvAction.getTypeface(), Typeface.BOLD);
    }
}
