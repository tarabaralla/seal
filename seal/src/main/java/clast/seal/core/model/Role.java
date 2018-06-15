package clast.seal.core.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING, name="type")
@Table(name = "roles")
public abstract class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@NotNull
	private String name;

	@ManyToMany(cascade= {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "managed_roles", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "managed_role_id"))
	private Set<Role> managedRoles;
	
	public Role() {
		managedRoles = new HashSet<>();
	}
	
	public Role(String name) {
		this.name = name;
		managedRoles = new HashSet<>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		return getAllManagedRoles().stream()
									.map( r -> r.getName() )
									.collect(Collectors.toList())
									.contains(role.getName());
	}

	public Set<Role> getDirectManagedRoles() {
		return Collections.unmodifiableSet(managedRoles);
	}

	public Set<Role> getAllManagedRoles() {
		Set<Role> allManagedRoles = new HashSet<>(managedRoles);

		for (Role subRole : getAllSubRoles()) {
			allManagedRoles.addAll(subRole.getAllManagedRoles());
		}

		return Collections.unmodifiableSet(allManagedRoles);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Role))
			return false;
		Role other = (Role) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
