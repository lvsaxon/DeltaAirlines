package com.android.delta_airlines;

import android.app.*; //Activity, AlertDialog, ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class CustomerAccount extends Activity{

	private String name;
	private TextView flight_txt, location_txt, destination_txt;
	private TextView ticketClass_txt, seat_txt, date_txt;
	private TextView time_txt, arrivalTime_txt, name_txt;
	
	private Context context;
	private Button returnTicket_btn;
	
	Vibrator vbrate;
	ProgressDialog deleteAccount;
	SQLiteDatabase deltaDB;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_account);
		context = CustomerAccount.this;
		
		name_txt = (TextView)findViewById(R.id.Name_txt);
		flight_txt = (TextView)findViewById(R.id.FlightNum_txt);
		location_txt = (TextView)findViewById(R.id.Location_txt);
		destination_txt = (TextView)findViewById(R.id.Destination_txt);
		ticketClass_txt = (TextView)findViewById(R.id.Ticket_txt);
		
		seat_txt = (TextView)findViewById(R.id.Seat_txt);
		date_txt = (TextView)findViewById(R.id.Date_txt);
		time_txt = (TextView)findViewById(R.id.Time_txt);
		arrivalTime_txt = (TextView)findViewById(R.id.ArrivalTime_txt);
		returnTicket_btn = (Button)findViewById(R.id.returnTicket_btn);
		
		DisplayCustomer_Details();
	}

	
	/** Display Customer Details **/
	private void DisplayCustomer_Details() {
		name = getIntent().getStringExtra("Name");
		
		deltaDB = new DeltaDatabaseHelper(this).getReadableDatabase();
		String query = "SELECT * FROM Customer_Flight_Information WHERE Customer_Name = '"+name+"'";
		Cursor cursor = deltaDB.rawQuery(query, null);
		
		try{
			
			if(cursor.moveToNext()){
			   String flightNum_String = cursor.getString(cursor.getColumnIndex("Flight_Number"));
			   String dateString = cursor.getString(cursor.getColumnIndex("Date"));
			   String locationString = cursor.getString(cursor.getColumnIndex("Location"));
			   String destinationString = cursor.getString(cursor.getColumnIndex("Destination"));
			   String timeString = cursor.getString(cursor.getColumnIndex("Time"));
			   String ticketClass_String = cursor.getString(cursor.getColumnIndex("Ticket_Class"));
			   String arrivalTime_String = cursor.getString(cursor.getColumnIndex("Arrival_Time"));
			   String assignedSeat_String = cursor.getString(cursor.getColumnIndex("Assigned_Seat"));
			   
			   name_txt.setText(name);
			   flight_txt.setText(flightNum_String);
			   date_txt.setText(dateString);
			   location_txt.setText(locationString);
			   destination_txt.setText(destinationString);
			   time_txt.setText(timeString);
			   ticketClass_txt.setText(ticketClass_String);
			   arrivalTime_txt.setText(arrivalTime_String);
			   seat_txt.setText(assignedSeat_String);
			   
			   TicketReturns(ticketClass_String, assignedSeat_String);
			}
		}finally{
			cursor.close();
		}
	}


	/** Customer Return Their Ticket that they Purchased **/
	private void TicketReturns(String ticketClass, String assignedSeat) {
		
		deltaDB  = new DeltaDatabaseHelper(this).getWritableDatabase();
		String query = "DELETE FROM Customer_Flight_Information WHERE Customer_Name = '"+name+"'";
		String query2 = "";
		
		switch(ticketClass){
			
			case "Economy Class":
				query2 = "DELETE FROM Seat_Counter WHERE Economy_Seats_Left = '"+assignedSeat+"'";
			break;	
			
			case "Business Class":
				query2 = "DELETE FROM Seat_Counter WHERE Business_Seats_Left = '"+assignedSeat+"'";
			break;
				
			case "First Class":
				query2 = "DELETE FROM Seat_Counter WHERE First_Seats_Left = '"+assignedSeat+"'";
			break;
		}
		
		final String[] statements = {query2, query};
		returnTicket_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				VibrateButton();
				
				//Create AlertDialog
				AlertDialog.Builder alert = new AlertDialog.Builder(context);
				alert.setIcon(R.drawable.dialog_alert).setTitle("Ticket Returns").setMessage("Are You Sure?");
				alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//Start ProgressDialog
						deleteAccount = ProgressDialog.show(context, "Returning Ticket", "Please Wait", false);
						deleteAccount.setCancelable(false);
						
						//Wait 2 Secs for deleting data & transition to MainActivity
						new Thread(new Runnable(){

							@Override
							public void run() {
								
								try{
									
									Thread.sleep(2000);
									for(String stmt: statements){
										deltaDB.execSQL(stmt);
									}
									deltaDB.close();
									
									Intent intent_DeltaActivity = new Intent(context, DeltaActivity.class);
									startActivity(intent_DeltaActivity);
									finish();
									
								}catch(Exception ex){
									ex.printStackTrace();
								}
								
								deleteAccount.dismiss();
								//Toast.makeText(context, "Ticket Returned", Toast.LENGTH_SHORT).show();
							}
						}).start();
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
		});
	}
	
	
	/** Vibrate Button onPress **/
	private void VibrateButton(){
		
		vbrate = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		vbrate.vibrate(100);
	}
}
