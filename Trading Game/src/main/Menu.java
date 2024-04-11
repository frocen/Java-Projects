package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;

import mibay.Address;
import mibay.Customer;
import mibay.MiBaySystem;
import mibay.Product;
import mibay.exceptions.MiBayException;
import mibay.packages.PlatinumPackage;
import mibay.packages.RegularPackage;

public class Menu {

	private MiBaySystem miBaySystem = new MiBaySystem();
	private static Scanner input = new Scanner(System.in);

	public void run() {
		readFile();
		menu();
	}

	public void readFile() {
		System.out.print("Loading file......");
		// iIf something wrong in try block, print error message
		try {
			BufferedReader reading = new BufferedReader(new FileReader("products.txt"));
			String line = reading.readLine();
			// Stop when there are no linne can read from file
			while (line != null) {
				// Divide line by ","
				String Information[] = line.split(",");
				// Store information in to three place
				String name = Information[0];
				int Weight = Integer.parseInt(Information[1]);
				double Cost = Double.parseDouble(Information[2]);
				// Add to product
				Product product = new Product(name, Weight, Cost);
				miBaySystem.addProduct(product);
				line = reading.readLine();
			}
			reading.close();
			System.out.println("succeed");
		} catch (IOException | MiBayException e) {
			System.err.print("Note: " + e.getMessage() + "\n");
		}
	}

	public void menu() {
		boolean end = true;
		while (end) {
			System.out.println("*** MiBaySystem Menu ***");
			System.out.println("Add Customer                         AC");
			System.out.println("Add Product                          AP");
			System.out.println("Prepare Package                      PP");
			System.out.println("Display All Customers                DC");
			System.out.println("Display All Products                 DP");
			System.out.println("Display All Packages                 DA");
			System.out.println("Seed Data                            SD");
			System.out.println("Exit Program                         EX");
			System.out.println("");
			System.out.print("Select an option:");
			// if something wrong in try block include methord in switch, show error message
			// form they throw
			try {
				String Userinput = input.nextLine().toUpperCase();
				switch (Userinput) {
				case "AC": {
					AC();
					break;
				}
				case "AP": {
					AP();
					break;
				}
				case "PP": {
					PP();
					break;
				}
				case "DC": {
					DC();
					break;
				}
				case "DP": {
					DP();
					break;
				}
				case "DA": {
					DA();
					break;
				}
				case "SD": {
					SD();
					break;
				}
				case "EX": {
					end = false;
					EX();
					break;
				}
				default: {
					break;
				}
				}
			} catch (Exception e) {
				System.err.print("Note: " + e.getMessage() + "\n");
			}
		}
	}

	// If user enter nothing, go menu
	public void checkString(String userinput) {
		if (userinput.isEmpty() || userinput == null || userinput.isBlank()) {
			menu();
		}
	}

	public void AC() throws MiBayException {
		System.out.println("- Add Customer (press enter to return to menu) -");
		System.out.println(String.format("ID:                 %d", miBaySystem.getCustomerID()));
		System.out.print("Enter First name:");
		String Fname = input.nextLine();
		checkString(Fname);
		System.out.print("Enter last name:");
		String Lname = input.nextLine();
		checkString(Lname);
		System.out.print("Enter street:");
		String St = input.nextLine();
		checkString(St);
		System.out.print("Enter suburb:");
		String Suburb = input.nextLine();
		checkString(Suburb);
		System.out.print("Enter postcode:");
		String code = input.nextLine();
		checkString(code);
		int postcode = Integer.parseInt(code);
		Address address = new Address(St, Suburb, postcode);
		Customer customer = new Customer(miBaySystem.getCustomerID(), Fname, Lname, address);
		// Add customer to program
		miBaySystem.addCustomer(customer);
		System.out.println(String.format("%s %s was successfully added to the system", Fname, Lname));
	}

	public void AP() throws MiBayException {
		System.out.println("- Add Product (press enter to return to menu) -");
		System.out.print("Enter name:");
		String name = input.nextLine();
		checkString(name);
		System.out.print("Enter weight:");
		String weight = input.nextLine();
		checkString(weight);
		System.out.print("Enter cost:");
		String cost = input.nextLine();
		checkString(cost);
		int Weight = Integer.parseInt(weight);
		double Cost = Double.parseDouble(cost);
		Product product = new Product(name, Weight, Cost);
		// if there is already exist, return false to success
		boolean success = miBaySystem.addProduct(product);
		if (success) {
			System.out.println(String.format("%s was successfully added to the system", name));
		} else {
			System.out.println(String.format("Unable to added %s to the system", name));
		}
	}

	public void PP() throws MiBayException {
		System.out.println("- Prepare Package (press enter to return to menu) -");
		// Check is there any customer in program, if not, go menu
		if (miBaySystem.getCustomerID() - 1 == 0) {
			System.err.println("Sorry,there are no customers in the system");
			menu();
		} else {
			for (int i = 0; i < miBaySystem.getCustomerID() - 1; i++) {
				System.out.println(String.format("%d. %s", i + 1, miBaySystem.getCustomerName(i)));
			}
		}
		System.out.print("Select a customer from the list:");
		String selectcustomer = input.nextLine();
		checkString(selectcustomer);
		// If user input a not exist id of customer, go menu
		int selectCustomer = Integer.parseInt(selectcustomer);
		if (selectCustomer <= 0 || selectCustomer > miBaySystem.getCustomerID() - 1) {
			menu();
		}
		// Check is there any product in program, if not, go menu
		if (miBaySystem.productLength() == 0) {
			System.err.println("Sorry,there are no products in the system");
			menu();
		} else {
			for (int i = 0; i < miBaySystem.productLength(); i++) {
				System.out.println(String.format("%d. %s", i + 1, miBaySystem.getProductName(i)));
			}
		}
		System.out.print("Select a product from the list:");
		String selectproduct = input.nextLine();
		checkString(selectproduct);
		int selectProduct = Integer.parseInt(selectproduct);
		// If user input a not exist id of product, go menu
		if (selectProduct <= 0 || selectProduct > miBaySystem.getProductlength()) {
			menu();
		}
		System.out.print("Enter the delivery date\nEnter day:");
		String inputday = input.nextLine();
		checkString(inputday);
		int day = Integer.parseInt(inputday);
		System.out.print("Enter month:");
		String inputmonth = input.nextLine();
		checkString(inputmonth);
		int month = Integer.parseInt(inputmonth);
		System.out.print("Enter year:");
		String inputyear = input.nextLine();
		checkString(inputyear);
		int year = Integer.parseInt(inputyear);
		LocalDate date = LocalDate.of(year, month, day);
		System.out.print("Is this a Platinum Package?");
		String yes = "Y";
		String no = "N";
		String YORN = input.nextLine().toUpperCase();
		checkString(YORN);
		// If YORN is Y, this is a PlatinumPackage
		// If YORN is N, this is a RegularPackage
		if (YORN.equals(yes)) {
			System.out.print("Enter your member number");
			String Membernumber = input.nextLine();
			checkString(Membernumber);
			PlatinumPackage onepackage = new PlatinumPackage(date, miBaySystem.getCustomer(selectCustomer - 1),
					miBaySystem.getProduct(selectProduct - 1), Membernumber);
			// Add package to program
			miBaySystem.addPackage(onepackage);
			boolean going = true;
			// In while loop, if user do not want to add more product, loop end
			while (going) {
				System.out.print("Would you like to add another product to the package? (Y/N):");
				String InYORN = input.nextLine().toUpperCase();
				checkString(InYORN);
				if (InYORN.equals(yes)) {
					for (int i = 0; i < miBaySystem.productLength(); i++) {
						System.out.println(String.format("%d. %s", i + 1, miBaySystem.getProductName(i)));
					}
					System.out.print("Select a product from the list:");
					selectproduct = input.nextLine();
					checkString(selectproduct);
					selectProduct = Integer.parseInt(selectproduct);
					if (selectProduct <= 0 || selectProduct > miBaySystem.getProductlength()) {
						menu();
					}
					boolean success = miBaySystem.addProductInPackage(miBaySystem.getProduct(selectProduct - 1));
					if (!success) {
						System.out.println("You already have this");
					} else {
						System.out.println(String.format("Product %s added to package successfully",
								miBaySystem.getProductName(selectProduct - 1)));
					}
				}
				if (InYORN.equals(no)) {
					going = false;
				}
			}
			System.out.println(String.format("Package for %s was successfully prepared.",
					miBaySystem.getCustomerName(selectCustomer - 1)));
		}
		if (YORN.equals(no)) {

			RegularPackage onepackage = new RegularPackage(date, miBaySystem.getCustomer(selectCustomer - 1),
					miBaySystem.getProduct(selectProduct - 1));
			miBaySystem.addPackage(onepackage);

			boolean going = true;
			while (going) {
				System.out.print("Would you like to add another product to the package? (Y/N):");
				String InYORN = input.nextLine().toUpperCase();
				checkString(InYORN);
				if (InYORN.equals(yes)) {
					for (int i = 0; i < miBaySystem.productLength(); i++) {
						System.out.println(String.format("%d. %s", i + 1, miBaySystem.getProductName(i)));
					}
					System.out.print("Select a product from the list:");
					selectproduct = input.nextLine();
					checkString(selectproduct);

					selectProduct = Integer.parseInt(selectproduct);
					if (selectProduct <= 0 || selectProduct > miBaySystem.getProductlength()) {
						menu();
					}
					boolean success = miBaySystem.addProductInPackage(miBaySystem.getProduct(selectProduct - 1));
					if (!success) {
						System.out.println("You already have this");
					} else {
						System.out.println(String.format("Product %s added to package successfully",
								miBaySystem.getProductName(selectProduct - 1)));
					}
				}
				if (InYORN.equals(no)) {
					going = false;
				}
			}
			System.out.println(String.format("Package for %s was successfully prepared.",
					miBaySystem.getCustomerName(selectCustomer - 1)));
		}

	}

	public void DC() {
		System.out.println("- Displaying all Customers -");
		System.out.println("ID       Name                  Address");
		if (miBaySystem.getCustomerID() - 1 == 0) {
			System.err.println("There are no customers in the system");
		} else {
			for (int i = 0; i < miBaySystem.getCustomerID() - 1; i++) {
				System.out.printf(miBaySystem.printCustomer(i));
			}
		}
	}

	public void DP() {
		System.out.println("- Displaying all Products -");
		if (miBaySystem.productLength() == 0) {
			System.err.println("There are no products in the system");
		} else {
			System.out.printf(miBaySystem.printProducts());
		}
	}

	public void DA() {
		System.out.println("- Displaying all Packages -");
		if (miBaySystem.getPackageslength() == 0) {
			System.err.println("There are no packages in the system");
		} else {
			System.out.println("--------------------------------------------------------------------");
			for (int i = 0; i < miBaySystem.getPackageslength(); i++) {
				System.out.println(miBaySystem.printPackage(i));
			}
		}
	}

	public void SD() throws MiBayException {
		try {
			Address address1 = new Address("550 Swanston Street", "VIC", 3000);
			Customer customer1 = new Customer(miBaySystem.getCustomerID(), "HuiKai", "Ling", address1);
			Address address2 = new Address("82 Dalgliesh Street", "South Yarra", 3141);
			Customer customer2 = new Customer(miBaySystem.getCustomerID() + 1, "Matthew", "Bolger", address2);
			boolean exist = false;
			//check two customer is exist or not
			for (int i = 0; i < miBaySystem.getCustomerID() - 1; i++) {
				if (new Customer(i, "HuiKai", "Ling", address1).equals(miBaySystem.getCustomer(i))
						|| new Customer(i, "Matthew", "Bolger", address2).equals(miBaySystem.getCustomer(i))) {
					exist = true;
				}
			}
			Product product1 = new Product("Apple", 500, 5.00);
			Product product2 = new Product("Program A", 100, 598.98);
			Product product3 = new Product("Amazing water", 250, 9999.99);
			Product product4 = new Product("Hot water", 250, 2.88);
			//check four productss is exist or not
			for (int i = 0; i < miBaySystem.getProductlength(); i++) {
				if (product1.getname().equals(miBaySystem.getProductName(i))
						|| product2.getname().equals(miBaySystem.getProductName(i))
						|| product3.getname().equals(miBaySystem.getProductName(i))
						|| product4.getname().equals(miBaySystem.getProductName(i))) {
					exist = true;
				}
			}
			LocalDate date1 = LocalDate.of(2021, 10, 11);
			PlatinumPackage Platinumpackage = new PlatinumPackage(date1, customer1, product3, "M9999");
			LocalDate date2 = LocalDate.of(2021, 11, 20);
			RegularPackage Regularpackage = new RegularPackage(date2, customer2, product2);
			//check two package is exist or not
			for (int i = 0; i < miBaySystem.getPackageslength(); i++) {
				if (Platinumpackage.equals(miBaySystem.getPackege(i))
						|| Regularpackage.equals(miBaySystem.getPackege(i))) {
					exist = true;
				}
			}
			// if any customers, products and packages is exist
			if (exist) {
				System.err.println("Customers, products or packages already exist, seeding aborted.");
			} else {
				miBaySystem.addCustomer(customer1);
				miBaySystem.addCustomer(customer2);
				miBaySystem.addProduct(product1);
				miBaySystem.addProduct(product2);
				miBaySystem.addProduct(product3);
				miBaySystem.addProduct(product4);
				miBaySystem.addPackage(Platinumpackage);
				miBaySystem.addPackage(Regularpackage);
				System.out.println("Customers, products and packages have been seeded.");
			}
		} catch (Exception e) {
			System.err.println("Customers, products or packages already exist, seeding aborted.");
		}
	}

	public void EX() {
		System.out.print("Program ending");
		miBaySystem.Saving();
	}
}
