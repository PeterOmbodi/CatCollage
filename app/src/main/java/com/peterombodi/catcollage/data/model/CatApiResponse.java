package com.peterombodi.catcollage.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Admin on 04.01.2017.
 */


@Root(name = "response")
public class CatApiResponse {
    @Element(name = "data")
    private ResponseData data;

    public ResponseData getResponseData() {
        return data;
    }

    public void setResponseData(ResponseData catsData) {
        this.data = catsData;
    }

    public Images getImages() {
        return data.getImages();
    }

    public List<Image> getImageList() {
        return data.getImages().getImageList();
    }

    @Override
    public String toString() {
        return "CatApiResponse{" +
                "catsData=" + data +
                '}';
    }
}

@Root(name="data")
class ResponseData {
    @Element(name = "images")
    private Images images;

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "CatsData{" +
                "images=" + images +
                '}';
    }
}

@Root(name="images")
class Images {
    @ElementList(inline = true)
    private List<Image> imageList;

    public List<Image> getImageList() {
        return imageList;
    }

    public void setImageList(List<Image> imageList) {
        this.imageList = imageList;
    }

    @Override
    public String toString() {
        return "Images{" +
                "imageList=" + imageList +
                '}';
    }
}





