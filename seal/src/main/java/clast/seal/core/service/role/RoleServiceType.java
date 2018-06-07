package clast.seal.core.service.role;

import clast.seal.core.service.JPARoleService;
import clast.seal.core.service.MongoRoleService;

public enum RoleServiceType {
	
	MONGO_DB_RS (MongoRoleService.class), JPA_RS (JPARoleService.class);

	private Class<? extends RoleService> roleService;
	
	private RoleServiceType (Class<? extends RoleService> roleService) {
		this.roleService = roleService;
	}

	public Class<? extends RoleService> getService() {
		return roleService;
	}
}
