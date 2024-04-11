package mibay;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.Arrays;

import mibay.packages.MiBayPackage;
import mibay.packages.PlatinumPackage;

public class MiBaySystem {
	final String file = "products.txt";
	private Customer[] customers;
	private Product[] products;
	private MiBayPackage[] packages;

	public MiBaySystem() {
		this.customers = new Customer[0];
		this.products = new Product[0];
		this.packages = new MiBayPackage[0];
	}

	public Customer getCustomer(int i) {
		return this.customers[i];
	}

	public Product getProduct(int i) {
		return this.products[i];
	}

	public MiBayPackage getPackege(int i) {
		return this.packages[i];
	}

	public void addCustomer(Customer customer) {
		// Make a copy of customer and array length plus one
		this.customers = Arrays.copyOf(this.customers, this.customers.length + 1);
		// Add customer
		this.customers[this.customers.length - 1] = customer;
	}

	// User need to see id start from 1.
	// so,customer ID is array length plus one
	public int getCustomerID() {
		return this.customers.length + 1;
	}

	// Get user full name
	public String getCustomerName(int i) {
		return this.customers[i].getfirstName() + " " + this.customers[i].getlastName();
	}

	public String printCustomer(int i) {
		return String.format("%-8d %-21s %s\n", this.customers[i].getid(), getCustomerName(i),
				this.customers[i].getaddress());
	}

	public int getProductlength() {
		return this.products.length;
	}

	public boolean addProduct(Product product) {
		// If product is already exist, return false
		for (int i = 0; i < this.products.length; i++) {
			if (this.products[i].getname().equals(product.getname())) {
				return false;
			}
		}
		// Make a copy of products and array length plus one
		this.products = Arrays.copyOf(this.products, this.products.length + 1);
		// Add product
		this.products[this.products.length - 1] = product;
		return true;
	}

	public int productLength() {
		return this.products.length;
	}

	public String getProductName(int i) {
		return this.products[i].getname();
	}

	public String printProducts() {
		String print = "Name               Weight      Cost\n";
		// If products array lenth is 0,means there are no product in this program
		if (this.products.length == 0) {
			System.err.println("There are no products in the system");
		} else {
			for (int i = 0; i < this.products.length; i++) {
				print += String.format("%-14s     %-5dg      $%.2f\n", this.products[i].getname(),
						this.products[i].getweight(), this.products[i].getcost());
			}
		}
		return print;
	}

	public int getPackageName() {
		return this.packages.length;
	}

	public void addPackage(MiBayPackage onepackage) {
		// Make a copy of packages and array length plus one
		this.packages = Arrays.copyOf(this.packages, this.packages.length + 1);
		// Add package
		this.packages[this.packages.length - 1] = onepackage;
	}

	public boolean addProductInPackage(Product product) {
		return this.packages[this.packages.length - 1].addProduct(product);
	}

	public int getPackageslength() {
		return this.packages.length;
	}

	public String printPackage(int i) {
		// Make copy of customer, product and date from exist package
		Customer PringCustomer = this.packages[i].getCustomer();
		Product[] PringProduct = this.packages[i].getProducts();
		LocalDate DeliverDate = this.packages[i].getDate();
		// If that package is PlatinumPackage, print PlatinumPackage
		// If that package is RegularPackage, print RegularPackage
		if (this.packages[i] instanceof PlatinumPackage) {
			PlatinumPackage printPpackage = (PlatinumPackage) this.packages[i];
			String print = "PlatinumPackage\n"
					+ String.format("Name:                    %s %s \n", PringCustomer.getfirstName(),
							PringCustomer.getlastName())
					+ "Address:                 " + PringCustomer.getaddress() + "\nMember Number:           "
					+ printPpackage.getmemberNumber() + "\nDeliver Date             " + DeliverDate
					+ String.format("\nTotal Cost               %.2f \n", this.packages[i].getCost())
					+ "Products Ordered \n" + "Name                     Weight    Cost";
			for (int j = 0; j < PringProduct.length; j++) {
				print += String.format("\n" + "%-20s     %-3dg      $%.2f", PringProduct[j].getname(),
						PringProduct[j].getweight(), PringProduct[j].getcost());
			}
			print += "\n--------------------------------------------------------------------";
			return print;
		} else {
			String print = "RegularPackage\n"
					+ String.format("Name:                    %s %s \n", PringCustomer.getfirstName(),
							PringCustomer.getlastName())
					+ "Address:                 " + PringCustomer.getaddress() + "\nDeliver Date             "
					+ DeliverDate + String.format("\nTotal Cost               %.2f \n", this.packages[i].getCost())
					+ "Products Ordered \n" + "Name                     Weight    Cost";
			for (int j = 0; j < PringProduct.length; j++) {
				print += String.format("\n" + "%-20s     %-3dg      $%.2f", PringProduct[j].getname(),
						PringProduct[j].getweight(), PringProduct[j].getcost());
			}
			print += "\n--------------------------------------------------------------------";
			return print;
		}
	}

	public void Saving() {
		//If in try block, something error, throw error message
		try {
			BufferedWriter writting = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < this.getProductlength(); i++) {
				Product saveProduct = this.products[i];
				String Store = saveProduct.getname() + "," + saveProduct.getweight() + "," + saveProduct.getcost();
				writting.write(Store + "\n");
			}
			writting.close();
		} catch (Exception e) {
			System.err.print("Note: " + e.getMessage() + "\n");
		}
	}
}
