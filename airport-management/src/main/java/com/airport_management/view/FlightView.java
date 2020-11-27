package com.airport_management.view;

import java.util.List;
import com.airport_management.model.Flight;


public interface FlightView {

	public void showAllFlights(List<Flight> flights);
	public void showFlightError(String message);
	public void flightAdded(Flight flight);
	public void flightRemoved(Flight flight);
}
