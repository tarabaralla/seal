package clast.seal.jpa.service;

import java.util.Set;

import clast.seal.core.model.User;
import clast.seal.core.service.user.UserService;

public class JPAUserService implements UserService {
	
	private JPAUserService() {}
	
	@Override
	public boolean createUser(User user) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Set<User> findAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
