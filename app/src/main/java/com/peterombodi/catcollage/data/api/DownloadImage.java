package com.peterombodi.catcollage.data.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.peterombodi.catcollage.R;
import com.peterombodi.catcollage.database.model.CollageItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import io.reactivex.subjects.PublishSubject;

import static com.peterombodi.catcollage.constants.Constants.STATUS_DOWNLOAD_ERROR;
import static com.peterombodi.catcollage.constants.Constants.STATUS_DOWNLOAD_OK;
import static com.peterombodi.catcollage.constants.Constants.STATUS_WAIT_DOWNLOAD;

/**
 * Created by Admin on 25.01.2017.
 */

public class DownloadImage implements IDownloadImage {

	private static final String TAG = "DownloadImage";
	private Context context;
	private PublishSubject<Integer> subjectLoadImage;

	public DownloadImage(Context context) {
		this.context = context;
		this.subjectLoadImage = getPublishSubject();
	}

	@Override
	public PublishSubject<Integer> getPublishSubject() {
		if (subjectLoadImage == null) subjectLoadImage = PublishSubject.create();
		return subjectLoadImage;
	}

	@Override
	public void downloadImage(ImageView _view, CollageItem _item) {
		_item.setLoadStatus(STATUS_WAIT_DOWNLOAD);
		Picasso.with(context)
			.load(_item.getUrl())
			.transform(new CropSquareTransformation())
			.placeholder(R.drawable.ic_action_download)
			.error(R.drawable.ic_action_warning)
			.into(_view, new com.squareup.picasso.Callback() {
				@Override
				public void onSuccess() {
					if (_item.getLoadStatus() != STATUS_DOWNLOAD_OK) {
						_item.setLoadStatus(STATUS_DOWNLOAD_OK);
						subjectLoadImage.onNext(-1);
					}
				}

				@Override
				public void onError() {
					if (_item.getLoadStatus() != STATUS_DOWNLOAD_ERROR) {
						_item.setLoadStatus(STATUS_DOWNLOAD_ERROR);
						subjectLoadImage.onNext(0);
					}
				}
			});
	}


	private class CropSquareTransformation implements Transformation {
		@Override
		public Bitmap transform(Bitmap source) {
			int size = Math.min(source.getWidth(), source.getHeight());
			int x = (source.getWidth() - size) / 2;
			int y = (source.getHeight() - size) / 2;
			Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
			if (result != source) {
				source.recycle();
			}
			return result;
		}

		@Override
		public String key() {
			return "square()";
		}
	}

//    public Context getContext(){
//        return context;
//    }

}
