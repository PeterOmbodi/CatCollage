package com.peterombodi.catcollage.presentation.customView;

import android.view.animation.Interpolator;

/**
 * @author Peter Ombodi (Created on 08.10.2018).
 * Company: Thinkmobiles
 * Email:  petro.ombodi@thinkmobiles.com
 */
public class FadeInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float input) {
        return input/2;
    }
}
