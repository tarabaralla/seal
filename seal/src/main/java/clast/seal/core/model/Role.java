package clast.seal.core.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Role {

	private final String name;
	
	private Set<Role> managedRoles;

	public Role(String name) {
		this.name = name;
		managedRoles = new HashSet<>();
	}

	public String getName() {
		return name;
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Role))
			return false;
		Role other = (Role) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
