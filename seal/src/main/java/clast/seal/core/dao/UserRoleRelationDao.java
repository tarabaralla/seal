package clast.seal.core.dao;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Query;

import clast.seal.core.model.UserRoleRelation;

public class UserRoleRelationDao extends BaseDao {

	public boolean createUserRoleRelation(UserRoleRelation userRoleRelation) {
		
		try {
			
			checkNewUserRoleRelation(userRoleRelation);
			
			//TODO rimuovere tutti i ruoli figli precedentementente assegnati all'utente prima di aggiungere il ruolo padre
			
			em.persist(userRoleRelation);
			
			return true;
			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to create user-role relation: " + e.getMessage(), e);
		}
		
	}

	private void checkNewUserRoleRelation(UserRoleRelation urr) {
		
		if( urr == null || urr.getUserId() == null || urr.getRoleId() == null ) {
			throw new IllegalArgumentException("IDs of user and role to associate cannot be null.");
		}
		
		if( urr.getId() != null ) {
			throw new IllegalArgumentException("UserRoleRelation is already persisted.");
		}
		
		if( !findUserRoleRelations(urr.getUserId(), urr.getRoleId()).isEmpty() ) {
			throw new IllegalArgumentException("Role: " + urr.getRoleId() + " is already assigned to User: " + urr.getUserId());
		}
		
		//TODO aggiungere controllo ruolo padre già assegnato all'utente
		
	}

	@SuppressWarnings("unchecked")
	public Set<UserRoleRelation> findUserRoleRelations(String userId, String roleId) {
		
		if( userId == null && roleId == null ) {
			Query q = em.createQuery("select urr from UserRoleRelation urr");
			return new HashSet<>(q.getResultList());
		} else if( userId != null && roleId == null ) {
			Query q = em.createQuery("select urr from UserRoleRelation urr where urr.userId = :userId");
			q.setParameter("userId", userId);
			return new HashSet<>(q.getResultList());
		}else if( userId == null && roleId != null ) {
			Query q = em.createQuery("select urr from UserRoleRelation urr where urr.roleId = :roleId");
			q.setParameter("roleId", roleId);
			return new HashSet<>(q.getResultList());
		}else {
			Query q = em.createQuery("select urr from UserRoleRelation urr where urr.userId = :userId and urr.roleId = :roleId");
			q.setParameter("userId", userId);
			q.setParameter("roleId", roleId);
			return new HashSet<>(q.getResultList());
		}
	}
}
