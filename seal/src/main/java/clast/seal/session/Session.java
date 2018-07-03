package clast.seal.session;

import java.io.Serializable;
import java.util.Set;

import javax.enterprise.context.SessionScoped;

import org.apache.log4j.Logger;

import clast.seal.dao.RoleDao;
import clast.seal.dao.UserDao;
import clast.seal.model.Role;
import clast.seal.model.User;

@SessionScoped
public class Session implements Serializable {

	private static final Logger logger = Logger.getLogger(Session.class);

	private static final long serialVersionUID = 1L;

	private transient User loggedUser;
	
	private transient UserDao userDao;
	
	private transient RoleDao roleDao;
	
	public Session() {
		userDao = new UserDao();
		roleDao = new RoleDao();
	}

	public boolean login(String username, String password) {
		
		try {
			
			if( loggedUser != null ) {
				throw new IllegalArgumentException("the session already contains a logged-in user.");
			}
			
			if( username == null || password == null ) {
				throw new IllegalArgumentException("username and password cannot be null.");
			}
			
			User user = userDao.findUserByUsername(username);
			
			if( user == null ) {
				throw new IllegalArgumentException("user not found.");
			}
			
			if( !password.equals(user.getPassword()) ) {
				throw new IllegalArgumentException("wrong password.");
			}
			
			loggedUser = user;
			
			return true;
			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to login: " + e.getMessage() );
		}
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public boolean logout() {
		
		if( loggedUser == null ) {
			throw new IllegalArgumentException("Unable to logout: no users logged in.");
		}
		
		loggedUser = null;
		return true;
		
	}

	public boolean createUser(User user, Set<Role> roles) {
		
		if( loggedUser == null ) {
			throw new IllegalArgumentException("Unable to create user: no users logged in.");
		}
		
		if( roles == null || roles.isEmpty() ) {
			throw new IllegalArgumentException("Unable to create user: every user must have at least one role.");
		}
		
		for( Role role : roles ) {
			if( !userDao.managesRole(loggedUser, role) ) {
				throw new IllegalArgumentException("Unable to create user: one or more roles to assign to new user not managed from logged id user.");
			}
		}
		
		userDao.createUser(user);

		User newUser = userDao.findUserByUsername(user.getUsername());
		
		for( Role role : roles ) {
			
			try {
				userDao.addRole(newUser, roleDao.findRoleByName(role.getName()));
			}catch (Exception e) {
				logger.error("Error during user creation. ", e);
			}
		}
		
		return true;
	}

}
