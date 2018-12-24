package com.peterombodi.catcollage.presentation.screen.collage_create;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.peterombodi.catcollage.ObjectGraph;
import com.peterombodi.catcollage.R;
import com.peterombodi.catcollage.database.model.CollageItem;
import com.peterombodi.catcollage.presentation.base.MVPFragment;
import com.peterombodi.catcollage.presentation.custom_view.collage.CollageView;
import com.peterombodi.catcollage.presentation.custom_view.collage.ICollageView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import static com.peterombodi.catcollage.utils.Helper.getProgressColor;


@EFragment(R.layout.fragment_create_collage)
@OptionsMenu(R.menu.menu_option)
public class CollageFragment extends MVPFragment<CollageContract.CollagePresenter> implements CollageContract.CreateCollageView, ICollageView {

    private static final String KEY_SUBSCRIBE = "KEY_SUBSCRIBE";
    private static final int BORDER_WIDTH = 4;
    private static final int SEEK_BAR_THUMB_RADIUS = 20;
    private static final int SEEK_BAR_THUMB_STROKE = 4;

    @Bean
    protected CollageInteractor interactor;
    @Bean
    ObjectGraph objectGraph;

    @ViewById(R.id.toolbar)
    protected Toolbar toolbar;
    @ViewById(R.id.progressBar)
    protected ProgressBar progressBar;
    @ViewById(R.id.sb_items_size)
    protected SeekBar sbItemsSize;
    @ViewById(R.id.sb_color)
    protected SeekBar sbBackColor;
    @ViewById(R.id.ivMinSize)
    protected ImageView minSizeView;
    @ViewById(R.id.ivMaxSize)
    protected ImageView maxSizeView;
    @ViewById(R.id.collage_FCC)
    protected CollageView collageView;

    @OptionsMenuItem(R.id.actionDownload)
    protected MenuItem menuDownload;
    @OptionsMenuItem(R.id.actionSave)
    protected MenuItem menuSave;
    @OptionsMenuItem(R.id.actionOpen)
    protected MenuItem menuOpen;
    @OptionsMenuItem(R.id.actionShare)
    protected MenuItem menuShare;


    @org.androidannotations.annotations.res.ColorRes(R.color.colorPrimary)
    protected int colorPrimary;
    @org.androidannotations.annotations.res.ColorRes(R.color.colorPrimaryLight)
    protected int colorPrimaryLight;
    @org.androidannotations.annotations.res.ColorRes(R.color.colorPrimaryDark)
    protected int colorPrimaryDark;

    private ViewTreeObserver.OnGlobalLayoutListener listener;


    @AfterInject
    @Override
    public void initPresenter() {
        presenter = objectGraph.getCollagePresenter();
        presenter.registerFragment(this, interactor);
    }

    @Override
    public String getScreenName() {
        return "CollageFragment";
    }

    @AfterViews
    protected void initViews() {
        sbBackColor.setOnSeekBarChangeListener(seekBarChangeListener);
        sbBackColor.setMax(256 * 3 - 1);
        setThumb(sbItemsSize, Color.argb(255, 0, 0, 255), colorPrimary);
        sbItemsSize.setMax(collageView.getMaxDensity());
        sbItemsSize.setOnSeekBarChangeListener(seekBarChangeListener);
        sbItemsSize.setProgressDrawable(getResources().getDrawable(R.drawable.bg_progress_bar));
        listener = () -> {
            setInitParams();
            sbItemsSize.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        };
        sbItemsSize.getViewTreeObserver().addOnGlobalLayoutListener(listener);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }
        collageView.setCallback(this);
        presenter.restoreCollage();
    }

    @OptionsItem(R.id.actionDownload)
    void loadImages() {
        presenter.loadData(collageView.getItemsCount());
    }

    @OptionsItem(R.id.actionSave)
    void saveCollage() {
        presenter.saveImages();
    }

    private void setInitParams() {
        int color = getProgressColor(sbBackColor.getProgress());

        minSizeView.getDrawable().mutate().setTint(color);
        maxSizeView.getDrawable().mutate().setTint(color);

        LinearGradient gradient = new LinearGradient(0f, 0f,
                sbBackColor.getProgressDrawable().getBounds().width() - SEEK_BAR_THUMB_RADIUS * 2, 0f,
                new int[]{Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW},
                null, Shader.TileMode.CLAMP);

        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setShader(gradient);

        sbBackColor.setProgressDrawable(shape);
        sbBackColor.invalidate();
        setThumb(sbItemsSize, colorPrimaryDark, colorPrimary);
        setThumb(sbBackColor, colorPrimaryDark, color);
        collageView.setColor(color);
    }

    // thumb for seekBar
    private void setThumb(SeekBar seekBar, int strokeColor, int thumbColor) {
        Bitmap thumb1 = Bitmap.createBitmap(SEEK_BAR_THUMB_RADIUS * 2, SEEK_BAR_THUMB_RADIUS * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(thumb1);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(strokeColor);
        canvas.drawCircle(SEEK_BAR_THUMB_RADIUS, SEEK_BAR_THUMB_RADIUS, SEEK_BAR_THUMB_RADIUS, p);
        p.setColor(thumbColor);
        canvas.drawCircle(SEEK_BAR_THUMB_RADIUS, SEEK_BAR_THUMB_RADIUS, SEEK_BAR_THUMB_RADIUS - SEEK_BAR_THUMB_STROKE, p);
        Drawable drawable = new BitmapDrawable(getResources(), thumb1);
        seekBar.setThumb(drawable);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    switch (seekBar.getId()) {
                        case R.id.sb_items_size:
                            if (fromUser) presenter.setCollageDensity(progress);
                            break;
                        case R.id.sb_color:
                            int color = getProgressColor(progress);
                            if (fromUser) {
                                setThumb(sbBackColor, colorPrimaryDark, color);
                                minSizeView.getDrawable().mutate().setTint(color);
                                maxSizeView.getDrawable().mutate().setTint(color);
                                collageView.setColor(color);
                            }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            };

    @Override
    public void setViewsEnabled(boolean enabled) {
        progressBar.setVisibility(enabled ? View.GONE : View.VISIBLE);
        collageView.setDragEnabled(enabled);
        sbItemsSize.setEnabled(enabled);
        setMenuItem(menuDownload, enabled);
        setMenuItem(menuSave, enabled);
        setMenuItem(menuOpen, enabled);
        setMenuItem(menuShare, enabled);
    }

    @Override
    public void buildCollage(int density) {
        collageView.setDragEnabled(false);
        collageView.buildCollage(density);
    }

    @Override
    public void setCollageView(ArrayList<CollageItem> collageItems) {
        collageView.setItemList(collageItems);
        // TODO: 21.12.2018 need implement new logic for set collage views
    }

    @Override
    public ImageView getItemPlaceholder(int viewId) {
        return collageView.getCollageItemView(viewId);
    }

    // Menu Items enabling set
    private void setMenuItem(MenuItem item, boolean enabled) {
        if (item != null) {
            item.setEnabled(enabled);
            item.getIcon().setAlpha(enabled ? 255 : 40);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.setCollage(collageView.getItemList());
        outState.putBoolean(KEY_SUBSCRIBE, presenter.disposeDownloading());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean(KEY_SUBSCRIBE))
            presenter.downloadingSubscribe();
        //presenter.restoreCollage();
    }

    @Override
    public void onBuildCollage(ArrayList<CollageItem> collageItems) {
        presenter.setCollage(collageItems);
    }
}
