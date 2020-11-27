package com.airport_management.service_layer.transaction;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.airport_management.exception.FlightNotFoundException;
import com.airport_management.model.Flight;
import com.airport_management.model.Plane;
import com.airport_management.repository.mongo.PlaneRepositoryMongo;
import com.airport_management.transaction.TransactionManager;
import com.airport_management.exception.PlaneNotFoundException;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class SearchServiceLayerIT {

	private static final String AIRPORT_DB_NAME = "airport";
	private static final String PLANE_COLLECTION_NAME = "plane";
	private static final String FLIGHT_COLLECTION_NAME = "flight";

	private static final Date NOW = getDates().get(0);
	private static final Date ONE_HOUR_LATER = getDates().get(1);
	private static final Date TWO_HOUR_LATER = getDates().get(2);
	private static final Date THREE_HOUR_LATER = getDates().get(3);
	private static final Plane PLANE_FIXTURE_1 = new Plane("model1-test");
	private static final Plane PLANE_FIXTURE_2 = new Plane("model2-test");
	private static final String ORIGIN_FIXTURE = "origin-test";
	private static final String NUM_FIXTURE = "num1-test";
	private static final String DESTINATION_FIXTURE = "destination-test";
	private static final Flight FLIGHT_FIXTURE = new Flight(NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);	
	
	private MongoClient client;
	private PlaneRepositoryMongo planeRepository;
	
	private AirportServiceLayer airportServiceLayer;
	private TransactionManager transactionManager;
	private MongoCollection<Document> planeCollection;
	private MongoCollection<Document> flightCollection;

	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		client = new MongoClient("localhost");
		 
		planeRepository = new PlaneRepositoryMongo(client, AIRPORT_DB_NAME, PLANE_COLLECTION_NAME);	
		
		MongoDatabase database = client.getDatabase(AIRPORT_DB_NAME);
		database.drop();
		planeCollection = database.getCollection(PLANE_COLLECTION_NAME);
		flightCollection = database.getCollection(FLIGHT_COLLECTION_NAME);

		planeRepository.startSession();
		
		transactionManager = new TransactionManager(client, AIRPORT_DB_NAME, PLANE_COLLECTION_NAME, FLIGHT_COLLECTION_NAME);
		airportServiceLayer = new AirportServiceLayer(transactionManager);
	}
	
	
	
	@Test
	public void testFindAllFlightsByOriginWhenExist() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		addTestFlightToRepository(FLIGHT_FIXTURE);
		List<Flight> flightsFounded = airportServiceLayer.findAllFlightsByOriginSL(ORIGIN_FIXTURE);
		assertThat(flightsFounded).containsExactly(FLIGHT_FIXTURE);
	}
	
	
	
	@Test
	public void testFindAllFlightsByOriginWhenNoExist() {
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsByOriginSL("new origin");
		});
		assertEquals("There aren't flights with this origin", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllFlightsByDestinationWhenExist() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		addTestFlightToRepository(FLIGHT_FIXTURE);
		List<Flight> flightsFounded = airportServiceLayer.findAllFlightsByDestinationSL(DESTINATION_FIXTURE);
		assertThat(flightsFounded).containsExactly(FLIGHT_FIXTURE);
	}
	
	
	
	@Test
	public void testFindAllFlightsByDestinationWhenNoExist() {
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsByDestinationSL("new destination");
		});
		assertEquals("There aren't flights with this destination", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllFlightsByDepartureDateInRangeWhenExist() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flight = new Flight(ONE_HOUR_LATER, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flight);
		List<Flight> flightsToReturn = airportServiceLayer.findAllFlightsWithDepartureDateInRangeSL(NOW, TWO_HOUR_LATER);
		assertThat(flightsToReturn).containsExactly(flight);
	}
	
	
	
	@Test
	public void testFindAllFlightsByDepartureDateInRangeWhenDepartureDateIsBeforeRange() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flight = new Flight(NUM_FIXTURE, NOW, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flight);		
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsWithDepartureDateInRangeSL(ONE_HOUR_LATER, TWO_HOUR_LATER);
		});	
		assertEquals("There aren't flights with departure date in the selected range", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllFlightsByDepartureDateInRangeWhenDepartureDateIsAfterRange() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flight = new Flight(TWO_HOUR_LATER, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flight);
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsWithDepartureDateInRangeSL(NOW, ONE_HOUR_LATER);
		});	
		assertEquals("There aren't flights with departure date in the selected range", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllFlightsByArrivalDateInRangeWhenExist() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flight = new Flight(NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flight);
		List<Flight> flightsToReturn = airportServiceLayer.findAllFlightsWithArrivalDateInRangeSL(NOW, TWO_HOUR_LATER);
		assertThat(flightsToReturn).containsExactly(flight);
	}
	
	
	
	@Test
	public void testFindAllFlightsByArrivalDateInRangeWhenArrivalDateIsBeforeRange() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flight = new Flight(NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flight);
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsWithArrivalDateInRangeSL(TWO_HOUR_LATER, THREE_HOUR_LATER);
		});	
		assertEquals("There aren't flights with arrival date in the selected range", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllFlightsByArrivalDateInRangeWhenArrivalDateIsAfterRange() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flight = new Flight(ONE_HOUR_LATER, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flight);
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsWithArrivalDateInRangeSL(NOW, ONE_HOUR_LATER);
		});	
		assertEquals("There aren't flights with arrival date in the selected range", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllFlightsAssociatesWithPlaneWhenExistFlights() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flight = new Flight(ONE_HOUR_LATER, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flight);
		List<Flight> flightsToReturn = airportServiceLayer.findAllFlightsAssociatesWithPlaneSL(PLANE_FIXTURE_1.getId());
		assertThat(flightsToReturn).containsExactly(flight);
	}
	
	
	
	@Test
	public void testFindAllFlightsAssociatesWithPlaneWhenNoExistAssociatesFlights() {
		addTestPlaneToRepository(PLANE_FIXTURE_2);	
		String id = PLANE_FIXTURE_2.getId();
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.findAllFlightsAssociatesWithPlaneSL(id);
		});
		assertEquals("There aren't flights associates with selected plane", ex.getMessage());
	}
	
	
	
	@Test
	public void testFindAllPlanesByModelWhenExist() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		List<Plane> planesToReturn = airportServiceLayer.findAllPlanesByModelSL(PLANE_FIXTURE_1.getModel());
		assertThat(planesToReturn).containsExactly(PLANE_FIXTURE_1);
	}
	
	
	
	@Test
	public void testFindAllPlanesByModelWhenNoExist() {		
		PlaneNotFoundException ex = assertThrows(PlaneNotFoundException.class, () -> {
			airportServiceLayer.findAllPlanesByModelSL("new-model");
		});
		assertEquals("There aren't planes with insert model", ex.getMessage());
	}
	
	
	
	
	// ########### private methods ###########
	
	private void addTestPlaneToRepository(Plane plane) {
		Document newDocument = new Document();
		
		planeCollection.insertOne(
				newDocument
					.append("model", plane.getModel()));
		
		plane.setId(newDocument.get("_id").toString());
	}
	
	
	
	private void addTestFlightToRepository(Flight flight) {
		Document newDocument = new Document();
		
		flightCollection.insertOne(
				newDocument
					.append("departure_date", flight.getDepartureDate())
					.append("arrival_date", flight.getArrivalDate())
					.append("origin", flight.getOrigin())
					.append("destination", flight.getDestination())
					.append("plane_id", flight.getPlane().getId()));
		
		flight.setFlightNum(newDocument.get("_id").toString());	
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


