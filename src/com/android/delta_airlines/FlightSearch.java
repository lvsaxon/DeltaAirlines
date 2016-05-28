package com.android.delta_airlines;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.*; //LayoutInflater, View, ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.*; //ArrayAdapter, ListView, TextView;
import android.widget.AdapterView.OnItemClickListener;


public class FlightSearch extends Activity implements AnimationListener {

	String assignedSeat_String, name, arrivalTime[];
	String defaultTime, default_ArrivalTime, departTime[];
	protected List<CustomerInformation> flightInfo;

	private ListView flight_ListView;
	private Animation slideUp;
	private FlightDetails_Adapter adapter;
	private CustomerInformation customerInfo;
	private Vibrator vbrate;

	Calendar calendar = Calendar.getInstance();
	DeltaDatabaseHelper dbHelper;
	SQLiteDatabase deltaDB;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flight_check_listview);
		
		Toast.makeText(getApplication(), "Search Complete", Toast.LENGTH_SHORT).show();

		dbHelper = new DeltaDatabaseHelper(FlightSearch.this);
		flight_ListView = (ListView)findViewById(R.id.flight_check_listview);
		name = "'"+getIntent().getStringExtra("Name")+"'";
		
		slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up_anim);
		slideUp.setAnimationListener(this);
		flight_ListView.setAnimation(slideUp);
		
		arrivalTime = new String[3];
		departTime = new String[3];
		
		String defaultTime = getIntent().getStringExtra("Time");
		departTime[0] = defaultTime;
		String default_ArrivalTime = getIntent().getStringExtra("Arrival Time");
		arrivalTime[0] = default_ArrivalTime;
		
		//2nd Flight
		Add2Hours_To_Time(defaultTime, 2);
		Add2Hour30_To_ArrivalTime(default_ArrivalTime, 2, 30);
		
		//3rd Flight
		Add4Hours_To_Time(defaultTime, 4);
		Add4Hours15_To_ArrivalTime(default_ArrivalTime, 4, 15);
		
		DisplayFlight_Information();
	}
	
	
	/** Calculate arrivalTime by adding 1 hour **/
	@SuppressLint("SimpleDateFormat")
	private void Add2Hour30_To_ArrivalTime(String _time, int hours, int mins) {
		String estimatedArrival = _time;
		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
		
		try{
			Date date = timeFormat.parse(estimatedArrival);
			calendar.setTime(date);
			calendar.add(Calendar.HOUR, hours);
			calendar.add(Calendar.MINUTE, mins);
			arrivalTime[1] = timeFormat.format(calendar.getTime());
			
		}catch(ParseException ex){
			ex.printStackTrace();
		}
	}


	/** Calculate Time by 2 hours **/
	@SuppressLint("SimpleDateFormat")
	private void Add2Hours_To_Time(String _time, int hours) {
		String time = _time;
		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
		
		try{
			Date date = timeFormat.parse(time);
			calendar.setTime(date);
			calendar.add(Calendar.HOUR, hours);
			departTime[1] = timeFormat.format(calendar.getTime());
			
		}catch(ParseException ex){
			ex.printStackTrace();
		}
	}
	
	
	/** Calculate the Arrival Time add 4 Hours  & 15 Mins**/
	@SuppressLint("SimpleDateFormat")
	private void Add4Hours15_To_ArrivalTime(String _time, int hours, int mins) {
		String estimatedArrival = _time;
		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
		
		try{
			Date date = timeFormat.parse(estimatedArrival);
			calendar.setTime(date);
			calendar.add(Calendar.HOUR, hours);
			calendar.add(Calendar.MINUTE, mins);
			arrivalTime[2] = timeFormat.format(calendar.getTime());
			
		}catch(ParseException ex){
			ex.printStackTrace();
		}
	}

	
	/** Calculate Time by 4 hours **/
	@SuppressLint("SimpleDateFormat")
	private void Add4Hours_To_Time(String _time, int hours) {
		String time = _time;
		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
		
		try{
			Date date = timeFormat.parse(time);
			calendar.setTime(date);
			calendar.add(Calendar.HOUR, hours);
			departTime[2] = timeFormat.format(calendar.getTime());
			
		}catch(ParseException ex){
			ex.printStackTrace();
		}
	}

	
	/** Display a List of Available Flights **/
	private void DisplayFlight_Information() {
		String[] flights = {"2332", "2310", "2307"};
		int length = flights.length;
		
		deltaDB = new DeltaDatabaseHelper(this).getReadableDatabase();
		String query = "SELECT * FROM Customer_Flight_Information WHERE Customer_Name = "+name;
		Cursor cursor = deltaDB.rawQuery(query, null);

		try{
			
			if(cursor.moveToNext()){
			   String date = cursor.getString(cursor.getColumnIndex("Date"));
			   
			   //When Nov.3; only 2 flights Available & departTimes are 7 or 2
			   if(date.contains("11/3/")){
				  length = 2;
				  Add2Hours_To_Time(departTime[0], 7);
				  Add2Hour30_To_ArrivalTime(arrivalTime[0], 7, 15);
			   }
				
			   flightInfo = new ArrayList<CustomerInformation>();
			   for(int i=0; i<length; i++){
				   String flightNum_String = flights[i];
				   String locationString = cursor.getString(cursor.getColumnIndex("Location"));
				   String destinationString = cursor.getString(cursor.getColumnIndex("Destination"));
				   String timeString = departTime[i];
				   String arrivalTime_String = arrivalTime[i];
				   assignedSeat_String = cursor.getString(cursor.getColumnIndex("Assigned_Seat"));
				   
				   //Add Flight# Info to Adapter & Display on ListView
				   customerInfo = new CustomerInformation(flightNum_String, locationString, destinationString, timeString, arrivalTime_String);
				   flightInfo.add(customerInfo);
			   }
			   
			   adapter = new FlightDetails_Adapter();
			   flight_ListView.setAdapter(adapter);
			   
			   SelectingListItem();
			}
		}finally{
			cursor.close();
		}
		
		deltaDB.close();
	}


	/** Selecting ListView Element; transition to ReserveASeat class **/
	private void SelectingListItem() {
		flight_ListView.setOnItemClickListener(new OnItemClickListener(){
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				VibrateButton();
				
				String query;
				deltaDB = new DeltaDatabaseHelper(FlightSearch.this).getWritableDatabase();
				
				switch(position){
				
					case 0: //2332
						query = "UPDATE Customer_Flight_Information "+
								"SET Flight_Number = '2332',"
								+" Arrival_Time = '"+ arrivalTime[0] +"',"
								+" Time = '"+ departTime[0] +"'"
								+" WHERE Customer_Name = "+name;
						deltaDB.execSQL(query);
					break;
					
					case 1: //2310
						query = "UPDATE Customer_Flight_Information " +
								"SET Flight_Number ='2310',"
								+" Arrival_Time = '"+ arrivalTime[1] +"',"
								+" Time = '"+ departTime[1] +"'"
								+" WHERE Customer_Name = "+name;
						deltaDB.execSQL(query);
					break;
						
					case 2: //2307
						query = "UPDATE Customer_Flight_Information " +
								"SET Flight_Number = '2307',"
								+" Arrival_Time = '"+ arrivalTime[2] +"',"
								+" Time = '"+ departTime[2] +"'"
								+" WHERE Customer_Name = "+name;
						deltaDB.execSQL(query);
					break;
				}
				deltaDB.close();
				
				Intent intent_PurchaseTicket = new Intent(FlightSearch.this, PurchaseTicket.class);
				intent_PurchaseTicket.putExtra("Name", name);
				startActivity(intent_PurchaseTicket);
			}
		});
	}
	
	
	@SuppressLint("ViewHolder")
	/** Custom Setup Adapter: Show Available Flight Info **/
	private class FlightDetails_Adapter extends ArrayAdapter<CustomerInformation> {
		
		private FlightDetails_Adapter() {
			super(getApplication(), R.layout.flight_check_details, flightInfo);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			CustomerInformation customerReservations = flightInfo.get(position);
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.flight_check_details, parent, false);
			
			//Flight#
			TextView flightNum_txt = (TextView)view.findViewById(R.id.flight_Num);
			flightNum_txt.setText(customerReservations.getFlightNumber());
			
			//Departure location & time
			TextView location_txt = (TextView)view.findViewById(R.id.city_depart);
			location_txt.setText(customerReservations.getCurrentLocation());
			
			TextView time_txt = (TextView)view.findViewById(R.id.time_depart);
			time_txt.setText(customerReservations.getDepartureTime());
			
			
			//Arrival destination & expectedTime
			TextView destination_txt = (TextView)view.findViewById(R.id.city_arrival);
			destination_txt.setText(customerReservations.getDestination());
			
			TextView expectedTime_txt = (TextView)view.findViewById(R.id.time_arrival);
			expectedTime_txt.setText(customerReservations.getArrivalTime());
			
			//Seat#
			TextView seatNumber_txt = (TextView)view.findViewById(R.id.seat_Num);
			customerReservations = new CustomerInformation(assignedSeat_String);
			seatNumber_txt.setText(customerReservations.getReservedSeats());
			
			return view;
		}
	}
	
	
	/** Get Numeric value from assignedSeat **/
	private void ParseString(SQLiteDatabase db, String seat) {
		char seatClass = 0;
		String query, seatNum = "";
		
		for(char c : seat.toCharArray()){
			if(Character.isDigit(c))
			   seatNum += c;
			else
			   seatClass = c;
		}
		
		switch(seatClass){
		
			case 'E':
				query = "DELETE FROM Seat_Counter WHERE Economy_Seats_Left = "+seatNum;
				db.execSQL(query);
			break;

			case 'B':
				query = "DELETE FROM Seat_Counter WHERE Business_Seats_Left = "+seatNum;
				db.execSQL(query);
			break;

			case 'F':
				query = "DELETE FROM Seat_Counter WHERE First_Seats_Left = "+seatNum;
				db.execSQL(query);
			break;
		}
	}
	
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		
		String query = "DELETE FROM Customer_Flight_Information WHERE Customer_Name = "+name;
		deltaDB = new DeltaDatabaseHelper(this).getWritableDatabase();
		ParseString(deltaDB, assignedSeat_String);
		
		deltaDB.execSQL(query);
		deltaDB.close();
	}
	
	
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	
	
	/** Vibrate Button onPress **/
	private void VibrateButton(){
		
		vbrate = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
		vbrate.vibrate(100);
	}
}
