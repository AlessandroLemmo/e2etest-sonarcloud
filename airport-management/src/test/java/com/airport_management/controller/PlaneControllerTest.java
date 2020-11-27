package com.airport_management.controller;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.airport_management.exception.PlaneNotFoundException;
import com.airport_management.exception.PlaneWithAssociateFlightException;
import com.airport_management.model.Flight;
import com.airport_management.model.Plane;
import com.airport_management.service_layer.transaction.AirportServiceLayer;
import com.airport_management.view.PlaneView;


public class PlaneControllerTest {
	
	@Mock 
	private PlaneView planeView;
	
	@Mock
	private AirportServiceLayer serviceLayer;
	
	@InjectMocks
	private PlaneController planeController;

	private static final String ID_FIXTURE = "id-test";
	private static final String MODEL_FIXTURE = "model-test";
	private static final String NUM_FIXTURE = "num-test";
	private static final Plane PLANE_FIXTURE = new Plane(ID_FIXTURE, MODEL_FIXTURE);
	private static final Flight FLIGHT_FIXTURE = new Flight(NUM_FIXTURE, null, null, null, null, PLANE_FIXTURE);
	
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	
	
	@Test
	public void testReturnAllPlanesWhenExistPlanes() {
		List<Plane> planes = asList(PLANE_FIXTURE);
		
		when(serviceLayer.findAllPlanesSL())
			.thenReturn(planes);
		
		List<Plane> returnedPlanes = planeController.returnAllPlanes();
		assertEquals(planes, returnedPlanes);

	}
	
	 
	
	@Test
	public void testReturnAllPlanesWhenNoExistPlanes() {		
		when(serviceLayer.findAllPlanesSL())
			.thenReturn(null);
		
		List<Plane> returnedPlanes = planeController.returnAllPlanes();
		assertEquals(null, returnedPlanes);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
	}
	
	
	
	@Test
	public void testAllPlanesWhenExistPlanes() {
		List<Plane> planes = asList(PLANE_FIXTURE);
		
		when(serviceLayer.findAllPlanesSL())
			.thenReturn(planes);
		
		planeController.allPlanes();
		verify(planeView).showAllPlanes(planes);	
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(planeView);
	}
	
	
	
	@Test
	public void testAllPlanesWhenNoExistPlanes() {
		when(serviceLayer.findAllPlanesSL())
			.thenReturn(null);
		
		planeController.allPlanes();
		verify(planeView).showAllPlanes(null);	
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(planeView);
	}
	
	
	
	@Test
	public void testFindPlaneWhenExistPlane() {		
		
		when(serviceLayer.findByIdSL(ID_FIXTURE))
			.thenReturn(PLANE_FIXTURE);
		
		Plane plane = planeController.idPlane(ID_FIXTURE);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		assertEquals(PLANE_FIXTURE, plane);
	}
	
	
	
	@Test
	public void testFindPlaneWhenNoExistPlane() {		
		
		when(serviceLayer.findByIdSL(ID_FIXTURE))
			.thenReturn(null);
		
		Plane plane = planeController.idPlane(ID_FIXTURE);
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		assertEquals(null, plane);
	}
	
	
	
	@Test
	public void testNewPlane() {
		planeController.newPlane(PLANE_FIXTURE);
		InOrder inOrder = inOrder(serviceLayer, planeView);
		inOrder.verify(serviceLayer).savePlaneSL(PLANE_FIXTURE);
		inOrder.verify(planeView).planeAdded(PLANE_FIXTURE);
		verifyNoMoreInteractions(serviceLayer, planeView);
	}
	
	
	
	@Test
	public void testDeletePlaneWhenAlreadyExist() {		
		planeController.deletePlane(PLANE_FIXTURE);
		InOrder inOrder = inOrder(serviceLayer, planeView);
		inOrder.verify(serviceLayer).deletePlaneSL(PLANE_FIXTURE);
		inOrder.verify(planeView).planeRemoved(PLANE_FIXTURE);
		verifyNoMoreInteractions(serviceLayer, planeView);
	}
	
	
	
	@Test
	public void testDeletePlaneWhenNoExist() {		
		
		doThrow(new PlaneNotFoundException("No existing plane with id " + PLANE_FIXTURE.getId()))
			.when(serviceLayer).deletePlaneSL(PLANE_FIXTURE);
		
		planeController.deletePlane(PLANE_FIXTURE);
		verify(planeView).showPlaneError("No existing plane with id " + PLANE_FIXTURE.getId());
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(planeView);
	}
	
	
	
	@Test
	public void testDeletePlaneWhenThereAreAssociatesFlights() {
		List<Flight> flights = asList(FLIGHT_FIXTURE);
		
		doThrow(new PlaneWithAssociateFlightException("Impossible to delete. There is the flight " + flights.get(0).getFlightNum() + " associates with this plane"))
			.when(serviceLayer).deletePlaneSL(PLANE_FIXTURE);
		
		planeController.deletePlane(PLANE_FIXTURE);
		verify(planeView).showPlaneError("Impossible to delete. There is the flight " + flights.get(0).getFlightNum() + " associates with this plane");
		verifyNoMoreInteractions(ignoreStubs(serviceLayer));
		verifyNoMoreInteractions(planeView);
	}	
}


