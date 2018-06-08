package clast.seal.core.service;

import clast.seal.core.service.user.UserService;

public class MongoUserService implements UserService {
	
	private MongoUserService() {}

	@Override
	public String getDescription() {
		return "Mongo User Service";
	}
}