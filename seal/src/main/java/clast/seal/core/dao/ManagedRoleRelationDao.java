package clast.seal.core.dao;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Query;

import clast.seal.core.model.ManagedRoleRelation;

public class ManagedRoleRelationDao extends BaseDao {

	public boolean createManagedRelation(ManagedRoleRelation managedRoleRelation) {
		
		try {

			checkNewManagedRoleRelation(managedRoleRelation);
			
			em.persist(managedRoleRelation);
			
			return true;
			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to create managedRole relation: " + e.getMessage(), e);
		}
		
	}
	
	public boolean deleteManagedRelation(ManagedRoleRelation managedRoleRelation) {
		
		Set<ManagedRoleRelation> toDeleteMRR = findManagedRoleRelations(managedRoleRelation.getRoleId(), managedRoleRelation.getManagedRoleId());
		
		if( toDeleteMRR.isEmpty() ) {
			throw new IllegalArgumentException("Unable to delete managedRole relation: Relation between Role: " + managedRoleRelation.getRoleId() + "and ManagedRole:" + managedRoleRelation.getManagedRoleId() + " not exist.");
		}
		
		em.getTransaction().begin();
		em.remove(toDeleteMRR.iterator().next());
		em.getTransaction().commit();
		
		return true;
		
	}

	private void checkNewManagedRoleRelation(ManagedRoleRelation mrr) {
		
		if( mrr == null || mrr.getRoleId() == null || mrr.getManagedRoleId() == null ) {
			throw new IllegalArgumentException("IDs of role and managedRole cannot be null.");
		}
		
		if( mrr.getId() != null ) {
			throw new IllegalArgumentException("Passed ManagedRoleRelation already persisted.");
		}
		
		if( !findManagedRoleRelations(mrr.getRoleId(), mrr.getManagedRoleId()).isEmpty() ) {
			throw new IllegalArgumentException("Role: " + mrr.getManagedRoleId() + " is already managed from Role: " + mrr.getRoleId());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public Set<ManagedRoleRelation> findManagedRoleRelations(String roleId, String managedRoleId) {
		
		if( roleId == null && managedRoleId == null ) {
			Query q = em.createQuery("select mrr from ManagedRoleRelation mrr");
			return new HashSet<>(q.getResultList());
		} else if( roleId != null && managedRoleId == null ) {
			Query q = em.createQuery("select mrr from ManagedRoleRelation mrr where mrr.roleId = :roleId");
			q.setParameter("roleId", roleId);
			return new HashSet<>(q.getResultList());
		}else if( roleId == null && managedRoleId != null ) {
			Query q = em.createQuery("select mrr from ManagedRoleRelation mrr where mrr.managedRoleId = :managedRoleId");
			q.setParameter("managedRoleId", managedRoleId);
			return new HashSet<>(q.getResultList());
		}else {
			Query q = em.createQuery("select mrr from ManagedRoleRelation mrr where mrr.roleId = :roleId and mrr.managedRoleId = :managedRoleId");
			q.setParameter("roleId", roleId);
			q.setParameter("managedRoleId", managedRoleId);
			return new HashSet<>(q.getResultList());
		}
	}

}
