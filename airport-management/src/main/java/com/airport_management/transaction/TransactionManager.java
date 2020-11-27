package com.airport_management.transaction;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.TransactionBody;

import com.airport_management.repository.mongo.RepositoryMongo;


public class TransactionManager implements TransactionManagerInterface {
	
	private MongoClient client;
	private String databaseName;
	private String collectionName1;
	private String collectionName2;
	
	
	public TransactionManager(MongoClient client, String databaseName, String collectionName1, String collectionName2) {
		this.client = client;
		this.databaseName = databaseName;
		this.collectionName1 = collectionName1;
		this.collectionName2 = collectionName2;
	}
	
	
	
	//Define options to use for the transaction
	TransactionOptions txnOptions = TransactionOptions.builder()
			.readPreference(ReadPreference.primary())
			.readConcern(ReadConcern.LOCAL)
			.writeConcern(WriteConcern.MAJORITY)
			.build();
	
	
	
	//TransactionBody transactionBody without <T> 
	private <T> T processTransaction(TransactionBody<T> transactionBody, ClientSession clientSession) {
		TransactionOptions transactionOptions = TransactionOptions.builder()
	            .readPreference(ReadPreference.primary())
	            .readConcern(ReadConcern.LOCAL)
	            .writeConcern(WriteConcern.MAJORITY)
	            .build();
		return clientSession.withTransaction(transactionBody , transactionOptions);
	}
	

	
	@Override
	public <T> T doInTransaction(TransactionCode<T> code) {
		
		T result = null;
		ClientSession clientSession  = client.startSession();
		
		try {
			RepositoryMongo repositoryMongo = new RepositoryMongo(client, clientSession, databaseName, collectionName1, collectionName2);

			TransactionBody<T> transactionBody = () -> code.apply(repositoryMongo);
			result = processTransaction(transactionBody,clientSession);
			return result;
		} 
		catch(MongoException ex){
			return null;
		}
		finally {
			clientSession.close();
		}	
	}
}
