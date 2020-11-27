package com.airport_management.service_layer.transaction;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.junit.Before;
import org.junit.Test;

import com.airport_management.exception.FlightNotFoundException;
import com.airport_management.exception.InconsistentDataException;
import com.airport_management.exception.PlaneAlreadyInServiceException;
import com.airport_management.model.Flight;
import com.airport_management.model.Plane;
import com.airport_management.repository.mongo.PlaneRepositoryMongo;
import com.airport_management.transaction.TransactionManager;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class FlightServiceLayerIT {

	private static final String AIRPORT_DB_NAME = "airport";
	private static final String PLANE_COLLECTION_NAME = "plane";
	private static final String FLIGHT_COLLECTION_NAME = "flight";

	private static final Date NOW = getDates().get(0);
	private static final Date ONE_HOUR_LATER = getDates().get(1);
	private static final Date TWO_HOUR_LATER = getDates().get(2);
	private static final Date THREE_HOUR_LATER = getDates().get(3);
	private static final Plane PLANE_FIXTURE_1 = new Plane("model1-test");
	private static final Plane PLANE_FIXTURE_2 = new Plane("model2-test");
	private static final String NUM_FIXTURE = "num1-test";
	private static final String ORIGIN_FIXTURE = "origin-test";
	private static final String DESTINATION_FIXTURE = "destination-test";
	private static final Flight FLIGHT_FIXTURE = new Flight(NUM_FIXTURE, NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);	
	
	private MongoClient client;
	private PlaneRepositoryMongo planeRepository;
	
	private AirportServiceLayer airportServiceLayer;
	private TransactionManager transactionManager;
	private MongoCollection<Document> planeCollection;
	private MongoCollection<Document> flightCollection;

	
	@Before
	public void setUp() {
		
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
	public void testSaveFlightWithSuccess() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flightToAdd = new Flight(NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		airportServiceLayer.saveFlightSL(flightToAdd);
		assertThat(readAllFlightsFromRepository()).containsExactly(flightToAdd);
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsBeforeArrivalDate() {
		Flight flightToAdd = new Flight(TWO_HOUR_LATER, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		InconsistentDataException ex = assertThrows(InconsistentDataException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("departure or arrival date is wrong", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsEqualToArrivalDate() {
		Flight flightToAdd = new Flight(NOW, NOW, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		InconsistentDataException ex = assertThrows(InconsistentDataException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("departure or arrival date is wrong", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenOriginAndDestinaitonAreEquals() {
		Flight flightToAdd = new Flight(NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, ORIGIN_FIXTURE, PLANE_FIXTURE_1);
		InconsistentDataException ex = assertThrows(InconsistentDataException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("origin or destination is wrong", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsEqualToAnotherFlightOfTheSamePlane() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flightAlreadyExist = new Flight(NOW, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flightAlreadyExist);
		Flight flightToAdd = new Flight(NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		PlaneAlreadyInServiceException ex = assertThrows(PlaneAlreadyInServiceException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("This plane is already in service. Departure or arrival date are equals to exsisting flight.", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenArrivalDateIsEqualToAnotherFlightOfTheSamePlane() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flightAlreadyExist = new Flight(ONE_HOUR_LATER, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flightAlreadyExist);
		Flight flightToAdd = new Flight(NOW, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		PlaneAlreadyInServiceException ex = assertThrows(PlaneAlreadyInServiceException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("This plane is already in service. Departure or arrival date are equals to exsisting flight.", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureOrArrivalDateIsEqualToAnotherFlight() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		addTestPlaneToRepository(PLANE_FIXTURE_2);
		Flight flightAlreadyExist = new Flight(NOW, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flightAlreadyExist);
		Flight flightToAdd = new Flight(NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_2);

		airportServiceLayer.saveFlightSL(flightToAdd);
		assertThat(readAllFlightsFromRepository()).containsExactly(flightAlreadyExist, flightToAdd);
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsBetweenDatesOfAnotherFlightOfTheSamePlane() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flightAlreadyExist = new Flight(NOW, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flightAlreadyExist);
		Flight flightToAdd = new Flight(ONE_HOUR_LATER, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		PlaneAlreadyInServiceException ex = assertThrows(PlaneAlreadyInServiceException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("This plane is already in service. Departure date is between dates of existing flight", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsAfterArrivalDateOfAnotherFlightOfTheSamePlane() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flightAlreadyExist = new Flight(NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flightAlreadyExist);
		Flight flightToAdd = new Flight(TWO_HOUR_LATER, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		airportServiceLayer.saveFlightSL(flightToAdd);
		assertThat(readAllFlightsFromRepository()).containsExactly(flightAlreadyExist, flightToAdd);
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsBetweenDatesOfAnotherFlight() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		addTestPlaneToRepository(PLANE_FIXTURE_2);
		Flight flightAlreadyExist = new Flight(NOW, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flightAlreadyExist);
		Flight flightToAdd = new Flight(ONE_HOUR_LATER, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_2);
		
		airportServiceLayer.saveFlightSL(flightToAdd);
		assertThat(readAllFlightsFromRepository()).containsExactly(flightAlreadyExist, flightToAdd);
	}
	
	
	
	@Test
	public void testSaveFlightWhenArrivalDateIsBetweenDatesOfAnotherFlightOfTheSamePlane() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flightAlreadyExist = new Flight(ONE_HOUR_LATER, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flightAlreadyExist);
		Flight flightToAdd = new Flight(NOW, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		PlaneAlreadyInServiceException ex = assertThrows(PlaneAlreadyInServiceException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("This plane is already in service. Arrival date is between dates of existing flight", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenArrivalDateIsBeforeDatesOfAnotherFlightOfTheSamePlane() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flightAlreadyExist = new Flight(TWO_HOUR_LATER, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flightAlreadyExist);
		Flight flightToAdd = new Flight(NOW, ONE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		airportServiceLayer.saveFlightSL(flightToAdd);
		assertThat(readAllFlightsFromRepository()).containsExactly(flightAlreadyExist, flightToAdd);
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsBeforeAndArrivalDateIsAfterDatesOfAnotherFlightOfTheSamePlane() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Flight flightAlreadyExist = new Flight(ONE_HOUR_LATER, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flightAlreadyExist);
		Flight flightToAdd = new Flight(NOW, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		
		PlaneAlreadyInServiceException ex = assertThrows(PlaneAlreadyInServiceException.class, () -> {
			airportServiceLayer.saveFlightSL(flightToAdd);
		});
		assertEquals("This plane is already in service. Departure date is before and arrival date is after dates of existing flight", ex.getMessage());
	}
	
	
	
	@Test
	public void testSaveFlightWhenDepartureDateIsBeforeAndArrivalDateIsAfterDatesOfAnotherFlight() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		addTestPlaneToRepository(PLANE_FIXTURE_2);
		Flight flightAlreadyExist = new Flight(ONE_HOUR_LATER, TWO_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_1);
		addTestFlightToRepository(flightAlreadyExist);
		Flight flightToAdd = new Flight(NOW, THREE_HOUR_LATER, ORIGIN_FIXTURE, DESTINATION_FIXTURE, PLANE_FIXTURE_2);
		
		airportServiceLayer.saveFlightSL(flightToAdd);
		assertThat(readAllFlightsFromRepository()).containsExactly(flightAlreadyExist, flightToAdd);
	}
	
	
	
	@Test
	public void testFindByNum() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		addTestFlightToRepository(FLIGHT_FIXTURE);
		Flight flightToReturn = airportServiceLayer.findByNumSL(FLIGHT_FIXTURE.getFlightNum());
		assertThat(flightToReturn).isEqualTo(FLIGHT_FIXTURE);
	}
	

	
	@Test
	public void testFindAllFlights() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		addTestFlightToRepository(FLIGHT_FIXTURE);
		List<Flight> flightsToReturn = airportServiceLayer.findAllFlightsSL();
		assertThat(flightsToReturn).containsExactly(FLIGHT_FIXTURE);
	}
	
	
	
	@Test
	public void testDeleteFlightWhenExist() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		addTestFlightToRepository(FLIGHT_FIXTURE);
		
		airportServiceLayer.deleteFlightSL(FLIGHT_FIXTURE);
		assertThat(readAllFlightsFromRepository()).isEmpty();
	}
	
	
	
	@Test
	public void testDeleteFlightWhenNoExist() {
		FlightNotFoundException ex = assertThrows(FlightNotFoundException.class, () -> {
			airportServiceLayer.deleteFlightSL(FLIGHT_FIXTURE);
		});
		assertEquals("No existing flight with num " + FLIGHT_FIXTURE.getFlightNum(), ex.getMessage());
	}
	
	
	
	
	// ############# private methods ############
	
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
	
	
	
	private List<Flight> readAllFlightsFromRepository() {
		return StreamSupport.
				stream(flightCollection.find().spliterator(), false)
				.map(d -> new Flight(""+d.get("_id"), 
						d.getDate("departure_date"),
						d.getDate("arrival_date"), 
						""+d.get("origin"),
						""+d.get("destination"),
						planeRepository.findById((d.get("plane_id")).toString())))
				.collect(Collectors.toList());
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


