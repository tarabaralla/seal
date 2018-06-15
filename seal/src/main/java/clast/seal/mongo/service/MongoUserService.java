package clast.seal.mongo.service;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import clast.seal.core.model.User;
import clast.seal.core.service.user.UserService;

public class MongoUserService extends MongoService implements UserService {
	
	private MongoUserService() {
		super();
	}
	
	@Override
	public boolean createUser(User user) {
		
		if( user.getId() != null && em.find(User.class, user.getId()) != null ) {
			throw new IllegalArgumentException("Cannot create user: a user with same id already exist." );
		}
		
		if( user.getUsername() != null ) {
			Query q = em.createQuery("select u from User u where u.username = :username");
			q.setParameter("username", user.getUsername());
			
			if( !q.getResultList().isEmpty() ) {
				throw new IllegalArgumentException("Cannot create user: username already assigned to another user." );
			}
		}
		
		em.persist(user);
		return true;
	}
	
	@Override
	public Set<User> findAllUsers() {
		TypedQuery<User> q = em.createQuery("select u from User u", User.class);
		return new HashSet<>(q.getResultList() );
	}

}
