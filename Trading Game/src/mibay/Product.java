package mibay;

import mibay.exceptions.MiBayException;

public class Product {
	private String name;
	private int weight;
	private double cost;

	public Product(String name, int weight, double cost) throws MiBayException {
		// if name is empty, throw error message
		// if weight and cost is less than or equal to 0,throw error message
		if (name.isEmpty() || name == null || name.isBlank()) {
			throw new MiBayException("Name can not be empty\n");
		}
		if (weight <= 0) {
			throw new MiBayException("Weight can not be less than or equal to 0");
		}
		if (cost <= 0) {
			throw new MiBayException("Cost can not be less than or equal to 0");
		}
		this.name = name;
		this.weight = weight;
		this.cost = cost;

	}

	public String getname() {
		return this.name;
	}

	public int getweight() {
		return this.weight;
	}

	public double getcost() {
		return this.cost;
	}

}
