package mibay.packages;

import java.time.LocalDate;
import java.util.Arrays;

import mibay.Customer;
import mibay.Product;
import mibay.exceptions.MiBayException;

public class RegularPackage implements MiBayPackage {
	private LocalDate date;
	private Customer customer;
	protected Product[] product;

	public RegularPackage(LocalDate date, Customer customer, Product product) throws MiBayException {
		// if date, customer and product is null throw error message
		// if date is later than todaythrow error message
		if (date == null || date.isBefore(LocalDate.now())) {
			throw new MiBayException("Date can not be empty or before today\n");
		}
		if (customer == null) {
			throw new MiBayException("Customer can not be empty\n");
		}
		if (product == null) {
			throw new MiBayException("Product can not be empty\n");
		}
		this.date = date;
		this.customer = customer;
		this.product = new Product[0];
		addProduct(product);
	}

	public LocalDate getDate() {
		return this.date;
	}

	public Customer getCustomer() {
		return this.customer;
	}

	public Product[] getProducts() {
		return this.product;
	}

	public boolean addProduct(Product product) {

		// check: if this package already have product, return false
		for (int i = 0; i < this.product.length; i++) {
			if (this.product[i].getname().equals(product.getname())) {
				return false;
			}
		}
		// get a copy of product, array length plus one
		this.product = Arrays.copyOf(this.product, this.product.length + 1);
		// add product in the end of array
		this.product[this.product.length - 1] = product;
		return true;
	}

	public double getCost() {
		double cost = 0;
		for (int i = 0; i < this.product.length; i++) {
			cost += this.product[i].getcost();
		}
		return cost;
	}
}
