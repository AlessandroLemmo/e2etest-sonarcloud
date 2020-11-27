package com.airport_management.service_layer;

import java.util.Date;
import java.util.List;
import com.airport_management.model.Flight;
import com.airport_management.model.Plane;


public interface SearchServiceLayer {
	
	public List<Flight> findAllFlightsByOriginSL(String origin);
	public List<Flight> findAllFlightsByDestinationSL(String destination);
	public List<Flight> findAllFlightsWithDepartureDateInRangeSL(Date start, Date end);
	public List<Flight> findAllFlightsWithArrivalDateInRangeSL(Date start, Date end);
	public List<Flight> findAllFlightsAssociatesWithPlaneSL(String planeId);
	public List<Plane> findAllPlanesByModelSL(String model);
}
