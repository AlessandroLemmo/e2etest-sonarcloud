package com.airport_management.repository.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;


public class RepositoryMongo {

	MongoClient client;
	ClientSession clientSession;
	String databaseName;
	String collectionName1;
	String collectionName2;
	
	public RepositoryMongo(MongoClient client, ClientSession clientSession, String databaseName, String collectionName1, String collectionName2) {
		this.client = client;
		this.clientSession = clientSession;
		this.databaseName = databaseName;
		this.collectionName1 = collectionName1;
		this.collectionName2 = collectionName2;
	}


	
	public PlaneRepositoryMongo createPlaneRepository() {
		PlaneRepositoryMongo planeRepositoryMongo = new PlaneRepositoryMongo(client, databaseName, collectionName1);
		planeRepositoryMongo.setClientSession(clientSession);
		return planeRepositoryMongo;
	}
	

	
	public FlightRepositoryMongo createFlightRepository() {
		PlaneRepositoryMongo planeRepositoryMongo = createPlaneRepository();
		FlightRepositoryMongo flightRepositoryMongo = new FlightRepositoryMongo(client, databaseName, collectionName2, planeRepositoryMongo);
		flightRepositoryMongo.setClientSession(clientSession);
		return flightRepositoryMongo;
	}
}