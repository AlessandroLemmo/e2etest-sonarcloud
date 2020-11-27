package com.airport_management.view;

import java.util.List;
import com.airport_management.model.Flight;
import com.airport_management.model.Plane;


public interface SearchView {

	public void showSearchFlightError(String message);
	public void showSearchPlaneError(String message);

	public void clearListSearchByOrigin();
	public void clearListSearchByDestination();
	public void clearListSearchByDepartureDate();
	public void clearListSearchByArrivalDate();
	public void clearListSearchAssociatesFlights();
	public void clearListSearchByModel();

	public void showAllFoundedFlightsByOrigin(List<Flight> flights);
	public void showAllFoundedFlightsByDestination(List<Flight> flights);
	public void showAllFoundedFlightsByDepartureDate(List<Flight> flights);
	public void showAllFoundedFlightsByArrivalDate(List<Flight> flights);
	public void showAllFoundedFlightsAssociatesWithPlane(List<Flight> flights);
	public void showAllFoundedPlanesByModel(List<Plane> planes);
}
