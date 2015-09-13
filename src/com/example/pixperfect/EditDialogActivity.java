package com.example.pixperfect;

import com.example.pixedit.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EditDialogActivity extends Activity implements OnClickListener{
	public final static String GRAYSCALE = "grayscale";
	public final static String NEGATE = "negate";
	public final static String EDGE_DETECT = "edge detection";
	public final static String REGULAR = "regular";
	public final static String SEPIA = "sepia";
	public final static String key = "edit type";
	
	private Button grayscaleButton;
	private Button negateButton;
	private Button regularButton;
	private Button edgeDetectionButton;
	private Button sepiaButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_pic_dialog);
		
		grayscaleButton = (Button) findViewById(R.id.grayscaleButton);
		negateButton = (Button) findViewById(R.id.negateButton);
		regularButton = (Button) findViewById(R.id.regularButton);
		edgeDetectionButton = (Button) findViewById(R.id.edgeDetectionButton);
		sepiaButton = (Button) findViewById(R.id.sepiaButton);
		
		grayscaleButton.setOnClickListener(this);
		negateButton.setOnClickListener(this);
		regularButton.setOnClickListener(this);
		edgeDetectionButton.setOnClickListener(this);
		sepiaButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		//Return to the main activity what type of image editing action the user selected
		if (view.getId() == R.id.grayscaleButton){
			Intent data = new Intent(); //use intent object to relay that information
			data.putExtra(key, GRAYSCALE); //put the edit action as a string extra
			setResult(RESULT_OK, data); //set the result of this activity as that intent object which has our action written as a string
			finish(); //kill activity
		}else if (view.getId() == R.id.negateButton){
			Intent data = new Intent();
			data.putExtra(key, NEGATE);
			setResult(RESULT_OK, data);
			finish();
		}else if(view.getId() == R.id.regularButton){
			Intent data = new Intent();
			data.putExtra(key, REGULAR);
			setResult(RESULT_OK, data);
			finish();
		}else if (view.getId() == R.id.edgeDetectionButton){
			Intent data = new Intent();
			data.putExtra(key, EDGE_DETECT);
			setResult(RESULT_OK, data);
			finish();
		}else if (view.getId() == R.id.sepiaButton){
			Intent data = new Intent();
			data.putExtra(key, SEPIA);
			setResult(RESULT_OK, data);
			finish();
		}
	}
}
