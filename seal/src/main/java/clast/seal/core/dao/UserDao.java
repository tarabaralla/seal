package clast.seal.core.dao;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import clast.seal.core.model.User;
import clast.seal.core.model.UserRoleRelation;

public class UserDao  extends BaseDao {
	
	private UserRoleRelationDao userRoleRelationDao;
	
	public UserDao() {
		super();
		userRoleRelationDao = new UserRoleRelationDao();
	}
	
	public boolean createUser(User user) {
		
		try {
			
			checkNewUser(user);
			
			em.persist(user);
			
			return true;
			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to create user: " + e.getMessage(), e);
		}
	}

	private void checkNewUser(User user) {
		
		if( user == null ) {
			throw new IllegalArgumentException("User cannot be null.");
		}
		
		if( user.getId() != null ) {
			throw new IllegalArgumentException("User is already persisted.");
		}
		
		if( user.getUsername() == null ) {
			throw new IllegalArgumentException("Username cannot be null.");
		}
		
		if( user.getPassword() == null ) {
			throw new IllegalArgumentException("Password cannot be null.");
		}

		if( findUserByUsername(user.getUsername()) != null ) {
			throw new IllegalArgumentException("Username " + user.getUsername() + " is already assigned to another user.");
		}
		
	}
	
	public boolean deleteUser(User user) {
		
		User u;
		try {
			u = em.find(User.class, user.getId());			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to delete user: user not found.");
		}
		
		if( u == null ) {
			throw new IllegalArgumentException("Unable to delete user: user with ID " + user.getId() + " not found.");
		}
		
		em.getTransaction().begin();
		for( UserRoleRelation urr : userRoleRelationDao.findUserRoleRelations(user.getId(), null) ) {
			em.remove(urr);
		}
		em.remove(u);
		em.getTransaction().commit();			
		
		return true;
	}
	
	public boolean updateUser(User user) {
		
		try {
			
			User u = checkOldUserForUpdate(user);
			
			u.setUsername(user.getUsername());
			u.setPassword(user.getPassword());
			u.setName(user.getName());
			u.setLastname(user.getLastname());
			u.setEmail(user.getEmail());
			u.setPhone(user.getPhone());
			
			em.getTransaction().begin();
			em.merge(u);
			em.getTransaction().commit();
			
			return true;
			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to update user: " + e.getMessage(), e);
		}
	}
	

	private User checkOldUserForUpdate(User user) {
		
		if( user == null ) {
			throw new IllegalArgumentException("User cannot be null.");
		}
		
		if( user.getId() == null ) {
			throw new IllegalArgumentException("User not persisted.");
		}
		
		User u = em.find(User.class, user.getId());			
		
		if( u == null ) {
			throw new IllegalArgumentException("User with ID " + user.getId() + " not found.");
		}
		
		if( user.getUsername() == null ) {
			throw new IllegalArgumentException("Username cannot be null.");
		}
		
		if( user.getPassword() == null ) {
			throw new IllegalArgumentException("Password cannot be null.");
		}

		if( !u.getUsername().equals(user.getUsername()) && findUserByUsername(user.getUsername()) != null ) {
			throw new IllegalArgumentException("Username " + user.getUsername() + " is already assigned to another user.");
		}
		
		return u;
		
	}
	
	public Set<User> findAllUsers(){
		TypedQuery<User> q = em.createQuery("select u from User u", User.class);
		return new HashSet<>(q.getResultList());
	}
	
	public Set<User> findUsersByFreeSearch(String text){
		
		if( text == null || "".equals(text) ) {
			throw new IllegalArgumentException("Unable to find users: text of search cannot be empty.");
		}
		
		TypedQuery<User> q = em.createQuery("select u from User u where u.username like :text or "
				+ "u.name like :text or "
				+ "u.lastname like :text or "
				+ "u.email like :text or "
				+ "u.phone like :text", User.class);
		q.setParameter("text", "%" + text + "%");
		
		return new HashSet<>(q.getResultList());
	}

	public User findUserByUsername(String username) {
		
		if( username == null ) {
			throw new IllegalArgumentException("Unable to find user: username cannot be null.");
		}
		
		try {
			TypedQuery<User> q = em.createQuery("select u from User u where u.username = :username", User.class);
			q.setParameter("username", username);
			return q.getSingleResult();
		}catch (NoResultException e) {
			return null;
		}
	}
	
	public User findUserById(String id) {
		if( id == null ) {
			throw new IllegalArgumentException("Unable to find user: user ID cannot be null.");
		}
		
		return em.find(User.class, id);
	}
	
	
}
