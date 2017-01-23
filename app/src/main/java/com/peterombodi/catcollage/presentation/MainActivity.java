package com.peterombodi.catcollage.presentation;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.peterombodi.catcollage.Application;
import com.peterombodi.catcollage.ObjectGraph;
import com.peterombodi.catcollage.R;
import com.peterombodi.catcollage.data.api.DownloadDataRx;
import com.peterombodi.catcollage.data.model.CatApiResponse;
import com.peterombodi.catcollage.data.model.Image;
import com.peterombodi.catcollage.presentation.base.ResponseCallback;
import com.peterombodi.catcollage.presentation.customView.borderedTextView.Border;
import com.peterombodi.catcollage.presentation.customView.borderedTextView.BorderedTextView;
import com.peterombodi.catcollage.presentation.customView.collageView.CollageView;
import com.peterombodi.catcollage.presentation.customView.collageView.ICollageView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

import static com.peterombodi.catcollage.presentation.customView.borderedTextView.BorderedTextView.BORDER_TOP;


public class MainActivity extends AppCompatActivity implements ResponseCallback {

    private static final String TAG = "MainActivity";
    private static final int BORDER_WIDTH = 4;
    private static final int SEEK_BAR_THUMB_RADIUS = 30;
    private static final String KEY_ARRAY_LIST = "KEY_ARRAY_LIST";
    private static final String KEY_SUBSCRIBE = "KEY_SUBSCRIBE";

    private static int SEEK_BAR_THUMB_SIZE = 42;
    private static int SEEK_BAR_THUMB_STROKE = 4;
    private SeekBar sbItemsSize;
    private SeekBar sbBackColor;
    private ICollageView iCollageView;
    private BorderedTextView btv1;
    private BorderedTextView btv2;
    private MenuItem action_download;
    private MenuItem action_save;

    private DownloadDataRx downloadDataRx;

    private Disposable subscription;
    private PublishSubject<Integer> subject;

    private ObjectGraph mGraph;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private int itemsQuantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sbItemsSize = (SeekBar) findViewById(R.id.sb_items_size);
        sbBackColor = (SeekBar) findViewById(R.id.sb_color);
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        progressBar = (ProgressBar) findViewById(R.id.progress_spinner);

        iCollageView = (CollageView) findViewById(R.id.cv_MA);

        btv1 = (BorderedTextView) findViewById(R.id.btv1_AM);
        btv2 = (BorderedTextView) findViewById(R.id.btv2_AM);

        sbItemsSize.setMax(CollageView.maxDensity);
        sbItemsSize.setOnSeekBarChangeListener(seekBarChangeListener);
        sbBackColor.setOnSeekBarChangeListener(seekBarChangeListener);
        sbBackColor.setMax(256 * 3 - 1);
        setThumb(sbItemsSize, Color.argb(255, 0, 0, 255), Color.GRAY);


        //downloadDataRx = ((Application) getApplication()).getDownloadDataRx();
        mGraph = ObjectGraph.getInstance(Application.getContext());

        downloadDataRx = mGraph.getDownloadDataRx();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        if (savedInstanceState == null) iCollageView.setCollage(2);
//        setBorder(Color.BLACK, BORDER_WIDTH);

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        action_download = menu.findItem(R.id.action_download);
        action_save = menu.findItem(R.id.action_save);
        Log.d(TAG, "onCreateOptionsMenu: " + action_download + action_save);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Операции для выбранного пункта меню
        switch (item.getItemId()) {
            case R.id.action_download:
                loadData();
                return true;
            case R.id.action_save:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        btv1.invalidate();
        btv2.invalidate();
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
                                iCollageView.setDragEnabled(false);
                                iCollageView.setCollage(_progress);
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

    private int getProgressColor(int _progress) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (_progress < 256) {
            b = 255 - _progress % 256;
            r = _progress;
        } else if (_progress < 256 * 2) {
            g = _progress % 256;
            r = 255 - _progress % 256;
        } else if (_progress < 256 * 3) {
            g = 255;
            r = _progress % 256;
        }
        return Color.argb(255, r, g, b);
    }

    @Override
    public void onRefreshResponse(Object _data) {
        Log.d(TAG, "onRefreshResponse: " + _data.toString());
    }

    @Override
    public void onRefreshFailure() {
        Log.d(TAG, "onRefreshFailure: ");
    }

    //    private void getCatsList() {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://thecatapi.com/")
//                .addConverterFactory(SimpleXmlConverterFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .build();
//
//        CatApiRestRx getData = retrofit.create(CatApiRestRx.class);
//
//        Observable<CatApiResponse> observableRetrofit = getData.connect("xml", iCollageView.getItemsCount(), "small");
//
//        observableRetrofit.subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(response -> {
//                    Log.d("CatApiResponse", response.toString());
//                });
//    }


    public void loadData() {
        setViewsEnabled(false);

        itemsQuantity = iCollageView.getItemsCount();
        if (subject == null) subject = iCollageView.getSubjectLoadImage();
        if (subscription==null || subscription.isDisposed()) subscription = subject.subscribe(this::progressCheck);

        Log.d(TAG, "+---- ---------------------- loadData: itemsCount=" + itemsQuantity);

        Observable<CatApiResponse> observableRetrofit = (Observable<CatApiResponse>)
                downloadDataRx.getPreparedObservable(downloadDataRx.getAPI().connect("xml", itemsQuantity, "small"),
                        CatApiResponse.class, true, false);

        observableRetrofit.subscribe(new DisposableObserver<CatApiResponse>() {

            @Override
            public void onComplete() {
                Log.d("CatApiResponse", "onComplete: ----------------------");
            }

            @Override
            public void onError(Throwable e) {
                Log.d("CatApiResponse", "-------------------- onError: " + e.toString());
            }

            @Override
            public void onNext(CatApiResponse response) {
                List<Image> images = response.getImageList();
                Log.d("CatApiResponse", "------------------------ images.size() = " + images.size());
                iCollageView.downloadImages(images);

            }
        });
    }

    private void setViewsEnabled(boolean _enabled) {
        progressBar.setVisibility(_enabled ? View.GONE : View.VISIBLE);
        iCollageView.setDragEnabled(_enabled);
        sbItemsSize.setEnabled(_enabled);
        if (action_download!=null) setMenuItem(action_download, _enabled);
        if (action_save!=null) setMenuItem(action_save, _enabled);
        invalidateOptionsMenu();
    }

    private void progressCheck(int _next) {

        switch (_next) {
            case 0:
//                break;
            case -1:
                itemsQuantity--;
                setViewsEnabled(itemsQuantity == 0);
                break;
            default:
                setViewsEnabled(false);
                itemsQuantity = _next;
        }

        Log.d(TAG, "+----progressCheck: itemsQuantity = " + itemsQuantity + " / _next = " + _next);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean isSubscribe = false;
        if (subscription != null && !subscription.isDisposed()) {
            isSubscribe = true;
            subscription.dispose();
            Log.d(TAG, "onSaveInstanceState: +---- "+subscription.isDisposed());
        }
        outState.putBoolean(KEY_SUBSCRIBE, isSubscribe);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(KEY_SUBSCRIBE)) {
                if (subject == null) subject = iCollageView.getSubjectLoadImage();
                Log.d(TAG, "onRestoreInstanceState: +----");
                subscription = subject.subscribe(this::progressCheck);
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

}
