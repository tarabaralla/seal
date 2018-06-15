package clast.seal.mongo.service;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import clast.seal.core.model.Role;
import clast.seal.core.service.role.RoleService;

public class MongoRoleService extends MongoService implements RoleService {
	
	private MongoRoleService() {
		super();
	}
	
	@Override
	public boolean createRole(Role role) {
		
		if( role.getId() != null && em.find(Role.class, role.getId()) != null ) {
			throw new IllegalArgumentException("Cannot create role: a role with same id already exist." );
		}
		
		if( role.getName() != null ) {
			Query q = em.createQuery("select r from Role r where r.name = :name");
			q.setParameter("name", role.getName());
			
			if( !q.getResultList().isEmpty() ) {
				throw new IllegalArgumentException("Cannot create role: name already assigned to another role." );
			}
		}
		
		em.persist(role);
		return true;
	}
	
	@Override
	public Set<Role> findAllRoles() {
		TypedQuery<Role> q = em.createQuery("select r from Role r", Role.class);
		return new HashSet<>(q.getResultList());
	}

}
