package com.airport_management.service_layer;

import java.util.List;
import com.airport_management.model.Flight;


public interface FlightServiceLayer {
	
	public void saveFlightSL(Flight flight);
	public void deleteFlightSL(Flight flight);
	public Flight findByNumSL(String num);
	public List<Flight> findAllFlightsSL();
}



