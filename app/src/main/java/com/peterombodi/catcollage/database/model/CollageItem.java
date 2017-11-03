package com.peterombodi.catcollage.database.model;

import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.peterombodi.catcollage.database.CatCollageDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.UUID;

/**
 * Created by Admin on 14.12.2016.
 */
// TODO: 22.09.2017 https://agrosner.gitbooks.io/dbflow/content/ STOP!!!!!!!!!!!


@Table(database = CatCollageDatabase.class)
public class CollageItem extends BaseModel implements Parcelable {

    public CollageItem() {
        this.id = UUID.randomUUID();
    }

    @PrimaryKey // at least one primary key required
    UUID id;

    @Column
    UUID collageId;

    @Column
    int viewId;

    @Column
    int posX;

    @Column
    int posY;

    @Column
    int itemSize;

    @Column
    int itemColor;

    BitmapDrawable bitmapDrawable;

    @Column
    String fileName;

    @Column
    String url;

    @Column
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

    // for oldest code
    public void setParams(int posX, int posY, float itemSize, int itemColor) {
        this.posX = posX;
        this.posY = posY;
        this.itemSize = (int) itemSize;
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
        dest.writeParcelable((Parcelable) this.bitmapDrawable.getBitmap(), flags);
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
        this.bitmapDrawable = in.readParcelable(BitmapDrawable.class.getClassLoader());
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
