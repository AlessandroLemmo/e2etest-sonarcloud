package com.airport_management.repository.mongo;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.GenericContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.airport_management.model.Plane;


public class PlaneRepositoryMongoTest {

	@SuppressWarnings("rawtypes")
	@ClassRule
	public static GenericContainer mongo = new GenericContainer("mongo:4.2.3")
	        .withExposedPorts(27017)
	        .withCommand("--replSet rs0");
	

	private static final String AIRPORT_DB_NAME = "airport";
	private static final String PLANE_COLLECTION_NAME = "plane";
	
	private static final Plane PLANE_FIXTURE_1 = new Plane("model1-test");
	private static final Plane PLANE_FIXTURE_2 = new Plane("model2-test");
	
	private MongoClient client;
	private PlaneRepositoryMongo planeRepository;
	private MongoCollection<Document> planeCollection;

	
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
		MongoDatabase database = client.getDatabase(AIRPORT_DB_NAME);
		database.drop();
		planeCollection = database.getCollection(PLANE_COLLECTION_NAME);
		planeRepository.startSession();
	}
	
	
		
	@Test
	public void testStartSession() {
		MongoClient client = spy(this.client);
		when(client.startSession()).thenReturn(planeRepository.getClientSession());
		planeRepository.startSession();
		verify(client).startSession();
	}
	
	
	
	@Test
	public void testSetClientSession() {
		ClientSession clientSession = client.startSession();
		planeRepository.setClientSession(clientSession);
		assertEquals(clientSession, planeRepository.getClientSession());
	}
	
	
	
	@Test
	public void testGetClientSession() {
		ClientSession clientSession = client.startSession();
		planeRepository.setClientSession(clientSession);
		ClientSession clientSessionReturned = planeRepository.getClientSession();
		assertEquals(clientSession, clientSessionReturned);
	}
	
	
	
	@Test
	public void testFindAllPlanesWhenDatabaseIsEmpty() {
		assertTrue(planeRepository.findAllPlanes().isEmpty());
	}
	
	
	
	@Test
	public void testFindAllPlanesWhenDatabaseIsNoEmpty() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		addTestPlaneToRepository(PLANE_FIXTURE_2);
				
		assertThat(planeRepository.findAllPlanes())
			.containsExactly(
					PLANE_FIXTURE_1,
					PLANE_FIXTURE_2);
	}
	
	
	
	@Test
	public void testFindByIdNotFound() {
		assertThat(planeRepository.findById(""+new ObjectId())).isNull();
	}
	
	
	
	@Test
	public void testFindByIdFound() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		addTestPlaneToRepository(PLANE_FIXTURE_2);
		
		assertThat(planeRepository.findById(PLANE_FIXTURE_2.getId()))
			.isEqualTo(PLANE_FIXTURE_2);
	}
	
	
	
	@Test
	public void testSavePlane() {
		Plane returnPlane = planeRepository.savePlane(PLANE_FIXTURE_1);
		assertThat(readAllPlanesFromRepository())
			.containsExactly(PLANE_FIXTURE_1);
		assertThat(returnPlane).isEqualTo(PLANE_FIXTURE_1);
	}
	
	

	@Test
	public void testDeletePlane() {
		addTestPlaneToRepository(PLANE_FIXTURE_1);
		String returnValue = planeRepository.deletePlane(PLANE_FIXTURE_1);
		assertThat(readAllPlanesFromRepository())
			.isEmpty();
		assertEquals("delete with success", returnValue);
	}
	
	
	
	
	// ########## private methods ###########
	
	private void addTestPlaneToRepository(Plane plane) {
		Document newDocument = new Document();
		
		planeCollection.insertOne(
				newDocument
					.append("model", plane.getModel()));
		
		plane.setId(newDocument.get("_id").toString());
	}
	
	
	
	private List<Plane> readAllPlanesFromRepository() {
		return StreamSupport.
				stream(planeCollection.find().spliterator(), false)
				.map(d -> new Plane(""+d.get("_id"), 
						""+d.get("model")))
				.collect(Collectors.toList());
	}
}


