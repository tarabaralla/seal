package clast.seal.core.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	@NotNull
	private String username;

	@NotNull
	private String password;

	private String name;

	@Column(name = "last_name")
	private String lastname;

	private String email;

	private String phone;

	@ManyToMany(cascade= {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;

	public User() {
		this.roles = new HashSet<>();
	}
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
		this.roles = new HashSet<>();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean addRole(Role role) {

		if (this.hasRole(role)) {
			throw new IllegalArgumentException("Passed role already assigned to this user");
		}

		Iterator<Role> iterator = roles.iterator();
		while (iterator.hasNext()) {
			if (role.hasSubRole(iterator.next())) {
				iterator.remove();
			}
		}

		return roles.add(role);
	}

	public boolean removeRole(Role role) {
		
		
		if (this.hasRole(role) && !hasDirectRole(role)) {
			throw new IllegalArgumentException("Only direct role can be removed from users. Role " + role.getName()
					+ " is a sub-role for User " + this.getName());
		} else if (!this.hasRole(role)) {
			throw new IllegalArgumentException(
					"Role " + role.getName() + " to remove isn't assigned to user " + this.getName());
		}

		return roles.remove(role);
	}

	public boolean hasRole(Role role) {
		for (Role r : roles) {
			if (r.getName().equals(role.getName()) || r.hasSubRole(role)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasDirectRole(Role role) {
		List<String> roleNames = roles.stream()
				.map(r -> r.getName())
				.collect(Collectors.toList());
		return roleNames.contains(role.getName());
	}

	public Set<Role> getDirectRoles() {
		return Collections.unmodifiableSet(roles);
	}

	public Set<Role> getAllRoles() {

		Set<Role> allRoles = new HashSet<>(roles);

		for (Role role : roles) {
			allRoles.addAll(role.getAllSubRoles());
		}

		return allRoles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
