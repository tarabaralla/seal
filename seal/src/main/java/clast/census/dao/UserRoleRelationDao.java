package clast.census.dao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Query;

import clast.census.model.UserRoleRelation;

public class UserRoleRelationDao extends BaseDao {
	
	private UserDao userDao;
	private RoleDao roleDao;
	
	public UserRoleRelationDao(UserDao userDao, RoleDao roleDao) {
		super();
		this.userDao = userDao;
		this.roleDao = roleDao;
	}

	public boolean createUserRoleRelation(UserRoleRelation userRoleRelation) {
		
		try {
			
			checkNewUserRoleRelation(userRoleRelation);
			
			Iterator<UserRoleRelation> iterator = findUserRoleRelations(userRoleRelation.getUserId(), null).iterator();
			
			getEntityManager().getTransaction().begin();
			while( iterator.hasNext() ) {
				UserRoleRelation urr = iterator.next();
				if( roleDao.hasSubRole(roleDao.findRoleById(userRoleRelation.getRoleId()), roleDao.findRoleById(urr.getRoleId())) ) {
					getEntityManager().remove(urr);
				}
			}
			
			getEntityManager().persist(userRoleRelation);

			getEntityManager().getTransaction().commit();
			
			
			return true;
			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to create user-role relation: " + e.getMessage(), e);
		}
		
	}
	
	public boolean deleteUserRoleRelation(UserRoleRelation userRoleRelation) {
		
		if( userRoleRelation == null || userRoleRelation.getUserId() == null || userRoleRelation.getRoleId() == null ) {
			throw new IllegalArgumentException("Unable to delete user-role relation: IDs of user and role cannot be null.");
		}
		
		Set<UserRoleRelation> urrToDelete = findUserRoleRelations(userRoleRelation.getUserId(), userRoleRelation.getRoleId());
		
		if( urrToDelete.isEmpty() ) {
			throw new IllegalArgumentException("Unable to delete user-role relation: Relation between User: " + userRoleRelation.getUserId() + " and Role: " + userRoleRelation.getRoleId() + " not exist.");
		}
		
		getEntityManager().getTransaction().begin();
		getEntityManager().remove(urrToDelete.iterator().next());
		getEntityManager().getTransaction().commit();
		
		return true;
		
	}

	@SuppressWarnings("unchecked")
	public Set<UserRoleRelation> findUserRoleRelations(String userId, String roleId) {
		
		if( userId == null && roleId == null ) {
			Query q = getEntityManager().createQuery("select urr from UserRoleRelation urr");
			return new HashSet<>(q.getResultList());
		} else if( userId != null && roleId == null ) {
			Query q = getEntityManager().createQuery("select urr from UserRoleRelation urr where urr.userId = :userId");
			q.setParameter("userId", userId);
			return new HashSet<>(q.getResultList());
		}else if( userId == null && roleId != null ) {
			Query q = getEntityManager().createQuery("select urr from UserRoleRelation urr where urr.roleId = :roleId");
			q.setParameter("roleId", roleId);
			return new HashSet<>(q.getResultList());
		}else {
			Query q = getEntityManager().createQuery("select urr from UserRoleRelation urr where urr.userId = :userId and urr.roleId = :roleId");
			q.setParameter("userId", userId);
			q.setParameter("roleId", roleId);
			return new HashSet<>(q.getResultList());
		}
	}
	
	private void checkNewUserRoleRelation(UserRoleRelation urr) {
		
		if( urr == null || urr.getUserId() == null || urr.getRoleId() == null ) {
			throw new IllegalArgumentException("IDs of user and role to associate cannot be null.");
		}
		
		if( urr.getId() != null ) {
			throw new IllegalArgumentException("UserRoleRelation is already persisted.");
		}
		
		if( userDao.findUserById(urr.getUserId()) == null || roleDao.findRoleById(urr.getRoleId()) == null) {
			throw new IllegalArgumentException("User and Role to associate must already be persisted.");
		}
		
		if( !findUserRoleRelations(urr.getUserId(), urr.getRoleId()).isEmpty() ) {
			throw new IllegalArgumentException("Role: " + urr.getRoleId() + " is already assigned to User: " + urr.getUserId());
		}
		
		Set<String> userRolesIds = findUserRoleRelations(urr.getUserId(), null).stream().map( r -> r.getRoleId() ).collect(Collectors.toSet());
		for( String roleId : userRolesIds ) {
			if( roleDao.hasSubRole( roleDao.findRoleById(roleId), roleDao.findRoleById(urr.getRoleId())) ) {
				throw new IllegalArgumentException("Role: " + urr.getRoleId() + " is SubRole of Role: " + roleId + ", already assigned to User: " + urr.getUserId());
			}
		}
	}
	
}
