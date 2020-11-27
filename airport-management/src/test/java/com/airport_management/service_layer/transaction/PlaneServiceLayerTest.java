package com.airport_management.service_layer.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Arrays.asList;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.airport_management.exception.PlaneNotFoundException;
import com.airport_management.exception.PlaneWithAssociateFlightException;
import com.airport_management.model.Flight;
import com.airport_management.model.Plane;
import com.airport_management.repository.mongo.FlightRepositoryMongo;
import com.airport_management.repository.mongo.PlaneRepositoryMongo;
import com.airport_management.repository.mongo.RepositoryMongo;
import com.airport_management.transaction.TransactionCode;
import com.airport_management.transaction.TransactionManager;


public class PlaneServiceLayerTest {
	
	@Mock
	TransactionManager transactionManager;
	
	@Mock
	RepositoryMongo repositoryMongo;
	
	
	@Mock
	PlaneRepositoryMongo planeRepositoryMongo;
	
	@Mock
	FlightRepositoryMongo flightRepositoryMongo;
	
	@InjectMocks
	AirportServiceLayer airportServiceLayer;
	
	private static final String ID_FIXTURE_1 = "id1-test";
	private static final String ID_FIXTURE_2 = "id2-test";
	private static final String MODEL_FIXTURE = "model-test";
	private static final Plane PLANE_FIXTURE_1 = new Plane(ID_FIXTURE_1, MODEL_FIXTURE);
	private static final Plane PLANE_FIXTURE_2 = new Plane(ID_FIXTURE_2, MODEL_FIXTURE);
	private static final Flight FLIGHT_FIXTURE = new Flight("num-test", null, null, null, null, PLANE_FIXTURE_1);
	
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		when(transactionManager.doInTransaction(any()))
			.thenAnswer(answer((TransactionCode<?> code) -> code.apply(repositoryMongo)));
		when(repositoryMongo.createPlaneRepository()).thenReturn(planeRepositoryMongo);
		when(repositoryMongo.createFlightRepository()).thenReturn(flightRepositoryMongo);
	}
	
	
	
	@Test
	public void testSavePlane() {
		
		when(planeRepositoryMongo.savePlane(PLANE_FIXTURE_1))
			.thenReturn(PLANE_FIXTURE_1);
		
		Plane planeToReturn = airportServiceLayer.savePlaneSL(PLANE_FIXTURE_1);
		verify(planeRepositoryMongo).savePlane(PLANE_FIXTURE_1);
		assertThat(planeToReturn).isEqualTo(PLANE_FIXTURE_1);
	}

	
	
	@Test
	public void testFindById() {
		
		when(planeRepositoryMongo.findById(ID_FIXTURE_1))
			.thenReturn(PLANE_FIXTURE_1);
		
		Plane planeToReturn = airportServiceLayer.findByIdSL(ID_FIXTURE_1);
		verify(planeRepositoryMongo).findById(ID_FIXTURE_1);
		assertThat(planeToReturn).isEqualTo(PLANE_FIXTURE_1);
	}
	
	
	
	@Test
	public void testFindAllPlanes() {
		List<Plane> planes = asList(PLANE_FIXTURE_1);
		
		when(planeRepositoryMongo.findAllPlanes())
			.thenReturn(planes);
		
		List<Plane> returnPlanes = airportServiceLayer.findAllPlanesSL();
		assertThat(returnPlanes).containsExactly(PLANE_FIXTURE_1);
	}
	
	
	
	@Test
	public void testDeletePlaneWhenExist() {
		
		when(planeRepositoryMongo.findById(ID_FIXTURE_1))
			.thenReturn(PLANE_FIXTURE_1);
		
		airportServiceLayer.deletePlaneSL(PLANE_FIXTURE_1);
		verify(planeRepositoryMongo).deletePlane(PLANE_FIXTURE_1);
	}
	
	
	
	@Test
	public void testDeletePlaneWhenNoExist() {
		
		when(planeRepositoryMongo.findById(ID_FIXTURE_1))
			.thenReturn(null);
		
		PlaneNotFoundException ex = assertThrows(PlaneNotFoundException.class, () -> {
			airportServiceLayer.deletePlaneSL(PLANE_FIXTURE_1);
		});
		
		assertEquals("No existing plane with id " + ID_FIXTURE_1, ex.getMessage());	
	}
	

	
	@Test
	public void testDeletePlaneWhenThereAreAssociatesFlights() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(planeRepositoryMongo.findById(ID_FIXTURE_1))
			.thenReturn(PLANE_FIXTURE_1);
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		PlaneWithAssociateFlightException ex = assertThrows(PlaneWithAssociateFlightException.class, () -> {
			airportServiceLayer.deletePlaneSL(PLANE_FIXTURE_1);
		});		
		assertEquals("Impossible to delete. There is the flight " + FLIGHT_FIXTURE.getFlightNum() + " associates with this plane", ex.getMessage());
	}
	
	
	
	@Test
	public void testDeletePlaneWhenThereAreFlightsButNotAssociatesWithThePlaneToDelete() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		when(planeRepositoryMongo.findById(ID_FIXTURE_2))
			.thenReturn(PLANE_FIXTURE_2);
		when(flightRepositoryMongo.findAllFlights())
			.thenReturn(flights);
		
		airportServiceLayer.deletePlaneSL(PLANE_FIXTURE_2);
		verify(planeRepositoryMongo).deletePlane(PLANE_FIXTURE_2);	
	}
}

