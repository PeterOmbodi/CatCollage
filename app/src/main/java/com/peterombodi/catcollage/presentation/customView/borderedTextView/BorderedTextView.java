package com.peterombodi.catcollage.presentation.customView.borderedTextView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Admin on 16.01.2017.
 */

public class BorderedTextView extends TextView {
    private static final String TAG = "BorderedTextView";
    private Paint paint = new Paint();
    public static final int BORDER_TOP = 0x00000001;
    public static final int BORDER_RIGHT = 0x00000002;
    public static final int BORDER_BOTTOM = 0x00000004;
    public static final int BORDER_LEFT = 0x00000008;

    //private Border[] borders;
    private Border border;
    private RectF rectf;


    public BorderedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BorderedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BorderedTextView(Context context) {
        super(context);
        init();
    }

    private void init() {

//        borders = new Border[BORDER_TOP];

//        Log.d(TAG, "init: borders.length = "+borders.length);
//        Log.d(TAG, "init: borders.getStyle = "+borders[0].getStyle());

        Border border = new Border(BORDER_TOP);
        border.setColor(Color.BLUE);
        border.setWidth(4);


        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
//        paint.setColor(Color.BLACK);
//        paint.setStrokeWidth(40);

        rectf = new RectF(0, 0, 0, 0);
        setBorders(border);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.d(TAG, "onDraw: borders isnull = " + (border == null));

        if (border == null) return;
        paint.setColor(border.getColor());
        paint.setStrokeWidth(border.getWidth());

        float size = (getWidth() > getHeight()) ? getWidth() : getHeight();
        setWidth((int) size);
        setHeight((int) size);

        rectf.set(border.getWidth(), border.getWidth(), size - border.getWidth(), size - border.getWidth());

        //rectf.set(border.getWidth(), border.getWidth(), getWidth() - border.getWidth(), getHeight() - border.getWidth());
        canvas.drawRoundRect(rectf, 2, 2, paint);
    }

//    public Border[] getBorders() {
//        return borders;
//    }
//
//    public void setBorders(Border[] borders) {
//        this.borders = borders;
//    }

    public Border getBorders() {
        return border;
    }

    public void setBorders(Border border) {
        this.border = border;
    }

//    public void setBorders(Border _borders, int _color, int _width) {
//        this.border = _borders;
//    }


}
