package clast.census.dao;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import clast.census.model.Role;
import clast.census.model.User;
import clast.census.model.UserRoleRelation;

public class UserDao  extends BaseDao {
	
	private RoleDao roleDao;
	private UserRoleRelationDao userRoleRelationDao;
	
	public UserDao() {
		super();
		roleDao = new RoleDao();
		userRoleRelationDao = new UserRoleRelationDao(this, roleDao);
	}
	
	public boolean createUser(User user) {
		
		try {
			
			checkNewUser(user);
			
			getEntityManager().getTransaction().begin();
			getEntityManager().persist(user);
			getEntityManager().getTransaction().commit();
			
			
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
			u = getEntityManager().find(User.class, user.getId());			
		}catch (Exception e) {
			throw new IllegalArgumentException("Unable to delete user: user not found.");
		}
		
		if( u == null ) {
			throw new IllegalArgumentException("Unable to delete user: user with ID " + user.getId() + " not found.");
		}
		
		getEntityManager().getTransaction().begin();
		for( UserRoleRelation urr : userRoleRelationDao.findUserRoleRelations(user.getId(), null) ) {
			getEntityManager().remove(urr);
		}
		getEntityManager().remove(u);
		getEntityManager().getTransaction().commit();			
		
		return true;
	}
	
	public boolean deleteAllUsers() {
		Iterator<User> allUsersIterator = findAllUsers().iterator();
		while( allUsersIterator.hasNext() ) {
			deleteUser(allUsersIterator.next());
		}
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
			
			getEntityManager().getTransaction().begin();
			getEntityManager().merge(u);
			getEntityManager().getTransaction().commit();
			
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
		
		getEntityManager().clear();
		User u = getEntityManager().find(User.class, user.getId());			
		
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
		TypedQuery<User> q = getEntityManager().createQuery("select u from User u", User.class);
		return new HashSet<>(q.getResultList());
	}
	
	public Set<User> findUsersByFreeSearch(String text){
		
		if( text == null || "".equals(text) ) {
			throw new IllegalArgumentException("Unable to find users: text of search cannot be empty.");
		}
		
		TypedQuery<User> q = getEntityManager().createQuery("select u from User u where u.username like :text or "
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
			TypedQuery<User> q = getEntityManager().createQuery("select u from User u where u.username = :username", User.class);
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
		
		return getEntityManager().find(User.class, id);
	}
	
	public boolean addRole(User user, Role role) {
		
		if(user == null || role == null ) {
			throw new IllegalArgumentException("Unable to add role: user and role cannot be null.");
		}
		return userRoleRelationDao.createUserRoleRelation( new UserRoleRelation(user.getId(), role.getId()) );
	}
	
	public boolean removeRole(User user, Role role) {
		
		if(user == null || role == null ) {
			throw new IllegalArgumentException("Unable to remove role: user and role cannot be null.");
		}
		return userRoleRelationDao.deleteUserRoleRelation( new UserRoleRelation(user.getId(), role.getId()) );
	}
	
	public Set<Role> findAllRoles(User user) {
		if( user == null || user.getId() == null ) {
			throw new IllegalArgumentException("Unable to find user roles: user must be persisted.");
		}
		Set<Role> roles = findDirectRoles(user);
		roles.addAll(findIndirectRoles(user));
		return roles;
	}

	public Set<Role> findDirectRoles(User user) {
		if( user == null || user.getId() == null ) {
			throw new IllegalArgumentException("Unable to find user direct-roles: user must be persisted.");
		}
		Set<UserRoleRelation> userRoleRelations = userRoleRelationDao.findUserRoleRelations(user.getId(), null);
		Set<Role> directRoles = new HashSet<>();
		for( UserRoleRelation userRoleRelation : userRoleRelations ) {
			directRoles.add( roleDao.findRoleById(userRoleRelation.getRoleId()) );
		}
		return directRoles;
	}
	
	public Set<Role> findIndirectRoles(User user) {
		if( user == null || user.getId() == null ) {
			throw new IllegalArgumentException("Unable to find user indirect-roles: user must be persisted.");
		}
		Set<Role> indirectRoles = new HashSet<>();
		Iterator<Role> iterator = findDirectRoles(user).iterator();
		while ( iterator.hasNext() ) {
			indirectRoles.addAll( roleDao.findAllSubRoles(iterator.next()) );
		}
		return indirectRoles;
	}
	
	public boolean hasRole(User user, Role role) {
		
		if( role == null || role.getId() == null ) {
			throw new IllegalArgumentException("Unable to verify role assignment: role ID cannot be null.");
		}
		
		return findAllRoles(user)
				.stream()
					.map( r -> r.getId() )
					.collect( Collectors.toSet() )
				.contains(role.getId());
	}
	
	public boolean directlyHasRole(User user, Role role) {

		if( role == null || role.getId() == null ) {
			throw new IllegalArgumentException("Unable to verify role assignment: role ID cannot be null.");
		}
		
		return findDirectRoles(user)
				.stream()
					.map( r -> r.getId() )
					.collect( Collectors.toSet() )
				.contains(role.getId());
	}
	
	public boolean indirectlyHasRole(User user, Role role) {

		if( role == null || role.getId() == null ) {
			throw new IllegalArgumentException("Unable to verify role assignment: role ID cannot be null.");
		}
		
		return findIndirectRoles(user)
				.stream()
				.map( r -> r.getId() )
				.collect( Collectors.toSet() )
				.contains(role.getId());
	}
	
	public Set<Role> findManagedRoles(User user) {
		
		if( user == null || user.getId() == null ) {
			throw new IllegalArgumentException("Unable to find managed roles: User ID cannot be null.");
		}
		
		Set<Role> managedRoles = new HashSet<>();
		for( Role role : findAllRoles(user) ) {
			managedRoles.addAll( roleDao.findAllManagedRoles(role) );
		}
		return managedRoles;
	}
	
	public Set<User> findManagedUsers(User user) {
		
		if( user == null || user.getId() == null ) {
			throw new IllegalArgumentException("Unable to find managed users: User ID cannot be null.");
		}
		
		Set<User> managedUsers = new HashSet<>();
		Set<Role> managedRoles = findManagedRoles(user);
		Iterator<Role> mrIterator = findManagedRoles(user).iterator();
		while( mrIterator.hasNext() ) {
			managedRoles.addAll(roleDao.findAllSubRoles(mrIterator.next()));
		}
		for( Role role : managedRoles ) {
			for( UserRoleRelation urr : userRoleRelationDao.findUserRoleRelations(null, role.getId()) ) {
				managedUsers.add( findUserById(urr.getUserId()) );
			}
		}
		return managedUsers;
	}
	
	public Set<User> findManagedUsersByRole(User user, Role role) {
		
		if( user == null || user.getId() == null || role == null || role.getId() == null ) {
			throw new IllegalArgumentException("Unable to find managed users by role: IDs of user and role cannot be null.");
		}

		if( !userHasRole(user, role) ) {
			throw new IllegalArgumentException("Unable to find managed users by role: User: " + user.getId() + " not has Role: " + role.getId());
		}
		
		Set<User> managedUsers = new HashSet<>();
		Set<Role> managedRoles = roleDao.findAllManagedRoles(role);
		Iterator<Role> mrIterator = roleDao.findAllManagedRoles(role).iterator();
		while( mrIterator.hasNext() ) {
			managedRoles.addAll(roleDao.findAllSubRoles(mrIterator.next()));
		}
		for( Role managedRole : managedRoles ) {
			for( UserRoleRelation urr : userRoleRelationDao.findUserRoleRelations(null, managedRole.getId()) ) {
				managedUsers.add( findUserById(urr.getUserId()) );
			}
		}
		return managedUsers;
	}

	public boolean managesRole(User user, Role role) {
		
		if( role == null || role.getId() == null ) {
			throw new IllegalArgumentException("Unable to verify role management: role ID cannot be null.");
		}
		
		return findManagedRoles(user)
				.stream()
					.map( r -> r.getId() )
					.collect(Collectors.toSet())
				.contains(role.getId());
	}

	private boolean userHasRole(User user, Role role) {
		Set<String> roleIds = findAllRoles(user).stream().map( r -> r.getId() ).collect(Collectors.toSet());
		return roleIds.contains(role.getId());
	}

	
}
