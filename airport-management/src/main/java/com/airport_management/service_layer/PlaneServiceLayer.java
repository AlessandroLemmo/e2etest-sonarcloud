package com.airport_management.service_layer;

import java.util.List;
import com.airport_management.model.Plane;


public interface PlaneServiceLayer {
	
	public Plane savePlaneSL(Plane plane);
	public Plane findByIdSL(String id);
	public List<Plane> findAllPlanesSL();
	public void deletePlaneSL(Plane plane);
}


