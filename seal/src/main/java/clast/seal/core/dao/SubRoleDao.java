package clast.seal.core.dao;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Query;

import clast.seal.core.model.DirectSubRole;
import clast.seal.core.model.IndirectSubRole;
import clast.seal.core.model.SubRole;
import clast.seal.core.model.SubRoleType;

public class SubRoleDao extends BaseDao {
	
	public SubRoleDao() {
		super();
	}

	public boolean addSubRole(SubRole sr) {
		
		if( sr == null || sr.getRoleId() == null || sr.getSubRoleId() == null ) {
			throw new IllegalArgumentException("Unable to add subRole: IDs of role and subRole cannot be null.");
		}
		
		if( sr.getId() != null ) {
			throw new IllegalArgumentException("Unable to add subRole: it already has an ID.");
		}
		
		if( sr.getRoleId().equals(sr.getSubRoleId()) ) {
			throw new IllegalArgumentException("Unable to add subRole: a role cannot be sub-role of itself.");
		}
		
		if( sr instanceof DirectSubRole && !findSubRoles(null, null, sr.getSubRoleId()).isEmpty() ) {
			throw new IllegalArgumentException("Unable to add subRole: subRole: " + sr.getSubRoleId() + " is already subRole of another role.");
		}
		
		if( sr instanceof IndirectSubRole && !findSubRoles(null, sr.getRoleId(), sr.getSubRoleId()).isEmpty() ) {
			throw new IllegalArgumentException("Unable to add subRole: a relation between role: " + sr.getRoleId() + " and subRole: " + sr.getSubRoleId() + " already exist.");
		}
		
		em.persist(sr);
		return true;
	}

	@SuppressWarnings("unchecked")
	public Set<SubRole> findSubRoles(SubRoleType type, String roleId, String subRoleId) {
		
		if( type == null && roleId == null && subRoleId == null ) {
			throw new IllegalArgumentException("Unable to find subRoles: at least one search field must be specified.");
		}
		
		if( type != null && roleId == null && subRoleId == null ) {
			Query q = em.createQuery("select sr from " + type.getQueryValue() + " sr");
			return new HashSet<>(q.getResultList());
		}else if( type != null && roleId != null && subRoleId == null ) {
			Query q = em.createQuery("select sr from " + type.getQueryValue() + " sr where sr.roleId = :roleId");
			q.setParameter("roleId", roleId);
			return new HashSet<>(q.getResultList());
		}else if( type != null && roleId == null && subRoleId != null ) {
			Query q = em.createQuery("select sr from " + type.getQueryValue() + " sr where sr.subRoleId = :subRoleId");
			q.setParameter("subRoleId", subRoleId);
			return new HashSet<>(q.getResultList());
		}else if( type != null && roleId != null && subRoleId != null ) {
			Query q = em.createQuery("select sr from " + type.getQueryValue() + " sr where sr.roleId = :roleId and sr.subRoleId = :subRoleId");
			q.setParameter("roleId", roleId);
			q.setParameter("subRoleId", subRoleId);
			return new HashSet<>(q.getResultList());
		}else if( type == null && roleId != null && subRoleId == null ) {
			Query q = em.createQuery("select sr from SubRole sr where sr.roleId = :roleId");
			q.setParameter("roleId", roleId);
			return new HashSet<>(q.getResultList());
		}else if( type == null && roleId == null && subRoleId != null ) {
			Query q = em.createQuery("select sr from SubRole sr where sr.subRoleId = :subRoleId");
			q.setParameter("subRoleId", subRoleId);
			return new HashSet<>(q.getResultList());
		}else {
			Query q = em.createQuery("select sr from SubRole sr where sr.roleId = :roleId and sr.subRoleId = :subRoleId");
			q.setParameter("roleId", roleId);
			q.setParameter("subRoleId", subRoleId);
			return new HashSet<>(q.getResultList());
		}
	}

}
