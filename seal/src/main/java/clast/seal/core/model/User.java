package clast.seal.core.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import clast.seal.core.persistence.Entity;

public class User extends Entity {
	
	private String username;
	private String password;
	private String name;
	private String lastname;
	private String email;
	private String phone;
	private Set<Role> roles;
	
	public User() {
		super();
		this.roles = new HashSet<>();		
	}

	public User(String uuid) {
		super(uuid);
		this.roles = new HashSet<>();
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
		
		if( this.hasRole(role) ) {
			throw new IllegalArgumentException("Passed role already assigned to this user");
		}
		
		Iterator<Role> iterator = roles.iterator();
		while(iterator.hasNext()) {
			if(role.hasSubRole(iterator.next())) {
				iterator.remove();
			}
		}
		
		return roles.add(role);
	}
	
	public boolean removeRole(Role role) {
		
		if( this.hasRole(role) && !roles.contains(role) ) {
			throw new IllegalArgumentException("Only direct role can be removed from users. Role " + role.getName() + " is a sub-role for User " + this.getName());
		}else if(!this.hasRole(role)) {
			throw new IllegalArgumentException("Role " + role.getName() + " to remove isn't assigned to user " + this.getName());
		}
		
		return roles.remove(role);
	}
	
	public boolean hasRole(Role role) {
		for(Role r : roles) {
			if(r.equals(role) || r.hasSubRole(role)) {
				return true;
			}
		}
		return false;
	}
	
	public Set<Role> getDirectRoles() {
		return Collections.unmodifiableSet(roles);
	}
	
	public Set<Role> getAllRoles() {
		
		Set<Role> allRoles = new HashSet<>(roles);
		
		for(Role role : roles) {
			allRoles.addAll(role.getAllSubRoles());
		}
		
		return allRoles;
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
