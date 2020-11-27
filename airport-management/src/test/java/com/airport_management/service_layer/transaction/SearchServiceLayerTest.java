package com.airport_management.service_layer.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.airport_management.exception.FlightNotFoundException;
import com.airport_management.model.Flight;
import com.airport_management.model.Plane;
import com.airport_management.repository.mongo.FlightRepositoryMongo;
import com.airport_management.repository.mongo.PlaneRepositoryMongo;
import com.airport_management.repository.mongo.RepositoryMongo;
import com.airport_management.transaction.TransactionCode;
import com.airport_management.transaction.TransactionManager;
import com.airport_management.exception.PlaneNotFoundException;


public class SearchServiceLayerTest {
	
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
	private static final String ID_FIXTURE_1 = "id1-test";
	private static final String MODEL_FIXTURE = "model-test";
	private static final String NUM_FIXTURE = "num-test";
	private static final String ORIGIN_FIXTURE = "origin-test";
	private static final String DESTINATION_FIXTURE = "destination-test";
	private static final Plane PLANE_FIXTURE_1 = new Plane(ID_FIXTURE_1, MODEL_FIXTURE);
	private static final Flight FLIGHT_FIXTURE = new Flight(NUM_FIXTURE, null, null, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
	
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		when(transactionManager.doInTransaction(any()))
			.thenAnswer(answer((TransactionCode<?> code) -> code.apply(repositoryMongo)));
		when(repositoryMongo.createPlaneRepository()).thenReturn(planeRepositoryMongo);
		when(repositoryMongo.createFlightRepository()).thenReturn(flightRepositoryMongo);
	}
	

	
	@Test
	public void testFindAllFlightsByOriginWhenExist() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		List<Flight> flightsFounded = airportServiceLayer.findAllFlightsByOriginSL(ORIGIN_FIXTURE);
		assertThat(flightsFounded).containsExactly(FLIGHT_FIXTURE);
	}
	
	
	
	@Test
	public void testFindAllFlightsByOriginWhenNoExist() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsByOriginSL("new origin");
		});
		assertEquals("There aren't flights with this origin", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllFlightsByDestinationWhenExist() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		List<Flight> flightsFounded = airportServiceLayer.findAllFlightsByDestinationSL(DESTINATION_FIXTURE);
		assertThat(flightsFounded).containsExactly(FLIGHT_FIXTURE);
	}
	
	
	
	@Test
	public void testFindAllFlightsByDestinationWhenNoExist() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsByDestinationSL("new destination");
		});	
		assertEquals("There aren't flights with this destination", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllFlightsByDepartureDateInRangeWhenExist() {
		Flight flight = new Flight(NUM_FIXTURE, ONE_HOUR_LATER, null, null, null, null);
		List<Flight> flights = asList(flight);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		List<Flight> flightsToReturn = airportServiceLayer.findAllFlightsWithDepartureDateInRangeSL(NOW, TWO_HOUR_LATER);
		assertThat(flightsToReturn).containsExactly(flight);
	}
	
	
	
	@Test
	public void testFindAllFlightsByDepartureDateInRangeWhenDepartureDateIsBeforeRange() {
		Flight flight = new Flight(NUM_FIXTURE, NOW, null, null, null, null);
		List<Flight> flights = asList(flight);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsWithDepartureDateInRangeSL(ONE_HOUR_LATER, TWO_HOUR_LATER);
		});	
		assertEquals("There aren't flights with departure date in the selected range", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllFlightsByDepartureDateInRangeWhenDepartureDateIsAfterRange() {
		Flight flight = new Flight(NUM_FIXTURE, TWO_HOUR_LATER, null, null, null, null);
		List<Flight> flights = asList(flight);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsWithDepartureDateInRangeSL(NOW, ONE_HOUR_LATER);
		});	
		assertEquals("There aren't flights with departure date in the selected range", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllFlightsByArrivalDateInRangeWhenExist() {
		Flight flight = new Flight(NUM_FIXTURE, null, ONE_HOUR_LATER, null, null, null);
		List<Flight> flights = asList(flight);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		List<Flight> flightsToReturn = airportServiceLayer.findAllFlightsWithArrivalDateInRangeSL(NOW, TWO_HOUR_LATER);
		assertThat(flightsToReturn).containsExactly(flight);
	}
	
	
	
	@Test
	public void testFindAllFlightsByArrivalDateInRangeWhenArrivalDateIsBeforeRange() {
		Flight flight = new Flight(NUM_FIXTURE, null, NOW, null, null, null);
		List<Flight> flights = asList(flight);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsWithArrivalDateInRangeSL(ONE_HOUR_LATER, TWO_HOUR_LATER);
		});	
		assertEquals("There aren't flights with arrival date in the selected range", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllFlightsByArrivalDateInRangeWhenArrivalDateIsAfterRange() {
		Flight flight = new Flight(NUM_FIXTURE, null, TWO_HOUR_LATER, null, null, null);
		List<Flight> flights = asList(flight);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsWithArrivalDateInRangeSL(NOW, ONE_HOUR_LATER);
		});	
		assertEquals("There aren't flights with arrival date in the selected range", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllFlightsAssociatesWithPlaneWhenExistFlights() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		List<Flight> flightsToReturn = airportServiceLayer.findAllFlightsAssociatesWithPlaneSL(ID_FIXTURE_1);
		assertThat(flightsToReturn).containsExactly(FLIGHT_FIXTURE);
	}
	
	
	
	@Test
	public void testFindAllFlightsAssociatesWithPlaneWhenNoExistFlights() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsAssociatesWithPlaneSL("new plane");
		});
		assertEquals("There aren't flights associates with selected plane", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllPlanesByModelWhenExist() {
		List<Plane> planes = asList(PLANE_FIXTURE_1);
		
		when(planeRepositoryMongo.findAllPlanes())
			.thenReturn(planes);
		
		List<Plane> planesToReturn = airportServiceLayer.findAllPlanesByModelSL(MODEL_FIXTURE);
		assertThat(planesToReturn).containsExactly(PLANE_FIXTURE_1);
	}
	
	
	
	@Test
	public void testFindAllPlanesByModelWhenNoExist() {
		List<Plane> planes = asList(PLANE_FIXTURE_1);
		
		when(planeRepositoryMongo.findAllPlanes())
			.thenReturn(planes);
		
		PlaneNotFoundException ex = assertThrows(PlaneNotFoundException.class, () -> {
			airportServiceLayer.findAllPlanesByModelSL("new-model");
		});
		assertEquals("There aren't planes with insert model", ex.getMessage());
	}
	
	
	
	private static final List<Date> getDates() {
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date oneHourLater = cal.getTime();
		cal.add(Calendar.HOUR_OF_DAY, 1);
		Date twoHourLater = cal.getTime();
		return asList(now, oneHourLater, twoHourLater);
	}	
}