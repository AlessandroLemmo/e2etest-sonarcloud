package com.airport_management.app;

import java.awt.EventQueue;
import java.util.concurrent.Callable;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.airport_management.controller.FlightController;
import com.airport_management.controller.PlaneController;
import com.airport_management.controller.SearchController;
import com.airport_management.service_layer.transaction.AirportServiceLayer;
import com.airport_management.transaction.TransactionManager;
import com.airport_management.view.swing.AirportSwingView;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.logging.Level; 
import java.util.logging.Logger;


@Command(mixinStandardHelpOptions = true)
public class App implements Callable<Void> {

	@Option(names = { "--mongo-host" }, description = "MongoDB host address")
	private String mongoHost = "localhost";

	@Option(names = { "--mongo-port" }, description = "MongoDB host port")
	private int mongoPort = 27017;

	@Option(names = { "--db-name" }, description = "Database name")
	private String databaseName = "airport";

	@Option(names = { "--db-plane-collection" }, description = "Plane collection name")
	private String planeCollectionName = "plane";
	
	@Option(names = { "--db-flight-collection" }, description = "Flight collection name")
	private String flightCollectionName = "flight";
	
	private static Logger logger = Logger.getLogger(App.class.getName());
	

	
	public static void main(String[] args) {
		
		new CommandLine(new App()).execute(args);
	}  

	

	@Override
	public Void call() throws Exception {
				
		EventQueue.invokeLater(() -> {
			try {
				
				ServerAddress serverAddress = new ServerAddress(mongoHost, mongoPort);
				MongoClient mongoClient = new MongoClient(serverAddress);

				TransactionManager transactionManager = new TransactionManager(mongoClient, databaseName, planeCollectionName, flightCollectionName);
				AirportServiceLayer serviceLayer = new AirportServiceLayer(transactionManager);

				AirportSwingView swingView = new AirportSwingView();
				PlaneController planeController = new PlaneController(swingView, serviceLayer);
				FlightController flightController = new FlightController(swingView, serviceLayer);
				SearchController searchController = new SearchController(swingView, serviceLayer);

				swingView.setAirportController(planeController, flightController, searchController);
				swingView.setVisible(true);
				planeController.allPlanes();
				flightController.allFlights();

			} catch (Exception e) {
				logger.log(Level.INFO, "error in app main", e);
			}
		});
		return null;
	}
}



