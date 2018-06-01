package clast.seal.core.dao;

import java.util.Set;

import clast.seal.core.model.Role;
import clast.seal.core.model.SealDb;

public class RoleDao {
	
	private SealDb db;

	public RoleDao(SealDb db) {
		this.db = db;
	}

	public boolean addRole(Role role) {
		return db.addRole(role);
	}

	public Set<Role> getAllRoles() {
		return db.getAllRoles();
	}

}
