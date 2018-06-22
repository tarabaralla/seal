package clast.seal.core.dao;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Query;

import clast.seal.core.model.DirectSubRoleRelation;
import clast.seal.core.model.IndirectSubRoleRelation;
import clast.seal.core.model.SubRoleRelation;
import clast.seal.core.model.SubRoleRelationType;

public class SubRoleRelationDao extends BaseDao {
	
	public SubRoleRelationDao() {
		super();
	}
	
	public boolean createSubRoleRelation(DirectSubRoleRelation subRole) {
		
		try {
			
			checkSubRoleRelation(subRole);
				
			Set<IndirectSubRoleRelation> indirectSubRoleRelations = new HashSet<>();
			
			Set<String> roleAncestorsIds = findSubRoleRelations(null, null, subRole.getRoleId())
					.stream()
					.map( sr -> sr.getRoleId())
					.collect(Collectors.toSet());
			
			Set<String> subRoleDescendantsIds = findSubRoleRelations(null, subRole.getSubRoleId(), null)
					.stream()
					.map( sr -> sr.getSubRoleId())
					.collect(Collectors.toSet());
			
			for(String roleAncestorId : roleAncestorsIds) {
				IndirectSubRoleRelation currentIndSubRole = new IndirectSubRoleRelation(roleAncestorId, subRole.getSubRoleId());
				checkSubRoleRelation(currentIndSubRole);
				indirectSubRoleRelations.add(currentIndSubRole);
			}
			
			for( String subRoleDescendantId : subRoleDescendantsIds ) {
				IndirectSubRoleRelation currentIndSubRole = new IndirectSubRoleRelation(subRole.getRoleId(), subRoleDescendantId);
				checkSubRoleRelation(currentIndSubRole);
				indirectSubRoleRelations.add(currentIndSubRole);
			}
			
			for( String roleAncestorId : roleAncestorsIds ) {
				for( String subRoleDescendantId : subRoleDescendantsIds ) {
					IndirectSubRoleRelation currentIndSubRole = new IndirectSubRoleRelation(roleAncestorId, subRoleDescendantId);
					checkSubRoleRelation(currentIndSubRole);
					indirectSubRoleRelations.add(currentIndSubRole);
				}
			}
			
			em.persist(subRole);
			for( IndirectSubRoleRelation indirectSubRoleRelation : indirectSubRoleRelations ) {
				em.persist(indirectSubRoleRelation);
			}
			
			return true;
			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to create subRole relation: " + e.getMessage(), e);
		}
		
	}

	void checkSubRoleRelation(SubRoleRelation sr) {
		
		if( sr == null || sr.getRoleId() == null || sr.getSubRoleId() == null ) {
			throw new IllegalArgumentException("IDs of role and subRole cannot be null.");
		}
		
		if( sr.getId() != null ) {
			throw new IllegalArgumentException("It already has an ID.");
		}
		
		if( sr.getRoleId().equals(sr.getSubRoleId()) ) {
			throw new IllegalArgumentException("A role cannot be sub-role of itself.");
		}
		
		if( sr instanceof DirectSubRoleRelation && !findSubRoleRelations(null, null, sr.getSubRoleId()).isEmpty() ) {
			throw new IllegalArgumentException("SubRoleRelation: " + sr.getSubRoleId() + " is already subRole of another role.");
		}
		
		if( sr instanceof IndirectSubRoleRelation && !findSubRoleRelations(null, sr.getRoleId(), sr.getSubRoleId()).isEmpty() ) {
			throw new IllegalArgumentException("A relation between role: " + sr.getRoleId() + " and subRole: " + sr.getSubRoleId() + " already exist.");
		}
		
		if( !findSubRoleRelations(null, sr.getSubRoleId(), sr.getRoleId()).isEmpty() ) {
			throw new IllegalArgumentException("Cyclic relations are not allowed.");
		}

	}

	@SuppressWarnings("unchecked")
	Set<SubRoleRelation> findSubRoleRelations(SubRoleRelationType type, String roleId, String subRoleId) {
		
		if( type == null && roleId == null && subRoleId == null ) {
			throw new IllegalArgumentException("Unable to find subRole relations: At least one search field must be specified.");
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
			Query q = em.createQuery("select sr from SubRoleRelation sr where sr.roleId = :roleId");
			q.setParameter("roleId", roleId);
			return new HashSet<>(q.getResultList());
		}else if( type == null && roleId == null && subRoleId != null ) {
			Query q = em.createQuery("select sr from SubRoleRelation sr where sr.subRoleId = :subRoleId");
			q.setParameter("subRoleId", subRoleId);
			return new HashSet<>(q.getResultList());
		}else {
			Query q = em.createQuery("select sr from SubRoleRelation sr where sr.roleId = :roleId and sr.subRoleId = :subRoleId");
			q.setParameter("roleId", roleId);
			q.setParameter("subRoleId", subRoleId);
			return new HashSet<>(q.getResultList());
		}
	}

}
