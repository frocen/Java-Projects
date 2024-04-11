package mibay;

import mibay.exceptions.MiBayException;

public class Address {
	private String street;
	private String suburb;
	private int postCode;

	public Address(String street, String suburb, int postCode) throws MiBayException {
		// if street and suburb is empty, throw error message
		// if post code is less than 1 or bigger than 9999,throw error message
		if (street.isEmpty() || street == null || street.isBlank()) {
			throw new MiBayException("Street can not be empty\n");
		}
		if (suburb.isEmpty() || suburb == null || suburb.isBlank()) {
			throw new MiBayException("Suburb can not be empty\n");
		}
		if (postCode < 1 || postCode > 9999) {
			throw new MiBayException("Cost can not be less than or equal to 0");
		}
		this.street = street;
		this.suburb = suburb;
		this.postCode = postCode;
	}

	public String getstreet() {
		return this.street;
	}

	public String getsuburb() {
		return this.suburb;
	}

	public int getpostCode() {
		return this.postCode;
	}
}
