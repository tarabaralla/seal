package clast.seal.core.service;

import clast.seal.core.service.role.RoleService;

public class JPARoleService implements RoleService {
	
	private JPARoleService() {}
	
	@Override
	public String getDescription() {
		return "JPA Role Service";
	}

}
