package com.peterombodi.catcollage.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

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

    public static Observable<File> saveFile(Bitmap bitmap, String fileName) {
        return Observable.create(subscriber -> {
            try {
                File file = new File(Environment.getExternalStorageDirectory(),"docs/");
                if (!file.exists() && !file.mkdirs()) {
                    throw new IOException();
                }
                file = new File(Environment.getExternalStorageDirectory() + "/docs/" + fileName);
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, new FileOutputStream(file));
                subscriber.onNext(file);
                subscriber.onComplete();
            } catch (IOException e) {
                e.printStackTrace();
                subscriber.onError(e);
            }
        });
    }
}
