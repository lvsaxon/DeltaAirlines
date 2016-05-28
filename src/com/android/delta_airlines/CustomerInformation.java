package com.android.delta_airlines;

public class CustomerInformation extends CustomerFlight_Information{

	String economySeats, businessSeats, firstSeats;
	
	//Customer Login Constructor
	public CustomerInformation(String _customerName, String _flightNum, String _location, String _destination, String _date, String _time, String _arrival, String _ticketClass) {
		super(_customerName, _flightNum, _location, _destination, _date, _time, _arrival, _ticketClass);
	}
	
	//Flight Search Constructor
	public CustomerInformation(String _flightNum, String _location, String _destination, String _time, String _arrival){
		super(_flightNum,  _location, _destination, _time, _arrival);
	}
	
	//Tickets Sold-Out
	public CustomerInformation(String _ticketClass, String _date, String _time){
		super(_ticketClass, _date, _time);
	}
	
	//Reserved Seat Constructor
	public CustomerInformation(String seats){
		super(seats);
	}
	
	//Empty Object
	public CustomerInformation(){
		super();
	}
	
	//Economy Seats
	public void setEconomy_SeatsAvailable(String _econ){
		economySeats = _econ;
	}
	
	public String getEconomy_SeatsAvailable(){
		return economySeats;
	}
	
	
	//Business Seats
	public void setBusiness_SeatsAvailable(String _bus){
		businessSeats = _bus;
	}
	
	public String getBusiness_SeatsAvailable(){
		return businessSeats;
	}
	
	//First Seats
	public void setFirst_SeatsAvailable(String _first){
		firstSeats = _first;
	}
	
	public String getFirst_SeatsAvailable(){
		return firstSeats;
	}
}



