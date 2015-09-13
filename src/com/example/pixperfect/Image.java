package com.example.pixperfect;

import android.graphics.Bitmap;

public class Image {
	private Bitmap originalImg;
	private Bitmap currentImg;

	public Image(Bitmap src){
		originalImg = src.copy(Bitmap.Config.ARGB_8888, true);
		currentImg = src.copy(Bitmap.Config.ARGB_8888, true);
	}

	public Image(){
		originalImg = null;
		currentImg = null;
	}

	public void grayscale(){
		currentImg.recycle();
		currentImg = PictureUtils.grayscale(originalImg);
	}

	public void regular(){
		currentImg.recycle();
		currentImg = originalImg.copy(Bitmap.Config.ARGB_8888, true);
	}

	public void negate(){
		currentImg.recycle();
		currentImg = PictureUtils.negate(originalImg);
	}

	public void edgeDetect(){
		currentImg.recycle();
		currentImg = PictureUtils.edgeDetect(originalImg, 25);
	}

	public void sepia(){
		currentImg.recycle();
		currentImg = PictureUtils.sepia(originalImg);
	}

	public Bitmap getBitmap(){
		return currentImg;
	}

	public void setBitmap(Bitmap src){
		originalImg = src.copy(Bitmap.Config.ARGB_8888, true);
		currentImg = src.copy(Bitmap.Config.ARGB_8888, true);
	}

	public void clearBitmap(){
		if(originalImg != null && currentImg != null){
			originalImg.recycle();
			currentImg.recycle();
			originalImg = null;
			currentImg = null;
		}
	}
}
