package com.peterombodi.catcollage.presentation.customView.collageView;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.peterombodi.catcollage.R;
import com.peterombodi.catcollage.data.model.CollageItem;
import com.peterombodi.catcollage.data.model.Image;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

//import java.util.ArrayList;
//import java.util.Random;
//import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Admin on 15.12.2016.
 */

public class CollageView extends PercentRelativeLayout implements ICollageView {

    private static final String TAG = "CollageView";
    public static final int maxDensity = 11;
    private static final int SMALL_SIZE = 1;
    private static final int MIDDLE_SIZE = 2;
    private static final int BIG_SIZE = 3;
    private static final CharSequence KEY_ITEM = "KEY_ITEM";
    private static final String TAG_SUPER_STATE = "TAG_SUPER_STATE";
    private static final String TAG_ITEM_LIST = "TAG_ITEM_LIST";
    private static final int KEY_URL = 1;
    private static final int KEY_ID = 2;


    private float gridLineWidth;
    private float sizeSquareSmall;
    private float sizeSquareMiddle;
    private float sizeSquareBig;

    private Context context;
    private PercentRelativeLayout percentRelativeLayout;
//    private PublishSubject<ArrayList<CollageItem>> subjectLoadImage;
//    private PublishSubject<Integer> subject1;

    private Disposable disposableSetCollage;
    private Disposable disposableGetImages;
    private PublishSubject<Integer> subjectLoadImage;
    private PublishSubject<Integer> subjectProgress;

    private boolean mInView = false;
    //private Bitmap tmpBitmap;
    private BitmapDrawable targetDrawable;
    private int targetViewId;
    private int sourceViewId;
    private ImageSwitcher sourceView;
    private ImageSwitcher targetView;


    private int itemsCount;
    private int itemsProgress;
    private ArrayList<CollageItem> collageItemList;
    private boolean dragEnabled;

    private Animation dragIn;
    private Animation dragOut;
    private Animation dropIn;
    private Animation dropOut;


    public CollageView(Context context) throws InterruptedException {
        super(context);
        init(context);
    }

    public CollageView(Context context, AttributeSet attrs) throws InterruptedException {
        super(context, attrs);
        init(context);
    }

    public CollageView(Context context, AttributeSet attrs, int defStyleAttr) throws InterruptedException {
        super(context, attrs, defStyleAttr);
        init(context);
    }


//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        super.onLayout(changed, left, top, right, bottom);
//        Log.d(TAG, "onLayout: ___________________");
//        //setCollage(5);
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: _____________________w, h, oldw, oldh - " + w + ", " + h + ", " + oldw + ", " + oldh);
        int viewHeight = h;
        int viewWidth = w;
        float squareSize = ((viewHeight > viewWidth) ? viewWidth : viewHeight);
        int newSize = (int) (squareSize);

        Log.d(TAG, "onSizeChanged: __________________ newSize = " + newSize);
        if (newSize != 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(newSize, newSize);
            // TODO: 11.01.2017 привязка к конкретной реализации!!!!
            percentRelativeLayout = (PercentRelativeLayout) findViewById(R.id.cv_MA);
            percentRelativeLayout.setLayoutParams(params);
            //percentRelativeLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            requestLayout();
            if (collageItemList != null) {
                Log.d(TAG, "onSizeChanged: collageItemList" + collageItemList.size());
//                setAllViews(collageItemList);
            }
        }

        //Log.d(TAG, "onSizeChanged: percentRelativeLayout"+percentRelativeLayout.getRootView().getClass().getName());
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//    }

    private void init(Context _context) throws InterruptedException {
        context = _context;
        Log.d(TAG, "init: --------------------------------");
        itemsCount = 0;

        gridLineWidth = 0.01f;
        sizeSquareSmall = (1 - gridLineWidth * 10) / 9;
        sizeSquareMiddle = (1 - gridLineWidth * 6) / 4.5f;
        sizeSquareBig = (1 - gridLineWidth * 4) / 3;


        if (!this.isInEditMode()) {
            if (collageItemList == null || collageItemList.size() == 0) {
                Log.d(TAG, "init: --------------------------------");
                //setCollage(2);
            }

            // animation drag on view
            dragIn = new AlphaAnimation(0, 1);
            dragIn.setDuration(200);
            dragOut = new AlphaAnimation(1, 0);
            dragOut.setDuration(200);

            // animation drop on view
            dropIn = AnimationUtils.loadAnimation(context, R.anim.left_to_right_in);
            dropOut = AnimationUtils.loadAnimation(context, R.anim.left_to_right_out);

            subjectLoadImage = PublishSubject.create();
        } else {
            ArrayList<CollageItem> itemArrayList = getNewCollageList(10);
            setAllViews(itemArrayList);
        }

    }

    @Override
    public void setCollage(int _density) {

        if (disposableSetCollage != null && !disposableSetCollage.isDisposed()) {
            disposableSetCollage.dispose();
        }
        disposableSetCollage = Observable.just(_density)
                .debounce(300, TimeUnit.MILLISECONDS)
                .map(this::getNewCollageList)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setAllViews);
    }


    @Override
    public void setColor(int _color) {
        setBackgroundColor(_color);
    }

    @Override
    public void setDragEnabled(boolean _enabled) {
        dragEnabled = _enabled;
    }

    @Override
    public int getItemsCount() {
        return itemsCount;
    }

    @Override
    public ArrayList<CollageItem> getItemList() {
        return collageItemList;
    }

    @Override
    public void setItemList(ArrayList<CollageItem> _items) {
        collageItemList = _items;
        Log.d(TAG, "setItemList: _________________");
        setAllViews(collageItemList);
    }

    @Override
    public void downloadImages(List<Image> _images) {

        if (disposableGetImages == null || disposableGetImages.isDisposed())
            disposableGetImages = subjectLoadImage.subscribe(this::progressCheck);

        int i = 0;
        subjectLoadImage.onNext(collageItemList.size());
        for (CollageItem item : collageItemList) {
            item.setUrl(_images.get(i).getUrl());
            i++;
            downloadItemImage(item);
        }
    }

    private void progressCheck(int _next) {
        switch (_next){
            case 0:
//                break;
            case 1:
                itemsProgress--;
                Log.d(TAG, "-------progressCheck: itemsQuantity = " + itemsProgress);
                break;
            default:
                itemsProgress = _next;
        }

    }

    private void downloadItemImage(CollageItem _item) {
        ImageSwitcher imageSwitcher = (ImageSwitcher) findViewById(_item.getViewId());
        if (imageSwitcher != null) {
            imageSwitcher.setDisplayedChild(0);
            imageSwitcher.setTag(_item.getUrl());
            Log.d(TAG, "downloadImages: x = " + _item.getPosX() + " / y = " + _item.getPosY()
                    + " / item.getUrl() =" + imageSwitcher.getTag() + " / getChildCount()* = "
                    + imageSwitcher.getChildCount());
            //ImageView imageView = (ImageView) imageSwitcher.getChildAt(imageSwitcher.getChildCount());
            ImageView imageView = (ImageView) imageSwitcher.getChildAt(0);
            Picasso.with(context)
                    .load(_item.getUrl())
                    .transform(new CropSquareTransformation())
                    .placeholder(R.drawable.ic_action_download)
                    .error(R.drawable.ic_action_warning)
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            _item.setBitmapDrawable((BitmapDrawable) imageView.getDrawable());
                            _item.setLoaded(true);
                            //do smth when picture is loaded successfully
                            Log.d(TAG, "downloadImages.onSuccess: **** " + _item.getUrl()+" / id = "+_item.getViewId());
                            subjectLoadImage.onNext(-1);
                        }

                        @Override
                        public void onError() {
                            _item.setBitmapDrawable((BitmapDrawable) imageView.getDrawable());
                            _item.setLoaded(false);
                            //do smth when there is picture loading error
                            Log.d(TAG, "downloadImages.onError: **** " + _item.getUrl()+" / id = "+_item.getViewId());
                            subjectLoadImage.onNext(0);
                        }
                    });
        } else {
            Log.d(TAG, "downloadImages: imageSwitcher = null");
        }
    }


    public PublishSubject<Integer> getSubjectLoadImage() {
        return subjectLoadImage;
    }


    public class CropSquareTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;
            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "square()";
        }
    }

    private ArrayList<CollageItem> getNewCollageList(int _density) throws InterruptedException {
        Log.d(TAG, "getNewCollageList: _density = " + _density);
//        TimeUnit.SECONDS.sleep(3);

//        enabledPointsSmall = enabledPoints(0, 9);
//        enabledPointsMiddle = enabledPoints(0, 8);
//        enabledPointsBig = enabledPoints(0, 7);

        ArrayList<ArrayList<Point>> enabledPoints = new ArrayList<>();
        enabledPoints.add(enabledPoints(0, 9));
        enabledPoints.add(enabledPoints(0, 8));
        enabledPoints.add(enabledPoints(0, 7));

        ArrayList<CollageItem> collageItems = new ArrayList<>();
        int cntSize3 = 0;
        int cntSize2 = 0;
        if (_density < 11) {
            switch (_density) {
                case 10:
                    cntSize3 = 6;
                    cntSize2 = 6;
                    break;
                case 9:
                    cntSize3 = 5;
                    cntSize2 = 5;
                    break;
                case 8:
                    cntSize3 = 4;
                    cntSize2 = 4;
                    break;
                case 7:
                    cntSize3 = 3;
                    cntSize2 = 6;
                    break;
                case 6:
                    cntSize3 = 2;
                    cntSize2 = 8;
                    break;
                case 5:
                    cntSize3 = 1;
                    cntSize2 = 9;
                    break;
                case 4:
                    cntSize2 = 7;
                    break;
                case 3:
                    cntSize2 = 5;
                    break;
                case 2:
                    cntSize2 = 3;
                    break;
                case 1:
                    cntSize2 = 1;
                    break;
            }
            for (int i = 1; i <= cntSize3; i++)
                addItem(collageItems, BIG_SIZE, enabledPoints, sizeSquareBig, getResources().getColor(android.R.color.holo_blue_bright));
            for (int i = 1; i <= cntSize2; i++)
                addItem(collageItems, MIDDLE_SIZE, enabledPoints, sizeSquareMiddle, getResources().getColor(android.R.color.holo_blue_light));

            while (enabledPoints.get(0).size() > 0) {
                addItem(collageItems, SMALL_SIZE, enabledPoints, sizeSquareSmall, getResources().getColor(android.R.color.holo_blue_dark));
            }
        } else {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    Point point = new Point(x * 3, y * 3);
                    CollageItem collageItem = new CollageItem();
//                    setDisabledPoints(point, BIG_SIZE);
                    collageItem.setParams(point.x, point.y, sizeSquareBig, getResources().getColor(android.R.color.holo_blue_bright));
//                    collageItem.setBitmapDrawable(BitmapFactory.decodeResource(getResources(), ic_autorenew_white_36dp));
                    collageItem.setBitmapDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_picture));
                    collageItems.add(collageItem);
                }
            }
        }
        itemsCount = collageItems.size();
        collageItemList = collageItems;
        return collageItems;

    }


    private void setAllViews(ArrayList<CollageItem> _collageItems) {
        removeAllViews();
        for (CollageItem item : _collageItems) {
            Log.d(TAG, "setAllViews: X = " + item.getPosX() + " / Y = " + item.getPosY()
                    + " / Size = " + item.getItemSize());
            setView(item);
        }
    }

    private ArrayList<Point> enabledPoints(int _from, int _to) {
        ArrayList<Point> points = new ArrayList<>();
        for (int x = _from; x < _to; x++) {
            for (int y = _from; y < _to; y++) {
                Point point = new Point(x, y);
                points.add(point);
            }
        }
        return points;
    }

    private void addItem(ArrayList<CollageItem> _collageItems, int _size, ArrayList<ArrayList<Point>> _points, float _sizeSquare, int _color) {
        Point point = getRandArrayElement(_points.get(_size - 1));
        if (point != null) {
            CollageItem collageItem = new CollageItem();
            setDisabledPoints(point, _size, _points);
            collageItem.setParams(point.x, point.y, _sizeSquare, _color);

            collageItem.setBitmapDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.ic_action_picture));
            _collageItems.add(collageItem);
        }
    }

    private void setView(CollageItem _item) {

        float marginX = (gridLineWidth + sizeSquareSmall) * _item.getPosX() + gridLineWidth;
        float marginY = (gridLineWidth + sizeSquareSmall) * _item.getPosY() + gridLineWidth;
        Log.d(TAG, "setView: marginX = " + gridLineWidth + " / " + sizeSquareSmall + " / " + _item.getPosX());

//        ImageView imageView = new ImageView(context);
//        imageView.setBackgroundColor(_item.getItemColor());
//        imageView.setImageBitmap(_item.getBitmapDrawable());

//        _item.setViewId(getNewId());
//        imageView.setId(_item.getViewId());
//        imageView.setOnLongClickListener(new ViewLongClickListener());
//        imageView.setOnDragListener(new ViewDragListener());
//        addView(imageView);


        _item.setViewId(getNewId());


        ImageSwitcher imageSwitcher = new ImageSwitcher(context);
        imageSwitcher.setId(_item.getViewId());
//        Animation in  = AnimationUtils.loadAnimation(context, R.anim.left_to_right_in);
//        Animation out = AnimationUtils.loadAnimation(context, R.anim.left_to_right_out);
//        imageSwitcher.setInAnimation(in);
//        imageSwitcher.setOutAnimation(out);

        imageSwitcher.setInAnimation(dragIn);
        imageSwitcher.setOutAnimation(dragOut);


        imageSwitcher.setBackgroundColor(_item.getItemColor());

        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(_item.getBitmapDrawable());


        imageSwitcher.addView(imageView);
        imageSwitcher.addView(new ImageView(context));

//        //Drawable d = new BitmapDrawable(getResources(), _item.getBitmapDrawable());
//        Drawable d = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_picture));
//
//       // imageView.setImageDrawable(d);
//
//        imageSwitcher.setImageResource(R.drawable.ic_action_picture);

        imageSwitcher.setOnLongClickListener(new ViewLongClickListener());
        imageSwitcher.setOnDragListener(new ViewDragListener());
        addView(imageSwitcher);


//        PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) imageView.getLayoutParams();
        PercentRelativeLayout.LayoutParams params = (PercentRelativeLayout.LayoutParams) imageSwitcher.getLayoutParams();

        PercentLayoutHelper.PercentLayoutInfo info = params.getPercentLayoutInfo();

        info.heightPercent = _item.getItemSize();
        info.widthPercent = _item.getItemSize();
        info.topMarginPercent = marginY;
        info.leftMarginPercent = marginX;
        Log.d(TAG, "setView: info" + info.heightPercent + " / " + info.topMarginPercent + " / " + info.leftMarginPercent);
        //frameLayout.setLayoutParams(params);
        //imageView.setLayoutParams(params);
        imageSwitcher.setLayoutParams(params);
    }

    private Point getRandArrayElement(ArrayList<Point> _points) {
        Random rand = new Random();
        if (_points.size() == 0) return null;
        return _points.get(rand.nextInt(_points.size()));
    }

    private void setDisabledPoints(Point _point, int _itemSize, ArrayList<ArrayList<Point>> _points) {
        int fromX = _point.x - 2;
        int fromY = _point.y - 2;
        fromX = fromX < 0 ? 0 : fromX;
        fromY = fromY < 0 ? 0 : fromY;
        removeDisabledPoints(_points.get(2), fromX, _point.x + _itemSize, fromY, _point.y + _itemSize);

        fromX = _point.x - 1;
        fromY = _point.y - 1;
        fromX = fromX < 0 ? 0 : fromX;
        fromY = fromY < 0 ? 0 : fromY;
        removeDisabledPoints(_points.get(1), fromX, _point.x + _itemSize, fromY, _point.y + _itemSize);

        fromX = _point.x;
        fromY = _point.y;
        removeDisabledPoints(_points.get(0), fromX, _point.x + _itemSize, fromY, _point.y + _itemSize);
    }


    private void removeDisabledPoints(ArrayList<Point> _points, int _fromX, int _toX, int _fromY, int _toY) {
        for (int x = _fromX; x < _toX; x++) {
            for (int y = _fromY; y < _toY; y++) {
                Point delPoint = new Point(x, y);
                //Log.d(TAG, "removeDisabledPoints: delPoint = " + delPoint.toString());
                _points.remove(delPoint);
            }
        }
    }


    private int getNewId() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return generateViewId();
        } else {
            return View.generateViewId();
        }
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in {@link ##setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */

    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    // This defines your touch listener
    private class ViewLongClickListener implements OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            if (dragEnabled) {
                ClipData data = ClipData.newPlainText("", "");
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("dd",v.);
//            ClipData data = ClipData.newIntent(KEY_ITEM,)
                DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    v.startDrag(data, shadowBuilder, v, 0);
                } else {
                    v.startDragAndDrop(data, shadowBuilder, v, 0);
                }
            }
            return true;

        }
    }

    private class ViewDragListener implements OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    mInView = false;
                    sourceView = (ImageSwitcher) event.getLocalState();
                    sourceView.setVisibility(View.INVISIBLE);
                    sourceViewId = sourceView.getId();
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    mInView = true;
                    targetView = (ImageSwitcher) v;
                    targetViewId = targetView.getId();
                    targetDrawable = (BitmapDrawable) ((ImageView) targetView.getChildAt(targetView.getDisplayedChild())).getDrawable();
                    targetView.setImageDrawable(getResources().getDrawable(R.drawable.ic_autorenew_white_36dp));
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    mInView = false;
                    targetView.setImageDrawable(targetDrawable);
                    break;
                case DragEvent.ACTION_DROP:

                    targetView.setInAnimation(dropIn);
                    targetView.setOutAnimation(dropOut);
                    sourceView.setInAnimation(dropIn);
                    sourceView.setOutAnimation(dropOut);

                    Drawable sourceDrawable = ((ImageView) sourceView.getChildAt(sourceView.getDisplayedChild())).getDrawable();

                    targetView.setImageDrawable(targetDrawable);
                    sourceView.setImageDrawable(sourceDrawable);
                    sourceView.setVisibility(View.VISIBLE);
                    String targetUrl = (String) targetView.getTag();
                    String sourceUrl = (String) sourceView.getTag();
//                    Log.d(TAG, "onDrag*: targetUrl = "+targetUrl+" / sourceUrl = " +sourceUrl);
//                    int idTarget = targetView.getChildAt(0).getId();
//                    int idSource = sourceView.getChildAt(0).getId();


                    for (CollageItem item : collageItemList) {
                        if (item.getViewId() == targetViewId) {
                            // TODO: 18.01.2017 URL!!!!!!!!!! 
                            targetView.setImageDrawable(sourceDrawable);
                            Log.d(TAG, "onDrag: targetViewId - " + item.getUrl()+" / "+sourceUrl);
                            item.setUrl(sourceUrl);
                            item.setBitmapDrawable((BitmapDrawable) sourceDrawable);
                        }
                        if (item.getViewId() == sourceViewId) {
                            sourceView.setImageDrawable(targetDrawable);
                            Log.d(TAG, "onDrag: sourceViewId - " + item.getUrl()+" / "+targetUrl);
                            item.setUrl(targetUrl);
                            item.setBitmapDrawable((BitmapDrawable) targetDrawable);
                        }
                    }

                    targetView.setId(getNewId());
                    sourceView.setId(targetViewId);
                    targetView.setId(sourceViewId);


                    targetView.setInAnimation(dragIn);
                    targetView.setOutAnimation(dragOut);
                    sourceView.setInAnimation(dragIn);
                    sourceView.setOutAnimation(dragOut);

                    break;
                case DragEvent.ACTION_DRAG_ENDED:

                    if (!mInView) {
                        Log.d(TAG, "onDrag: --------------------------------------");
                        sourceView.post(() -> {
                            if (sourceView.getVisibility() != View.VISIBLE) {
                                sourceView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    mInView = true;
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    // Log.d(TAG, "onDrag: ACTION_DRAG_LOCATION");

                default:
//                    Log.d(TAG, "onDrag: default");

                    break;
            }
            return true;

        }
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        if (disposableGetImages != null && !disposableGetImages.isDisposed())
            disposableGetImages.dispose();

        Bundle bundle = new Bundle();
        bundle.putParcelable(TAG_SUPER_STATE, super.onSaveInstanceState());
        bundle.putParcelableArrayList(TAG_ITEM_LIST, collageItemList);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;
            collageItemList = bundle.getParcelableArrayList(TAG_ITEM_LIST);
            setAllViews(collageItemList);

            disposableGetImages = subjectLoadImage.subscribe(this::progressCheck);
            subjectLoadImage.onNext(collageItemList.size());
            Log.d(TAG, "+----onRestoreInstanceState:  collageItemList.size() = " + collageItemList.size());
            // TODO: 19.01.2017 тут все грузится с космоса/кеша, а надо из массива
            // хранить в массиве УРИ и при его наличии качать с него, иначе космос
            for (CollageItem item : collageItemList) {
//                if (item.isLoaded()){
//
//                } else {
                    downloadItemImage(item);
//                }
            }
            state = bundle.getParcelable(TAG_SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }


}
