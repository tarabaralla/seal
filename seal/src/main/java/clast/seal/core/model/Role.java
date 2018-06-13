package clast.seal.core.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import clast.seal.core.persistence.Entity;

public abstract class Role extends Entity {

	private String name;
	
	private Set<Role> managedRoles;
	
	public Role() {
		super();
		managedRoles = new HashSet<>();
	}

	public Role(String uuid) {
		super(uuid);
		managedRoles = new HashSet<>();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public abstract void addSubRole(Role role);
	
	public abstract boolean removeSubRole(Role role);
	
	public abstract boolean hasSubRole(Role role);

	public abstract Set<Role> getDirectSubRoles();
	
	public abstract Set<Role> getAllSubRoles();
	
	public boolean addManagedRole(Role role) {
		return managedRoles.add(role);
	}
	
	public boolean removeManagedRole(Role role) {
		return managedRoles.remove(role);
	}
	
	public boolean managesRole(Role role) {
		return getAllManagedRoles().contains(role);
	}
	
	public Set<Role> getDirectManagedRoles() {
		return Collections.unmodifiableSet(managedRoles);
	}
	
	public Set<Role> getAllManagedRoles() {
		Set<Role> allManagedRoles = new HashSet<>(managedRoles);
		
		for(Role subRole : getAllSubRoles()) {
			allManagedRoles.addAll(subRole.getAllManagedRoles());
		}
		
		return Collections.unmodifiableSet(allManagedRoles);
	}

	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	@Override
	public final boolean equals(Object obj) {
		return super.equals(obj);
	}

}
