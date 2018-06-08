package clast.seal.core.service.role;

import java.util.Set;

import clast.seal.core.model.Role;
import clast.seal.core.service.Service;

public interface RoleService extends Service {

	public abstract boolean createRole(Role role);

	public abstract Set<Role> findAllRoles();

}
