package com.airport_management.service_layer.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static java.util.Arrays.asList;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.airport_management.exception.FlightNotFoundException;
import com.airport_management.exception.InconsistentDataException;
import com.airport_management.exception.PlaneAlreadyInServiceException;
import com.airport_management.model.Flight;
import com.airport_management.model.Plane;
import com.airport_management.repository.mongo.FlightRepositoryMongo;
import com.airport_management.repository.mongo.PlaneRepositoryMongo;
import com.airport_management.repository.mongo.RepositoryMongo;
import com.airport_management.transaction.TransactionCode;
import com.airport_management.transaction.TransactionManager;


public class FlightServiceLayerTest {

	@Mock
	TransactionManager transactionManager;
	
	@Mock
	RepositoryMongo repositoryMongo;	
	
	@Mock
	PlaneRepositoryMongo planeRepositoryMongo;
	
	@Mock
	FlightRepositoryMongo flightRepositoryMongo;
	
	@InjectMocks
	AirportServiceLayer airportServiceLayer;
	
	private static final Date NOW = getDates().get(0);
	private static final Date ONE_HOUR_LATER = getDates().get(1);
	private static final Date TWO_HOUR_LATER = getDates().get(2);
	private static final Date THREE_HOUR_LATER = getDates().get(3);
	private static final Plane PLANE_FIXTURE_1 = new Plane("id1-test", "model1-test");
	private static final Plane PLANE_FIXTURE_2 = new Plane("id2-test", "model2-test");
	private static final String FLIGHT_NUM_FIXTURE_1 = "num1-test";
	private static final String FLIGHT_NUM_FIXTURE_2 = "num2-test";
	private static final String ORIGIN_FIXTURE = "origin-test";
	private static final String DESTINATION_FIXTURE = "destination-test";
	private static final Flight FLIGHT_FIXTURE = new Flight(FLIGHT_NUM_FIXTURE_1, null, null, null, null, null);
	
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		when(transactionManager.doInTransaction(any()))
			.thenAnswer(answer((TransactionCode<?> code) -> code.apply(repositoryMongo)));
		when(repositoryMongo.createPlaneRepository()).thenReturn(planeRepositoryMongo);
		when(repositoryMongo.createFlightRepository()).thenReturn(flightRepositoryMongo);
	}
	

	
	@Test
	public void testSaveFlightWithSuccess() {
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_1, NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		when(flightRepositoryMongo.saveFlight(flightToAdd))
			.thenReturn(flightToAdd);
		airportServiceLayer.saveFlightSL(flightToAdd);
		verify(flightRepositoryMongo).saveFlight(flightToAdd);
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsBeforeArrivalDate() {
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_1, TWO_HOUR_LATER, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		InconsistentDataException ex = assertThrows(InconsistentDataException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("departure or arrival date is wrong", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsEqualToArrivalDate() {
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_1, NOW, NOW, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		InconsistentDataException ex = assertThrows(InconsistentDataException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("departure or arrival date is wrong", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenOriginAndDestinaitonAreEquals() {
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_1, NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, ORIGIN_FIXTURE, PLANE_FIXTURE_1);
		InconsistentDataException ex = assertThrows(InconsistentDataException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("origin or destination is wrong", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsEqualToAnotherFlightOfTheSamePlane() {
		
		Flight flightAlreadyExist = new Flight(FLIGHT_NUM_FIXTURE_1, NOW, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		List<Flight> flights = asList(flightAlreadyExist);
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_2, NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		PlaneAlreadyInServiceException ex = assertThrows(PlaneAlreadyInServiceException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("This plane is already in service. Departure or arrival date are equals to exsisting flight.", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenArrivalDateIsEqualToAnotherFlightOfTheSamePlane() {

		Flight flightAlreadyExist = new Flight(FLIGHT_NUM_FIXTURE_1, ONE_HOUR_LATER, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		List<Flight> flights = asList(flightAlreadyExist);
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_2, NOW, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		PlaneAlreadyInServiceException ex = assertThrows(PlaneAlreadyInServiceException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("This plane is already in service. Departure or arrival date are equals to exsisting flight.", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureOrArrivalDateIsEqualToAnotherFlight() {

		Flight flightAlreadyExist = new Flight(FLIGHT_NUM_FIXTURE_1, NOW, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		List<Flight> flights = asList(flightAlreadyExist);
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_2, NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_2);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		airportServiceLayer.saveFlightSL(flightToAdd);
		verify(flightRepositoryMongo).saveFlight(flightToAdd);
		verifyNoMoreInteractions(ignoreStubs(flightRepositoryMongo));
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsBetweenDatesOfAnotherFlightOfTheSamePlane() {
		
		Flight flightAlreadyExist = new Flight(FLIGHT_NUM_FIXTURE_1, NOW, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		List<Flight> flights = asList(flightAlreadyExist);
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_2, ONE_HOUR_LATER, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		PlaneAlreadyInServiceException ex = assertThrows(PlaneAlreadyInServiceException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("This plane is already in service. Departure date is between dates of existing flight", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsAfterToAnotherFlightOfTheSamePlane() {
		
		Flight flightAlreadyExist = new Flight(FLIGHT_NUM_FIXTURE_1, NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		List<Flight> flights = asList(flightAlreadyExist);
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_2, TWO_HOUR_LATER, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		airportServiceLayer.saveFlightSL(flightToAdd);
		verify(flightRepositoryMongo).saveFlight(flightToAdd);
		verifyNoMoreInteractions(ignoreStubs(flightRepositoryMongo));
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsBetweenDatesOfAnotherFlight() {

		Flight flightAlreadyExist = new Flight(FLIGHT_NUM_FIXTURE_1, NOW, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		List<Flight> flights = asList(flightAlreadyExist);
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_2, ONE_HOUR_LATER, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_2);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		airportServiceLayer.saveFlightSL(flightToAdd);
		verify(flightRepositoryMongo).saveFlight(flightToAdd);
		verifyNoMoreInteractions(ignoreStubs(flightRepositoryMongo));
	}
	
	

	@Test
	public void testSaveFlightWhenArrivalDateIsBetweenDatesOfAnotherFlightOfTheSamePlane() {
		
		Flight flightAlreadyExist = new Flight(FLIGHT_NUM_FIXTURE_1, ONE_HOUR_LATER, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		List<Flight> flights = asList(flightAlreadyExist);
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_2, NOW, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		PlaneAlreadyInServiceException ex = assertThrows(PlaneAlreadyInServiceException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("This plane is already in service. Arrival date is between dates of existing flight", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenArrivalDateIsBeforeDatesOfAnotherFlightOfTheSamePlane() {
		
		Flight flightAlreadyExist = new Flight(FLIGHT_NUM_FIXTURE_1, TWO_HOUR_LATER, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		List<Flight> flights = asList(flightAlreadyExist);
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_2, NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		airportServiceLayer.saveFlightSL(flightToAdd);
		verify(flightRepositoryMongo).saveFlight(flightToAdd);
		verifyNoMoreInteractions(ignoreStubs(flightRepositoryMongo));
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsBeforeAndArrivalDateIsAfterDatesOfAnotherFlightOfTheSamePlane() {
		
		Flight flightAlreadyExist = new Flight(FLIGHT_NUM_FIXTURE_1, ONE_HOUR_LATER, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		List<Flight> flights = asList(flightAlreadyExist);
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_2, NOW, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		PlaneAlreadyInServiceException ex = assertThrows(PlaneAlreadyInServiceException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("This plane is already in service. Departure date is before and arrival date is after dates of existing flight", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsBeforeAndArrivalDateIsAfterDatesOfAnotherFlight() {
		
		Flight flightAlreadyExist = new Flight(FLIGHT_NUM_FIXTURE_1, ONE_HOUR_LATER, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		List<Flight> flights = asList(flightAlreadyExist);
		Flight flightToAdd = new Flight(FLIGHT_NUM_FIXTURE_2, NOW, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_2);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		airportServiceLayer.saveFlightSL(flightToAdd);
		verify(flightRepositoryMongo).saveFlight(flightToAdd);
		verifyNoMoreInteractions(ignoreStubs(flightRepositoryMongo));
	}
	
	
	
	@Test
	public void testFindByNum() {
		
		when(flightRepositoryMongo.findByNum(FLIGHT_NUM_FIXTURE_1))
			.thenReturn(FLIGHT_FIXTURE);
		
		Flight flightToReturn = airportServiceLayer.findByNumSL(FLIGHT_NUM_FIXTURE_1);
		verify(flightRepositoryMongo).findByNum(FLIGHT_NUM_FIXTURE_1);
		assertThat(flightToReturn).isEqualTo(FLIGHT_FIXTURE);
		verifyNoMoreInteractions(ignoreStubs(flightRepositoryMongo));

	}
	
	
	
	@Test
	public void testFindAllFlights() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		List<Flight> flightsToReturn = airportServiceLayer.findAllFlightsSL();
		verify(flightRepositoryMongo).findAllFlights();
		assertThat(flightsToReturn).containsExactly(FLIGHT_FIXTURE);
		verifyNoMoreInteractions(ignoreStubs(flightRepositoryMongo));
	}
	
	
	
	@Test
	public void testDeleteFlightWhenExist() {
		
		when(flightRepositoryMongo.findByNum(FLIGHT_NUM_FIXTURE_1))
			.thenReturn(FLIGHT_FIXTURE);
		
		airportServiceLayer.deleteFlightSL(FLIGHT_FIXTURE);
		verify(flightRepositoryMongo).deleteFlight(FLIGHT_FIXTURE);
		verifyNoMoreInteractions(ignoreStubs(flightRepositoryMongo));
	}
	
	
	
	@Test
	public void testDeleteFlightWhenNoExist() {
		
		when(flightRepositoryMongo.findByNum(FLIGHT_NUM_FIXTURE_1))
			.thenReturn(null);
		
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.deleteFlightSL(FLIGHT_FIXTURE);
		});
		assertEquals("No existing flight with num " + FLIGHT_NUM_FIXTURE_1, ex.getMessage());
	}
	
	
	
	private static final List<Date> getDates() {
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date oneHourLater = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date twoHourLater = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date threeHourLater = cal.getTime();
		return asList(now, oneHourLater, twoHourLater, threeHourLater);
	}	
}


