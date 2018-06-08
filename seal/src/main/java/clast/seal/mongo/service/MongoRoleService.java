package clast.seal.mongo.service;

import java.util.Set;

import clast.seal.core.model.Role;
import clast.seal.core.service.role.RoleService;

public class MongoRoleService extends MongoService implements RoleService {
	
	private MongoRoleService() {
		super();
	}
	
	@Override
	public boolean createRole(Role role) {
		return db.createRole(role);
	}
	
	@Override
	public Set<Role> findAllRoles() {
		return db.findAllRoles();
	}

}
