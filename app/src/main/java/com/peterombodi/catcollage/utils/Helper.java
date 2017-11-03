package com.peterombodi.catcollage.utils;

import android.graphics.Color;

/**
 * Created by Peter on 22.09.2017.
 */

public class Helper {

	public static int getProgressColor(int _progress) {
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
}
