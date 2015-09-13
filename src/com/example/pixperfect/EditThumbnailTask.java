package com.example.pixperfect;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class EditThumbnailTask extends AsyncTask<Object, Void, Image>{
	private WeakReference<ImageView> imageViewReference;
	public EditThumbnailTask(ImageView iv){
		imageViewReference = new WeakReference<ImageView>(iv);
	}
	
	@Override
	protected Image doInBackground(Object...args) {
		// TODO Auto-generated method stub
		Image image = (Image) args[0];
		String editType = (String) args[1];
		
		if (editType.equals(EditDialogActivity.GRAYSCALE)){
			image.grayscale();
		}else if (editType.equals(EditDialogActivity.NEGATE)){
			image.negate();
		}else if (editType.equals(EditDialogActivity.EDGE_DETECT)){
			image.edgeDetect();
		}else if (editType.equals(EditDialogActivity.REGULAR)){
			image.regular();
		}else if (editType.equals(EditDialogActivity.SEPIA)){
			image.sepia();
		}
		
		return image;
	}

	@Override
	protected void onPostExecute(Image result) {
		// TODO Auto-generated method stub
		if (imageViewReference != null){
			final ImageView imageView = imageViewReference.get();
			imageView.setImageBitmap(result.getBitmap());
		}
		super.onPostExecute(result);
	}
}
