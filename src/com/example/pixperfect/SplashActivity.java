package com.example.pixperfect;

import com.example.pixedit.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash); //sets the layout file for this view
		
		//create a timer thread
		Thread timer = new Thread(){
			public void run(){
				try{
					sleep(1000); //wait 1000 millis before launching the main activity
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				
				//launch main activity
				Intent startMain = new Intent("com.example.pixedit.MAINACTIVITY");
				startActivity(startMain);
			}
		};
		
		timer.start(); 
	}
	
	@Override
	protected void onPause() {
		//when the splash activity pauses - that is, another activity starts (the main one) - destroy the splash screen
		super.onPause();
		finish(); //kill activity
	}
}
