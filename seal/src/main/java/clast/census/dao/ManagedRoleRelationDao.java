package clast.census.dao;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Query;

import clast.census.model.ManagedRoleRelation;

public class ManagedRoleRelationDao extends BaseDao {
	
	private RoleDao roleDao;
	
	public ManagedRoleRelationDao(RoleDao roleDao) {
		super();
		this.roleDao = roleDao;
	}

	public boolean createManagedRoleRelation(ManagedRoleRelation managedRoleRelation) {
		
		try {

			checkNewManagedRoleRelation(managedRoleRelation);
			
			getEntityManager().getTransaction().begin();
			getEntityManager().persist(managedRoleRelation);
			getEntityManager().getTransaction().commit();
			
			return true;
			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to create managedRole relation: " + e.getMessage(), e);
		}
		
	}
	
	public boolean deleteManagedRoleRelation(ManagedRoleRelation managedRoleRelation) {
		
		if( managedRoleRelation == null || managedRoleRelation.getRoleId() == null || managedRoleRelation.getManagedRoleId() == null ) {
			throw new IllegalArgumentException("Unable to delete managedRole relation: IDs of role and managedRole cannot be null.");
		}
		
		Set<ManagedRoleRelation> mrrToDelete = findManagedRoleRelations(managedRoleRelation.getRoleId(), managedRoleRelation.getManagedRoleId());
		
		if( mrrToDelete.isEmpty() ) {
			throw new IllegalArgumentException("Unable to delete managedRole relation: Relation between Role: " + managedRoleRelation.getRoleId() + "and ManagedRole:" + managedRoleRelation.getManagedRoleId() + " not exist.");
		}
		
		getEntityManager().getTransaction().begin();
		getEntityManager().remove(mrrToDelete.iterator().next());
		getEntityManager().getTransaction().commit();
		
		return true;
		
	}
	
	@SuppressWarnings("unchecked")
	public Set<ManagedRoleRelation> findManagedRoleRelations(String roleId, String managedRoleId) {
		
		if( roleId == null && managedRoleId == null ) {
			Query q = getEntityManager().createQuery("select mrr from ManagedRoleRelation mrr");
			return new HashSet<>(q.getResultList());
		} else if( roleId != null && managedRoleId == null ) {
			Query q = getEntityManager().createQuery("select mrr from ManagedRoleRelation mrr where mrr.roleId = :roleId");
			q.setParameter("roleId", roleId);
			return new HashSet<>(q.getResultList());
		}else if( roleId == null && managedRoleId != null ) {
			Query q = getEntityManager().createQuery("select mrr from ManagedRoleRelation mrr where mrr.managedRoleId = :managedRoleId");
			q.setParameter("managedRoleId", managedRoleId);
			return new HashSet<>(q.getResultList());
		}else {
			Query q = getEntityManager().createQuery("select mrr from ManagedRoleRelation mrr where mrr.roleId = :roleId and mrr.managedRoleId = :managedRoleId");
			q.setParameter("roleId", roleId);
			q.setParameter("managedRoleId", managedRoleId);
			return new HashSet<>(q.getResultList());
		}
	}
	
	private void checkNewManagedRoleRelation(ManagedRoleRelation mrr) {
		
		if( mrr == null || mrr.getRoleId() == null || mrr.getManagedRoleId() == null ) {
			throw new IllegalArgumentException("IDs of role and managedRole cannot be null.");
		}
		
		if( mrr.getId() != null ) {
			throw new IllegalArgumentException("Passed ManagedRoleRelation already persisted.");
		}
		
		if( roleDao.findRoleById(mrr.getRoleId()) == null || roleDao.findRoleById(mrr.getManagedRoleId()) == null ) {
			throw new IllegalArgumentException("Role and ManagedRole must be already persisted.");
		}
		
		if( !findManagedRoleRelations(mrr.getRoleId(), mrr.getManagedRoleId()).isEmpty() ) {
			throw new IllegalArgumentException("Role: " + mrr.getManagedRoleId() + " is already managed from Role: " + mrr.getRoleId());
		}
		
	}

}
