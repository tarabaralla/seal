package clast.seal.session;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;

import clast.seal.dao.UserDao;
import clast.seal.model.User;

@Default
@SessionScoped
public class SessionImpl implements Session {
	
	private UserDao userDao;
	
	private User loggedUser;
	
	public SessionImpl() {
		userDao = new UserDao();
	}

	@Override
	public boolean login(String username, String password) {
		
		try {
			
			if( loggedUser != null ) {
				throw new IllegalArgumentException("the session already contains a logged-in user");
			}
			
			if( username == null || password == null ) {
				throw new IllegalArgumentException("user and password cannot be null");
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

	@Override
	public User getLoggedUser() {
		return loggedUser;
	}

}
