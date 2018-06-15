package clast.seal.core.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "leaf")
public class LeafRole extends Role {
	
	public LeafRole() {
		super();
	}
	
	public LeafRole(String name) {
		super(name);
	}
	
	@Override
	public void addSubRole(Role role) {
		throw new UnsupportedOperationException("Only CompositeRole can manage sub-roles");
	}
	
	@Override
	public boolean removeSubRole(Role role) {
		throw new UnsupportedOperationException("Only CompositeRole can manage sub-roles");
	}
	
	@Override
	public boolean hasSubRole(Role role) {
		return false;
	}
	
	@Override
	public Set<Role> getDirectSubRoles() {
		return new HashSet<>();
	}
	
	@Override
	public Set<Role> getAllSubRoles() {
		return new HashSet<>();
	}
	
}
