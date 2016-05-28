package com.android.delta_airlines;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PurchaseTicket extends Activity{
	
	TextView flightNum_txt, location_txt, time_txt;
	TextView destination_txt, arrivalTime_txt, date_txt;
	TextView price_txt, assignedSeat_txt, name_txt;
	
	Vibrator vbrate;
	String name;
	SQLiteDatabase deltaDB;
	ProgressDialog confirmPurchase;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase_plane_ticket);
		
		name = getIntent().getStringExtra("Name");
		
		name_txt = (TextView)findViewById(R.id.Name_txt);
		flightNum_txt = (TextView)findViewById(R.id.flightNum_txt);
		date_txt = (TextView)findViewById(R.id.DepartDate_txt);
		location_txt = (TextView)findViewById(R.id.Location_txt);
		destination_txt = (TextView)findViewById(R.id.Destination_txt);
		
		time_txt = (TextView)findViewById(R.id.DepartTime_txt);
		arrivalTime_txt = (TextView)findViewById(R.id.ArrivalTime_txt);
		assignedSeat_txt = (TextView)findViewById(R.id.Seat_txt);
		price_txt = (TextView)findViewById(R.id.Price_txt);
		
		deltaDB = new DeltaDatabaseHelper(this).getReadableDatabase();
		String query = "SELECT * FROM Customer_Flight_Information WHERE Customer_Name = "+name;
		Cursor cursor = deltaDB.rawQuery(query, null);
		
		try{
			if(cursor.moveToNext()){
			   String customerName_String = cursor.getString(cursor.getColumnIndex("Customer_Name"));
			   String flightNum_String = cursor.getString(cursor.getColumnIndex("Flight_Number"));
			   String dateString = cursor.getString(cursor.getColumnIndex("Date"));
			   String locationString = cursor.getString(cursor.getColumnIndex("Location"));
			   String destinationString = cursor.getString(cursor.getColumnIndex("Destination"));
			   String timeString = cursor.getString(cursor.getColumnIndex("Time"));
			   String arrivalTime_String = cursor.getString(cursor.getColumnIndex("Arrival_Time"));
			   String assignedSeat_String = cursor.getString(cursor.getColumnIndex("Assigned_Seat"));
			
			   //Set TextView to gathered Data
			   name_txt.setText(customerName_String);
			   flightNum_txt.setText(flightNum_String);
			   date_txt.setText(dateString);
			   location_txt.setText(CityInitials(locationString));
			   destination_txt.setText(CityInitials(destinationString));
			   time_txt.setText(timeString);
			   arrivalTime_txt.setText(arrivalTime_String);
			   assignedSeat_txt.setText(assignedSeat_String);

			   DetermineBalance();
			}
		}finally{
			cursor.close();
		}
		
		deltaDB.close();
	}
	
	/** Abbreviate Cities **/
	private String CityInitials(String cityLocations) {
		String cityInitials = "";
		
		switch(cityLocations){
			   
			case "Atlanta":
				cityInitials = "ATL";
			break;
			
			case "Greensboro":
				cityInitials = "GSO";
			break;
		}
		
		return cityInitials;
	}

	/** onPress User Purchases Plane Ticket **/
	public void PurchaseTicket_OnClick(View view){
		VibrateButton();
		purchaseTicket();
	}
	
	
	/** Confirming Purchase **/
	void purchaseTicket(){
		confirmPurchase = ProgressDialog.show(PurchaseTicket.this, "Confirming Purchase", "Please Wait", false);
		confirmPurchase.setCancelable(true);
		
		new Thread(new Runnable(){

			@Override
			public void run() {

				try{
					//Wait 2 Secs
					Thread.sleep(2000);
					
					Intent intent_RegistrationComplete = new Intent(PurchaseTicket.this, Registration_Complete.class);
					startActivity(intent_RegistrationComplete);
					finish();
					
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				confirmPurchase.dismiss();
			}
		}).start();
	}
	
	
	/** Determine Balance based on the Ticket Class**/
	private void DetermineBalance() {
		
		String query = "SELECT Ticket_Class FROM Customer_Flight_Information WHERE Customer_Name = "+name;
		Cursor cursor = deltaDB.rawQuery(query, null);
		
		try{
			if(cursor.moveToFirst()){
			   String ticketClass = cursor.getString(cursor.getColumnIndex("Ticket_Class"));
			   
			   switch(ticketClass){
			   	
			   		case "Economy Class":
			   			price_txt.setText("$315");
			   		break;
			   		
			   		case "Business Class":
			   			price_txt.setText("$365");
			   		break;
			   		
			   		case "First Class":
			   			price_txt.setText("$420");
			   		break;	
			   }
			}
		}finally{
			cursor.close();
		}
	}
	
	
	/** Vibrate Method for Button Presses **/
	private void VibrateButton(){
		
		vbrate = (Vibrator)PurchaseTicket.this.getSystemService(Context.VIBRATOR_SERVICE);
		vbrate.vibrate(100);
	}
}

