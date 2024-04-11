package model;

import dao.UserDao;
import dao.UserDaoImpl;

import java.sql.SQLException;

public class Model {
    private UserDao userDao;
    private User currentUser;

    public void setup() throws SQLException {
        userDao.setup();
    }

    public Model() {
        userDao = new UserDaoImpl();
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

}
