package com.airport_management.service_layer.transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.airport_management.model.Flight;
import com.airport_management.model.Plane;
import com.airport_management.repository.mongo.FlightRepositoryMongo;
import com.airport_management.repository.mongo.PlaneRepositoryMongo;
import com.airport_management.service_layer.FlightServiceLayer;
import com.airport_management.service_layer.PlaneServiceLayer;
import com.airport_management.transaction.TransactionManager;
import com.airport_management.exception.PlaneAlreadyInServiceException;
import com.airport_management.exception.PlaneNotFoundException;
import com.airport_management.exception.PlaneWithAssociateFlightException;
import com.airport_management.exception.InconsistentDataException;
import com.airport_management.exception.FlightNotFoundException;


public class AirportServiceLayer implements PlaneServiceLayer, FlightServiceLayer {

	TransactionManager transactionManager;
	
	public AirportServiceLayer(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

		
	
	
	//########## plane methods ###########
	
	public Plane savePlaneSL(Plane plane) {
		return transactionManager.doInTransaction(planeRepository -> planeRepository.createPlaneRepository().savePlane(plane));
	}
	
	public Plane findByIdSL(String id) {
		return transactionManager.doInTransaction(planeRepository -> planeRepository.createPlaneRepository().findById(id));
	}
	
	public List<Plane> findAllPlanesSL() {
		return transactionManager.doInTransaction(planeRepository -> planeRepository.createPlaneRepository().findAllPlanes());
	}
	
	public void deletePlaneSL(Plane plane) {
		transactionManager.doInTransaction(
				repositoryMongo -> {
					PlaneRepositoryMongo planeRepositoryMongo = repositoryMongo.createPlaneRepository();
					FlightRepositoryMongo flightRepositoryMongo = repositoryMongo.createFlightRepository();
					
					if (planeRepositoryMongo.findById(plane.getId()) == null) {
						throw new PlaneNotFoundException("No existing plane with id " + plane.getId());
					}
					
					List<Flight> flights = flightRepositoryMongo.findAllFlights();
					for(int i = 0; i < flights.size(); i++) {
						
						if(flights.get(i).getPlane().getId().equals(plane.getId())) {
							throw new PlaneWithAssociateFlightException("Impossible to delete. There is the flight " + 
									flights.get(i).getFlightNum() + " associates with this plane");
						}
					}				
					return planeRepositoryMongo.deletePlane(plane);			
				});
	}
	
	


	
	//########### flight methods ##########
	
	public void saveFlightSL(Flight flight) {
				
		transactionManager.doInTransaction(
				flightRepository -> {
					
					FlightRepositoryMongo flightRepositoryMongo = flightRepository.createFlightRepository();
					List<Flight> flights = flightRepositoryMongo.findAllFlights();
					Date departureDate = flight.getDepartureDate();
					Date arrivalDate = flight.getArrivalDate();
					String origin = flight.getOrigin();
					String destination = flight.getDestination();
					String planeId = flight.getPlane().getId();
					
					if(arrivalDate.before(departureDate) || departureDate.compareTo(arrivalDate) == 0) {
						throw new InconsistentDataException("departure or arrival date is wrong");
					}
					
					if(origin.equals(destination)) {
						throw new InconsistentDataException("origin or destination is wrong");
					}
					
					for(int i = 0; i < flights.size(); i++) {
						
						if((departureDate.compareTo(flights.get(i).getDepartureDate()) == 0 || arrivalDate.compareTo(flights.get(i).getArrivalDate()) == 0) &&
								flights.get(i).getPlane().getId().equals(planeId)) {
							throw new PlaneAlreadyInServiceException("This plane is already in service. Departure or arrival date are equals to exsisting flight.");
						}
						
						
						if(departureDate.after(flights.get(i).getDepartureDate()) && 
								departureDate.before(flights.get(i).getArrivalDate()) &&
								flights.get(i).getPlane().getId().equals(planeId)) {
							
							throw new PlaneAlreadyInServiceException("This plane is already in service. Departure date is between dates of existing flight");
						}
						
						if(arrivalDate.after(flights.get(i).getDepartureDate()) && 
								arrivalDate.before(flights.get(i).getArrivalDate()) &&
								flights.get(i).getPlane().getId().equals(planeId)) {
							
							throw new PlaneAlreadyInServiceException("This plane is already in service. Arrival date is between dates of existing flight");
						}
						
						if(departureDate.before(flights.get(i).getDepartureDate()) &&
								arrivalDate.after(flights.get(i).getArrivalDate()) &&
								flights.get(i).getPlane().getId().equals(planeId)) {
							
							throw new PlaneAlreadyInServiceException("This plane is already in service. Departure date is before and arrival date is after dates of existing flight");
						}				
					}
					
					return flightRepositoryMongo.saveFlight(flight);
				});
	}
	
	
	
	public Flight findByNumSL(String num) {
		return transactionManager.doInTransaction(flightRepository -> flightRepository.createFlightRepository().findByNum(num));
	}
	
	
	
	public List<Flight> findAllFlightsSL() {
		return transactionManager.doInTransaction(flightRepository -> flightRepository.createFlightRepository().findAllFlights());
	}
	
	
	
	public void deleteFlightSL(Flight flight) {
		
		transactionManager.doInTransaction(
				flightRepository -> {
					
					FlightRepositoryMongo flightRepositoryMongo = flightRepository.createFlightRepository();
					
					if (flightRepositoryMongo.findByNum(flight.getFlightNum()) == null) {				
						throw new FlightNotFoundException("No existing flight with num " + flight.getFlightNum());
					}
					return flightRepositoryMongo.deleteFlight(flight);	
				});
	}
	
	
	
	
	
	//############## search methods #################
	
		public List<Flight> findAllFlightsByOriginSL(String origin) {
			
			return transactionManager.doInTransaction(
					flightRepository -> {
						
						FlightRepositoryMongo flightRepositoryMongo = flightRepository.createFlightRepository();
						List<Flight> allFlights = flightRepositoryMongo.findAllFlights();
						List<Flight> flightsToReturn = new ArrayList<>();
						
						for(int i = 0; i < allFlights.size(); i++) {
							if(allFlights.get(i).getOrigin().equals(origin)) {
								flightsToReturn.add(allFlights.get(i));
							}
						}
						
						if(flightsToReturn.isEmpty())
							throw new FlightNotFoundException("There aren't flights with this origin");
						
						return flightsToReturn;	
					});
		}
		
		
		
		public List<Flight> findAllFlightsByDestinationSL(String destination) {
			
			return transactionManager.doInTransaction(
					flightRepository -> {
						
						FlightRepositoryMongo flightRepositoryMongo = flightRepository.createFlightRepository();
						List<Flight> allFlights = flightRepositoryMongo.findAllFlights();
						List<Flight> flightsToReturn = new ArrayList<>();
						
						for(int i = 0; i < allFlights.size(); i++) {
							if(allFlights.get(i).getDestination().equals(destination)) {
								flightsToReturn.add(allFlights.get(i));
							}
						}
						
						if(flightsToReturn.isEmpty())
							throw new FlightNotFoundException("There aren't flights with this destination");
						
						return flightsToReturn;	
					});
		}
		
		
		
		public List<Flight> findAllFlightsWithDepartureDateInRangeSL(Date start, Date end) {
			return transactionManager.doInTransaction(
					flightRepository -> {
						
						FlightRepositoryMongo flightRepositoryMongo = flightRepository.createFlightRepository();
						List<Flight> allFlights = flightRepositoryMongo.findAllFlights();
						List<Flight> flightsToReturn = new ArrayList<>();

						for(int i = 0; i < allFlights.size(); i++) {
							if(allFlights.get(i).getDepartureDate().after(start) && allFlights.get(i).getDepartureDate().before(end)) {
								flightsToReturn.add(allFlights.get(i));
							}
						}
						
						if(flightsToReturn.isEmpty())
							throw new FlightNotFoundException("There aren't flights with departure date in the selected range");
						
						return flightsToReturn;	
					});
		}
		
		
		
		public List<Flight> findAllFlightsWithArrivalDateInRangeSL(Date start, Date end) {
			return transactionManager.doInTransaction(
					flightRepository -> {
						
						FlightRepositoryMongo flightRepositoryMongo = flightRepository.createFlightRepository();
						List<Flight> allFlights = flightRepositoryMongo.findAllFlights();
						List<Flight> flightsToReturn = new ArrayList<>();
		
						for(int i = 0; i < allFlights.size(); i++) {
							if(allFlights.get(i).getArrivalDate().after(start) && allFlights.get(i).getArrivalDate().before(end)) {
								flightsToReturn.add(allFlights.get(i));
							}
						}
						
						if(flightsToReturn.isEmpty())
							throw new FlightNotFoundException("There aren't flights with arrival date in the selected range");
						
						return flightsToReturn;	
					});
		}
		
		
		
		public List<Flight> findAllFlightsAssociatesWithPlaneSL(String planeId) {
			return transactionManager.doInTransaction(
					flightRepository -> {
						
						FlightRepositoryMongo flightRepositoryMongo = flightRepository.createFlightRepository();
						List<Flight> allFlights = flightRepositoryMongo.findAllFlights();
						List<Flight> flightsToReturn = new ArrayList<>();
						
						for(int i = 0; i < allFlights.size(); i++) {
							if(allFlights.get(i).getPlane().getId().equals(planeId)) {
								flightsToReturn.add(allFlights.get(i));
							}
						}
						
						if(flightsToReturn.isEmpty())
							throw new FlightNotFoundException("There aren't flights associates with selected plane");
						
						return flightsToReturn;	
					});
		}
		
		
		
		public List<Plane> findAllPlanesByModelSL(String model) {
			return transactionManager.doInTransaction(
					planeRepository -> {
						
						PlaneRepositoryMongo planeRepositoryMongo = planeRepository.createPlaneRepository();
						List<Plane> allPlanes = planeRepositoryMongo.findAllPlanes();
						List<Plane> planesToReturn = new ArrayList<>();
						
						for(int i = 0; i < allPlanes.size(); i++) {
							if(allPlanes.get(i).getModel().equals(model)) {
								planesToReturn.add(allPlanes.get(i));
							}
						}
						
						if(planesToReturn.isEmpty())
							throw new PlaneNotFoundException("There aren't planes with insert model");
						
						return planesToReturn;
					});
		}
}


