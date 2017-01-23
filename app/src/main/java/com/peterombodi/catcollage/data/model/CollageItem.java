package com.peterombodi.catcollage.data.model;

import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Admin on 14.12.2016.
 */

public class CollageItem implements Parcelable {

    private int viewId;

    private int posX;

    private int posY;

    private float itemSize;

    private int itemColor;

    private BitmapDrawable bitmapDrawable;

    private String url;

    private boolean loaded;

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

    public float getItemSize() {
        return itemSize;
    }

    public void setItemSize(float itemSize) {
        this.itemSize = itemSize;
    }

    public int getItemColor() {
        return itemColor;
    }

    public void setItemColor(int itemColor) {
        this.itemColor = itemColor;
    }

    public BitmapDrawable getBitmapDrawable() {
        return bitmapDrawable;
    }

    public void setBitmapDrawable(BitmapDrawable bitmapDrawable) {
        this.bitmapDrawable = bitmapDrawable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void setParams(int posX, int posY, float itemSize, int itemColor) {
        this.posX = posX;
        this.posY = posY;
        this.itemSize = itemSize;
        this.itemColor = itemColor;
    }

    @Override
    public String toString() {
        return "CollageItem{" +
                "viewId=" + viewId +
                ", posX=" + posX +
                ", posY=" + posY +
                ", itemSize=" + itemSize +
                ", itemColor=" + itemColor +
                ", bitmapDrawable=" + bitmapDrawable +
                ", url='" + url + '\'' +
                ", loaded=" + loaded +
                '}';
    }

    public CollageItem() {
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
        dest.writeString(this.url);
        dest.writeByte(this.loaded ? (byte) 1 : (byte) 0);
    }

    protected CollageItem(Parcel in) {
        this.viewId = in.readInt();
        this.posX = in.readInt();
        this.posY = in.readInt();
        this.itemSize = in.readFloat();
        this.itemColor = in.readInt();
        this.bitmapDrawable = in.readParcelable(BitmapDrawable.class.getClassLoader());
        this.url = in.readString();
        this.loaded = in.readByte() != 0;
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
