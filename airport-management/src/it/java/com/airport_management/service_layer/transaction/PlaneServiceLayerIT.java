package com.airport_management.service_layer.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import com.airport_management.exception.PlaneNotFoundException;
import com.airport_management.exception.PlaneWithAssociateFlightException;
import com.airport_management.model.Flight;
import com.airport_management.model.Plane;
import com.airport_management.repository.mongo.PlaneRepositoryMongo;
import com.airport_management.transaction.TransactionManager;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;


public class PlaneServiceLayerIT {

	private static final String AIRPORT_DB_NAME = "airport";
	private static final String PLANE_COLLECTION_NAME = "plane";
	private static final String FLIGHT_COLLECTION_NAME = "flight";

	private static final Plane PLANE_FIXTURE_1 = new Plane("model1-test");
	private static final Plane PLANE_FIXTURE_2 = new Plane("model2-test");
	private static final Flight FLIGHT_FIXTURE = new Flight(null, null, null, null, PLANE_FIXTURE_2);
	
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
	public void testSavePlaneSL() {
		airportServiceLayer.savePlaneSL(PLANE_FIXTURE_1);
		assertThat(readAllPlanesFromRepository()).containsExactly(PLANE_FIXTURE_1);
	}
	
	
	
	@Test
	public void testFindByIdSL() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		Plane planeToReturn = airportServiceLayer.findByIdSL(PLANE_FIXTURE_1.getId());
		assertThat(planeToReturn).isEqualTo(PLANE_FIXTURE_1);
	}
	
	
	
	@Test
	public void testFindAllPlanesSL() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		List<Plane> planesToReturn = airportServiceLayer.findAllPlanesSL();
		assertThat(planesToReturn).containsExactly(PLANE_FIXTURE_1);
	}
	
	
	
	@Test
	public void testDeletePlaneWhenExistSL() {		
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		airportServiceLayer.deletePlaneSL(PLANE_FIXTURE_1);
		assertThat(readAllPlanesFromRepository()).isEmpty();
	}
	
	
	
	@Test
	public void testDeletePlaneWhenNoExistSL() {
		PLANE_FIXTURE_1.setId(new ObjectId().toString());
		PlaneNotFoundException ex = assertThrows(PlaneNotFoundException.class, () -> {
			airportServiceLayer.deletePlaneSL(PLANE_FIXTURE_1);
		});
		assertEquals("No existing plane with id " + PLANE_FIXTURE_1.getId(), ex.getMessage());	
	}
	
	
	
	@Test
	public void testDeletePlaneWhenThereAreAssociatesFlights() {	
		addTestPlaneToRepository(PLANE_FIXTURE_2);
		addTestFlightToRepository(FLIGHT_FIXTURE);
		
		PlaneWithAssociateFlightException ex = assertThrows(PlaneWithAssociateFlightException.class, () -> {
			airportServiceLayer.deletePlaneSL(PLANE_FIXTURE_2);
		});		
		assertEquals("Impossible to delete. There is the flight " + FLIGHT_FIXTURE.getFlightNum() + " associates with this plane", ex.getMessage());
	}
	
	
	
	@Test
	public void testDeletePlaneWhenThereAreFlightsButNotAssociatesWithThePlaneToDelete() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		addTestPlaneToRepository(PLANE_FIXTURE_2);
		addTestFlightToRepository(FLIGHT_FIXTURE);
		airportServiceLayer.deletePlaneSL(PLANE_FIXTURE_1);
		assertThat(readAllPlanesFromRepository()).containsExactly(PLANE_FIXTURE_2);	
	}
	
	
	
	
	// ############# private methods ################
	
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
	
	private List<Plane> readAllPlanesFromRepository() {
		return StreamSupport.
				stream(planeCollection.find().spliterator(), false)
				.map(d -> new Plane(""+d.get("_id"), 
						""+d.get("model")))
				.collect(Collectors.toList());
	}	
}
