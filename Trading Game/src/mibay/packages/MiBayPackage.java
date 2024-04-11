package mibay.packages;

import java.time.LocalDate;
import mibay.Customer;
import mibay.Product;

public interface MiBayPackage {
	public LocalDate getDate();

	public Customer getCustomer();

	public Product[] getProducts();

	public boolean addProduct(Product product);

	public double getCost();
}
