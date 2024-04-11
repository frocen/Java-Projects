package dao;

import java.sql.SQLException;

import model.User;

public interface UserDao {
	void setup() throws SQLException;
	User getUser(String username, String password) throws SQLException;
	User createUser(String username, String password, String firstName, String lastName, String photo) throws SQLException;
	boolean isExist(String username)throws SQLException;
	void updateProfile(String username,String firstName, String lastName, String photo)throws SQLException;
	String getQuote();
}
