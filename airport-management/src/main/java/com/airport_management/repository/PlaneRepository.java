package com.airport_management.repository;

import java.util.List;
import com.airport_management.model.Plane;


public interface PlaneRepository {

	public List<Plane> findAllPlanes();
	public Plane findById(String id);
	public Plane savePlane(Plane plane);
	public String deletePlane(Plane plane);
	
}
