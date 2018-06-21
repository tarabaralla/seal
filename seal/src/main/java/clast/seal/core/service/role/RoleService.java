package clast.seal.core.service.role;

import java.util.Set;

import clast.seal.core.model.Role;
import clast.seal.core.service.Service;

public interface RoleService extends Service {

	public abstract boolean createRole(Role role);

	public abstract boolean deleteRole(Role role);
	
	public abstract Set<Role> findAll();

	public abstract Role findByName(String name);

}
