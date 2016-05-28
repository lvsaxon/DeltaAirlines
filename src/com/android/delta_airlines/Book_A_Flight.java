package com.android.delta_airlines;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.*; //Activity, AlertDialog, DatePickerDialog, Dialog, ProgressDialog
import android.content.*; //Context, DialogInterface, Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.*; //Gravity, LayoutInflater, View;
import android.view.ViewGroup.LayoutParams;
import android.widget.*; //AdapterView, ArrayAdapter, Spinner, Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;


public class Book_A_Flight extends Activity {

	int hour, minutes;
	int year, month, day;
	int curYear, curMonth, curDay;
	private String[] cities;
	private Spinner spinner_CityOrigin, spinner_CityDestination;
	private ArrayAdapter<String> cityAdapter;
	
	private static final int TIME_DIALOG = 0;
	//private static final int DATE_DIALOG = 0;

	String newCustomerString, currentLocation_String, destinationString;
	String departureTime_String, arrivalTime_String, dateString;
	String ticketClass_String, seatNumber;
	
	Context context;
	String arrivalTime = "", ticket = "";
	Calendar calendar = Calendar.getInstance();
	ProgressDialog flightCheck_Progress;
	
	Vibrator vbrate;
	ViewPager swipePages;
	RadioGroup ticketClass;
	Button checking_Flights_Btn, seatNum_Btn;
	EditText customerName, setDate, setTime;
	
	SQLiteDatabase deltaDB;
	SeatNumbers seatNumbers;
	CustomerInformation customerInfo, soldOutInfo;
	SwipeView_TicketClass swipeImages_Adapter;
	DeltaDatabaseHelper dbHelper;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booking_flight);
		context = Book_A_Flight.this;
		
		customerInfo = new CustomerInformation();
		dbHelper = new DeltaDatabaseHelper(context);
		seatNumbers = new SeatNumbers(context);
		swipeImages_Adapter = new SwipeView_TicketClass(context);
		
		//Initialize Interface Widgets by IDs
		customerName = (EditText)findViewById(R.id.customer_name);
		spinner_CityOrigin = (Spinner)findViewById(R.id.city_origin);
		spinner_CityDestination = (Spinner)findViewById(R.id.city_destination);
		setTime = (EditText)findViewById(R.id.departTime_editTxt);
		setDate = (EditText)findViewById(R.id.setDate_editTxt);
		ticketClass = (RadioGroup)findViewById(R.id.radio_group);
		seatNum_Btn = (Button)findViewById(R.id.seat_select_btn);
		
		//Initialize TicketClass Images by setting Adapter & SlideView Animations
		swipePages = (ViewPager)findViewById(R.id.view_pager);
		swipePages.setPageTransformer(true, swipeImages_Adapter);
		swipePages.setAdapter(swipeImages_Adapter);
		
		
		//Attach String Vars to Spinner Dropdown Adapters
		cities = new String[] {"Greensboro", "Atlanta"};
		cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, cities);
		
		//Adding Spinner Objects to Layout View
		spinner_CityOrigin.setAdapter(cityAdapter);
		spinner_CityDestination.setAdapter(cityAdapter);
		
		DepartureDate();
		Time_of_Departure();
		TicketClass();
		SwipeImages();
		airplaneFull();
		
		checking_Flights_Btn = (Button)findViewById(R.id.check_flights_btn);
		checking_Flights_Btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				VibrateButton();
				RegisterCustomer();
			}
		});
	}

	
	/** Register New Customer **/
	private void RegisterCustomer() {
		//Get Customer Information
		newCustomerString = customerName.getText().toString();
		currentLocation_String = spinner_CityOrigin.getSelectedItem().toString();
		destinationString = spinner_CityDestination.getSelectedItem().toString();
		departureTime_String = setTime.getText().toString();
		dateString = setDate.getText().toString();
		ticketClass_String = TicketClass();
		seatNumber = seatNumbers.getTicket_SeatNumber();

		//Spinner Selection Items
		CurrentLocation_Selection();
		DestinationSelection();
		
		
		//Error Checking: Make sure Customer fills out everything
		if(newCustomerString.isEmpty()){
		   customerName.setError("Please Enter Your Name!");
		   Toast.makeText(context, "Incomplete Registration", Toast.LENGTH_SHORT).show();
		
		}else if(dateString.isEmpty()){
			  setDate.setError("");
			  Toast.makeText(context, "Incomplete Registration", Toast.LENGTH_SHORT).show();
		
		}else if(departureTime_String.isEmpty()){
			  setTime.setError("");
			  
		}else if(currentLocation_String.equals(destinationString)){
			  DuplicateLocations_AlertDialog();
		
		}else if(ticketClass_String.isEmpty()){
			  Toast.makeText(context, "Please Choose A Ticket Class!", Toast.LENGTH_SHORT).show();
		
		}else if(seatNumber.isEmpty()){
			  soldOutInfo = new CustomerInformation(ticketClass_String, dateString, departureTime_String);
			  allTicketSold();
		}else{	
			
			//Inserting new Customer Information
		    customerInfo.setCustomerName(newCustomerString);
		    customerInfo.setCurrentLocation(currentLocation_String);
		    customerInfo.setDestination(destinationString);
		    customerInfo.setDepartureDate(dateString);
		    customerInfo.setDepartureTime(departureTime_String);
		    customerInfo.setTicketClass(ticketClass_String);
		    customerInfo.setReservedSeats(seatNumber);
		    customerInfo.setArrivalTime(arrivalTime_String);
			  
		    dbHelper.InsertingCustomer(customerInfo);
		    CheckingForFlight();
		 }
	}
	
	
	/** User Selects their Current Location **/
	private void CurrentLocation_Selection(){
		spinner_CityOrigin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
			}
	
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		 });
   }  
	
	
	/** User Selects their Specified Destination **/
	private void DestinationSelection() {
		spinner_CityDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
			}
	
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		 });
	}

	
	/** Notify User if Locations are Duplicated **/
	private void DuplicateLocations_AlertDialog(){
	   AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
	   alert.setIcon(R.drawable.dialog_alert).setTitle("Error").setMessage("Duplicate Locations Present!");
	   alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		
	 	   @Override
		   public void onClick(DialogInterface dialog, int which) {
			   dialog.cancel();
		   }
	   });
	
	   alert.show();
	}

	
	/** User Selects Departure Time **/
	private void Time_of_Departure(){
		
		setTime.setOnClickListener(new View.OnClickListener() {
			
			@Override
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				showDialog(TIME_DIALOG);
			}
		});
	}
	
	
	/** Create TimePicker Dialog **/
	@Override
	protected Dialog onCreateDialog(int id){
		
		if(id == TIME_DIALOG){
		   return new TimePickerDialog(context, timePicker, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false);
		}
		
		return null;
	}
	
	//Time Picker Listener
	private TimePickerDialog.OnTimeSetListener timePicker = new TimePickerDialog.OnTimeSetListener() {

		@Override
		@SuppressLint("SimpleDateFormat")
		public void onTimeSet(TimePicker view, int _hourOfDay, int _minute) {
			VibrateButton();
		
			hour = _hourOfDay;
			minutes = _minute;
			String AM_or_PM = "";
			
			calendar.set(Calendar.HOUR, hour);
			calendar.set(Calendar.MINUTE, minutes);
	        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm");
			String timeTxt = dateFormat.format(calendar.getTime());
			
			if(hour >= 12)
			   AM_or_PM = "PM";
			else if(hour < 12)
			   AM_or_PM = "AM";
			
			Calculate_ArrivalTime(timeTxt+" "+AM_or_PM);
			setTime.setText(timeTxt+" "+AM_or_PM);
			setTime.setError(null);
		}
	};
	
	
	/** Calculate Arrival Times **/
	@SuppressLint("SimpleDateFormat")
	private void Calculate_ArrivalTime(String time){
		
		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
		try{
			Date date = timeFormat.parse(time);
			calendar.setTime(date);
			calendar.add(Calendar.HOUR, 1);
			arrivalTime_String = timeFormat.format(calendar.getTime());
		
		}catch(ParseException ex){
			ex.printStackTrace();
		}
	}
	
	
	/** User Selects Departure Date **/
	private void DepartureDate(){
		
		//Date Dialog
		setDate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//showDialog(DATE_DIALOG);
				
				//Default Click: get Today's Date
				Calendar cal = Calendar.getInstance();
				year = cal.get(Calendar.YEAR);
		        month = cal.get(Calendar.MONTH)+1;
		        day = cal.get(Calendar.DAY_OF_MONTH);
		        setDate.setText(month+"/"+day+"/"+year);
		        
			    showCalendar();
			}
		});
	}

	
	/** Display Calendar **/
	@SuppressLint("InflateParams")
	private void showCalendar(){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		LayoutInflater calendarInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = calendarInflater.inflate(R.layout.calendar_view, null);
		
		CalendarView cal = (CalendarView)view.findViewById(R.id.calendarView);
	    cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
        	
        	@Override
			public void onSelectedDayChange(CalendarView view, int _year, int _monthOfYear, int _dayOfMonth) {
				year = _year;
				month = _monthOfYear+1;
				day = _dayOfMonth;
				
				calendar.set(Calendar.YEAR, year);
			    calendar.set(Calendar.MONTH, month);
			    calendar.set(Calendar.DAY_OF_MONTH, day);
			}
        });
        
        alert.setTitle("Select A Date").setView(view);
		alert.setPositiveButton("Set", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				VibrateButton();
				
				setDate.setText(month+"/"+day+"/"+year);
				setDate.setError(null);
			}
		});
		
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		
		alert.show();
	}
	
	
	/** Create New Date Dialog **/
	/*@Override
	protected Dialog onCreateDialog(int id){
		
		if(id == DATE_DIALOG){
		   return new DatePickerDialog(context, datePicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		}
		
		return null;
	}*/
	
	//Date Dialog
	/*private DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int _year, int _monthOfYear, int _dayOfMonth) {
			   year = _year;
			   month = _monthOfYear+1;
			   day = _dayOfMonth;

			   calendar.set(Calendar.YEAR, year);
			   calendar.set(Calendar.MONTH, month);
			   calendar.set(Calendar.DAY_OF_MONTH, day);
			   
			   setDate.setText(month+"/"+day+"/"+year);
			   setDate.setError(null);
		}
	};*/
	
	
	/** User Selects Ticket Class **/
	String TicketClass(){
		
		ticketClass.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				switch(checkedId){
				
					case R.id.economy_class:
						ticket = "Economy Class";
						swipePages.setCurrentItem(0);
					break;	
				
					case R.id.business_class:
						ticket = "Business Class";
						swipePages.setCurrentItem(1);
					break;
					
					case R.id.first_class:
						ticket = "First Class";
						swipePages.setCurrentItem(2);
					break;
				}
			}
		});
		
		return ticket;
	}
	
	
	/** Notify User if Tickets have Been Sold-Out **/
	void allTicketSold(){

		try{
		   String[] numT = new String[2];
		   numT[2] = "Sold Out";
		   
		}catch(Exception ex){
		   AlertDialog.Builder alert = new AlertDialog.Builder(this);
		   alert.setIcon(R.drawable.dialog_alert).setTitle("Sold Out\n"+soldOutInfo.getDepartureDate() +" "+ soldOutInfo.getDepartureTime());
		   alert.setMessage("Sorry :P, "+soldOutInfo.getTicketClass()+" Tickets Are Sold Out!\n");
	       alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
		       @Override
			   public void onClick(DialogInterface dialog, int which) {
				   dialog.cancel();
			   }
		   });
		   alert.show();
			   
		   ex.printStackTrace();
		}
	}
	
	
	/** Alternative: User Selects Ticket Class **/
	@SuppressWarnings("deprecation")
	void SwipeImages(){
		
		swipePages.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageSelected(int position) {

				switch(position){
				
					case 0:
						ticketClass.check(R.id.economy_class);
					break;	
				
					case 1:
						ticketClass.check(R.id.business_class);
					break;
					
					case 2:
						ticketClass.check(R.id.first_class);
					break;	
				}
			}
		});
	}
	
	
	/** Seat Button Clicked **/
	void airplaneFull(){
		
		seatNum_Btn.setOnClickListener(new View.OnClickListener() {
				
			@Override
			public void onClick(View v) {
				VibrateButton();
				
				if(ticketClass.getCheckedRadioButtonId() == -1)
				   Toast.makeText(context, "Please Select A Ticket Class", Toast.LENGTH_SHORT).show();
				
				else{
				    AlertDialog.Builder alert = new AlertDialog.Builder(context);
					LayoutInflater calendarInflater = (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
					final View view = calendarInflater.inflate(R.layout.seat_num_selection, null);
					
					alert.setTitle("Select A Seat").setView(view);
					seatNumbers.TicketClass(view, ticket);
					
					alert.setPositiveButton("Select", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							VibrateButton();
							
							seatNum_Btn.setEnabled(false);
							dialog.dismiss();
						}
					});
					
					alert.show();
				}
			}
		});
	}
	
	
	/** Verify Flight Availability If All Info Is filled Out**/
	void CheckingForFlight(){
		
		flightCheck_Progress = ProgressDialog.show(context, "Checking For Available Flights", "Please Wait", false);
		flightCheck_Progress.setCancelable(true);
		new Thread(new Runnable(){

			@Override
			public void run() {

				try{
					//Wait for 2 Secs
					Thread.sleep(2000);
					
					Intent intent_FlightSearch = new Intent(Book_A_Flight.this, FlightSearch.class);
					intent_FlightSearch.putExtra("Name", customerName.getText().toString());
					intent_FlightSearch.putExtra("Time", departureTime_String);
					intent_FlightSearch.putExtra("Arrival Time", arrivalTime_String);
					
					startActivity(intent_FlightSearch);
				
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				flightCheck_Progress.dismiss();
			}
		}).start();
	}
	
	
	/** If User comes back to this Activity: set Dialog to null & Reset Activity **/
	@Override
	public void onResume(){
		super.onResume();
		
		customerName.setError(null);
		setTime.setText(null);
		setDate.setText(null);
		setDate.setError(null);
		
		if(flightCheck_Progress != null){
		   flightCheck_Progress.dismiss();
		   flightCheck_Progress = null;
		}
	}
	
	
	/**If User goes pressed onBack Restore Activity to Defaults**/
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		
		seatNumbers.RestoreEconSeat_toDefaults();
		seatNumbers.RestoreBusSeat_toDefaults();
		seatNumbers.RestoreFirstSeat_toDefaults();
	}
	
	/** Vibrate Button onPress **/
	private void VibrateButton(){
		
		vbrate = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
		vbrate.vibrate(100);
	}
}

