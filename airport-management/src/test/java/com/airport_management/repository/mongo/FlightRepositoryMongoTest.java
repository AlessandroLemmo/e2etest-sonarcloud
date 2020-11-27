package com.airport_management.repository.mongo;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.airport_management.model.Flight;
import com.airport_management.model.Plane;


public class FlightRepositoryMongoTest {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static GenericContainer mongo = new GenericContainer("mongo:4.2.3")
	        .withExposedPorts(27017)
	        .withCommand("--replSet rs0");
	

	private static final String AIRPORT_DB_NAME = "airport";
	private static final String PLANE_COLLECTION_NAME = "plane";
	private static final String FLIGHT_COLLECTION_NAME = "flight";
	
	private static final Plane PLANE_FIXTURE = new Plane("model-test");
	private static final Flight FLIGHT_FIXTURE_1 = new Flight(new Date(), new Date(), "origin-test", "destination-test", PLANE_FIXTURE);
	private static final Flight FLIGHT_FIXTURE_2 = new Flight(new Date(), new Date(), "origin-test", "destination-test", PLANE_FIXTURE);

	private MongoClient client;
	private FlightRepositoryMongo flightRepository;
	private PlaneRepositoryMongo planeRepository;
	private MongoCollection<Document> flightCollection;
	private MongoCollection<Document> planeCollection;
	
	@Mock
	ClientSession clientSession;

	
	@BeforeClass
	public static void init() throws UnsupportedOperationException, IOException, InterruptedException {
		mongo.start();
		mongo.execInContainer("/bin/bash", "-c", "mongo --eval 'rs.initiate()' --quiet");
		mongo.execInContainer("/bin/bash", "-c",
		            "until mongo --eval 'rs.isMaster()' | grep ismaster | grep true > /dev/null 2>&1;do sleep 1;done");
	}
	
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
		client = new MongoClient(
				new ServerAddress(
						mongo.getContainerIpAddress(), 
						mongo.getMappedPort(27017)));
		
		planeRepository = new PlaneRepositoryMongo(client, AIRPORT_DB_NAME, PLANE_COLLECTION_NAME);
		flightRepository = new FlightRepositoryMongo(client, AIRPORT_DB_NAME, FLIGHT_COLLECTION_NAME, planeRepository);
		MongoDatabase database = client.getDatabase(AIRPORT_DB_NAME);
		database.drop();
		flightCollection = database.getCollection(FLIGHT_COLLECTION_NAME);
		planeCollection = database.getCollection(PLANE_COLLECTION_NAME);
		flightRepository.startSession();
		planeRepository.startSession();
	}
	
	
	
	@Test
	public void testStartSession() {
		MongoClient client = spy(this.client);
		when(client.startSession()).thenReturn(flightRepository.getClientSession());
		flightRepository.startSession();
		verify(client).startSession();
	}
	
	
	
	@Test
	public void testSetClientSession() {
		ClientSession clientSession = client.startSession();
		flightRepository.setClientSession(clientSession);
		assertEquals(clientSession, flightRepository.getClientSession());
	}
	
	
	
	@Test
	public void testGetClientSession() {
		ClientSession clientSession = client.startSession();
		flightRepository.setClientSession(clientSession);
		ClientSession clientSessionReturned = flightRepository.getClientSession();
		assertEquals(clientSession, clientSessionReturned);
	}
	
	
	
	@Test
	public void testFindAllFlightsWhenDatabaseIsEmpty() {
		assertTrue(flightRepository.findAllFlights().isEmpty());
	}

	
	
	@Test
	public void testFindAllFlightsWhenDatabaseIsNotEmpty() {
		
		addTestPlaneToRepository(PLANE_FIXTURE);		
		addTestFlightToRepository(FLIGHT_FIXTURE_1);
		addTestFlightToRepository(FLIGHT_FIXTURE_2);
		
		assertThat(flightRepository.findAllFlights())
			.containsExactly(
					FLIGHT_FIXTURE_1,
					FLIGHT_FIXTURE_2);	
	}


	
	@Test
	public void testFindByNumNotFound() {
		assertThat(flightRepository.findByNum(""+new ObjectId())).isNull();
	}
	
	
	
	@Test
	public void testFindByNumFound() {

		addTestPlaneToRepository(PLANE_FIXTURE);
		addTestFlightToRepository(FLIGHT_FIXTURE_1);
		addTestFlightToRepository(FLIGHT_FIXTURE_2);
		
		assertThat(flightRepository.findByNum(FLIGHT_FIXTURE_2.getFlightNum()))
			.isEqualTo(FLIGHT_FIXTURE_2);
	}
	
	
	
	@Test
	public void testSaveFlight() {
		addTestPlaneToRepository(PLANE_FIXTURE);		
		Flight returnFlight = flightRepository.saveFlight(FLIGHT_FIXTURE_1);
		assertThat(readAllFlightsFromRepository()).containsExactly(FLIGHT_FIXTURE_1);
		assertThat(returnFlight).isEqualTo(FLIGHT_FIXTURE_1);
	}


	
	@Test
	public void testDeleteFlight() {
		addTestPlaneToRepository(PLANE_FIXTURE);		
		addTestFlightToRepository(FLIGHT_FIXTURE_1);
		String returnValue = flightRepository.deleteFlight(FLIGHT_FIXTURE_1);
		assertThat(readAllFlightsFromRepository()).isEmpty();
		assertEquals("delete with success", returnValue);
	}
	
	
	
		
	//################ private methods #################

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
	
	
	
	private void addTestPlaneToRepository(Plane plane) {
		Document newDocument = new Document();
		
		planeCollection.insertOne(
				newDocument
					.append("model", plane.getModel()));
		
		plane.setId(newDocument.get("_id").toString());
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
}


