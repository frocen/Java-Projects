package mibay;

import mibay.exceptions.MiBayException;

public class Customer {
	private int id;
	private String firstName;
	private String lastName;
	private Address address;

	public Customer(int id, String firstName, String lastName, Address address) throws MiBayException {
		// if firstName ,address and lastName is empty, throw error message
		// if id is less than or equal to 0,throw error message
		if (id <= 0) {
			throw new MiBayException("id can not be less than or equal to 0");
		}
		if (firstName.isEmpty() || firstName == null || firstName.isBlank()) {
			throw new MiBayException("First Name can not be empty\n");
		}
		if (lastName.isEmpty() || lastName == null || lastName.isBlank()) {
			throw new MiBayException("Last Name can not be empty\n");
		}
		if (address == null) {
			throw new MiBayException("Address can not be empty\n");
		}
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;

	}

	public int getid() {
		return this.id;
	}

	public String getfirstName() {
		return this.firstName;
	}

	public String getlastName() {
		return this.lastName;
	}

	public String getaddress() {
		return String.format("%s %s %d", address.getstreet(), address.getsuburb(), address.getpostCode());
	}

}
