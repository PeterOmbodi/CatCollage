package com.peterombodi.catcollage.presentation;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.VelocityTracker;
import android.widget.FrameLayout;
import android.widget.OverScroller;

/**
 * Created by Admin on 26.12.2016.
 */

public class ItemView extends FrameLayout {

    private Context context;

    //Fling components
    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;

    /* Positions of the last motion event */
    private float mLastTouchX, mLastTouchY;
    /* Drag threshold */
    private int mTouchSlop;
    /* Fling Velocity */
    private int mMaximumVelocity, mMinimumVelocity;
    /* Drag Lock */
    private boolean mDragging = false;

    public int viewCenterX = 0;
    public int viewCenterY = 0;


    public ItemView(Context context) {
        super(context);
        init(context);
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(Context _context) {
        context = _context;
//        mScroller = new OverScroller(context);
//        mVelocityTracker = VelocityTracker.obtain();
//        //Get system constants for touch thresholds
//        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }

//        /*
//  * Monitor touch events passed down to the children and
//  * intercept as soon as it is determined we are dragging.  This
//  * allows child views to still receive touch events if they are
//  * interactive (i.e. Buttons)
//  */
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                //Initial location
//                mLastTouchX = viewCenterX;
//                mLastTouchY = viewCenterY;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                final float x = event.getX();
//                final float y = event.getY();
//                final int yDiff = (int) Math.abs(y - mLastTouchY);
//                final int xDiff = (int) Math.abs(x - mLastTouchX);
//                //Verify that either difference is enough to be a drag
//                if (yDiff > mTouchSlop || xDiff > mTouchSlop) {
//                    mDragging = true;
//                    mVelocityTracker.addMovement(event);
//                    //Start capturing events ourselves
//                    return true;
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                mDragging = false;
//                break;
//        }
//
//        return super.onInterceptTouchEvent(event);
//    }
//
//    /*
//  * Feed all touch events we receive to the detector for
//  * processing.
//  */
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        mVelocityTracker.addMovement(event);
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                // We've already stored the initial point,
//                // but if we got here a child view didn't capture
//                // the event, so we need to.
//                return true;
//            case MotionEvent.ACTION_MOVE:
//                final float x = event.getX();
//                final float y = event.getY();
//                float deltaY = mLastTouchY - y;
//                float deltaX = mLastTouchX - x;
//                //Check for slop on direct events
//                if (!mDragging && (Math.abs(deltaY) > mTouchSlop || Math.abs(deltaX) > mTouchSlop)) {
//                    mDragging = true;
//                }
//                if (mDragging) {
//                    //Scroll the view
//                    scrollBy((int) deltaX, (int) deltaY);
//                    //Update the last touch event
//                    mLastTouchX = x;
//                    mLastTouchY = y;
//                }
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                mDragging = false;
//                break;
//            case MotionEvent.ACTION_UP:
//                mDragging = false;
//                break;
//        }
//        return super.onTouchEvent(event);
//    }

}
