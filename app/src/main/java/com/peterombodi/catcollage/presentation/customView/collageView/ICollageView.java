package com.peterombodi.catcollage.presentation.customView.collageView;

import android.widget.ImageView;

import com.peterombodi.catcollage.database.model.CollageItem;

import java.util.ArrayList;

/**
 * Created by Admin on 20.12.2016.
 */

public interface ICollageView {

    void setCollage(int _density);

    void setColor(int _color);

    void setDragEnabled(boolean _enabled);

    int getItemsCount();

    int getItemsForLoadCount();

    ArrayList<CollageItem> getItemList();

    void setItemList(ArrayList<CollageItem> _items);

    //void downloadImages(List<Image> _images);

    //PublishSubject<Integer> getSubjectLoadImage();

    ImageView getCollageItemView(int _switcherId);

}
