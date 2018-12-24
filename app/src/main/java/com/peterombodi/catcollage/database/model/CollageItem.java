package com.peterombodi.catcollage.database.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by Admin on 14.12.2016.
 */
public class CollageItem implements Parcelable {

    public CollageItem() {
    }

    UUID id;

    UUID collageId;

    int viewId;

    int posX;

    int posY;

    int itemSize;

    int itemColor;

    Drawable bitmapDrawable;

    String fileName;

    String url;

    int loadStatus;  /* 0 -wait, 1- ok, 2 - error */


    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getItemSize() {
        return itemSize;
    }

    public void setItemSize(int itemSize) {
        this.itemSize = itemSize;
    }

    public int getItemColor() {
        return itemColor;
    }

    public void setItemColor(int itemColor) {
        this.itemColor = itemColor;
    }

    public Drawable getBitmapDrawable() {
        return bitmapDrawable;
    }

    public void setDrawable(Drawable bitmapDrawable) {
        this.bitmapDrawable = bitmapDrawable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLoadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(int loadStatus) {
        this.loadStatus = loadStatus;
    }

    public void setParams(int posX, int posY, int itemSize, int itemColor) {
        this.posX = posX;
        this.posY = posY;
        this.itemSize = itemSize;
        this.itemColor = itemColor;
    }

    @Override
    public String toString() {
        return "CollageItem{" +
            "id=" + id +
            ", collageId=" + collageId +
            ", viewId=" + viewId +
            ", posX=" + posX +
            ", posY=" + posY +
            ", itemSize=" + itemSize +
            ", itemColor=" + itemColor +
            ", bitmapDrawable=" + bitmapDrawable +
            ", fileName='" + fileName + '\'' +
            ", url='" + url + '\'' +
            ", loadStatus=" + loadStatus +
            '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.viewId);
        dest.writeInt(this.posX);
        dest.writeInt(this.posY);
        dest.writeFloat(this.itemSize);
        dest.writeInt(this.itemColor);
        dest.writeParcelable((Parcelable) this.bitmapDrawable, flags);
        dest.writeString(this.fileName);
        dest.writeString(this.url);
        dest.writeInt(this.loadStatus);
    }

    protected CollageItem(Parcel in) {
        this.viewId = in.readInt();
        this.posX = in.readInt();
        this.posY = in.readInt();
        this.itemSize = in.readInt();
        this.itemColor = in.readInt();
        this.bitmapDrawable = in.readParcelable(Drawable.class.getClassLoader());
        this.fileName = in.readString();
        this.url = in.readString();
        this.loadStatus = in.readByte();
    }

    public static final Creator<CollageItem> CREATOR = new Creator<CollageItem>() {
        @Override
        public CollageItem createFromParcel(Parcel source) {
            return new CollageItem(source);
        }

        @Override
        public CollageItem[] newArray(int size) {
            return new CollageItem[size];
        }
    };
}
