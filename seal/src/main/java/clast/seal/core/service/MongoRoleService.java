package clast.seal.core.service;

import clast.seal.core.service.role.RoleService;

public class MongoRoleService implements RoleService {
	
	private MongoRoleService() {}
	
	@Override
	public String getDescription() {
		return "Mongo Role Service";
	}

}