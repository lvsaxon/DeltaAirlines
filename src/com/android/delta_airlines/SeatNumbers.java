package com.android.delta_airlines;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.ToggleButton;


public class SeatNumbers {

	int id, btnId[];
	String takenSeats;
	String Seat, ticketClass;
	TableRow row, row2, row3, row4;
	
	View view;
	Context context;
	SQLiteDatabase deltaDB;
	DeltaDatabaseHelper dbHelper;
	CustomerInformation customerInfo;
	
	
	public SeatNumbers(Context c){
		context = c;
		btnId = new int[15];
		
		Seat = "";
		takenSeats = "";
		customerInfo = new CustomerInformation();
		dbHelper = new DeltaDatabaseHelper(context);
	}
	
	
	/** Determine Which ticketClass a ticket is **/
	public void TicketClass(View view, String ticket){
		
		deltaDB = new DeltaDatabaseHelper(context).getReadableDatabase();
		String query = "";
		Cursor cursor;
		
		row = (TableRow)view.findViewById(R.id.first_row);
		row2 = (TableRow)view.findViewById(R.id.second_row);
		row3 = (TableRow)view.findViewById(R.id.third_row);
		row4 = (TableRow)view.findViewById(R.id.fourth_row);
	
		try{
			
			if(ticket.equals("Economy Class")){
			    //Check if all Economy Seats were taken
				query = "SELECT Economy_Seats_Left FROM Seat_Counter";
				cursor = deltaDB.rawQuery(query, null);
		        
				try{
			        int count = 0;
					if(cursor.moveToNext()){
					   count = cursor.getCount();
					}
					
					if(count < 8)
					   EconSeats();
					else if(count == 8)
					   SoldOut("Economy Class Seats");
				}finally{
		        	cursor.close();
		        }
			
		    }else if(ticket.equals("Business Class")){
				//Check if all Business Seats were taken
				query = "SELECT Business_Seats_Left FROM Seat_Counter";
				cursor = deltaDB.rawQuery(query, null);
		        
				try{
			        int count = 0;
					if(cursor.moveToNext()){
					   count = cursor.getCount();
					}
					
					if(count < 8)
					   BusSeats();
					else if(count == 8)
					   SoldOut("Business Class Seats");
				}finally{
		        	cursor.close();
		        }
			
		    }else if(ticket.equals("First Class")){
				//Check if all First Seats were taken
				query = "SELECT First_Seats_Left FROM Seat_Counter";
				cursor = deltaDB.rawQuery(query, null);
		        
				try{
			        int count = 0;
					if(cursor.moveToNext()){
					   count = cursor.getCount();
					}
					
					if(count < 5)
					   FirstSeats();
					else if(count == 5)
					   SoldOut("First Class Seats");
				}finally{
		        	cursor.close();
		        }
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		deltaDB.close();
	}


	//Economy Seats
	void EconSeats(){
		
		for(int i=1; i<=15; i++){
	    	final ToggleButton seatBtn = new ToggleButton(context);
	    	TableRow.LayoutParams tr = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	        seatBtn.setLayoutParams(tr);
	        seatBtn.setText(i+"E");
	        seatBtn.setTextOff(i+"E");
	        seatBtn.setId(i);
	        seatBtn.setTextOn(i+"E");
	        btnId[i-1] = i;
	        
	        if(i<=4)
	           row.addView(seatBtn);
	        
	        if(i>4 && i<=8)
	           row2.addView(seatBtn);
	        
	        if(i>8 && i<=12)
	           row3.addView(seatBtn);
	        
	        if(i>12 && i<=15)
		       row4.addView(seatBtn);
	        
	        //Check to see if any Seats were taken
	        deltaDB = new DeltaDatabaseHelper(context).getReadableDatabase();
			String query = "SELECT Economy_Seats_Left FROM Seat_Counter WHERE Economy_Seats_Left = '"+i+"E'";
			Cursor cursor = deltaDB.rawQuery(query, null);
	        
			try{
		        
				if(cursor.moveToFirst()){
				   takenSeats = cursor.getString(cursor.getColumnIndex("Economy_Seats_Left"));
				}
			}finally{
	        	cursor.close();
	        }
			deltaDB.close();
			
			if(seatBtn.getText().equals(takenSeats))
			   seatBtn.setEnabled(false);
			
			seatBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					for(int i=0; i<btnId.length; i++){
						if(v.getId() == btnId[i]){
						   id = v.getId();
						   Seat = id+"E";
						   v.setEnabled(false);
						   
						   customerInfo.setEconomy_SeatsAvailable(Seat);
						   dbHelper.InsertingSeatsTaken(customerInfo);
						   
						   Toast.makeText(context, "Seat: "+Seat+" Selected!", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
	    }
	}
	
	//Business Seats
	void BusSeats(){
		
		for(int i=1; i<=8; i++){
	    	final ToggleButton seatBtn = new ToggleButton(context);
	    	TableRow.LayoutParams tr = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	        seatBtn.setLayoutParams(tr);
	        seatBtn.setText(i+"B");
	        seatBtn.setTextOff(i+"B");
	        seatBtn.setId(i);
	        seatBtn.setTextOn(i+"B");
	        btnId[i-1] = i;
	        
	        if(i<=4)
	           row.addView(seatBtn);
	        if(i>4 && i<=8)
	           row2.addView(seatBtn);
	        
	        //Check to see if any Seats were taken
	        deltaDB = new DeltaDatabaseHelper(context).getReadableDatabase();
			String query = "SELECT Business_Seats_Left FROM Seat_Counter WHERE Business_Seats_Left = '"+i+"B'";
			Cursor cursor = deltaDB.rawQuery(query, null);
	        
			try{
		        
				if(cursor.moveToFirst()){
				   takenSeats = cursor.getString(cursor.getColumnIndex("Business_Seats_Left"));
				}
			}finally{
	        	cursor.close();
	        }
			deltaDB.close();
			
			if(seatBtn.getText().equals(takenSeats))
			   seatBtn.setEnabled(false);
			
			seatBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					for(int i=0; i<btnId.length; i++){
						if(v.getId() == btnId[i]){
						   id = v.getId();
						   Seat = id+"B";
						   v.setEnabled(false);
						   
						   customerInfo.setBusiness_SeatsAvailable(Seat);
						   dbHelper.InsertingSeatsTaken(customerInfo);
						   
						   Toast.makeText(context, "Seat: "+Seat+" Selected!", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
	    }
	}
	
	//First Seats
	void FirstSeats(){
		
		for(int i=1; i<=5; i++){
	    	final ToggleButton seatBtn = new ToggleButton(context);
	    	TableRow.LayoutParams tr = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	        seatBtn.setLayoutParams(tr);
	        seatBtn.setText(i+"F");
	        seatBtn.setTextOff(i+"F");
	        seatBtn.setId(i);
	        seatBtn.setTextOn(i+"F");
	        btnId[i-1] = i;
	        
	        if(i<=3)
	           row.addView(seatBtn);
	        if(i>3 && i<=5)
	           row2.addView(seatBtn);
	        
	        //Check to see if any Seats were taken
	        deltaDB = new DeltaDatabaseHelper(context).getReadableDatabase();
			String query = "SELECT First_Seats_Left FROM Seat_Counter WHERE First_Seats_Left = '"+i+"F'";
			Cursor cursor = deltaDB.rawQuery(query, null);
	        
			try{
		        
				if(cursor.moveToFirst()){
				   takenSeats = cursor.getString(cursor.getColumnIndex("First_Seats_Left"));
				}
				
			}finally{
	        	cursor.close();
	        }
			deltaDB.close();
			
			if(seatBtn.getText().equals(takenSeats))
			   seatBtn.setEnabled(false);
			
			seatBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					for(int i=0; i<btnId.length; i++){
						if(v.getId() == btnId[i]){
						   id = v.getId();
						   Seat = id+"F";
						   v.setEnabled(false);
						   
						   customerInfo.setFirst_SeatsAvailable(Seat);
						   dbHelper.InsertingSeatsTaken(customerInfo);
						   
						   Toast.makeText(context, "Seat: "+Seat+" Selected!", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});
	    }
	}
	
	
	//Get Ticket Seat#
	public String getTicket_SeatNumber(){
		
		return Seat;
	}
	
	
	/** Restore Economy Seat to Default if Selected **/
	public void RestoreEconSeat_toDefaults(){
		deltaDB = new DeltaDatabaseHelper(context).getWritableDatabase();
		try{
			
			String query = "DELETE FROM Seat_Counter WHERE Economy_Seats_Left ="+ "'"+customerInfo.getEconomy_SeatsAvailable()+"'";
			deltaDB.execSQL(query);
			
		}catch(SQLiteException ex){
			ex.printStackTrace();
		}
		
		deltaDB.close();
	}
	
	
	/** Restore Business Seat to Default if Selected **/
	public void RestoreBusSeat_toDefaults(){
		deltaDB = new DeltaDatabaseHelper(context).getWritableDatabase();
		try{
			
			String query = "DELETE FROM Seat_Counter WHERE Business_Seats_Left ="+ "'"+customerInfo.getBusiness_SeatsAvailable()+"'";
			deltaDB.execSQL(query);
			
		}catch(SQLiteException ex){
			ex.printStackTrace();
		}
		
		deltaDB.close();
	}
	
	
	/** Restore First Seat to Default if Selected **/
	public void RestoreFirstSeat_toDefaults(){
		deltaDB = new DeltaDatabaseHelper(context).getWritableDatabase();
		try{
			
			String query = "DELETE FROM Seat_Counter WHERE First_Seats_Left ="+ "'"+customerInfo.getFirst_SeatsAvailable()+"'";
			deltaDB.execSQL(query);
			
		}catch(SQLiteException ex){
			ex.printStackTrace();
		}
		
		deltaDB.close();
	}
	
	
	/** All Tickets Sold Out **/
	private void SoldOut(String seats) {
		   
	   AlertDialog.Builder alert = new AlertDialog.Builder(context);
	   alert.setIcon(R.drawable.dialog_alert).setTitle("Sold Out!").setMessage("Sorry :P, All "+seats+" Are Taken!");
       alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
	       @Override
		   public void onClick(DialogInterface dialog, int which) {
			   dialog.cancel();
		   }
	   });
       
	   alert.show();
	}
}
