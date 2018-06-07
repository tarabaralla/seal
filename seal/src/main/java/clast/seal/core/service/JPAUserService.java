package clast.seal.core.service;

import clast.seal.core.service.user.UserService;

public class JPAUserService implements UserService {
	
	@Override
	public String getDescription() {
		return "JPA User Service";
	}

}
