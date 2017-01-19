package com.peterombodi.catcollage.presentation.customView.collageView;

import com.peterombodi.catcollage.data.model.CollageItem;
import com.peterombodi.catcollage.data.model.Image;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by Admin on 20.12.2016.
 */

public interface ICollageView {

    void setCollage(int _density);

    void setColor(int _color);

    void setDragEnabled(boolean _enabled);

    int getItemsCount();

    ArrayList<CollageItem> getItemList();

    void setItemList(ArrayList<CollageItem> _items);

    void downloadImages(List<Image> _images);

    PublishSubject<Integer> getSubjectLoadImage();

}
