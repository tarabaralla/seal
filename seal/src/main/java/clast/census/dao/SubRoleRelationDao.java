package clast.census.dao;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Query;

import clast.census.model.DirectSubRoleRelation;
import clast.census.model.IndirectSubRoleRelation;
import clast.census.model.SubRoleRelation;
import clast.census.model.SubRoleRelationType;

public class SubRoleRelationDao extends BaseDao {
	
	private RoleDao roleDao;
	
	public SubRoleRelationDao(RoleDao roleDao) {
		super();
		this.roleDao = roleDao;
	}
	
	public boolean createSubRoleRelation(DirectSubRoleRelation subRoleRelation) {
		
		try {
			
			checkNewSubRoleRelation(subRoleRelation);
				
			Set<IndirectSubRoleRelation> indirectSubRoleRelations = new HashSet<>();
			
			Set<String> roleAncestorsIds = findSubRoleRelations(null, null, subRoleRelation.getRoleId())
					.stream()
					.map( srr -> srr.getRoleId())
					.collect(Collectors.toSet());
			
			Set<String> subRoleDescendantsIds = findSubRoleRelations(null, subRoleRelation.getSubRoleId(), null)
					.stream()
					.map( srr -> srr.getSubRoleId())
					.collect(Collectors.toSet());
			
			for(String roleAncestorId : roleAncestorsIds) {
				IndirectSubRoleRelation currentIndSubRoleRelation = new IndirectSubRoleRelation(roleAncestorId, subRoleRelation.getSubRoleId());
				checkNewSubRoleRelation(currentIndSubRoleRelation);
				indirectSubRoleRelations.add(currentIndSubRoleRelation);
			}
			
			for( String subRoleDescendantId : subRoleDescendantsIds ) {
				IndirectSubRoleRelation currentIndSubRoleRelation = new IndirectSubRoleRelation(subRoleRelation.getRoleId(), subRoleDescendantId);
				checkNewSubRoleRelation(currentIndSubRoleRelation);
				indirectSubRoleRelations.add(currentIndSubRoleRelation);
			}
			
			for( String roleAncestorId : roleAncestorsIds ) {
				for( String subRoleDescendantId : subRoleDescendantsIds ) {
					IndirectSubRoleRelation currentIndSubRoleRelation = new IndirectSubRoleRelation(roleAncestorId, subRoleDescendantId);
					checkNewSubRoleRelation(currentIndSubRoleRelation);
					indirectSubRoleRelations.add(currentIndSubRoleRelation);
				}
			}
			getEntityManager().getTransaction().begin();
			getEntityManager().persist(subRoleRelation);
			for( IndirectSubRoleRelation indirectSubRoleRelation : indirectSubRoleRelations ) {
				getEntityManager().persist(indirectSubRoleRelation);
			}
			getEntityManager().getTransaction().commit();
			
			return true;
			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to create subRole relation: " + e.getMessage(), e);
		}
		
	}
	
	public boolean deleteSubRoleRelation(DirectSubRoleRelation subRoleRelation) {
		
		if( subRoleRelation == null || subRoleRelation.getRoleId() == null || subRoleRelation.getSubRoleId() == null ) {
			throw new IllegalArgumentException("Unable to delete subRole relation: IDs of role and subRole cannot be null.");
		}
		
		if( findSubRoleRelations(SubRoleRelationType.DIRECT, subRoleRelation.getRoleId(), subRoleRelation.getSubRoleId()).isEmpty() ) {
			throw new IllegalArgumentException("Unable to delete subRole relation: Direct relation between Role: " + subRoleRelation.getRoleId() + "and SubRole:" + subRoleRelation.getSubRoleId() + " not exist.");
		}
		
		Set<SubRoleRelation> subRoleAncestorsRelations = findSubRoleRelations(null, null, subRoleRelation.getSubRoleId());
		
		Set<String> subRoleDescendantsIds = findSubRoleRelations(null, subRoleRelation.getSubRoleId(), null)
				.stream()
				.map( srr -> srr.getSubRoleId())
				.collect(Collectors.toSet());
		
		Set<SubRoleRelation> toDeleteRelations = new HashSet<>();
		for( SubRoleRelation subRoleAncestorRelation : subRoleAncestorsRelations ) {
			for( String subRoleDescendantId : subRoleDescendantsIds ) {
				toDeleteRelations.addAll( findSubRoleRelations(SubRoleRelationType.INDIRECT, subRoleAncestorRelation.getRoleId(), subRoleDescendantId));
			}
		}
		
		getEntityManager().getTransaction().begin();
		for( SubRoleRelation currentSubRoleRelation : subRoleAncestorsRelations ) {
			getEntityManager().remove(currentSubRoleRelation);
		}
		for( SubRoleRelation currentSubRoleRelation : toDeleteRelations ) {
			getEntityManager().remove(currentSubRoleRelation);
		}
		getEntityManager().getTransaction().commit();
		
		return true;
		
	}

	@SuppressWarnings("unchecked")
	public Set<SubRoleRelation> findSubRoleRelations(SubRoleRelationType type, String roleId, String subRoleId) {
		
		if( type == null && roleId == null && subRoleId == null ) {
			throw new IllegalArgumentException("Unable to find subRole relations: At least one search field must be specified.");
		}
		
		if( type != null && roleId == null && subRoleId == null ) {
			Query q = getEntityManager().createQuery("select srr from " + type.getQueryValue() + " srr");
			return new HashSet<>(q.getResultList());
		}else if( type != null && roleId != null && subRoleId == null ) {
			Query q = getEntityManager().createQuery("select srr from " + type.getQueryValue() + " srr where srr.roleId = :roleId");
			q.setParameter("roleId", roleId);
			return new HashSet<>(q.getResultList());
		}else if( type != null && roleId == null && subRoleId != null ) {
			Query q = getEntityManager().createQuery("select srr from " + type.getQueryValue() + " srr where srr.subRoleId = :subRoleId");
			q.setParameter("subRoleId", subRoleId);
			return new HashSet<>(q.getResultList());
		}else if( type != null && roleId != null && subRoleId != null ) {
			Query q = getEntityManager().createQuery("select srr from " + type.getQueryValue() + " srr where srr.roleId = :roleId and srr.subRoleId = :subRoleId");
			q.setParameter("roleId", roleId);
			q.setParameter("subRoleId", subRoleId);
			return new HashSet<>(q.getResultList());
		}else if( type == null && roleId != null && subRoleId == null ) {
			Query q = getEntityManager().createQuery("select srr from SubRoleRelation srr where srr.roleId = :roleId");
			q.setParameter("roleId", roleId);
			return new HashSet<>(q.getResultList());
		}else if( type == null && roleId == null && subRoleId != null ) {
			Query q = getEntityManager().createQuery("select srr from SubRoleRelation srr where srr.subRoleId = :subRoleId");
			q.setParameter("subRoleId", subRoleId);
			return new HashSet<>(q.getResultList());
		}else {
			Query q = getEntityManager().createQuery("select srr from SubRoleRelation srr where srr.roleId = :roleId and srr.subRoleId = :subRoleId");
			q.setParameter("roleId", roleId);
			q.setParameter("subRoleId", subRoleId);
			return new HashSet<>(q.getResultList());
		}
	}
	
	private void checkNewSubRoleRelation(SubRoleRelation srr) {
		
		if( srr == null || srr.getRoleId() == null || srr.getSubRoleId() == null ) {
			throw new IllegalArgumentException("IDs of role and subRole cannot be null.");
		}
		
		if( srr.getId() != null ) {
			throw new IllegalArgumentException("Passed SubRoleRelation already persisted.");
		}
		
		if( roleDao.findRoleById(srr.getRoleId()) == null || roleDao.findRoleById(srr.getSubRoleId()) == null ) {
			throw new IllegalArgumentException("Role and SubRole must be already persisted.");
		}
		
		if( srr.getRoleId().equals(srr.getSubRoleId()) ) {
			throw new IllegalArgumentException("A role cannot be sub-role of itself.");
		}
		
		if( srr instanceof DirectSubRoleRelation && !findSubRoleRelations(null, null, srr.getSubRoleId()).isEmpty() ) {
			throw new IllegalArgumentException("SubRoleRelation: " + srr.getSubRoleId() + " is already subRole of another role.");
		}
		
		if( !findSubRoleRelations(null, srr.getSubRoleId(), srr.getRoleId()).isEmpty() ) {
			throw new IllegalArgumentException("Cyclic relations are not allowed.");
		}
		
	}

}
