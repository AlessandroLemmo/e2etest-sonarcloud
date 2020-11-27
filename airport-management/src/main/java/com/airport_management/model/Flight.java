package com.airport_management.model;

import java.util.Date;
import java.util.Objects;


public class Flight {

	private String number;
	private Date departureDate;
	private Date arrivalDate;
	private String origin;
	private String destination;
	private Plane plane;
	
	
	public Flight(String number, Date departureDate, Date arrivalDate, String origin, String destination, Plane plane) {
		super();
		this.number = number;
		this.departureDate = departureDate;
		this.arrivalDate = arrivalDate;
		this.origin = origin;
		this.destination = destination;
		this.plane = plane;
	}

	
	public Flight(Date departureDate, Date arrivalDate, String origin, String destination, Plane plane) {
		super();
		this.departureDate = departureDate;
		this.arrivalDate = arrivalDate;
		this.origin = origin;
		this.destination = destination;
		this.plane = plane;
	}
	
	
	public Flight() {}


	public Plane getPlane() {
		return plane;
	}


	public void setPlane(Plane plane) {
		this.plane = plane;
	}


	public String getFlightNum() {
		return number;
	}


	public void setFlightNum(String number) {
		this.number = number;
	}


	public Date getDepartureDate() {
		return departureDate;
	}


	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}


	public Date getArrivalDate() {
		return arrivalDate;
	}


	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}
	

	public String getOrigin() {
		return origin;
	}


	public void setOrigin(String origin) {
		this.origin = origin;
	}


	public String getDestination() {
		return destination;
	}


	public void setDestination(String destination) {
		this.destination = destination;
	}

	
	
	@Override
	public int hashCode() {
		return Objects.hash(number, departureDate, arrivalDate, origin, destination, plane);
	}

	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Flight other = (Flight) obj;
		return Objects.equals(number, other.number) && 
				Objects.equals(departureDate, other.departureDate) && 
				Objects.equals(arrivalDate, other.arrivalDate) && 
				Objects.equals(origin, other.origin) &&
				Objects.equals(destination, other.destination) &&
				Objects.equals(plane, other.plane);
	}

	
	
	@Override
	public String toString() {
		return "flight_num=" + number + 
				", departure_date=" + departureDate + 
				", arrival_date=" + arrivalDate + 
				", origin=" + origin + 
				", desination=" + destination + 
				", plane[" + plane + "]";
	}
}
