package com.android.delta_airlines;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public class DeltaDatabaseHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VER = 1;
	private static final String DATABASE_NAME = "Delta.customerDB";
	private static final String CREATE_CUSTOMER_TABLE = "CREATE TABLE IF NOT EXISTS Customer_Flight_Information"
	+"(Customer_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
	+" Customer_Name TEXT(25),"
	+" Flight_Number TEXT(25),"
	+" Location TEXT(25),"
	+" Destination TEXT(25),"
	+" Date TEXT(25),"
	+" Time TEXT(25),"
	+" Arrival_Time TEXT(25),"
	+" Ticket_Class TEXT(25),"
	+" Assigned_Seat TEXT(25) );";
	
	/* Customer Table Attributes */
	private static final String CUSTOMER_TABLE_NAME = "Customer_Flight_Information";
	private static final String CUSTOMER_NAME = "Customer_Name";
	private static final String LOCATION = "Location";
	private static final String DESTINATION = "Destination";
	private static final String DATE = "Date";
	private static final String TIME = "Time";
	private static final String ARRIVAL_TIME = "Arrival_Time";
	private static final String TICKET_CLASS = "Ticket_Class";
	private static final String ASSIGNED_SEAT = "Assigned_Seat";
	
/********************************************************************************************/
	/* New Table: Seat Counter */
	private static final String CREATE_SEAT_COUNTER_TABLE = "CREATE TABLE IF NOT EXISTS Seat_Counter"
			+"(Seats_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
			+" Economy_Seats_Left INTEGER,"
			+" Business_Seats_Left INTEGER,"
			+" First_Seats_Left INTEGER );";
	
	/* Seat Counter Table Attributes */
	private static final String SEAT_COUNTER_TABLE_NAME = "Seat_Counter";
	private static final String ECON_SEATS_LEFT = "Economy_Seats_Left";
	private static final String BUS_SEATS_LEFT = "Business_Seats_Left";
	private static final String FIRST_SEATS_LEFT = "First_Seats_Left";
	
	SQLiteDatabase deltaDB;
	ContentValues contentValue;
	
	@Override
	/** Create DB table **/
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(CREATE_CUSTOMER_TABLE);
		db.execSQL(CREATE_SEAT_COUNTER_TABLE);
		deltaDB = db;
	}

	
	@Override
	/** Delete table if exists to upgrade the DB **/
	public void onUpgrade(SQLiteDatabase db, int oldData, int newData) {
		String query = "DROP TABLE IF EXISTS "+ CUSTOMER_TABLE_NAME;
		String query2 = "DROP TABLE IF EXISTS "+ SEAT_COUNTER_TABLE_NAME;
		
		db.execSQL(query);
		db.execSQL(query2);
		onCreate(db);
	}
	
	
	/** Setting up DB with DatabaseHelper **/
	public DeltaDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VER);
	}

	
	/** Insert Customer Flight Info into Delta Database **/
	public void InsertingCustomer(CustomerInformation customerInformation) {
		deltaDB = this.getWritableDatabase();
		contentValue = new ContentValues();
		
		contentValue.put(CUSTOMER_NAME, customerInformation.getCustomerName());
		contentValue.put(LOCATION, customerInformation.getCurrentLocation());
		contentValue.put(DESTINATION, customerInformation.getDestination());
		contentValue.put(DATE, customerInformation.getDepartureDate());
		contentValue.put(TIME, customerInformation.getDepartureTime());
		contentValue.put(ARRIVAL_TIME, customerInformation.getArrivalTime());
		contentValue.put(TICKET_CLASS, customerInformation.getTicketClass());
		contentValue.put(ASSIGNED_SEAT, customerInformation.getReservedSeats());
		
		deltaDB.insert(CUSTOMER_TABLE_NAME, null, contentValue);
		deltaDB.close();
	}
	
	
	/** Insert # of Seat Classes Left **/
	public void InsertingSeatsTaken(CustomerInformation customerInformation){
		deltaDB = this.getWritableDatabase();
		contentValue = new ContentValues();
		
		contentValue.put(ECON_SEATS_LEFT, customerInformation.getEconomy_SeatsAvailable());
		contentValue.put(BUS_SEATS_LEFT, customerInformation.getBusiness_SeatsAvailable());
		contentValue.put(FIRST_SEATS_LEFT, customerInformation.getFirst_SeatsAvailable());
		
		deltaDB.insert(SEAT_COUNTER_TABLE_NAME, null, contentValue);
		deltaDB.close();
	}
	
	
	/** Searches for the Customer's Name within the Database **/
	public String SearchingForFirstName(String customer_name) {
		Cursor cursor;
		String query, customerName="";
		
		deltaDB = this.getReadableDatabase();
		query = "SELECT Customer_Name FROM "+ CUSTOMER_TABLE_NAME+" WHERE Customer_Name = '"+customer_name+"'";
		cursor = deltaDB.rawQuery(query, null);
		
		try{
			if(cursor.moveToFirst())
			   customerName = cursor.getString(0);
		}catch(SQLiteException ex){
			ex.printStackTrace();
		}
		
		/*if(cursor.moveToFirst()){
			do{
				customerName = cursor.getString(0);
				if(customerName.equals(customer_name))
				   break;
			}
			while(cursor.moveToNext());
		}*/
		
		return customerName;
	}
}

