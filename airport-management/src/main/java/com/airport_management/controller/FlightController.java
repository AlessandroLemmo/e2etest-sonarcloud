package com.airport_management.controller;

import java.io.Serializable;
import java.util.List;

import com.airport_management.exception.FlightNotFoundException;
import com.airport_management.exception.InconsistentDataException;
import com.airport_management.exception.PlaneAlreadyInServiceException;
import com.airport_management.model.Flight;
import com.airport_management.service_layer.transaction.AirportServiceLayer;
import com.airport_management.view.FlightView;

 
public class FlightController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private transient FlightView flightView;
	private transient AirportServiceLayer serviceLayer;
	
	public FlightController(FlightView flightView, AirportServiceLayer serviceLayer) {
		this.flightView = flightView;
		this.serviceLayer = serviceLayer;
	}

	
	
	public void allFlights() {
		List<Flight> flights = serviceLayer.findAllFlightsSL();
		flightView.showAllFlights(flights);
	}
	
	
	
	public Flight idFlight(String num) {
		return serviceLayer.findByNumSL(num);
	}
	
	
	
	public void newFlight(Flight flight) {
		
		try {
			serviceLayer.saveFlightSL(flight);
			flightView.flightAdded(flight);
		}
		catch (PlaneAlreadyInServiceException | InconsistentDataException ex) {
			flightView.showFlightError(ex.getMessage());
		}
	}
	
	
	
	public void deleteFlight(Flight flight) {
		
		try {
			serviceLayer.deleteFlightSL(flight);
			flightView.flightRemoved(flight);
		}
		catch (FlightNotFoundException ex) {
			flightView.showFlightError(ex.getMessage());
		}	
	}
}
