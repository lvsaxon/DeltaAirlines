package com.android.delta_airlines;

public abstract class CustomerFlight_Information {

	private String customerName, currentLocation, destination;
	private String ticketClass, reservedSeats, flightNumber;
	private String departureDate, departureTime, arrivalTime;
	 
	//Customer Login Information Constructor
	public CustomerFlight_Information(String _customerName, String _flightNum, String _location, String _destination, String _date, String _time, String _arrival, String _ticketClass){
		
		customerName = _customerName;
		flightNumber = _flightNum;
		currentLocation = _location;
		destination = _destination;
		
		departureDate = _date;
		departureTime = _time;
		arrivalTime = _arrival;
		ticketClass = _ticketClass;
	}
	
	//Flight Search Constructor
	public CustomerFlight_Information(String _flightNum, String _location, String _destination, String _time, String _arrival){
		
		flightNumber = _flightNum;
		currentLocation = _location;
		destination = _destination;
		departureTime = _time;
		arrivalTime = _arrival;
	}
	
	//Tickets Sold-Out
	public CustomerFlight_Information(String ticket, String date, String time){
			
		ticketClass = ticket;
		departureDate = date;
		departureTime = time;
	}
	
	//Reserved Seat Constructor
	public CustomerFlight_Information(String seats){
		
		reservedSeats = seats;
	}
	
	//Empty Object
	public CustomerFlight_Information(){
		
	}
	
	//Customer Names
	public void setCustomerName(String newName){
		customerName = newName;
	}
	
	public String getCustomerName(){
		return customerName;
	}
	
	
	//Current Locations
	public void setCurrentLocation(String newCurrent){
		currentLocation = newCurrent;
	}
	
	public String getCurrentLocation(){
		return currentLocation;
	}
	
	
	//Destinations
	public void setDestination(String newDestination){
		destination = newDestination;
	}

	public String getDestination(){
		return destination;
	}
	
	
	//Ticket Classes
	public void setTicketClass(String newTicketClass){
		ticketClass = newTicketClass;
	}
	
	public String getTicketClass(){
		return ticketClass;
	}
	
	
	//Flight Number
	public void setFlightNumber(String _newFlightNum){
		flightNumber = _newFlightNum;
	}
	
	public String getFlightNumber(){
		return flightNumber;
	}
	
	
	//Departure Date
	public void setDepartureDate(String _newDate){
		departureDate = _newDate;
	}
	
	public String getDepartureDate(){
		return departureDate;
	}
	
	
	//Departure Time
	public void setDepartureTime(String _newTime){
		departureTime = _newTime;
	}
	
	public String getDepartureTime(){
		return departureTime;
	}
	
	
	//Arrival Time
	public void setArrivalTime(String _newTime){
		arrivalTime = _newTime;
	}
	
	public String getArrivalTime(){
		return arrivalTime;
	}
	
	
	//Seat Reservations
	public void setReservedSeats(String _seats){
		reservedSeats = _seats;
	}
	
	public String getReservedSeats(){
		return reservedSeats;
	}
}

