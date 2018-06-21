package clast.seal.core.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
@DiscriminatorValue(value = "composite")
public class CompositeRole extends Role {

	@ManyToMany( cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY )
	@JoinTable(name = "sub_roles", joinColumns = @JoinColumn(name = "parent_role_id"), inverseJoinColumns = @JoinColumn(name = "sub_role_id"))
	private Set<Role> subRoles;
	
	public CompositeRole() {
		super();
		subRoles = new HashSet<>();
	}

	public CompositeRole(String name) {
		super(name);
		subRoles = new HashSet<>();
	}

	@Override
	public void addSubRole(Role role) {
		
		if(this.equals(role)) {
			throw new IllegalArgumentException("A role cannot be sub-role of him self");
		}

		if (getAllSubRoles().contains(role)) {
			throw new IllegalArgumentException("A role cannot add a sub-role that is already present in its sub-roles tree");
		}

		for (Role subRole : role.getAllSubRoles()) {
			if (getAllSubRoles().contains(subRole)) {
				throw new IllegalArgumentException("A role cannot add a sub-role that has one or more sub-roles those are already present in its sub-roles tree");
			}
		}

		if (role.getAllSubRoles().contains(this)) {
			throw new IllegalArgumentException("Cyclic relations between roles aren't admitted");
		}

		subRoles.add(role);
	}

	@Override
	public boolean removeSubRole(Role role) {

		if (subRoles.contains(role)) {
			return subRoles.remove(role);
		}

		boolean subRoleRemoved = false;
		Iterator<Role> iterator = getAllSubRoles().iterator();

		while (iterator.hasNext() && !subRoleRemoved) {

			try {
				subRoleRemoved = iterator.next().removeSubRole(role);
			} catch (UnsupportedOperationException e) {
				subRoleRemoved = false;
			}
		}

		return subRoleRemoved;
	}

	@Override
	public boolean hasSubRole(Role role) {
		return getAllSubRoles().contains(role);
	}
	
	@Override
	public Set<Role> getDirectSubRoles() {
		return Collections.unmodifiableSet(subRoles);
	}

	@Override
	public Set<Role> getAllSubRoles() {

		Set<Role> allSubRoles = new HashSet<>(subRoles);

		for (Role subRole : subRoles) {
			allSubRoles.addAll(subRole.getAllSubRoles());
		}

		return Collections.unmodifiableSet(allSubRoles);
	}

}
