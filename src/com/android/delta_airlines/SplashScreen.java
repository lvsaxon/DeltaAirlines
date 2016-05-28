package com.android.delta_airlines;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends Activity implements AnimationListener {

	//Splash Screen Timer
	private static int SPLASH_TIMER = 3500;
	private Animation fadeIn;
	private ImageView deltaImage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		
		deltaImage = (ImageView)findViewById(R.id.delta_img);
		
		//Loading Animation
		fadeIn = AnimationUtils.loadAnimation(this, R.anim.intro_fade_in_anim);
		fadeIn.setAnimationListener(this);
		deltaImage.setAnimation(fadeIn);
		
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {

				Intent intent_DeltaActivity = new Intent(SplashScreen.this, DeltaActivity.class);
				startActivity(intent_DeltaActivity);
				
				finish();
			}
		}, SPLASH_TIMER);
	}
	
	
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
	
	}
	
}

