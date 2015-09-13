package com.example.pixperfect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pixedit.R;

public class MainActivity extends Activity implements OnClickListener{
	private ImageView imageView;
	private ImageButton takePictureButton;
	private Button saveButton;
	private Button loadButton;
	private Button editPictureButton;
	
	//request codes for intents that return a result
	private final static int CAMERA_REQUEST = 1; 
	private final static int SELECT_PICTURE_REQUEST = 2;
	private final static int EDIT_ACTION_REQUEST = 3;
	
	private Image img;
	private Image thumbnail; //a scaled down preview of the full-sized image
	
	private String cameraPictureFilePath = null;
	private boolean readyToSave = true; //a background ASyncTask performs the operation for editing the full-scale image...only allow user to save after operation is complete

	private EditImageTask editImageTask;
	private EditThumbnailTask editThumbnailTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		imageView = (ImageView) findViewById(R.id.imageView1);
		takePictureButton = (ImageButton) findViewById(R.id.takePictureButton);
		saveButton = (Button) findViewById(R.id.saveButton);
		loadButton = (Button) findViewById(R.id.loadButton);
		editPictureButton = (Button) findViewById(R.id.editPictureButton);
		
		img = new Image();
		thumbnail = new Image();
		
		takePictureButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		loadButton.setOnClickListener(this);
		editPictureButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		//user wants to take a picture
		if (view.getId() == R.id.takePictureButton){
			//use Intent to open up stock camera application to allow the user to take a picture
			Intent takePic = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			//specify where to save this picture so it can be loaded by the application later
			File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PixPerfect"); 
			if (!dir.exists())
				dir.mkdirs();

			// Create a unique media file name
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());  //get the date + time picture was taken
			File file = new File(dir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg"); //the actual path of the picture
			takePic.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			cameraPictureFilePath = dir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg";
			
			startActivityForResult(takePic, CAMERA_REQUEST);
			
		}
		//user wants to save
		else if (view.getId() == R.id.saveButton){
			if (readyToSave)
				saveImage();
			else
				Toast.makeText(getApplicationContext(), "Waiting on operation to finish...", Toast.LENGTH_LONG).show();
		}
		//user wants to load a picture from memory
		else if (view.getId() == R.id.loadButton){
			//If there is already an ongoing edit process when user hits "load image", cancel all processes
			if(editImageTask != null){
				editImageTask.cancel(true);
				editImageTask = null;
			}
			if(editThumbnailTask != null){
				editThumbnailTask.cancel(true);
				editThumbnailTask = null;
			}
			//and then free up bitmap memory
			if(thumbnail != null && img != null){
				thumbnail.clearBitmap();
				img.clearBitmap();
			}
			
			//let the user choose a picture through whatever program he/she wishes
			Intent selectPicture = new Intent(Intent.ACTION_GET_CONTENT);
			selectPicture.setType("image/*");
			startActivityForResult(Intent.createChooser(selectPicture, "Select Picture"), SELECT_PICTURE_REQUEST);
			
		}
		//user wants to edit
		else if (view.getId() == R.id.editPictureButton){
			//open up the EditDialogAcitivity to let the user pick a picture operation
			Intent viewEditDialog = new Intent("com.example.pixedit.EDITDIALOGACTIVITY");
			startActivityForResult(viewEditDialog, EDIT_ACTION_REQUEST);
		}
	}

	@TargetApi(19)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){ 		//RESULT_OK means the Intent went ok and returned proper data
			//temporarily load the picture that the user just took
			File file = new File(cameraPictureFilePath);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8;
			Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath()); //temporary bitmap file
			Bitmap scaledBmp = BitmapFactory.decodeFile(file.getAbsolutePath(), options); //temporary bitmap file
			
			//fix the orientation of the bitmap
			try {
				ExifInterface exif = new ExifInterface(file.getAbsolutePath());
				int orientation = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));
				int rotation = 0;
				if (orientation == 6){
					rotation = 90;
				}else if (orientation == 8){
					rotation = -90;
				}
				
				Matrix matrix = new Matrix();
	            matrix.postRotate(rotation);
	            
	            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
	            img.setBitmap(bmp);
	            scaledBmp = Bitmap.createBitmap(scaledBmp, 0, 0, scaledBmp.getWidth(), scaledBmp.getHeight(), matrix, true);
	            thumbnail.setBitmap(scaledBmp);
			} catch (IOException e) {
				e.printStackTrace();
			}
            
            //garbage collect; no need for bmp anymore
            bmp.recycle(); 
            scaledBmp.recycle();
            
            imageView.setImageBitmap(thumbnail.getBitmap());
            
            //add picture to gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
		}
		
		else if (requestCode == SELECT_PICTURE_REQUEST && resultCode == RESULT_OK){
			Uri uri = data.getData();
			loadImageFromUri(uri);
			
		}else if (requestCode == EDIT_ACTION_REQUEST && resultCode == RESULT_OK ){						
			String editType = data.getStringExtra(EditDialogActivity.key); //get what type of edit action the user requested

			if (editThumbnailTask == null || editThumbnailTask.getStatus().equals(Status.FINISHED)){
				editThumbnailTask = new EditThumbnailTask(imageView); //task to edit and post to imageview the thumbnail image
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
					editThumbnailTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, thumbnail, editType);
				else
					editThumbnailTask.execute(thumbnail, editType);
				
			}else{
				//previous thumbnail task didn't finish - don't let user make another additional picture edit
				Toast.makeText(getApplicationContext(), "Please wait for previous operation to complete!", Toast.LENGTH_LONG).show();
			}
			
			if (editImageTask == null || editImageTask.getStatus().equals(Status.FINISHED)){
				editImageTask = new EditImageTask(); //task to edit in background the full-size image
				
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
					editImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, img,  editType);
				else
					editImageTask.execute(img,  editType);
				
			}else{
				Toast.makeText(getApplicationContext(), "Waiting on Image Operation", Toast.LENGTH_SHORT).show();
			}
			
			
			if (!(editImageTask == null) && !(editImageTask.getStatus().equals(Status.FINISHED))){
				editImageTask.cancel(true);
			}
			editImageTask = new EditImageTask(); //task to edit in background the full-size image
			
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
				editImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, img,  editType);
			else
				editImageTask.execute(img,  editType);
			
			readyToSave = false;
		}
	}
	
	private void saveImage(){
		File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PixPerfect"); //the folder into which to store pics
		if (!dir.exists()){
			dir.mkdirs();
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());  //get the date + time picture was taken
		File mediaFile = new File(dir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg"); //the actual path of the picture

		try {
			FileOutputStream out;
			out = new FileOutputStream(mediaFile);
			Bitmap bmp = img.getBitmap();
			bmp.compress(Bitmap.CompressFormat.JPEG, 90, out); //write our picture onto phone
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//add picture to gallery
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    Uri contentUri = Uri.fromFile(mediaFile);
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	}
	
	private void loadImageFromUri(Uri uri){
		try {
			InputStream stream = getContentResolver().openInputStream(uri);
			Bitmap bmp = BitmapFactory.decodeStream(stream);
			stream.close();
			
			stream = getContentResolver().openInputStream(uri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8; //arbitrarily chosen scale value - probably want to change that eventually!
			Bitmap scaledBmp = BitmapFactory.decodeStream(stream, null, options);
            stream.close();
         
            //rotate the image to get the original orientation 
            String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
            @SuppressWarnings("deprecation")
			Cursor cur = managedQuery(uri, orientationColumn, null, null, null);
            int orientation = -1;
            if (cur != null && cur.moveToFirst()) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
            }  
            Matrix matrix = new Matrix();
            
            matrix.postRotate(orientation);
            bmp = Bitmap.createBitmap(bmp , 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
            scaledBmp = Bitmap.createBitmap(scaledBmp , 0, 0, scaledBmp.getWidth(), scaledBmp.getHeight(), matrix, true);
            
            img.setBitmap(bmp);
            thumbnail.setBitmap(scaledBmp);
            imageView.setImageBitmap(thumbnail.getBitmap());
            
            bmp.recycle();
            scaledBmp.recycle();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private class EditImageTask extends AsyncTask<Object, Void, Image>{	
		@Override
		protected Image doInBackground(Object...args) {
			if (!isCancelled()){
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
			
			return null;
		}

		@TargetApi(19)
		@Override
		protected void onPostExecute(Image result) {
			// TODO Auto-generated method stub
			readyToSave = true;
		}
	}
}