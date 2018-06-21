package clast.seal.core.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "roles")
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@NotNull
	private String name;
	
	public Role() {
	}
	
	public Role(String name) {
		this.name = name;
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

//	public boolean addManagedRole(Role role) {
//		return managedRoles.add(role);
//	}
//
//	public boolean removeManagedRole(Role role) {
//		return managedRoles.remove(role);
//	}
//
//	public boolean managesRole(Role role) {
//		return getAllManagedRoles().stream()
//									.map( r -> r.getName() )
//									.collect(Collectors.toList())
//									.contains(role.getName());
//	}
//
//	public Set<Role> getDirectManagedRoles() {
//		return Collections.unmodifiableSet(managedRoles);
//	}
//
//	public Set<Role> getAllManagedRoles() {
//		Set<Role> allManagedRoles = new HashSet<>(managedRoles);
//
////		for (Role subRole : getAllSubRoles()) {
////			allManagedRoles.addAll(subRole.getAllManagedRoles());
////		}
//
//		return Collections.unmodifiableSet(allManagedRoles);
//	}

}
