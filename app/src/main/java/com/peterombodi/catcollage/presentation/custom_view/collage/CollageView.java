package com.peterombodi.catcollage.presentation.custom_view.collage;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Guideline;
import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.peterombodi.catcollage.R;
import com.peterombodi.catcollage.database.model.CollageItem;
import com.peterombodi.catcollage.presentation.custom_view.FadeInterpolator;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static android.support.constraint.ConstraintSet.HORIZONTAL;
import static android.support.constraint.ConstraintSet.VERTICAL;

/**
 * Created by Peter on 02.10.2017.
 */

public class CollageView extends ConstraintLayout {

    private static final String TAG = "CollageView";
    private static final int maxDensity = 11;
    private static final int SMALL_SIZE = 1;
    private static final int MIDDLE_SIZE = 2;
    private static final int BIG_SIZE = 3;
    private static final String TAG_SUPER_STATE = "TAG_SUPER_STATE";
    private static final String TAG_DRAG_ENABLED = "TAG_DRAG_ENABLED";
    private static final int densitySetBigImages[] = new int[]{0, 0, 0, 0, 1, 2, 3, 4, 5, 6};
    private static final int densitySetMiddleImages[] = new int[]{1, 3, 5, 7, 9, 8, 6, 4, 5, 6};

    private Context context;
    private ConstraintLayout constraintLayout;

    private Disposable disposableTransition;
    private CompositeDisposable compositeDisposable;
    private PublishSubject<Integer> subjectSetCollage;
    private ICollageView callback;

    private float gridLineWidthRelative;
    private int gridLineWidth;
    private boolean mInView = false;
    private BitmapDrawable targetDrawable;
    private int targetViewId;
    private int sourceViewId;
    private ImageSwitcher sourceView;
    private ImageSwitcher targetView;

    private ArrayList<CollageItem> collageItemList;
    private boolean dragEnabled;

    private Animation dragIn;
    private Animation dragOut;
    private Animation dropIn;
    private Animation dropOut;

    private int[][] guidelines;

    public CollageView(Context context) {
        super(context);
        init(context);
    }

    public CollageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CollageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CollageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setCallback(ICollageView callback) {
        this.callback = callback;
    }

    public void buildCollage(int density) {
        subjectSetCollage.onNext(density);
    }

    public void setColor(int color) {
        setBackgroundColor(color);
    }

    public void setDragEnabled(boolean enabled) {
        dragEnabled = enabled;
    }

    public int getItemsCount() {
        return collageItemList.size();
    }

    public ArrayList<CollageItem> getItemList() {
        return collageItemList;
    }

    public void setItemList(ArrayList<CollageItem> items) {
        setAllViews(items, false);
    }

    public ImageView getCollageItemView(int switcherId) {
        ImageSwitcher imageSwitcher = findViewById(switcherId);
        if (imageSwitcher == null)
            return null;
        imageSwitcher.setDisplayedChild(0);
        return (ImageView) imageSwitcher.getChildAt(0);
    }

    public int getMaxDensity() {
        return maxDensity;
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        if (collageViewSize != 0) {
//            ViewGroup.LayoutParams params = constraintLayout.getLayoutParams();
//            params.height = collageViewSize;
//            params.width = collageViewSize;
//            constraintLayout.setVisibility(VISIBLE);
//            constraintLayout.setLayoutParams(params);
//            requestLayout();
//            Log.d(TAG, "onSizeChanged: " + collageViewSize);
//        }
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = reconcileSize(100, widthMeasureSpec);
        final int heightSize = reconcileSize(100, heightMeasureSpec);
        final int collageViewSize = ((heightSize > widthSize) ? widthSize : heightSize);
        gridLineWidth = (int) (collageViewSize * gridLineWidthRelative);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        compositeDisposable.dispose();
    }

    private void init(Context context) {
        this.context = context;
        constraintLayout = this;
        gridLineWidthRelative = 0.01f;
        gridLineWidth = 4;
        if (this.isInEditMode())
            setAllViews(generateCollageList(10), false);
        else {
            // animation drag on view
            dragIn = new AlphaAnimation(0, 1);
            dragIn.setDuration(1500);
            dragOut = new AlphaAnimation(1, 0);
            dragOut.setDuration(1500);
            // animation drop on view
            dropIn = AnimationUtils.loadAnimation(context, R.anim.left_to_right_in);
            dropOut = AnimationUtils.loadAnimation(context, R.anim.left_to_right_out);

        }
        compositeDisposable = new CompositeDisposable();
        subjectSetCollage = PublishSubject.create();
        compositeDisposable.add(
                subjectSetCollage
                        .debounce(200, TimeUnit.MILLISECONDS)
                        .map(this::generateCollageList)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(list -> setAllViews(list, true)));

    }

    private ArrayList<CollageItem> generateCollageList(int density) {

        ArrayList<ArrayList<Point>> enabledPoints = new ArrayList<>();
        enabledPoints.add(enabledPoints(0, 9));
        enabledPoints.add(enabledPoints(0, 8));
        enabledPoints.add(enabledPoints(0, 7));

        ArrayList<CollageItem> collageItems = new ArrayList<>();
        if (density < 11) {
            for (int i = 1; i <= densitySetBigImages[density - 1]; i++)
                addItem(collageItems, BIG_SIZE, enabledPoints);
            for (int i = 1; i <= densitySetMiddleImages[density - 1]; i++)
                addItem(collageItems, MIDDLE_SIZE, enabledPoints);

            while (enabledPoints.get(0).size() > 0) {
                addItem(collageItems, SMALL_SIZE, enabledPoints);
            }
        } else {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    Point point = new Point(x * 3, y * 3);
                    CollageItem collageItem = new CollageItem();
                    collageItem.setParams(point.x, point.y, BIG_SIZE, -1);
                    collageItem.setDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cat_2));
                    collageItems.add(collageItem);
                }
            }
        }
        return collageItems;
    }

    private void setAllViews(ArrayList<CollageItem> collageItems, boolean rebuild) {
        addGuidelines();
        if (rebuild && collageItemList != null) {
            decreaseGuidelines(collageItems);
        } else {
            collageItemList = collageItems;
            for (CollageItem item : collageItems) {
                setView(item);
            }
            increaseGuidelines(collageItemList);
        }
        if (callback != null)
            callback.onBuildCollage(collageItems);
    }

    private void addGuidelines() {
        if (guidelines == null) {
            guidelines = new int[2][10];
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            for (int i = 0; i < 10; i++) {
                addGuideline(constraintSet, VERTICAL, i);
                addGuideline(constraintSet, HORIZONTAL, i);
            }
            constraintSet.applyTo(constraintLayout);
        }
    }

    private void addGuideline(ConstraintSet constraintSet, int orientation, int i) {
        Guideline guideLine = new Guideline(context);
        guideLine.setId(getNewId());
        constraintLayout.addView(guideLine);
        constraintSet.create(guideLine.getId(), orientation);
        constraintSet.setGuidelinePercent(guideLine.getId(), gridLineWidthRelative);
        guidelines[orientation][i] = guideLine.getId();
    }

    private void decreaseGuidelines(ArrayList<CollageItem> collageItems) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);

        TransitionSet set = new TransitionSet();
        Fade fade = new Fade();
//		fade.setDuration(500);
        fade.setInterpolator(new FadeInterpolator());
        set.addTransition(fade);

        ChangeBounds changeBounds = new ChangeBounds();
//		changeBounds.setStartDelay(100);
        set.addTransition(changeBounds);

        set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        set.addListener(new DecreaseTransitionListener(collageItems, constraintSet));
        set.setDuration(200);
        set.setInterpolator(new LinearOutSlowInInterpolator());
        TransitionManager.beginDelayedTransition(constraintLayout, set);

        for (int i = 0; i < 10; i++) {
            constraintSet.setGuidelinePercent(guidelines[VERTICAL][i], 0.25f + i * gridLineWidthRelative * 5);
            constraintSet.setGuidelinePercent(guidelines[HORIZONTAL][i], 0.25f + i * gridLineWidthRelative * 5);
        }

        if (collageItemList != null)
            constraintLayout.setVisibility(INVISIBLE);
        constraintSet.applyTo(constraintLayout);
    }


    private void increaseGuidelines(ArrayList<CollageItem> collageItems) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        TransitionSet set1 = new TransitionSet();

        Fade fade = new Fade();
        fade.setDuration(100);
        set1.addTransition(fade);

        set1.addTransition(new ChangeBounds());

        set1.setOrdering(TransitionSet.ORDERING_TOGETHER);
        set1.addListener(new IncreaseTransitionListener(collageItems));
        set1.setDuration(100);

        set1.setInterpolator(new AccelerateInterpolator());

        TransitionManager.beginDelayedTransition(constraintLayout, set1);
        constraintLayout.setVisibility(VISIBLE);
        for (int i = 0; i < 10; i++) {
            float percent = ((1f - gridLineWidthRelative) / 9f) * i + gridLineWidthRelative;
            constraintSet.setGuidelinePercent(guidelines[VERTICAL][i], percent);
            constraintSet.setGuidelinePercent(guidelines[HORIZONTAL][i], percent);
        }
        constraintSet.applyTo(constraintLayout);
    }

    private ArrayList<Point> enabledPoints(int from, int to) {
        ArrayList<Point> points = new ArrayList<>();
        for (int x = from; x < to; x++) {
            for (int y = from; y < to; y++) {
                Point point = new Point(x, y);
                points.add(point);
            }
        }
        return points;
    }

    private void addItem(ArrayList<CollageItem> collageItems, int size, ArrayList<ArrayList<Point>> points) {
        Point point = getRandArrayElement(points.get(size - 1));
        if (point != null) {
            CollageItem collageItem = new CollageItem();
            setDisabledPoints(point, size, points);
            collageItem.setParams(point.x, point.y, size, -1);
            collageItem.setDrawable(ContextCompat.getDrawable(context, R.drawable.ic_cat_2));
            collageItems.add(collageItem);
        }
    }

    private void setView(CollageItem item) {
        item.setViewId(getNewId());

        ImageSwitcher imageSwitcher = new ImageSwitcher(context);
        imageSwitcher.setId(item.getViewId());

        imageSwitcher.setInAnimation(dragIn);
        imageSwitcher.setOutAnimation(dragOut);

        imageSwitcher.setBackgroundColor(item.getItemColor());
        Random rnd = new Random();
        imageSwitcher.setBackgroundColor(Color.argb(150, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));

        ImageView imageView = new ImageView(context);
        Drawable image = item.getBitmapDrawable();
        //image.mutate().setTint(item.getItemColor());
        imageView.setImageDrawable(image);

        imageSwitcher.addView(imageView);
        imageSwitcher.addView(new ImageView(context));

        imageSwitcher.setOnLongClickListener(new ViewLongClickListener());
        imageSwitcher.setOnDragListener(new ViewDragListener());
        addView(imageSwitcher);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        params.height = 0;
        params.width = 0;
        params.setMargins(0, 0, gridLineWidth, 0);
        imageSwitcher.setLayoutParams(params);

        ConstraintSet set = new ConstraintSet();
        set.clone(constraintLayout);

        set.connect(imageSwitcher.getId(), ConstraintSet.LEFT, guidelines[VERTICAL][item.getPosX()], ConstraintSet.LEFT, 0);
        set.connect(imageSwitcher.getId(), ConstraintSet.RIGHT, guidelines[VERTICAL][item.getPosX() + item.getItemSize()], ConstraintSet.RIGHT, gridLineWidth);
        set.connect(imageSwitcher.getId(), ConstraintSet.TOP, guidelines[HORIZONTAL][item.getPosY()], ConstraintSet.TOP, 0);
        set.connect(imageSwitcher.getId(), ConstraintSet.BOTTOM, guidelines[HORIZONTAL][item.getPosY() + item.getItemSize()], ConstraintSet.BOTTOM, gridLineWidth);
        Guideline g = (Guideline) findViewById(guidelines[VERTICAL][item.getPosX()]);
        set.applyTo(constraintLayout);
    }

    private Point getRandArrayElement(ArrayList<Point> points) {
        Random rand = new Random();
        if (points.size() == 0) return null;
        return points.get(rand.nextInt(points.size()));
    }

    private void setDisabledPoints(Point point, int itemSize, ArrayList<ArrayList<Point>> points) {
        int fromX = point.x - 2;
        int fromY = point.y - 2;
        fromX = fromX < 0 ? 0 : fromX;
        fromY = fromY < 0 ? 0 : fromY;
        removeDisabledPoints(points.get(2), fromX, point.x + itemSize, fromY, point.y + itemSize);

        fromX = point.x - 1;
        fromY = point.y - 1;
        fromX = fromX < 0 ? 0 : fromX;
        fromY = fromY < 0 ? 0 : fromY;
        removeDisabledPoints(points.get(1), fromX, point.x + itemSize, fromY, point.y + itemSize);

        fromX = point.x;
        fromY = point.y;
        removeDisabledPoints(points.get(0), fromX, point.x + itemSize, fromY, point.y + itemSize);
    }


    private void removeDisabledPoints(ArrayList<Point> points, int fromX, int toX, int fromY, int toY) {
        for (int x = fromX; x < toX; x++) {
            for (int y = fromY; y < toY; y++) {
                Point delPoint = new Point(x, y);
                points.remove(delPoint);
            }
        }
    }

    private int getNewId() {
        return View.generateViewId();
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

    private class DecreaseTransitionListener implements Transition.TransitionListener {

        private ArrayList<CollageItem> collageItems;
        private ConstraintSet constraintSet;

        DecreaseTransitionListener(ArrayList<CollageItem> collageItems, ConstraintSet constraintSet) {
            this.collageItems = collageItems;
            this.constraintSet = constraintSet;
        }

        @Override
        public void onTransitionStart(@NonNull Transition transition) {

        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition) {
            if (disposableTransition != null && !disposableTransition.isDisposed())
                disposableTransition.dispose();

            disposableTransition = Observable.just(createNewCollage())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::moveGuidelines);
        }

        private ArrayList<CollageItem> createNewCollage() {
            for (CollageItem item : collageItemList)
                constraintLayout.removeView(findViewById(item.getViewId()));
            collageItemList = collageItems;
            for (CollageItem item : collageItems)
                setView(item);
            return collageItems;
        }

        private void moveGuidelines(ArrayList<CollageItem> collageItems) {
            constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            TransitionSet set = new TransitionSet();
            set.addTransition(new Fade());
            set.addTransition(new ChangeBounds());
            set.setOrdering(TransitionSet.ORDERING_TOGETHER);
            set.addListener(new IncreaseTransitionListener(collageItems));
            set.setDuration(100);
            set.setInterpolator(new LinearOutSlowInInterpolator());

            TransitionManager.beginDelayedTransition(constraintLayout, set);
            constraintLayout.setVisibility(VISIBLE);
            for (int i = 0; i < 10; i++) {
                float percent = ((1f - gridLineWidthRelative) / 9f) * i + gridLineWidthRelative;
                constraintSet.setGuidelinePercent(guidelines[VERTICAL][i], percent);
                constraintSet.setGuidelinePercent(guidelines[HORIZONTAL][i], percent);
            }
            constraintLayout.setAlpha(1f);
            constraintSet.applyTo(constraintLayout);
        }

        @Override
        public void onTransitionCancel(@NonNull Transition transition) {
        }

        @Override
        public void onTransitionPause(@NonNull Transition transition) {
        }

        @Override
        public void onTransitionResume(@NonNull Transition transition) {
        }
    }


    private class IncreaseTransitionListener implements Transition.TransitionListener {

        private ArrayList<CollageItem> collageItems;

        IncreaseTransitionListener(ArrayList<CollageItem> collageItems) {
            this.collageItems = collageItems;
        }

        @Override
        public void onTransitionStart(@NonNull Transition transition) {
        }

        @Override
        public void onTransitionEnd(@NonNull Transition transition) {
            //Log.d(TAG, "onTransitionEnd1: ");
        }

        @Override
        public void onTransitionCancel(@NonNull Transition transition) {
        }

        @Override
        public void onTransitionPause(@NonNull Transition transition) {
        }

        @Override
        public void onTransitionResume(@NonNull Transition transition) {
        }
    }

    private class ViewLongClickListener implements OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            if (dragEnabled) {
                ClipData data = ClipData.newPlainText("", "");
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
                    targetView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_repeat));
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
                    String targetUrl = (String) targetView.getChildAt(0).getTag();
                    String sourceUrl = (String) sourceView.getChildAt(0).getTag();
//                    Log.d(TAG, "onDrag*: targetUrl = "+targetUrl+" / sourceUrl = " +sourceUrl);
//                    int idTarget = targetView.getChildAt(0).getId();
//                    int idSource = sourceView.getChildAt(0).getId();
                    for (CollageItem item : collageItemList) {
                        if (item.getViewId() == targetViewId) {
                            // TODO: 18.01.2017 URL!!!!!!!!!!
                            targetView.setImageDrawable(sourceDrawable);
                            Log.d(TAG, "onDrag: targetViewId - " + item.getUrl() + " / " + sourceUrl);
                            item.setUrl(sourceUrl);
                            //item.setBitmapDrawable((BitmapDrawable) sourceDrawable);
                        }
                        if (item.getViewId() == sourceViewId) {
                            sourceView.setImageDrawable(targetDrawable);
                            Log.d(TAG, "onDrag: sourceViewId - " + item.getUrl() + " / " + targetUrl);
                            item.setUrl(targetUrl);
                            //item.setBitmapDrawable((BitmapDrawable) targetDrawable);
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
        Bundle bundle = new Bundle();
        bundle.putParcelable(TAG_SUPER_STATE, super.onSaveInstanceState());
        // TODO: 05.10.2017 java.lang.RuntimeException: android.os.TransactionTooLargeException: on API>=24
//		bundle.putParcelableArrayList(TAG_ITEM_LIST, collageItemList);

        bundle.putBoolean(TAG_DRAG_ENABLED, dragEnabled);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;

            // TODO: 05.10.2017 java.lang.RuntimeException: android.os.TransactionTooLargeException: on API>=24
//			collageItemList = bundle.getParcelableArrayList(TAG_ITEM_LIST);
//			setAllViews(collageItemList);

            // TODO: 19.01.2017 тут все грузится с космоса/кеша, а надо из массива
            // хранить в массиве УРИ и при его наличии качать с него, иначе космос
//            for (CollageItem item : collageItemList) {
//                if (item.getUrl() != null) downloadItemImage(item);
//            }
            dragEnabled = bundle.getBoolean(TAG_DRAG_ENABLED);
            state = bundle.getParcelable(TAG_SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }

    private int reconcileSize(int contentSize, int measureSpec) {
        final int mode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.EXACTLY:
                return specSize;
            case MeasureSpec.AT_MOST:
                return (contentSize < specSize ? contentSize : specSize);
            case MeasureSpec.UNSPECIFIED:
            default:
                return contentSize;
        }
    }
}
