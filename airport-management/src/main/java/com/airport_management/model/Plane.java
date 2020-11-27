package com.airport_management.model;

import java.util.Objects;


public class Plane {
	
	private String id;
	private String model;


	public Plane(String model) {
		this.model = model;
	}

	
	public Plane(String id, String model) {
		this.id = id;
		this.model = model;
	}
	
	
	public Plane() {}
		

	public String getId() {
		return id;
	}

	
	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getModel() {
		return model;
	}

	
	public void setModel(String model) {
		this.model = model;
	}
	
	
	
	@Override
	public int hashCode() {
		return Objects.hash(id, model);
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
		Plane other = (Plane) obj;
		return Objects.equals(id, other.id) &&  
				Objects.equals(model, other.model);			
	}

	
	
	@Override
	public String toString() {
		return "id=" + id + ", model=" + model;
	}
}
