package com.airport_management.repository;

import java.util.List;
import com.airport_management.model.Flight;


public interface FlightRepository {
	
	public List<Flight> findAllFlights();
	public Flight findByNum(String number);
	public Flight saveFlight(Flight flight);
	public String deleteFlight(Flight flight);
	
}


