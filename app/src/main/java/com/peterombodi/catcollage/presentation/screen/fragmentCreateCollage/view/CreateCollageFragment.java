package com.peterombodi.catcollage.presentation.screen.fragmentCreateCollage.view;

import android.content.Context;
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
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.peterombodi.catcollage.Application;
import com.peterombodi.catcollage.ObjectGraph;
import com.peterombodi.catcollage.R;
import com.peterombodi.catcollage.presentation.customView.borderedTextView.Border;
import com.peterombodi.catcollage.presentation.customView.borderedTextView.BorderedTextView;
import com.peterombodi.catcollage.presentation.customView.collageView.CollageView2;
import com.peterombodi.catcollage.presentation.customView.collageView.ICollageView;
import com.peterombodi.catcollage.presentation.screen.fragmentCreateCollage.ICreateCollage;
import com.peterombodi.catcollage.presentation.screen.fragmentCreateCollage.presenter.CreateCollagePresenter;

import static com.peterombodi.catcollage.presentation.customView.borderedTextView.BorderedTextView.BORDER_TOP;
import static com.peterombodi.catcollage.utils.Helper.getProgressColor;

public class CreateCollageFragment extends Fragment implements ICreateCollage.IView{

    private static final String TAG = "CreateCollageFragment";
    private static final int BORDER_WIDTH = 4;
    private static final int SEEK_BAR_THUMB_RADIUS = 20;
    private static final String KEY_SUBSCRIBE = "KEY_SUBSCRIBE";
    private static int SEEK_BAR_THUMB_STROKE = 4;

    private SeekBar sbItemsSize;
    private SeekBar sbBackColor;
    private ICollageView iCollageView;
    private BorderedTextView btv1;
    private BorderedTextView btv2;
    private MenuItem action_download;
    private MenuItem action_save;
    private ProgressBar progressBar;

    private ObjectGraph mGraph;
    private CreateCollagePresenter presenter;

    private  ViewTreeObserver.OnGlobalLayoutListener listener;

    public CreateCollageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_collage, container, false);

        iCollageView = (CollageView2) view.findViewById(R.id.cv_MA);

        presenter.registerView(this);

        sbItemsSize = (SeekBar) view.findViewById(R.id.sb_items_size);
        sbBackColor = (SeekBar) view.findViewById(R.id.sb_color);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_spinner);

        btv1 = (BorderedTextView) view.findViewById(R.id.btv1_AM);
        btv2 = (BorderedTextView) view.findViewById(R.id.btv2_AM);

        sbItemsSize.setMax(CollageView2.maxDensity);
        sbItemsSize.setOnSeekBarChangeListener(seekBarChangeListener);
        sbBackColor.setOnSeekBarChangeListener(seekBarChangeListener);
        sbBackColor.setMax(256 * 3 - 1);
        setThumb(sbItemsSize, Color.argb(255, 0, 0, 255), Color.GRAY);

        //set initial color and gradient for views
        listener = new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                setInitParams();
                removeOnGlobalLayoutListener(sbItemsSize, this);
            }
        };
        sbItemsSize.getViewTreeObserver().addOnGlobalLayoutListener(listener);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar1);
        if (toolbar != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
        }
        if (savedInstanceState == null) iCollageView.setCollage(2);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        if (presenter==null) {
            mGraph = ObjectGraph.getInstance(Application.getContext());
            presenter = mGraph.getCreateCollagePresenter();
        }
        super.onAttach(context);
    }

     @Override
    public void onDestroyView() {
        presenter.unRegisterView();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_option, menu);
        action_download = menu.findItem(R.id.action_download);
        action_save = menu.findItem(R.id.action_save);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Операции для выбранного пункта меню
        switch (item.getItemId()) {
            case R.id.action_download:
                presenter.loadData(iCollageView.getItemsCount());
                return true;
            case R.id.action_save:
                presenter.saveImages();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < 16) {
            v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }

    private void setInitParams(){
        int color = getProgressColor(sbBackColor.getProgress());

        btv1.setWidth(btv1.getHeight());
        btv2.setWidth(btv2.getHeight());
        setBorder(color, BORDER_WIDTH);
        btv1.invalidate();
        btv2.invalidate();

        LinearGradient gradient = new LinearGradient(0f, 0f,
                sbBackColor.getProgressDrawable().getBounds().width() - SEEK_BAR_THUMB_RADIUS * 2, 0f,
                new int[]{Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW},
                null, Shader.TileMode.CLAMP);

        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setShader(gradient);

        sbBackColor.setProgressDrawable(shape);
        sbBackColor.invalidate();

        setThumb(sbBackColor, Color.DKGRAY, color);
        iCollageView.setColor(color);

    }

    // thumb for seekBar
    private void setThumb(SeekBar _seekBar, int _strokeColor, int _thumbColor) {
        Bitmap thumb1 = Bitmap.createBitmap(SEEK_BAR_THUMB_RADIUS * 2, SEEK_BAR_THUMB_RADIUS * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(thumb1);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(_strokeColor);
        canvas.drawCircle(SEEK_BAR_THUMB_RADIUS, SEEK_BAR_THUMB_RADIUS, SEEK_BAR_THUMB_RADIUS, p);
        p.setColor(_thumbColor);
        canvas.drawCircle(SEEK_BAR_THUMB_RADIUS, SEEK_BAR_THUMB_RADIUS, SEEK_BAR_THUMB_RADIUS - SEEK_BAR_THUMB_STROKE, p);
        Drawable drawable = new BitmapDrawable(getResources(), thumb1);
        _seekBar.setThumb(drawable);
    }

    //border for TextView
    private void setBorder(int _color, int _width) {
        Border border = new Border(BORDER_TOP);
        border.setColor(_color);
        border.setWidth(_width);
        btv1.setBorders(border);
        btv2.setBorders(border);
    }

    // Menu Items enabling set
    private void setMenuItem(MenuItem _item, boolean _enabled) {
        _item.setEnabled(_enabled);
        _item.getIcon().setAlpha(_enabled ? 255 : 40);

    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar _seekBar, int _progress, boolean _fromUser) {
                    switch (_seekBar.getId()) {
                        case R.id.sb_items_size:
                            if (_fromUser) {
                                Log.d(TAG, "onProgressChanged: ***************************************");
                                presenter.buildCollage(_progress);
                            }
                            break;
                        case R.id.sb_color:
                            Log.d(TAG, "onProgressChanged: progress = " + _progress);
                            int color = getProgressColor(_progress);
                            if (_fromUser) {
                                setThumb(sbItemsSize, color, Color.DKGRAY);
                                setThumb(sbBackColor, Color.GRAY, color);
                                setBorder(color, BORDER_WIDTH);
                                iCollageView.setColor(color);
                            }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }
            };

    @Override
    public void setViewsEnabled(boolean _enabled) {
        Log.d(TAG, "setViewsEnabled: +++++++++++++"+_enabled);
        progressBar.setVisibility(_enabled ? View.GONE : View.VISIBLE);
        iCollageView.setDragEnabled(_enabled);
        sbItemsSize.setEnabled(_enabled);
        if (action_download != null) setMenuItem(action_download, _enabled);
        if (action_save != null) setMenuItem(action_save, _enabled);
        // TODO: 24.01.2017 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //invalidateOptionsMenu();
    }

    @Override
    public ICollageView getICollageView() {
        return iCollageView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.saveCollage();
        outState.putBoolean(KEY_SUBSCRIBE, presenter.downloadingDispose());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean(KEY_SUBSCRIBE))
            presenter.downloadingSubscribe();
        presenter.restoreCollage();

    }
}
