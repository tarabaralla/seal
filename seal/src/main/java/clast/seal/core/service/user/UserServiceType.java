package clast.seal.core.service.user;

import clast.seal.core.service.JPAUserService;
import clast.seal.core.service.MongoUserService;

public enum UserServiceType {
	
	MONGO_DB_US (MongoUserService.class), JPA_US (JPAUserService.class);
	
	private Class<? extends UserService> userService;
	
	private UserServiceType (Class<? extends UserService> userService) {
		this.userService = userService;
	}

	public Class<? extends UserService> getService() {
		return userService;
	}

}
