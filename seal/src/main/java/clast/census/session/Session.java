package clast.census.session;

import java.io.Serializable;
import java.util.Set;

import javax.enterprise.context.SessionScoped;

import org.apache.log4j.Logger;

import clast.census.dao.RoleDao;
import clast.census.dao.UserDao;
import clast.census.model.Role;
import clast.census.model.User;

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

	public User getLoggedUser() {
		return loggedUser;
	}

	public boolean login(String username, String password) {

		try {

			if (loggedUser != null) {
				throw new IllegalArgumentException("the session already contains a logged-in user.");
			}

			if (username == null || password == null) {
				throw new IllegalArgumentException("username and password cannot be null.");
			}

			User user = userDao.findUserByUsername(username);

			if (user == null) {
				throw new IllegalArgumentException("user not found.");
			}

			if (!user.checkPassword(password)) {
				throw new IllegalArgumentException("wrong password.");
			}

			loggedUser = user;

			return true;

		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to login: " + e.getMessage());
		}
	}

	public boolean logout() {

		if (loggedUser == null) {
			throw new IllegalArgumentException("Unable to logout: no users logged in.");
		}

		loggedUser = null;
		return true;

	}

	public boolean updateProfile(User user) {

		try {

			checkProfileUpdating(user);

			userDao.updateUser(user);
			loggedUser = user;

			return true;

		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to update profile: " + e.getMessage());
		}
	}

	private void checkProfileUpdating(User user) {

		checkLoggedIn();

		if (user == null || !loggedUser.getId().equals(user.getId())) {
			throw new IllegalArgumentException("only logged in user data can be changed.");
		}

	}

	public boolean deleteProfile() {

		try {

			checkLoggedIn();

			userDao.deleteUser(loggedUser);
			loggedUser = null;

			return true;

		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to delete profile: " + e.getMessage());
		}
	}

	public Set<Role> getAllRoles() {
		checkLoggedIn();
		return userDao.findAllRoles(loggedUser);
	}

	public Set<Role> getDirectRoles() {
		checkLoggedIn();
		return userDao.findDirectRoles(loggedUser);
	}

	public Set<Role> getIndirectRoles() {
		checkLoggedIn();
		return userDao.findIndirectRoles(loggedUser);
	}

	public boolean hasRole(Role role) {
		checkLoggedIn();
		return userDao.hasRole(loggedUser, role);
	}

	public boolean directlyHasRole(Role role) {
		checkLoggedIn();
		return userDao.directlyHasRole(loggedUser, role);
	}

	public boolean indirectlyHasRole(Role role) {
		checkLoggedIn();
		return userDao.indirectlyHasRole(loggedUser, role);
	}

	public Set<Role> getManagedRoles() {
		checkLoggedIn();
		return userDao.findManagedRoles(loggedUser);
	}

	public Set<User> getManagedUsers() {
		checkLoggedIn();
		return userDao.findManagedUsers(loggedUser);
	}

	public Set<User> getManagedUsersByRole(Role role) {
		checkLoggedIn();
		return userDao.findManagedUsersByRole(loggedUser, role);
	}

	public boolean managesRole(Role role) {
		checkLoggedIn();
		return userDao.managesRole(loggedUser, role);
	}

	public boolean createUser(User user, Set<Role> roles) {

		try {

			checkNewUser(roles);

			userDao.createUser(user);

			User newUser = userDao.findUserByUsername(user.getUsername());

			for (Role role : roles) {

				try {
					userDao.addRole(newUser, roleDao.findRoleByName(role.getName()));
				} catch (Exception e) {
					logger.error("Error during user creation. ", e);
				}
			}

			return true;

		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to create user: " + e.getMessage());
		}
	}

	private void checkNewUser(Set<Role> roles) {

		checkLoggedIn();

		if (roles == null || roles.isEmpty()) {
			throw new IllegalArgumentException("every user must have at least one role.");
		}

		for (Role role : roles) {
			if (!userDao.managesRole(loggedUser, role)) {
				throw new IllegalArgumentException(
						"one or more roles to assign to new user not managed from logged id user.");
			}
		}
	}

	public boolean deleteUser(String username) {

		try {

			User user = checkUserToDelete(username);

			return userDao.deleteUser(user);

		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to delete user: " + e.getMessage());
		}
	}

	private User checkUserToDelete(String username) {

		checkLoggedIn();

		User user = userDao.findUserByUsername(username);

		if (user == null) {
			throw new IllegalArgumentException("user not found.");
		}

		if (loggedUser.getId().equals(user.getId())) {
			throw new IllegalArgumentException("logged in user cannot delete himself.");
		}

		for (Role role : userDao.findAllRoles(user)) {
			if (!userDao.managesRole(loggedUser, role)) {
				throw new IllegalArgumentException("logged in user not manages one or more roles of user to delete.");
			}
		}

		return user;
	}

	public boolean addRole(User user, Role role) {

		try {

			checkRoleOperation(role);

			userDao.addRole(user, role);

			return true;

		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to add role to user: " + e.getMessage());
		}

	}

	public boolean removeRole(User user, Role role) {

		try {

			checkRoleOperation(role);

			userDao.removeRole(user, role);

			return true;

		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to remove role to user: " + e.getMessage());
		}

	}

	private void checkRoleOperation(Role role) {

		checkLoggedIn();

		if (!userDao.managesRole(loggedUser, role)) {
			throw new IllegalArgumentException("logged in user not manages role to add/remove.");
		}

	}

	private void checkLoggedIn() {
		if (loggedUser == null) {
			throw new IllegalArgumentException("no users logged in.");
		}
	}

}
