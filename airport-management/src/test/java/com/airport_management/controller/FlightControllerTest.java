package com.airport_management.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static java.util.Arrays.asList;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.airport_management.exception.FlightNotFoundException;
import com.airport_management.exception.InconsistentDataException;
import com.airport_management.exception.PlaneAlreadyInServiceException;
import com.airport_management.model.Flight;
import com.airport_management.repository.FlightRepository;
import com.airport_management.service_layer.transaction.AirportServiceLayer;
import com.airport_management.view.FlightView;


public class FlightControllerTest {

	@Mock 
	private FlightView flightView;
	
	@Mock
	private FlightRepository flightRepository;
	
	@Mock
	private AirportServiceLayer serviceLayer;
	
	@InjectMocks
	private FlightController flightController;
	
	private static final String NUM_FIXTURE = "num-test";
	private static final Flight FLIGHT_FIXTURE = new Flight(NUM_FIXTURE, null, null, null, null, null);

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	
	
	@Test
	public void testAllFlightsWhenNoExistFlights() {
		when(serviceLayer.findAllFlightsSL())
			.thenReturn(null);
		
		flightController.allFlights();
		verify(flightView).showAllFlights(null);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(flightView);
	}
	
	
	
	@Test
	public void testAllFlightsWhenExistFlights() {
		List<Flight> flights = asList(new Flight());
		
		when(serviceLayer.findAllFlightsSL())
			.thenReturn(flights);
		
		flightController.allFlights();
		verify(flightView).showAllFlights(flights);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(flightView);
	}
	
	
	
	@Test
	public void testFindFlightWhenExistFlight() {
		
		when(serviceLayer.findByNumSL(NUM_FIXTURE))
			.thenReturn(FLIGHT_FIXTURE);
		
		Flight flight = flightController.idFlight(NUM_FIXTURE);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		assertEquals(FLIGHT_FIXTURE, flight);
	}
	
	
	
	@Test
	public void testFindFlightWhenNoExistFlight() {

		when(serviceLayer.findByNumSL(NUM_FIXTURE))
			.thenReturn(null);
		
		Flight flight = flightController.idFlight(NUM_FIXTURE);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		assertEquals(null, flight);
	}
	
	
	
	@Test
	public void testNewFlight() {
		flightController.newFlight(FLIGHT_FIXTURE);
		InOrder inOrder = inOrder(serviceLayer, flightView);
		inOrder.verify(serviceLayer).saveFlightSL(FLIGHT_FIXTURE);
		inOrder.verify(flightView).flightAdded(FLIGHT_FIXTURE);
		verifyNoMoreInteractions(serviceLayer, flightView);
	}
	
	
	
	@Test
	public void testNewFlightWhenAssociatedPlaneIsAlreadyInService() {
		
		doThrow(new PlaneAlreadyInServiceException("Plane already in service"))
			.when(serviceLayer).saveFlightSL(FLIGHT_FIXTURE);
		
		flightController.newFlight(FLIGHT_FIXTURE);
		verify(flightView).showFlightError("Plane already in service");
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(flightView);
	}
	
	
	
	@Test
	public void TestNewFlightWhenThereIsAnInconsistenceOfInputData() {

		doThrow(new InconsistentDataException("Inconsistence of input data"))
			.when(serviceLayer).saveFlightSL(FLIGHT_FIXTURE);
		
		flightController.newFlight(FLIGHT_FIXTURE);
		verify(flightView).showFlightError("Inconsistence of input data");
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(flightView);
	}
	
	
	
	@Test
	public void testDeleteFlightWhenAlreadyExist() {
		
		when(serviceLayer.findByNumSL(NUM_FIXTURE))
			.thenReturn(FLIGHT_FIXTURE);
		
		flightController.deleteFlight(FLIGHT_FIXTURE);
		InOrder inOrder = inOrder(serviceLayer, serviceLayer, flightView);
		inOrder.verify(serviceLayer).deleteFlightSL(FLIGHT_FIXTURE);
		inOrder.verify(flightView).flightRemoved(FLIGHT_FIXTURE);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(flightView);
	}
	
	
	
	@Test
	public void testDeleteFlightWhenNoExist() {
		
		doThrow(new FlightNotFoundException("Flight not found"))
			.when(serviceLayer).deleteFlightSL(FLIGHT_FIXTURE);
		
		flightController.deleteFlight(FLIGHT_FIXTURE);
		verify(flightView).showFlightError("Flight not found");
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(flightView);
	}
}






