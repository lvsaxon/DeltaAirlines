package com.android.delta_airlines;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;


public class Registration_Complete extends Activity implements AnimationListener{

	int sixSeconds = 6000;
	private Animation fadeIn;
	private MediaPlayer player;
	private TextView enjoyFlight_txt;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_complete);
		
		Toast.makeText(getApplication(), "Thank You For Choosing Delta Airlines!", Toast.LENGTH_SHORT).show();

		enjoyFlight_txt = (TextView)findViewById(R.id.reg_complete);
		player = MediaPlayer.create(this, R.raw.plane_flyby);
		
		//Loading Animation
		fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_anim);
		fadeIn.setAnimationListener(this);
		
		//Play soundBit
		player.seekTo(5000);  //Skip 5secs
		player.start();
		player.setVolume(10, 10);
		enjoyFlight_txt.startAnimation(fadeIn);
		
		//When 10 Seconds are over; transition to DeltaLogin Activity
		CountDownTimer countDown = new CountDownTimer(sixSeconds, 1000){

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFinish() {
				
				Intent intent_DeltaLogin = new Intent(getApplication(), DeltaActivity.class);
				intent_DeltaLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); 
				startActivity(intent_DeltaLogin);

				finish();
			}
		};
		countDown.start();
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
	
	
	/** Release Player when finished **/
	@Override
	public void onPause(){
		super.onPause();
		player.release();
	}
	
	
	@Override
	public void onBackPressed() {
	    // Do Nothing
	}
}
