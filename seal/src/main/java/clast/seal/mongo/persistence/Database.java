package clast.seal.mongo.persistence;

import java.util.Set;

import clast.seal.core.model.Role;
import clast.seal.core.model.User;

public interface Database {

	public abstract boolean createUser(User user);
	
	public abstract Set<User> findAllUsers();

	public abstract boolean createRole(Role role);

	public abstract Set<Role> findAllRoles();

}
