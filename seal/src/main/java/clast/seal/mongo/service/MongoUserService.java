package clast.seal.mongo.service;

import java.util.Set;

import clast.seal.core.model.User;
import clast.seal.core.service.user.UserService;

public class MongoUserService extends MongoService implements UserService {
	
	private MongoUserService() {
		super();
	}
	
	@Override
	public boolean createUser(User user) {
		return db.createUser(user);
	}
	
	@Override
	public Set<User> findAllUsers() {
		return db.findAllUsers();
	}

}
