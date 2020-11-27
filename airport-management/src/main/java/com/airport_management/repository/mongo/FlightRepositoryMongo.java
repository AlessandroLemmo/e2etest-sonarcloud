package com.airport_management.repository.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import com.airport_management.model.Flight;
import com.airport_management.model.Plane;
import com.airport_management.repository.FlightRepository;


public class FlightRepositoryMongo implements FlightRepository{

	private static final String FIELD_PK = "_id";
	private PlaneRepositoryMongo planeRepositoryMongo;
	private MongoCollection<Document> flightCollection;
	private MongoClient client;
	private ClientSession clientSession;
	
	
	public FlightRepositoryMongo(MongoClient client, String databaseName, String collectionName2, PlaneRepositoryMongo planeRepositoryMongo) {
		
		this.client = client;
		MongoDatabase database = client.getDatabase(databaseName);
		
		if(!database.listCollectionNames().into(new ArrayList<String>()).contains(collectionName2))
			database.createCollection(collectionName2);

		flightCollection = database.getCollection(collectionName2);
		this.planeRepositoryMongo = planeRepositoryMongo;
	}
	
	
	public void startSession() {
		clientSession = client.startSession();
	}

	
	public void setClientSession(ClientSession clientSession) {
		this.clientSession = clientSession;
		
	}
	
	
	public ClientSession getClientSession() {
		return clientSession;
	}

	
	
	@Override
	public List<Flight> findAllFlights() {
		return StreamSupport.
				stream(flightCollection.find(clientSession).spliterator(), false)
				.map(this::fromDocumentToFlight)
				.collect(Collectors.toList());
	}
	
	
	
	private Flight fromDocumentToFlight(Document d) {	
		Plane plane = planeRepositoryMongo.findById(""+d.get("plane_id"));
		return new Flight(""+d.get(FIELD_PK), 
				d.getDate("departure_date"),
				d.getDate("arrival_date"), 
				""+d.get("origin"),
				""+d.get("destination"),
				plane); 
	}
	
	
	
	@Override
	public Flight findByNum(String number) {
		Document d = flightCollection.find(clientSession,  Filters.eq("_id", new ObjectId(number))).first();
		if(d != null)
			return fromDocumentToFlight(d);
		return null; 
	}
	
	
	
	@Override
	public Flight saveFlight(Flight flight) {		
		Document newDocument = new Document();
		flightCollection.insertOne(
				clientSession,
				newDocument
					.append("departure_date", flight.getDepartureDate())
					.append("arrival_date", flight.getArrivalDate())
					.append("origin", flight.getOrigin())
					.append("destination", flight.getDestination())
					.append("plane_id", flight.getPlane().getId()));
		
		flight.setFlightNum(newDocument.get(FIELD_PK).toString());
		return flight;
	}

	
	
	@Override
	public String deleteFlight(Flight flight) {
		String flightNum = flight.getFlightNum();
		flightCollection.deleteOne(clientSession, Filters.eq("_id", new ObjectId(flightNum)));
		return "delete with success";
	}
}
