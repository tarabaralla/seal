package clast.seal.mongo.persistence;

import java.util.Set;

import clast.seal.core.model.Role;
import clast.seal.core.model.User;

public class MongoDatabase implements Database {

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
	
	@Override
	public boolean createRole(Role role) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Set<Role> findAllRoles() {
		// TODO Auto-generated method stub
		return null;
	}

}
