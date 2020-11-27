package com.airport_management.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.airport_management.exception.FlightNotFoundException;
import com.airport_management.model.Flight;
import com.airport_management.service_layer.transaction.AirportServiceLayer;
import com.airport_management.view.SearchView;
import com.airport_management.exception.PlaneNotFoundException;
import com.airport_management.model.Plane;


public class SearchController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private transient SearchView searchView;
	private transient AirportServiceLayer serviceLayer;
	
	public SearchController(SearchView searchView, AirportServiceLayer serviceLayer) {		
		this.searchView = searchView;
		this.serviceLayer = serviceLayer;
	}
	
	
	
	public void findAllFlightsByOrigin(String origin) {
		
		try {
			List<Flight> flights = serviceLayer.findAllFlightsByOriginSL(origin);
			searchView.showAllFoundedFlightsByOrigin(flights);
		}
		catch(FlightNotFoundException ex) {
			searchView.showSearchFlightError(ex.getMessage());
			searchView.clearListSearchByOrigin();
		}
	}
	
	
	
	public void findAllFlightsByDestination(String destination) {
		try {
			List<Flight> flights = serviceLayer.findAllFlightsByDestinationSL(destination);
			searchView.showAllFoundedFlightsByDestination(flights);
		}
		catch(FlightNotFoundException ex) {
			searchView.showSearchFlightError(ex.getMessage());
			searchView.clearListSearchByDestination();
		}
	}
	
	
	
	public void findAllFlightsWithDepartureDateInRange(Date start, Date end) {
		try {
			List<Flight> flights = serviceLayer.findAllFlightsWithDepartureDateInRangeSL(start, end);
			searchView.showAllFoundedFlightsByDepartureDate(flights);
		} 
		catch(FlightNotFoundException ex) {
			searchView.showSearchFlightError(ex.getMessage());
			searchView.clearListSearchByDepartureDate();
		}
	}
	
	
	
	public void findAllFlightsWithArrivalDateInRange(Date start, Date end) {
		try {
			List<Flight> flights = serviceLayer.findAllFlightsWithArrivalDateInRangeSL(start, end);
			searchView.showAllFoundedFlightsByArrivalDate(flights);
		} 
		catch(FlightNotFoundException ex) {
			searchView.showSearchFlightError(ex.getMessage());
			searchView.clearListSearchByArrivalDate();
		}
	}
	
	
	
	public void findAllFlightsAssiociatesWithPlane(String planeId) {
		try {
			List<Flight> flights = serviceLayer.findAllFlightsAssociatesWithPlaneSL(planeId);
			searchView.showAllFoundedFlightsAssociatesWithPlane(flights);
		}
		catch (FlightNotFoundException ex) {
			searchView.showSearchPlaneError(ex.getMessage());
			searchView.clearListSearchAssociatesFlights();
		}
	}
	
	
	
	public void findAllPlanesByModel(String model) {
		try {
			List<Plane> planes = serviceLayer.findAllPlanesByModelSL(model);
			searchView.showAllFoundedPlanesByModel(planes);
		}
		catch(PlaneNotFoundException ex) {
			searchView.showSearchPlaneError(ex.getMessage());
			searchView.clearListSearchByModel();
		}
	}
	
}


