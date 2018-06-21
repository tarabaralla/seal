package clast.seal.mongo.service;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import clast.seal.core.model.CompositeRole;
import clast.seal.core.model.LeafRole;
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
	public boolean deleteRole(Role role) {
		Role r;
		try {
			r = em.find(Role.class, role.getId());			
		}catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Cannot delete role: role id cannot be null.");
		}
		
		if( r == null ) {
			throw new IllegalArgumentException("Cannot delete role: role with id " + role.getId() + " not found.");
		}
		
		em.getTransaction().begin();
		em.remove(r);
		em.getTransaction().commit();			
		
		return true;
	}
	
	@Override
	public Set<Role> findAll() {
		TypedQuery<Role> q = em.createQuery("select r from Role r", Role.class);
		return new HashSet<>(q.getResultList());
	}
	
	@Override
	public Role findByName(String name) {
		try {
			Query q = em.createQuery("select r from CompositeRole r where r.name = :name", CompositeRole.class);
			q.setParameter("name", name);
			return ((CompositeRole)q.getSingleResult());
		}catch (NoResultException e) {
			Query q = em.createQuery("select r from LeafRole r where r.name = :name", LeafRole.class);
			q.setParameter("name", name);
			return ((LeafRole)q.getSingleResult());
		}
	}

}
