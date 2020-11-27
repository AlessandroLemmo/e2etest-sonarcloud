package com.airport_management.controller;

import static org.mockito.Mockito.doThrow;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.airport_management.exception.FlightNotFoundException;
import com.airport_management.exception.PlaneNotFoundException;
import com.airport_management.model.Flight;
import com.airport_management.model.Plane;
import com.airport_management.service_layer.transaction.AirportServiceLayer;
import com.airport_management.view.SearchView;


public class SearchControllerTest {

	@Mock 
	private SearchView searchView;
	
	@Mock
	private AirportServiceLayer serviceLayer;
	
	@InjectMocks
	private SearchController searchController;
	
	private static final String NUM_FIXTURE = "num-test";
	private static final String ORIGIN_FIXTURE = "origin-test";
	private static final String DESTINATION_FIXTURE = "destination-test";
	private static final String ID_FIXTURE = "id-test";
	private static final String MODEL_FIXTURE = "model-test";
	private static final Date DEPARTURE_DATE_FIXTURE = new Date();
	private static final Date ARRIVAL_DATE_FIXTURE = new Date();
	private static final Date START_DATE_FIXTURE = new Date();
	private static final Date END_DATE_FIXTURE = new Date();
	private static final Plane PLANE_FIXTURE = new Plane(ID_FIXTURE, MODEL_FIXTURE);
	private static final Flight FLIGHT_FIXTURE = new Flight(NUM_FIXTURE, DEPARTURE_DATE_FIXTURE, ARRIVAL_DATE_FIXTURE, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE);
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	
	
	@Test
	public void testFindAllFlightsByOriginWhenNoExist() {		
		
		doThrow(new FlightNotFoundException("No existing flight with the insert origin"))
			.when(serviceLayer).findAllFlightsByOriginSL(ORIGIN_FIXTURE);
		
		searchController.findAllFlightsByOrigin(ORIGIN_FIXTURE);
		verify(searchView).showSearchFlightError("No existing flight with the insert origin");
		verify(searchView).clearListSearchByOrigin();
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(searchView);
	}
	
	
	
	@Test
	public void testFindAllFlightsByOriginWhenExist() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(serviceLayer.findAllFlightsByOriginSL(ORIGIN_FIXTURE))
			.thenReturn(flights);
		
		searchController.findAllFlightsByOrigin(ORIGIN_FIXTURE);
		verify(searchView).showAllFoundedFlightsByOrigin(flights);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(searchView);
	}
	
	
	@Test
	public void testFindAllFlightsByDestinationWhenNoExist() {		
		
		doThrow(new FlightNotFoundException("No existing flight with the insert origin"))
			.when(serviceLayer).findAllFlightsByDestinationSL(DESTINATION_FIXTURE);
		
		searchController.findAllFlightsByDestination(DESTINATION_FIXTURE);
		verify(searchView).showSearchFlightError("No existing flight with the insert origin");
		verify(searchView).clearListSearchByDestination();
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(searchView);
	}
	
	
	
	@Test
	public void testFindAllFlightsByDestinationWhenExist() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(serviceLayer.findAllFlightsByDestinationSL(DESTINATION_FIXTURE))
			.thenReturn(flights);
		
		searchController.findAllFlightsByDestination(DESTINATION_FIXTURE);
		verify(searchView).showAllFoundedFlightsByDestination(flights);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(searchView);
	}
	
	
	
	@Test
	public void testFindAllFlightsByDepartureDateInRangeWhenNoExist() {
		
		doThrow(new FlightNotFoundException("No existing flight with the insert departure date"))
			.when(serviceLayer).findAllFlightsWithDepartureDateInRangeSL(START_DATE_FIXTURE, END_DATE_FIXTURE);
	
		searchController.findAllFlightsWithDepartureDateInRange(START_DATE_FIXTURE, END_DATE_FIXTURE);
		verify(searchView).showSearchFlightError("No existing flight with the insert departure date");
		verify(searchView).clearListSearchByDepartureDate();
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(searchView);
	}
	
	
	
	@Test
	public void testFindAllFlightsByDepartureDateInRangeWhenExist() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(serviceLayer.findAllFlightsWithDepartureDateInRangeSL(START_DATE_FIXTURE, END_DATE_FIXTURE))
			.thenReturn(flights);
		
		searchController.findAllFlightsWithDepartureDateInRange(START_DATE_FIXTURE, END_DATE_FIXTURE);
		verify(searchView).showAllFoundedFlightsByDepartureDate(flights);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(searchView);
	}
	
	
	
	@Test
	public void testFindAllFlightsByArrivalDateInRangeWhenNoExist() {
		
		doThrow(new FlightNotFoundException("No existing flight with the insert arrival date"))
			.when(serviceLayer).findAllFlightsWithArrivalDateInRangeSL(START_DATE_FIXTURE, END_DATE_FIXTURE);
	
		searchController.findAllFlightsWithArrivalDateInRange(START_DATE_FIXTURE, END_DATE_FIXTURE);
		verify(searchView).showSearchFlightError("No existing flight with the insert arrival date");
		verify(searchView).clearListSearchByArrivalDate();
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(searchView);
	}
	
	
	
	@Test
	public void testFindAllFlightsByArrivalDateInRangeWhenExist() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(serviceLayer.findAllFlightsWithArrivalDateInRangeSL(START_DATE_FIXTURE, END_DATE_FIXTURE))
			.thenReturn(flights);
		
		searchController.findAllFlightsWithArrivalDateInRange(START_DATE_FIXTURE, END_DATE_FIXTURE);
		verify(searchView).showAllFoundedFlightsByArrivalDate(flights);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(searchView);
	}
	
	
	
	@Test
	public void testFindAllAssociatesFlightsWhenNoExist() {		
		
		doThrow(new FlightNotFoundException("No existing flights associates at the selected plane"))
			.when(serviceLayer).findAllFlightsAssociatesWithPlaneSL(ID_FIXTURE);
		
		searchController.findAllFlightsAssiociatesWithPlane(ID_FIXTURE);
		verify(searchView).showSearchPlaneError("No existing flights associates at the selected plane");
		verify(searchView).clearListSearchAssociatesFlights();
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(searchView);
	}
	
	
	
	@Test
	public void testFindAllAssociatesFlightsWhenExist() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(serviceLayer.findAllFlightsAssociatesWithPlaneSL(ID_FIXTURE))
			.thenReturn(flights);
		
		searchController.findAllFlightsAssiociatesWithPlane(ID_FIXTURE);
		verify(searchView).showAllFoundedFlightsAssociatesWithPlane(flights);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(searchView);
	}
	
	
	
	@Test
	public void testFindAllPlanesByModelWhenNoExist() {
		
		doThrow(new PlaneNotFoundException("No existing planes with the insert model"))
			.when(serviceLayer).findAllPlanesByModelSL(MODEL_FIXTURE);
		
		searchController.findAllPlanesByModel(MODEL_FIXTURE);
		verify(searchView).showSearchPlaneError("No existing planes with the insert model");
		verify(searchView).clearListSearchByModel();
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(searchView);
	}

	
	
	@Test
	public void testFindAllPlanesByModelWhenExist() {
		List<Plane> planes = asList(PLANE_FIXTURE);
		
		when(serviceLayer.findAllPlanesByModelSL(MODEL_FIXTURE))
			.thenReturn(planes);
		
		searchController.findAllPlanesByModel(MODEL_FIXTURE);
		verify(searchView).showAllFoundedPlanesByModel(planes);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(searchView);
	}
}


