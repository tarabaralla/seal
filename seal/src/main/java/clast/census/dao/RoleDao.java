package clast.census.dao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import clast.census.model.DirectSubRoleRelation;
import clast.census.model.ManagedRoleRelation;
import clast.census.model.Role;
import clast.census.model.SubRoleRelation;
import clast.census.model.SubRoleRelationType;

public class RoleDao extends BaseDao{
	
	private SubRoleRelationDao subRoleRelationDao;
	private ManagedRoleRelationDao managedRoleRelationDao;
	
	public RoleDao() {
		super();
		subRoleRelationDao = new SubRoleRelationDao(this);
		managedRoleRelationDao = new ManagedRoleRelationDao(this);
	}

	public boolean createRole(Role role) {
		
		try {

			checkRoleCreation(role);
			
			getEntityManager().getTransaction().begin();
			getEntityManager().persist(role);
			getEntityManager().getTransaction().commit();
			
			return true;
			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to create role: " + e.getMessage(), e);
		}
	}

	private void checkRoleCreation(Role role) {
		
		if( role == null ) {
			throw new IllegalArgumentException("Role cannot be null.");
		}
		
		if( role.getId() != null ) {
			throw new IllegalArgumentException("Role is already persisted.");
		}
		
		if( role.getName() == null ) {
			throw new IllegalArgumentException("Role name cannot be null.");
		}

		if( findRoleByName(role.getName()) != null ) {
			throw new IllegalArgumentException("Role name is already assigned to another role.");
		}
	}
	
	public boolean deleteRole(Role role, boolean cascade) {
		
		try {
			
			Role r = checkRoleDeletion(role);
			
			getEntityManager().getTransaction().begin();
			delete(r, cascade);
			getEntityManager().getTransaction().commit();			
			
			return true;
			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to delete role: " + e.getMessage(), e);
		}
	}

	private Role checkRoleDeletion(Role role) {
		
		Role r;
		
		try {
			r = getEntityManager().find(Role.class, role.getId());			
		}catch (Exception e) {
			throw new IllegalArgumentException("role not found.");
		}
		
		if( r == null ) {
			throw new IllegalArgumentException("role with ID " + role.getId() + " not found.");
		}
		
		return r;
	}

	private void delete(Role role, boolean cascade) {
		
		for( ManagedRoleRelation mrr : managedRoleRelationDao.findManagedRoleRelations(role.getId(), null) ) {
			getEntityManager().remove(mrr);
		}
		
		if( cascade ) {
			for( SubRoleRelation srr : subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.DIRECT, role.getId(), null) ) {
				delete( findRoleById(srr.getSubRoleId()), true );
			}
		}
		
		for( SubRoleRelation srr : subRoleRelationDao.findSubRoleRelations(null, role.getId(), null) ) {
			getEntityManager().remove(srr);
		}
		
		getEntityManager().remove(role);
	}
	
	public boolean deleteAllRoles() {
		Iterator<Role> allRolesIterator = findAllRoles().iterator();
		while( allRolesIterator.hasNext() ) {
			deleteRole(allRolesIterator.next(), false);
		}
		return true;
	}

	public boolean updateRoleName(String roleId, String name) {
		
		try {
			
			Role role = checkRoleUpdate(roleId, name);
			
			role.setName(name);
			
			getEntityManager().getTransaction().begin();
			getEntityManager().merge(role);
			getEntityManager().getTransaction().commit();
			
			return true;
			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to update role: " + e.getMessage(), e);
		}
		
	}

	private Role checkRoleUpdate(String roleId, String name) {
		
		Role role = findRoleById(roleId);
		
		if( role == null ) {
			throw new IllegalArgumentException("role with ID " + roleId + " not found.");
		}
		
		if( name == null ) {
			throw new IllegalArgumentException("role name cannot be null.");
		}

		if( findRoleByName(name) != null ) {
			throw new IllegalArgumentException("role name is already assigned to another role.");
		}
		
		return role;
	}
	
	public Set<Role> findAllRoles() {
		TypedQuery<Role> q = getEntityManager().createQuery("select r from Role r", Role.class);
		return new HashSet<>(q.getResultList());
	}
	
	public Set<Role> findRolesByFreeSearch(String text) {
		
		if( text == null || "".equals(text) ) {
			throw new IllegalArgumentException("Unable to find roles: text of search cannot be empty.");
		}
		
		TypedQuery<Role> q = getEntityManager().createQuery("select r from Role r where r.name like :text", Role.class);
		q.setParameter("text", "%" + text + "%");
		
		return new HashSet<>(q.getResultList());
	}
	
	public Role findRoleByName(String name) {
		
		if( name == null ) {
			throw new IllegalArgumentException("Unable to find role: role name cannot be null.");
		}
		
		try {
			TypedQuery<Role> q = getEntityManager().createQuery("select r from Role r where r.name = :name", Role.class);
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
		
		return getEntityManager().find(Role.class, id);
	}

	public boolean addSubRole(Role role, Role subRole) {
		return subRoleRelationDao.createSubRoleRelation( new DirectSubRoleRelation(role.getId(), subRole.getId()) );
	}
	
	public boolean removeSubRole(Role role, Role subRole) {
		return subRoleRelationDao.deleteSubRoleRelation( new DirectSubRoleRelation( role.getId(), subRole.getId()) );
	}
	
	public Set<Role> findAllSubRoles(Role role) {
		return findSubRoles(null, role);
	}
	
	public Set<Role> findDirectSubRoles(Role role) {
		return findSubRoles(SubRoleRelationType.DIRECT, role);
	}
	
	public Set<Role> findIndirectSubRoles(Role role) {
		return findSubRoles(SubRoleRelationType.INDIRECT, role);
	}
	
	private Set<Role> findSubRoles(SubRoleRelationType subRoleRelationType, Role role) {
		if( role == null || role.getId() == null ) {
			throw new IllegalArgumentException("Unable to find sub-roles: role ID cannot be null.");
		}
		Set<SubRoleRelation> subRoleRelations = subRoleRelationDao.findSubRoleRelations(subRoleRelationType, role.getId(), null);
		Set<Role> subRoles = new HashSet<>();
		for( SubRoleRelation subRoleRelation : subRoleRelations ) {
			subRoles.add( findRoleById(subRoleRelation.getSubRoleId()) );
		}
		return subRoles;
	}
	
	public boolean hasSubRole(Role role, Role subRole) {
		return !subRoleRelationDao.findSubRoleRelations(null, role.getId(), subRole.getId()).isEmpty();
	}
	
	public boolean hasDirectSubRole(Role role, Role subRole) {
		return !subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.DIRECT, role.getId(), subRole.getId()).isEmpty();
	}
	
	public boolean hasIndirectSubRole(Role role, Role subRole) {
		return !subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.INDIRECT, role.getId(), subRole.getId()).isEmpty();
	}
	
	public boolean addManagedRole(Role role, Role managedRole) {
		return managedRoleRelationDao.createManagedRoleRelation( new ManagedRoleRelation(role.getId(), managedRole.getId()) );
	}
	
	public boolean removeManagedRole(Role role, Role managedRole) {
		return managedRoleRelationDao.deleteManagedRoleRelation( new ManagedRoleRelation(role.getId(), managedRole.getId()) );
	}
	
	public Set<Role> findAllManagedRoles(Role role) {
		if( role == null || role.getId() == null ) {
			throw new IllegalArgumentException("Unable to find managed roles: role ID cannot be null.");
		}
		Set<Role> managedRoles = findDirectManagedRoles(role);
		managedRoles.addAll(findIndirectManagedRoles(role));
		return managedRoles;
	}
	
	public Set<Role> findDirectManagedRoles(Role role) {
		if( role == null || role.getId() == null ) {
			throw new IllegalArgumentException("Unable to find direct managed roles: role ID cannot be null.");
		}
		Set<ManagedRoleRelation> managedRoleRelations = managedRoleRelationDao.findManagedRoleRelations(role.getId(), null);
		Set<Role> managedRoles = new HashSet<>();
		for( ManagedRoleRelation managedRoleRelation : managedRoleRelations ) {
			managedRoles.add( findRoleById(managedRoleRelation.getManagedRoleId()) );
		}
		return managedRoles;
	}
	
	public Set<Role> findIndirectManagedRoles(Role role) {
		if( role == null || role.getId() == null ) {
			throw new IllegalArgumentException("Unable to find indirect managed roles: role ID cannot be null.");
		}
		Set<Role> managedRoles = new HashSet<>();
		Iterator<Role> iterator = findAllSubRoles(role).iterator();
		while( iterator.hasNext() ) {
			managedRoles.addAll( findAllManagedRoles(iterator.next()) );
		}
		return managedRoles;
	}
	
	public boolean managesRole(Role role, Role managedRole) {
		return findAllManagedRoles(role)
				.stream()
					.map( dmr -> dmr.getId() )
					.collect(Collectors.toSet())
				.contains( managedRole.getId() );
	}
	
	public boolean directlyManagesRole(Role role, Role managedRole) {
		return findDirectManagedRoles(role)
				.stream()
					.map( dmr -> dmr.getId() )
					.collect(Collectors.toSet())
				.contains( managedRole.getId() );
	}
	
	public boolean indirectlyManagesRole(Role role, Role managedRole) {
		
		return findIndirectManagedRoles(role)
				.stream()
					.map( dmr -> dmr.getId() )
					.collect(Collectors.toSet())
					.contains( managedRole.getId() );
	}

}
