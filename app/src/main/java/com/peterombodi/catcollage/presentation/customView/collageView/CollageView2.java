package com.peterombodi.catcollage.presentation.customView.collageView;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import com.peterombodi.catcollage.R;
import com.peterombodi.catcollage.database.model.CollageItem;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static android.support.constraint.ConstraintSet.HORIZONTAL;
import static android.support.constraint.ConstraintSet.VERTICAL;
import static com.peterombodi.catcollage.constants.Constants.STATUS_DOWNLOAD_OK;

/**
 * Created by Peter on 02.10.2017.
 */

public class CollageView2 extends ConstraintLayout implements ICollageView {

	private static final String TAG = "CollageView";
	public static final int maxDensity = 11;
	private static final int SMALL_SIZE = 1;
	private static final int MIDDLE_SIZE = 2;
	private static final int BIG_SIZE = 3;
	//	private static final int GUIDELINE_GRID = 0;
//	private static final int GUIDELINE_BOTTOM = 1;
	private static final CharSequence KEY_ITEM = "KEY_ITEM";
	private static final String TAG_SUPER_STATE = "TAG_SUPER_STATE";
	private static final String TAG_ITEM_LIST = "TAG_ITEM_LIST";
	private static final String TAG_DRAG_ENABLED = "TAG_DRAG_ENABLED";


	private float gridLineWidth;
	private int gridLineWidthDp;

	private Context context;

	private ConstraintLayout constraintLayout;

	//private Disposable disposableSetCollage;
	private Disposable disposableTransition;

	private boolean mInView = false;
	private BitmapDrawable targetDrawable;
	private int targetViewId;
	private int sourceViewId;
	private ImageSwitcher sourceView;
	private ImageSwitcher targetView;
	private int collageViewSize;


	private int itemsCount;

	private ArrayList<CollageItem> collageItemList;
	private boolean dragEnabled;

	private Animation dragIn;
	private Animation dragOut;
	private Animation dropIn;
	private Animation dropOut;

//	private DownloadImage downloadImage;

	private int[][] guidelines;

	private PublishSubject<Integer> subjectSetCollage;


	public CollageView2(Context context) throws InterruptedException {
		super(context);
		init(context);
	}

	public CollageView2(Context context, AttributeSet attrs) throws InterruptedException {
		super(context, attrs);
		init(context);
	}

	public CollageView2(Context context, AttributeSet attrs, int defStyleAttr) throws InterruptedException {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d(TAG, "onSizeChanged: _____________________w, h, oldw, oldh - " + w + ", " + h + ", " + oldw + ", " + oldh);
		int viewHeight = h;
		int viewWidth = w;
		float squareSize = ((viewHeight > viewWidth) ? viewWidth : viewHeight);
		collageViewSize = (int) (squareSize);
		gridLineWidthDp = (int) (squareSize * gridLineWidth);
		Log.d(TAG, "onSizeChanged: __________________ newSize = " + collageViewSize + " / gridLineWidthDp = " + gridLineWidthDp);
		if (collageViewSize != 0) {
			ViewGroup.LayoutParams params = constraintLayout.getLayoutParams();
			params.height = collageViewSize;
			params.width = collageViewSize;
			constraintLayout.setVisibility(VISIBLE);
			constraintLayout.setLayoutParams(params);
			requestLayout();
			if (collageItemList != null) {
				Log.d(TAG, "onSizeChanged: collageItemList" + collageItemList.size());
//                setAllViews(collageItemList);
			}
		}


	}


	private void init(Context _context) throws InterruptedException {
		context = _context;
		Log.d(TAG, "init: --------------------------------");
		constraintLayout = this;
		itemsCount = 0;
		gridLineWidth = 0.01f;
		gridLineWidthDp = 4;

		if (!this.isInEditMode()) {
			// animation drag on view
			dragIn = new AlphaAnimation(0, 1);
			dragIn.setDuration(200);
			dragOut = new AlphaAnimation(1, 0);
			dragOut.setDuration(200);
			// animation drop on view
			dropIn = AnimationUtils.loadAnimation(context, R.anim.left_to_right_in);
			dropOut = AnimationUtils.loadAnimation(context, R.anim.left_to_right_out);
		} else {
			ArrayList<CollageItem> itemArrayList = getNewCollageList(10);
			setAllViews(itemArrayList);
		}
		subjectSetCollage = PublishSubject.create();
		subjectSetCollage
			.debounce(300, TimeUnit.MILLISECONDS)
			.map(this::getNewCollageList)
			.subscribeOn(Schedulers.newThread())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(this::setAllViews);
	}

	@Override
	public void setCollage(int _density) {
		subjectSetCollage.onNext(_density);
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
		itemsCount = collageItemList.size();
		return itemsCount;
	}

	@Override
	public int getItemsForLoadCount() {
		int i = 0;
		for (CollageItem item : collageItemList) {
			if (item.getLoadStatus() != STATUS_DOWNLOAD_OK) i++;
		}
		return i;
	}

	@Override
	public ArrayList<CollageItem> getItemList() {
		return collageItemList;
	}

	@Override
	public void setItemList(ArrayList<CollageItem> _items) {
		collageItemList = _items;
		setAllViews(collageItemList);
	}

	@Override
	public ImageView getCollageItemView(int _switcherId) {
		ImageSwitcher imageSwitcher = (ImageSwitcher) findViewById(_switcherId);
		if (imageSwitcher != null) {
			imageSwitcher.setDisplayedChild(0);
			return (ImageView) imageSwitcher.getChildAt(0);
		}
		return null;
	}

	private void newGuidelines() {
		guidelines = new int[2][10];
		ConstraintSet constraintSet = new ConstraintSet();
		constraintSet.clone(constraintLayout);
		for (int i = 0; i < 10; i++) {
			addGuideline(constraintSet, VERTICAL, i);
			addGuideline(constraintSet, HORIZONTAL, i);
		}
		constraintSet.applyTo(constraintLayout);
	}

	private void addGuideline(ConstraintSet constraintSet, int orientation, int i) {
		Guideline guideLine = new Guideline(context);
		guideLine.setId(getNewId());
		constraintLayout.addView(guideLine);
		constraintSet.create(guideLine.getId(), orientation);
		//constraintSet.setGuidelinePercent(guideLine.getId(), ((1f - gridLineWidth) / 9f) * i + gridLineWidth);
		constraintSet.setGuidelinePercent(guideLine.getId(), gridLineWidth);
		guidelines[orientation][i] = guideLine.getId();
	}

	private void decreaseGuidelines(ArrayList<CollageItem> _collageItems) {

		ConstraintSet constraintSet = new ConstraintSet();
		constraintSet.clone(constraintLayout);

		TransitionSet set = new TransitionSet();
		set.addTransition(new Fade());

		ChangeBounds changeBounds = new ChangeBounds();
		changeBounds.setStartDelay(100);
		set.addTransition(changeBounds);

		set.setOrdering(TransitionSet.ORDERING_TOGETHER);
		set.addListener(new DecreaseTransitionListener(_collageItems, constraintSet));
		set.setDuration(300);
//		set.setInterpolator(new AccelerateInterpolator());
		set.setInterpolator(new LinearOutSlowInInterpolator());

		TransitionManager.beginDelayedTransition(constraintLayout, set);

		for (int i = 0; i < 10; i++) {
			constraintSet.setGuidelinePercent(guidelines[VERTICAL][i], 0.25f + i * gridLineWidth * 5);
			constraintSet.setGuidelinePercent(guidelines[HORIZONTAL][i], 0.25f + i * gridLineWidth * 5);
		}

		if (collageItemList != null)
			constraintLayout.setVisibility(INVISIBLE);
		constraintSet.applyTo(constraintLayout);
	}


	private void increaseGuidelines(ArrayList<CollageItem> _collageItems) {
		ConstraintSet constraintSet = new ConstraintSet();
		constraintSet.clone(constraintLayout);
		TransitionSet set1 = new TransitionSet();

		Fade fade = new Fade();
		fade.setDuration(200);
		set1.addTransition(fade);

		set1.addTransition(new ChangeBounds());

		set1.setOrdering(TransitionSet.ORDERING_TOGETHER);
		set1.addListener(new IncreaseTransitionListener(_collageItems));
		set1.setDuration(200);

		set1.setInterpolator(new AccelerateInterpolator());

		TransitionManager.beginDelayedTransition(constraintLayout, set1);
		constraintLayout.setVisibility(VISIBLE);
		for (int i = 0; i < 10; i++) {
			float percent = ((1f - gridLineWidth) / 9f) * i + gridLineWidth;
			constraintSet.setGuidelinePercent(guidelines[VERTICAL][i], percent);
			constraintSet.setGuidelinePercent(guidelines[HORIZONTAL][i], percent);
		}
		constraintSet.applyTo(constraintLayout);

	}


	private ArrayList<CollageItem> getNewCollageList(int _density) throws InterruptedException {
		Log.d(TAG, "getNewCollageList: _density = " + _density + " ** ");
//        TimeUnit.SECONDS.sleep(3);

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
				addItem(collageItems, BIG_SIZE, enabledPoints, ContextCompat.getColor(context, android.R.color.holo_blue_bright));
			for (int i = 1; i <= cntSize2; i++)
				addItem(collageItems, MIDDLE_SIZE, enabledPoints, ContextCompat.getColor(context, android.R.color.holo_blue_light));

			while (enabledPoints.get(0).size() > 0) {
				addItem(collageItems, SMALL_SIZE, enabledPoints, ContextCompat.getColor(context, android.R.color.holo_blue_dark));
			}
		} else {
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					Point point = new Point(x * 3, y * 3);
					CollageItem collageItem = new CollageItem();
					collageItem.setParams(point.x, point.y, BIG_SIZE, ContextCompat.getColor(context, android.R.color.holo_blue_bright));
					collageItem.setBitmapDrawable((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ic_action_picture));
					collageItems.add(collageItem);
				}
			}
		}
		itemsCount = collageItems.size();
		//collageItemList = collageItems;
		return collageItems;
	}

	private void setAllViews(ArrayList<CollageItem> _collageItems) {
		if (guidelines != null) {
			decreaseGuidelines(_collageItems);
		} else {
			newGuidelines();
			for (CollageItem item : _collageItems) {
				setView(item);
			}
			increaseGuidelines(_collageItems);
		}

		if (collageItemList == null) collageItemList = _collageItems;

		Log.d(TAG, "setAllViews: getNewCollageList " + "  *** ");
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

	private void addItem(ArrayList<CollageItem> _collageItems, int _size, ArrayList<ArrayList<Point>> _points, int _color) {
		Point point = getRandArrayElement(_points.get(_size - 1));
		if (point != null) {
			CollageItem collageItem = new CollageItem();
			setDisabledPoints(point, _size, _points);
			collageItem.setParams(point.x, point.y, _size, _color);

			collageItem.setBitmapDrawable((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.ic_action_picture));
			_collageItems.add(collageItem);
		}
	}

	private void setView(CollageItem _item) {
		_item.setViewId(getNewId());

		ImageSwitcher imageSwitcher = new ImageSwitcher(context);
		imageSwitcher.setId(_item.getViewId());

		imageSwitcher.setInAnimation(dragIn);
		imageSwitcher.setOutAnimation(dragOut);

		imageSwitcher.setBackgroundColor(_item.getItemColor());

		ImageView imageView = new ImageView(context);
		imageView.setImageDrawable(_item.getBitmapDrawable());

		imageSwitcher.addView(imageView);
		imageSwitcher.addView(new ImageView(context));

		imageSwitcher.setOnLongClickListener(new ViewLongClickListener());
		imageSwitcher.setOnDragListener(new ViewDragListener());
		addView(imageSwitcher);

		ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

		params.height = 0;
		params.width = 0;
		params.setMargins(0, 0, gridLineWidthDp, 0);
		imageSwitcher.setLayoutParams(params);

		ConstraintSet set = new ConstraintSet();
		set.clone(constraintLayout);

		set.connect(imageSwitcher.getId(), ConstraintSet.LEFT, guidelines[VERTICAL][_item.getPosX()], ConstraintSet.LEFT, 0);
		set.connect(imageSwitcher.getId(), ConstraintSet.RIGHT, guidelines[VERTICAL][_item.getPosX() + _item.getItemSize()], ConstraintSet.RIGHT, gridLineWidthDp);
		set.connect(imageSwitcher.getId(), ConstraintSet.TOP, guidelines[HORIZONTAL][_item.getPosY()], ConstraintSet.TOP, 0);
		set.connect(imageSwitcher.getId(), ConstraintSet.BOTTOM, guidelines[HORIZONTAL][_item.getPosY() + _item.getItemSize()], ConstraintSet.BOTTOM, gridLineWidthDp);
		Guideline g = (Guideline) findViewById(guidelines[VERTICAL][_item.getPosX()]);
		set.applyTo(constraintLayout);
		Log.d(TAG, "setView: " + _item.getPosX() + " / Guideline: " + g.getLeft() + " / " + g.getTop() + " / imageSwitcher: " + imageSwitcher.getWidth() + " / " + imageSwitcher.getHeight());
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
			for (CollageItem item : collageItemList) {
				constraintLayout.removeView(findViewById(item.getViewId()));
			}
			collageItemList = collageItems;
			for (CollageItem item : collageItems) {
				setView(item);
			}
			return collageItems;
		}

		private void moveGuidelines(ArrayList<CollageItem> _collageItems) {

			constraintSet = new ConstraintSet();
			constraintSet.clone(constraintLayout);

			TransitionSet set = new TransitionSet();
			set.addTransition(new Fade());
			set.addTransition(new ChangeBounds());
			set.setOrdering(TransitionSet.ORDERING_TOGETHER);
			set.addListener(new IncreaseTransitionListener(_collageItems));
			set.setDuration(200);
			set.setInterpolator(new LinearOutSlowInInterpolator());

			TransitionManager.beginDelayedTransition(constraintLayout, set);
			constraintLayout.setVisibility(VISIBLE);
			for (int i = 0; i < 10; i++) {
				float percent = ((1f - gridLineWidth) / 9f) * i + gridLineWidth;
				constraintSet.setGuidelinePercent(guidelines[VERTICAL][i], percent);
				constraintSet.setGuidelinePercent(guidelines[HORIZONTAL][i], percent);
			}
			constraintSet.applyTo(constraintLayout);
			Log.d(TAG, "increaseGuidelines: onTransition apply ");
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
			Log.d(TAG, "onTransitionEnd1: ");
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
					targetView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_autorenew_white_36dp));
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

}
