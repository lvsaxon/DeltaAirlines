package com.android.delta_airlines;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;


public class DeltaActivity extends Activity{
	
	Vibrator vbrate;
	AutoCompleteTextView customerName_txt;
	SQLiteDatabase deltaDB;
	DeltaDatabaseHelper dbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_delta);
		
		customerName_txt = (AutoCompleteTextView)findViewById(R.id.name);
		dbHelper = new DeltaDatabaseHelper(this);
		
		AutoComplete_CustomerName();
	}
	
	
	/** Main Activity Button Listeners **/
	public void whenLoggingInOnClick(View view){
		String check_customerName = "";
		VibrateButton();
		
		switch(view.getId()){
		     
		    //Login Btn: Customer Logs into their Account  
	 		case R.id.login_btn:
	 			String customerName = customerName_txt.getText().toString();
	 			check_customerName = dbHelper.SearchingForFirstName(customerName);
	 			
	 			if(customerName.equals(check_customerName) && !customerName.isEmpty()){
		 		   Toast.makeText(getApplication(), "Logged In!", Toast.LENGTH_SHORT).show();

	 			   Intent intent_CustomerAccount = new Intent(DeltaActivity.this, CustomerAccount.class);	
	 			   intent_CustomerAccount.putExtra("Name", customerName);
	 			   startActivity(intent_CustomerAccount);
	 			}else
	 			   Toast.makeText(getApplication(), "Name Not Registered!", Toast.LENGTH_SHORT).show();
	 			
	 	    break;
	 		
	 		//Flight Register Btn: Customer Books A Flight
	 		case R.id.signin_btn:
	 			Intent intent_BookAFlight = new Intent(DeltaActivity.this, Book_A_Flight.class);
	 		    startActivity(intent_BookAFlight);
	 		    
	 		    Toast.makeText(getApplication(), "Register For a Flight!", Toast.LENGTH_SHORT).show();
	 			
	 		break;	
		}
	}
	
	
	/** AutoComplete the Customer's name on Login**/
	private void AutoComplete_CustomerName(){
		ArrayAdapter<String> adapter;
		ArrayList<String> names = new ArrayList<>();
		
		deltaDB = new DeltaDatabaseHelper(this).getReadableDatabase();
		String query = "SELECT Customer_Name FROM Customer_Flight_Information";
		Cursor cursor = deltaDB.rawQuery(query, null);
		
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            names.add(cursor.getString(cursor.getColumnIndex("Customer_Name")));
        }
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
		customerName_txt.setAdapter(adapter);
		customerName_txt.setThreshold(1);
		
		deltaDB.close();
	}
	
	/** AlertDialog: Exit App **/
	private void ExitApp_AlertDialog(){
	   AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
	   alert.setIcon(R.drawable.dialog_alert).setTitle("Exit Application").setMessage("Are You Sure?");
	   alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		
	 	   @Override
		   public void onClick(DialogInterface dialog, int which) {
	 		  Intent intent_exit = new Intent(Intent.ACTION_MAIN);

              intent_exit.addCategory(Intent.CATEGORY_HOME);        //transitions home when exiting
              intent_exit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Empties back-stack when exiting
              startActivity(intent_exit);
	 		   
              finish();
	 	   }
	   });
	   
	   alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
	   });
	
	   alert.show();
	}
	
	
	/** Exiting App onBackPressed **/
	@Override
	public void onBackPressed(){
		ExitApp_AlertDialog();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		customerName_txt.setText("");
	}
	
	
	/** Vibrate Method for Button Presses **/
	private void VibrateButton(){
		
		vbrate = (Vibrator)DeltaActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
		vbrate.vibrate(100);
	}
}