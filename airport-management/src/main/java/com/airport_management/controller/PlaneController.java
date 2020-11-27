package com.airport_management.controller;

import java.io.Serializable;
import java.util.List;

import com.airport_management.model.Plane;
import com.airport_management.service_layer.transaction.AirportServiceLayer;
import com.airport_management.view.*;
import com.airport_management.exception.PlaneNotFoundException;
import com.airport_management.exception.PlaneWithAssociateFlightException;


public class PlaneController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private transient PlaneView planeView;	
	private transient AirportServiceLayer serviceLayer;

	
	public PlaneController(PlaneView planeView, AirportServiceLayer serviceLayer) {
		this.planeView = planeView;
		this.serviceLayer = serviceLayer;
	} 
	
	
	
	public List<Plane> returnAllPlanes() {
		return serviceLayer.findAllPlanesSL();
	}
	
	
	
	public void allPlanes() {
		List<Plane> planes = serviceLayer.findAllPlanesSL();
		planeView.showAllPlanes(planes);
	}
	
	
	
	public Plane idPlane(String id) {
		return serviceLayer.findByIdSL(id);
	}

	
	 
	public void newPlane(Plane plane) {
		serviceLayer.savePlaneSL(plane);
		planeView.planeAdded(plane);
		
	}
	
	
	
	public void deletePlane(Plane plane) {
		
		try {
			serviceLayer.deletePlaneSL(plane);
			planeView.planeRemoved(plane);
		}
		catch(PlaneNotFoundException | PlaneWithAssociateFlightException ex) {
			planeView.showPlaneError(ex.getMessage());
		}
	}
}
