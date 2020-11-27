package com.airport_management.repository.mongo;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.GenericContainer;

import com.airport_management.repository.FlightRepository;
import com.airport_management.repository.PlaneRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;


public class RepositoryMongoTest {
	
	@SuppressWarnings("rawtypes")
	@ClassRule
	public static GenericContainer mongo = new GenericContainer("mongo:4.2.3")
	        .withExposedPorts(27017)
	        .withCommand("--replSet rs0");
	

	private static final String AIRPORT_DB_NAME = "airport";
	private static final String PLANE_COLLECTION_NAME = "plane";
	private static final String FLIGHT_COLLECTION_NAME = "flight";
	
	private MongoClient client;
	private RepositoryMongo repositoryMongo;
	private ClientSession clientSession;
	
	
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
		 
		clientSession = client.startSession();
		repositoryMongo = new RepositoryMongo(client, clientSession, AIRPORT_DB_NAME, PLANE_COLLECTION_NAME, FLIGHT_COLLECTION_NAME);
		MongoDatabase database = client.getDatabase(AIRPORT_DB_NAME);
		database.drop();		
	}
	
	
	
	@Test
	public void testCreatePlaneRepository() {
		PlaneRepositoryMongo planeRepository = repositoryMongo.createPlaneRepository();
		assertTrue(planeRepository instanceof PlaneRepository);
	}
	
	
	
	@Test
	public void testCreateFlightRepository() {
		FlightRepositoryMongo flightRepository = repositoryMongo.createFlightRepository();
		assertTrue(flightRepository instanceof FlightRepository);
	}
}
