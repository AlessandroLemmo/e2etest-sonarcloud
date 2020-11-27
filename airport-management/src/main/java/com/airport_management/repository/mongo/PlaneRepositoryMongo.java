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

import com.airport_management.model.Plane;
import com.airport_management.repository.PlaneRepository;


public class PlaneRepositoryMongo implements PlaneRepository {

	private static final String FIELD_PK = "_id";
	private MongoCollection<Document> planeCollection;
	private MongoClient client;
	private ClientSession clientSession;
	
	
	public PlaneRepositoryMongo(MongoClient client, String databaseName, String collectionName1) {
		
		this.client = client;		
		MongoDatabase database = client.getDatabase(databaseName);
		
		if(!database.listCollectionNames().into(new ArrayList<String>()).contains(collectionName1))
			database.createCollection(collectionName1);

		planeCollection = database.getCollection(collectionName1);
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
	public List<Plane> findAllPlanes() {
		return StreamSupport.
				stream(planeCollection.find(clientSession).spliterator(), false)
				.map(this::fromDocumentToPlane)
				.collect(Collectors.toList());
	}



	private Plane fromDocumentToPlane(Document d) {
		return new Plane(""+d.get(FIELD_PK),
				""+d.get("model"));
	}
	
	
	
	@Override
	public Plane findById(String id) {
		Document d = planeCollection.find(clientSession, Filters.eq("_id", new ObjectId(id))).first();
		if(d != null)
			return fromDocumentToPlane(d);
		return null; 
	}


	
	@Override
	public Plane savePlane(Plane plane) {
		Document newDocument = new Document();	
		
		planeCollection.insertOne(
				clientSession,
				newDocument.append("model", plane.getModel()));
		
		plane.setId(newDocument.get(FIELD_PK).toString());	
		return plane;
	}
	
	

	@Override
	public String deletePlane(Plane plane) {
		String id = plane.getId();
		planeCollection.deleteOne(clientSession, Filters.eq("_id", new ObjectId(id)));
		return "delete with success";
	}
	
}
