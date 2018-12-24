package com.peterombodi.catcollage.presentation.custom_view;

import android.view.animation.Interpolator;

/**
 * @author Peter Ombodi (Created on 08.10.2018).
 * Company: p.ombodi
 * Email:  p.ombodi@gmail.com
 */
public class FadeInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float input) {
        return input/2;
    }
}
