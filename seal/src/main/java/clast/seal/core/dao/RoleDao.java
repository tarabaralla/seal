package clast.seal.core.dao;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import clast.seal.core.model.Role;

public class RoleDao extends BaseDao{
	
	public RoleDao() {
		super();
	}

	public boolean createRole(Role role) {
		
		if( role.getId() != null ) {
			throw new IllegalArgumentException("Unable to create role: it already has an ID.");
		}
		
		if( role.getName() == null ) {
			throw new IllegalArgumentException("Unable to create role: role name cannot be null.");
		}

		if( findRoleByName(role.getName()) != null ) {
			throw new IllegalArgumentException("Unable to create role: name is already assigned to another role.");
		}
		
		em.persist(role);
		return true;
	}
	
	public boolean deleteRole(Role role) {
		
		Role r;
		try {
			r = em.find(Role.class, role.getId());			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to delete role: role not found.");
		}
		
		if( r == null ) {
			throw new IllegalArgumentException("Unable to delete role: role with ID " + role.getId() + " not found.");
		}
		
		em.getTransaction().begin();
		em.remove(r);
		em.getTransaction().commit();			
		
		return true;
	}

	public boolean updateRoleName(String roleId, String name) {
		
		Role role = findRoleById(roleId);
		
		if( role == null ) {
			throw new IllegalArgumentException("Unable to update role: role with ID " + roleId + " not found.");
		}
		
		role.setName(name);

		em.getTransaction().begin();
		em.merge(role);
		em.getTransaction().commit();
		
		return true;
	}
	
	public Role findRoleByName(String name) {
		
		if( name == null ) {
			throw new IllegalArgumentException("Unable to find role: role name cannot be null.");
		}
		
		try {
			TypedQuery<Role> q = em.createQuery("select r from Role r where r.name = :name", Role.class);
			q.setParameter("name", name);
			return q.getSingleResult();
		}catch (NoResultException e) {
			return null;
		}
	}

	public Role findRoleById(String id) {
		
		if( id == null ) {
			throw new IllegalArgumentException("Unable to find role: role ID cannot be null.");
		}
		
		try {
			return em.find(Role.class, id);
		}catch (Exception e) {
			return null;
		}
	}

	public Set<Role> findAllRoles() {
		TypedQuery<Role> q = em.createQuery("select r from Role r", Role.class);
		return new HashSet<>(q.getResultList());
	}

	public boolean addSubRole(Role role, Role subRole) {
		return true;
	}

}
