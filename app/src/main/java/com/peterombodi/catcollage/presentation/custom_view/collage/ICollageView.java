package com.peterombodi.catcollage.presentation.custom_view.collage;

import android.widget.ImageView;

import com.peterombodi.catcollage.database.model.CollageItem;

import java.util.ArrayList;

/**
 * Created by Admin on 20.12.2016.
 */

public interface ICollageView {
    void onBuildCollage(ArrayList<CollageItem> collageItems);
}
