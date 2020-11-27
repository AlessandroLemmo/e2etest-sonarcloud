package com.airport_management.view;

import java.util.List;
import com.airport_management.model.Plane;


public interface PlaneView {
	
	public void showAllPlanes(List<Plane> planes);
	public void showPlaneError(String message);
	public void planeAdded(Plane plane);
	public void planeRemoved(Plane plane);	
}
