package mibay.packages;

import java.time.LocalDate;

import mibay.Customer;
import mibay.Product;
import mibay.exceptions.MiBayException;

public class PlatinumPackage extends RegularPackage {
	private String memberNumber;

	public PlatinumPackage(LocalDate date, Customer customer, Product product, String memberNumber)
			throws MiBayException {
		super(date, customer, product);
		// if memberNumber is invalid, throw error message
		if (!isValidMemberNumber(memberNumber)) {
			throw new MiBayException(
					"member number must be 5 characters in length. The 1st character must be an uppercase\n"
							+ "letter and all the remaining characters must be digits");
		}
		this.memberNumber = memberNumber;
	}

	@Override
	public double getCost() {
		double cost = 0;
		// add all price of products together
		for (int i = 0; i < product.length; i++) {
			cost += product[i].getcost();
		}
		return cost * 0.9;
	}

	public String getmemberNumber() {
		return this.memberNumber;
	}

	public static boolean isValidMemberNumber(String memberNumber) {
		char upper = memberNumber.charAt(0);
		char M1 = memberNumber.charAt(1);
		char M2 = memberNumber.charAt(2);
		char M3 = memberNumber.charAt(3);
		char M4 = memberNumber.charAt(4);
		// If first character is upper char and rest four are number, return true
		if (Character.isUpperCase(upper) && Character.isDigit(M1) && Character.isDigit(M2) && Character.isDigit(M3)
				&& Character.isDigit(M4)) {
			return true;
		} else {
			return false;
		}
	}
}
