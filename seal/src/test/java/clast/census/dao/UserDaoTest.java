package clast.census.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import clast.census.BaseTest;
import clast.census.dao.RoleDao;
import clast.census.dao.UserDao;
import clast.census.dao.UserRoleRelationDao;
import clast.census.model.Role;
import clast.census.model.User;

public class UserDaoTest extends BaseTest {

	private UserDao userDao;
	private RoleDao roleDao;
	private UserRoleRelationDao userRoleRelationDao;
	
	private User u1;
	private User u2;
	private User u3;
	private User u4;
	private User u5;
	
	private Role r1;
	private Role r2;
	private Role r3;
	private Role r4;
	private Role r5;
	private Role r6;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setUp() {
		
		userDao = new UserDao();
		roleDao = new RoleDao();
		userRoleRelationDao = new UserRoleRelationDao(userDao, roleDao);
		
		u1 = new User("usr1", "pwd1");
		u2 = new User("usr2", "pwd2");
		u3 = new User("usr3", "pwd3");
		u4 = new User("usr4", "pwd4");
		u5 = new User("usr5", "pwd5");
		userDao.createUser(u1);
		userDao.createUser(u2);
		userDao.createUser(u3);
		userDao.createUser(u4);
		userDao.createUser(u5);
		
		r1 = new Role("role1");
		r2 = new Role("role2");
		r3 = new Role("role3");
		r4 = new Role("role4");
		r5 = new Role("role5");
		r6 = new Role("role6");
		roleDao.createRole(r1);
		roleDao.createRole(r2);
		roleDao.createRole(r3);
		roleDao.createRole(r4);
		roleDao.createRole(r5);
		roleDao.createRole(r6);
	}
	
	@After
	public void tearDown() {
		userDao.deleteAllUsers();
		roleDao.deleteAllRoles();
	}
	
	@Test
	public void testCreateUser1() {
		User user = new User("user1", "password1");
		assertTrue(userDao.createUser(user));
	}
	
	@Test
	public void testCreateUser2() {
		User user = createTestUser();
		assertTrue(userDao.createUser(user));
		verifyUser(user);
	}
	
	@Test
	public void testCreateNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user: User cannot be null.");
		
		userDao.createUser(null);
	}
	
	@Test
	public void testCreateAlreadyExistingUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user: User is already persisted.");
		
		User user = new User("user", "password");
		userDao.createUser(user);
		userDao.createUser(user);
	}
	
	@Test
	public void testCreateUserWithoutUsername() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user: Username cannot be null.");
		
		User user = new User();
		user.encryptPassword("password");
		userDao.createUser(user);
	}
	
	@Test
	public void testCreateUserWithoutPassword() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user: Password cannot be null.");
		
		User user = new User();
		user.setUsername("username");
		userDao.createUser(user);
	}
	
	@Test
	public void testCreateUserWithOccupiedUsername() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user: Username user1 is already assigned to another user.");
		
		User user1 = new User("user1", "password1");
		userDao.createUser(user1);
		
		User user2 = new User("user1", "password2");
		userDao.createUser(user2);
	}
	
	@Test
	public void testDeleteUser() {
		
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r4, r5);
		userDao.addRole(u1, r1);
		userDao.addRole(u1, r5);
		userDao.addRole(u2, r4);
		userDao.addRole(u2, r3);
		userDao.addRole(u3, r1);
		userDao.addRole(u4, r2);
		userDao.addRole(u5, r1);
		userDao.addRole(u5, r4);
		
		Set<String> usernames = userDao.findAllUsers().stream().map( u -> u.getUsername() ).collect(Collectors.toSet());
		assertEquals(5, usernames.size());
		assertTrue(usernames.contains(u1.getUsername()));
		assertTrue(usernames.contains(u2.getUsername()));
		assertTrue(usernames.contains(u3.getUsername()));
		assertTrue(usernames.contains(u4.getUsername()));
		assertTrue(usernames.contains(u5.getUsername()));
		
		Set<String> userRoleRelations = userRoleRelationDao.findUserRoleRelations(null, null).stream().map( urr -> urr.toString() ).collect(Collectors.toSet());
		assertEquals(8, userRoleRelations.size());
		assertTrue(userRoleRelations.contains(stringOf(u1.getId(), r1.getId())));
		assertTrue(userRoleRelations.contains(stringOf(u1.getId(), r5.getId())));
		assertTrue(userRoleRelations.contains(stringOf(u2.getId(), r4.getId())));
		assertTrue(userRoleRelations.contains(stringOf(u2.getId(), r3.getId())));
		assertTrue(userRoleRelations.contains(stringOf(u3.getId(), r1.getId())));
		assertTrue(userRoleRelations.contains(stringOf(u4.getId(), r2.getId())));
		assertTrue(userRoleRelations.contains(stringOf(u5.getId(), r1.getId())));
		assertTrue(userRoleRelations.contains(stringOf(u5.getId(), r4.getId())));
		
		assertTrue(userDao.deleteUser(u2));
		assertTrue(userDao.deleteUser(u4));
		
		usernames = userDao.findAllUsers().stream().map( u -> u.getUsername() ).collect(Collectors.toSet());
		assertEquals(3, usernames.size());
		assertTrue(usernames.contains(u1.getUsername()));
		assertTrue(usernames.contains(u3.getUsername()));
		assertTrue(usernames.contains(u5.getUsername()));
		
		userRoleRelations = userRoleRelationDao.findUserRoleRelations(null, null).stream().map( urr -> urr.toString() ).collect(Collectors.toSet());
		assertEquals(5, userRoleRelations.size());
		assertTrue(userRoleRelations.contains(stringOf(u1.getId(), r1.getId())));
		assertTrue(userRoleRelations.contains(stringOf(u1.getId(), r5.getId())));
		assertTrue(userRoleRelations.contains(stringOf(u3.getId(), r1.getId())));
		assertTrue(userRoleRelations.contains(stringOf(u5.getId(), r1.getId())));
		assertTrue(userRoleRelations.contains(stringOf(u5.getId(), r4.getId())));
		
		assertTrue(userDao.deleteUser(u1));
		assertTrue(userDao.deleteUser(u3));
		assertTrue(userDao.deleteUser(u5));

		usernames = userDao.findAllUsers().stream().map( u -> u.getUsername() ).collect(Collectors.toSet());
		assertTrue(usernames.isEmpty());
		
		userRoleRelations = userRoleRelationDao.findUserRoleRelations(null, null).stream().map( urr -> urr.toString() ).collect(Collectors.toSet());
		assertTrue(userRoleRelations.isEmpty());
	}
	
	@Test
	public void testDeleteNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user: user not found.");
		
		userDao.deleteUser(null);
	}
	
	@Test
	public void testDeleteUserWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user: user not found.");
		
		userDao.deleteUser( new User() );
	}
	
	@Test
	public void testDeleteNotPersistedUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user: user with ID " + u1.getId() + " not found.");
		
		userDao.deleteUser(u1);
		userDao.deleteUser(u1);
	}
	
	@Test
	public void testDeleteAllUsers() {
		assertTrue(userDao.deleteAllUsers());
		assertTrue(userDao.findAllUsers().isEmpty());
	}
	
	@Test
	public void testUpdateUser() {
		u1.setUsername("u1");
		u1.encryptPassword("p1");
		u1.setName("n1");
		u1.setLastname("l1");
		u1.setEmail("e1");
		u1.setPhone("p1");
		
		assertTrue(userDao.updateUser(u1));
		verifyUser(u1);
	}
	
	@Test
	public void testUpdateNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to update user: User cannot be null.");
		
		userDao.updateUser(null);
	}
	
	@Test
	public void testUpdateUserWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to update user: User not persisted.");
		
		userDao.updateUser(new User("us1", "pd1"));
	}
	
	@Test
	public void testUpdateNotPersistedUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("User with ID " + u1.getId() + " not found.");
		
		userDao.deleteUser(u1);
		userDao.updateUser(u1);
	}
	
	@Test
	public void testUpdateWithUserWithNullUsername() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to update user: Username cannot be null.");
		
		u1.setUsername(null);
		userDao.updateUser(u1);
	}
	
	@Test
	public void testUpdateWithUserWithNullPassword() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to update user: Password cannot be null.");
		
		u1.setPassword(null);
		userDao.updateUser(u1);
	}
	
	@Test
	public void testUpdateWithAlreadyAssignedUsername() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Username " + u2.getUsername() + " is already assigned to another user.");
		
		u1.setUsername(u2.getUsername());
		userDao.updateUser(u1);
	}
	
	@Test
	public void findAllUsers() {
		assertEquals(5,userDao.findAllUsers().size());
		
		userDao.createUser(new User("usr6", "pwd6"));
		Set<String> usernames = userDao.findAllUsers().stream().map( r -> r.getUsername()).collect(Collectors.toSet());
		assertEquals(6, usernames.size());
		assertTrue( usernames.contains("usr1"));
		assertTrue( usernames.contains("usr2"));
		assertTrue( usernames.contains("usr3"));
		assertTrue( usernames.contains("usr4"));
		assertTrue( usernames.contains("usr5"));
		assertTrue( usernames.contains("usr6"));
		
		userDao.createUser(new User("usr7", "pwd7"));
		userDao.createUser(new User("usr8", "pwd8"));
		usernames = userDao.findAllUsers().stream().map( r -> r.getUsername()).collect(Collectors.toSet());
		assertEquals(8, usernames.size());
		assertTrue( usernames.contains("usr1"));
		assertTrue( usernames.contains("usr2"));
		assertTrue( usernames.contains("usr3"));
		assertTrue( usernames.contains("usr4"));
		assertTrue( usernames.contains("usr5"));
		assertTrue( usernames.contains("usr6"));
		assertTrue( usernames.contains("usr7"));
		assertTrue( usernames.contains("usr8"));
	}
	
	@Test
	public void testFindUsersByFreeSearch() {
		User u11 = new User("user11", "passw0rd");
		User u21 = new User("us21er", "p4ssword");
		User u31 = new User("31 user", "p4ssw0rd");
		User u41 = new User("41u", "pa55word");
		userDao.createUser(u11);
		userDao.createUser(u21);
		userDao.createUser(u31);
		userDao.createUser(u41);
		
		u2.setName("name3");
		userDao.updateUser(u2);
		
		u3.setLastname("lastname4");
		userDao.updateUser(u3);
		
		u4.setEmail("email5");
		userDao.updateUser(u4);
		
		u5.setPhone("phone1");
		userDao.updateUser(u5);
		
		Set<String> usernames = userDao.findUsersByFreeSearch("1").stream().map( u -> u.getUsername() ).collect(Collectors.toSet());
		assertEquals(6, usernames.size());
		assertTrue(usernames.contains(u1.getUsername()));
		assertTrue(usernames.contains(u11.getUsername()));
		assertTrue(usernames.contains(u21.getUsername()));
		assertTrue(usernames.contains(u31.getUsername()));
		assertTrue(usernames.contains(u41.getUsername()));
		assertTrue(usernames.contains(u5.getUsername()));
		
		usernames = userDao.findUsersByFreeSearch("4").stream().map( u -> u.getUsername() ).collect(Collectors.toSet());
		assertEquals(3, usernames.size());
		assertTrue(usernames.contains(u4.getUsername()));
		assertTrue(usernames.contains(u41.getUsername()));
		assertTrue(usernames.contains(u3.getUsername()));
		
		usernames = userDao.findUsersByFreeSearch(" ").stream().map( u -> u.getUsername() ).collect(Collectors.toSet());
		assertEquals(1, usernames.size());
		assertTrue(usernames.contains(u31.getUsername())); 
		
		usernames = userDao.findUsersByFreeSearch("ai").stream().map( u -> u.getUsername() ).collect(Collectors.toSet());
		assertEquals(1, usernames.size());
		assertTrue(usernames.contains(u4.getUsername())); 
	}
	
	@Test
	public void findUsersByNullFreeSearch() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find users: text of search cannot be empty.");
		
		userDao.findUsersByFreeSearch(null);
	}
	
	@Test
	public void testFindUsersByEmptySearch() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find users: text of search cannot be empty.");
		
		userDao.findUsersByFreeSearch("");
	}
	
	@Test
	public void testFindUserByUsername() {
		assertNotNull(userDao.findUserByUsername("usr1"));
		assertNull(userDao.findUserByUsername("user1234"));
	}
	
	@Test
	public void testFindUserByNullUsername() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find user: username cannot be null.");
		
		userDao.findUserByUsername(null);
	}
	
	@Test
	public void testFindUserById() {
		assertNotNull(userDao.findUserById(u1.getId()));
		assertNull(userDao.findUserById("123"));
	}
	
	@Test
	public void testFindUserByNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find user: user ID cannot be null.");
		
		userDao.findUserById(null);
	}
	
	@Test
	public void testAddRole() {
		assertTrue(userDao.addRole(u1, r1));
	}
	
	@Test
	public void testAddRoleToNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add role: user and role cannot be null.");
		
		userDao.addRole(null, r1);
	}
	
	@Test
	public void testAddNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add role: user and role cannot be null.");
		
		userDao.addRole(u1, null);
	}
	
	@Test
	public void testAddNullRoleToNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add role: user and role cannot be null.");
		
		userDao.addRole(null, null);
	}
	
	@Test
	public void testRemoveRole() {
		userDao.addRole(u1, r1);
		assertTrue(userDao.removeRole(u1, r1));
	}
	
	@Test
	public void testRemoveRoleToNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to remove role: user and role cannot be null.");
		
		userDao.removeRole(null, r1);
	}
	
	@Test
	public void testRemoveNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to remove role: user and role cannot be null.");
		
		userDao.removeRole(u1, null);
	}
	
	@Test
	public void testRemoveNullRoleToNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to remove role: user and role cannot be null.");
		
		userDao.removeRole(null, null);
	}
	
	@Test
	public void testFindAllRoles() {
		
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r4, r5);
		userDao.addRole(u1, r1);
		userDao.addRole(u1, r5);
		
		Set<String> roleNames = userDao.findAllRoles(u1).stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(4, roleNames.size());
		assertTrue(roleNames.contains(r1.getName()));
		assertTrue(roleNames.contains(r2.getName()));
		assertTrue(roleNames.contains(r3.getName()));
		assertTrue(roleNames.contains(r5.getName()));
	}
	
	@Test
	public void testFindAllRolesOfNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find user roles: user must be persisted.");
		
		userDao.findAllRoles(null);
	}
	
	@Test
	public void testFindAllRolesOfUserWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find user roles: user must be persisted.");
		
		userDao.findAllRoles(createTestUser());
	}
	
	@Test
	public void testFindDirectRoles() {
		
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r4, r5);
		userDao.addRole(u1, r1);
		userDao.addRole(u1, r5);
		
		Set<String> roleNames = userDao.findDirectRoles(u1).stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(2, roleNames.size());
		assertTrue(roleNames.contains(r1.getName()));
		assertTrue(roleNames.contains(r5.getName()));
	}
	
	@Test
	public void testFindDirectRolesOfNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find user direct-roles: user must be persisted.");
		
		userDao.findDirectRoles(null);
	}
	
	@Test
	public void testFindDirectRolesOfUserWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find user direct-roles: user must be persisted.");
		
		userDao.findDirectRoles(createTestUser());
	}
	
	@Test
	public void testFindIndirectRoles() {
		
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r4, r5);
		userDao.addRole(u1, r1);
		userDao.addRole(u1, r5);
		
		Set<String> roleNames = userDao.findIndirectRoles(u1).stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(2, roleNames.size());
		assertTrue(roleNames.contains(r2.getName()));
		assertTrue(roleNames.contains(r3.getName()));
	}
	
	@Test
	public void testFindIndirectRolesOfNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find user indirect-roles: user must be persisted.");
		
		userDao.findIndirectRoles(null);
	}
	
	@Test
	public void testFindindirectRolesOfUserWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find user indirect-roles: user must be persisted.");
		
		userDao.findIndirectRoles(createTestUser());
	}
	
	@Test
	public void testHasRole() {
		
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r4, r5);
		userDao.addRole(u1, r1);
		userDao.addRole(u1, r5);
		
		assertTrue(userDao.hasRole(u1, r1));
		assertTrue(userDao.hasRole(u1, r2));
		assertTrue(userDao.hasRole(u1, r3));
		assertTrue(userDao.hasRole(u1, r5));
		assertFalse(userDao.hasRole(u1, r4));
	}
	
	@Test
	public void testHasNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to verify role assignment: role ID cannot be null.");
		
		userDao.hasRole(u1, null);
	}
	
	@Test
	public void testHasRoleNotPersisted() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to verify role assignment: role ID cannot be null.");
		
		userDao.hasRole(u1, new Role("r1234"));
	}
	
	@Test
	public void testDirectlyHasRole() {
		
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r4, r5);
		userDao.addRole(u1, r1);
		userDao.addRole(u1, r5);
		
		assertTrue(userDao.directlyHasRole(u1, r1));
		assertFalse(userDao.directlyHasRole(u1, r2));
		assertFalse(userDao.directlyHasRole(u1, r3));
		assertTrue(userDao.directlyHasRole(u1, r5));
		assertFalse(userDao.directlyHasRole(u1, r4));
	}
	
	@Test
	public void testDirectlyHasNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to verify role assignment: role ID cannot be null.");
		
		userDao.directlyHasRole(u1, null);
	}
	
	@Test
	public void testDirectlyHasRoleNotPersisted() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to verify role assignment: role ID cannot be null.");
		
		userDao.directlyHasRole(u1, new Role("r1234"));
	}
	
	@Test
	public void testIndirectlyHasRole() {
		
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r4, r5);
		userDao.addRole(u1, r1);
		userDao.addRole(u1, r5);
		
		assertFalse(userDao.indirectlyHasRole(u1, r1));
		assertTrue(userDao.indirectlyHasRole(u1, r2));
		assertTrue(userDao.indirectlyHasRole(u1, r3));
		assertFalse(userDao.indirectlyHasRole(u1, r5));
		assertFalse(userDao.indirectlyHasRole(u1, r4));
	}
	
	@Test
	public void testIndirectlyHasNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to verify role assignment: role ID cannot be null.");
		
		userDao.indirectlyHasRole(u1, null);
	}
	
	@Test
	public void testIndirectlyHasRoleNotPersisted() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to verify role assignment: role ID cannot be null.");
		
		userDao.indirectlyHasRole(u1, new Role("r1234"));
	}
	
	@Test
	public void testFindManagedRoles() {
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		roleDao.addSubRole(r4, r5);
		roleDao.addManagedRole(r1, r1);
		roleDao.addManagedRole(r1, r3);
		roleDao.addManagedRole(r2, r3);
		roleDao.addManagedRole(r6, r4);
		roleDao.addManagedRole(r5, r6);
		userDao.addRole(u1, r1);
		userDao.addRole(u1, r6);
		userDao.addRole(u2, r2);
		userDao.addRole(u3, r3);
		userDao.addRole(u4, r4);
		userDao.addRole(u5, r5);
		
		Set<String> managedRoleNames = userDao.findManagedRoles(u1).stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(4, managedRoleNames.size());
		assertTrue(managedRoleNames.contains(r1.getName()));
		assertTrue(managedRoleNames.contains(r3.getName()));
		assertTrue(managedRoleNames.contains(r4.getName()));
		assertTrue(managedRoleNames.contains(r6.getName()));
	}
	
	@Test
	public void testFindManagedRolesOfNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find managed roles: User ID cannot be null.");
		
		userDao.findManagedRoles(null);
	}
	
	@Test
	public void testFindManagedRolesOfUserWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find managed roles: User ID cannot be null.");
		
		userDao.findManagedRoles(createTestUser());
	}
	
	@Test
	public void testFindManagedUsers() {
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		roleDao.addSubRole(r4, r5);
		roleDao.addManagedRole(r1, r1);
		roleDao.addManagedRole(r1, r3);
		roleDao.addManagedRole(r2, r3);
		roleDao.addManagedRole(r6, r4);
		roleDao.addManagedRole(r5, r6);
		userDao.addRole(u1, r1);
		userDao.addRole(u1, r6);
		userDao.addRole(u2, r2);
		userDao.addRole(u3, r3);
		userDao.addRole(u4, r4);
		userDao.addRole(u5, r5);
		
		Set<String> managedUsersIds = userDao.findManagedUsers(u1).stream().map( u -> u.getId() ).collect(Collectors.toSet());
		assertEquals(5, managedUsersIds.size());
		assertTrue(managedUsersIds.contains(u1.getId()));
		assertTrue(managedUsersIds.contains(u2.getId()));
		assertTrue(managedUsersIds.contains(u3.getId()));
		assertTrue(managedUsersIds.contains(u4.getId()));
		assertTrue(managedUsersIds.contains(u5.getId()));
	}
	
	@Test
	public void testFindManagedUsersOfNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find managed users: User ID cannot be null.");
		
		userDao.findManagedUsers(null);
	}
	
	@Test
	public void testFindManagedUsersOfUserWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find managed users: User ID cannot be null.");
		
		userDao.findManagedUsers(createTestUser());
	}
	
	@Test
	public void testFindManagedUsersByRole() {
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		roleDao.addSubRole(r4, r5);
		roleDao.addManagedRole(r1, r1);
		roleDao.addManagedRole(r1, r3);
		roleDao.addManagedRole(r2, r3);
		roleDao.addManagedRole(r6, r4);
		roleDao.addManagedRole(r5, r6);
		userDao.addRole(u1, r1);
		userDao.addRole(u1, r6);
		userDao.addRole(u2, r2);
		userDao.addRole(u3, r3);
		userDao.addRole(u4, r4);
		userDao.addRole(u5, r5);
		
		Set<String> managedUsersIds = userDao.findManagedUsersByRole(u1, r1).stream().map( u -> u.getId() ).collect(Collectors.toSet());
		assertEquals(5, managedUsersIds.size());
		assertTrue(managedUsersIds.contains(u1.getId()));
		assertTrue(managedUsersIds.contains(u2.getId()));
		assertTrue(managedUsersIds.contains(u3.getId()));
		assertTrue(managedUsersIds.contains(u4.getId()));
		assertTrue(managedUsersIds.contains(u5.getId()));
		
		managedUsersIds = userDao.findManagedUsersByRole(u1, r5).stream().map( u -> u.getId() ).collect(Collectors.toSet());
		assertEquals(1, managedUsersIds.size());
		assertTrue(managedUsersIds.contains(u1.getId()));
		
		managedUsersIds = userDao.findManagedUsersByRole(u1, r6).stream().map( u -> u.getId() ).collect(Collectors.toSet());
		assertEquals(2, managedUsersIds.size());
		assertTrue(managedUsersIds.contains(u4.getId()));
		assertTrue(managedUsersIds.contains(u5.getId()));
		
		managedUsersIds = userDao.findManagedUsersByRole(u1, r3).stream().map( u -> u.getId() ).collect(Collectors.toSet());
		assertEquals(0, managedUsersIds.size());
	}
	
	@Test
	public void testFindManagedUsersByRoleOfNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find managed users by role: IDs of user and role cannot be null.");
		
		userDao.findManagedUsersByRole(null, r1);
	}
	
	@Test
	public void testFindManagedUsersByRoleOfUserWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find managed users by role: IDs of user and role cannot be null.");
		
		userDao.findManagedUsersByRole(createTestUser(), r1);
	}
	
	@Test
	public void testFindManagedUsersByNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find managed users by role: IDs of user and role cannot be null.");
		
		userDao.findManagedUsersByRole(u1, null);
	}
	
	@Test
	public void testFindManagedUsersByRoleWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find managed users by role: IDs of user and role cannot be null.");
		
		userDao.findManagedUsersByRole(u1, new Role("role") );
	}
	
	@Test
	public void testFindManagedUserByNotAssignedRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find managed users by role: User: " + u1.getId() + " not has Role: " + r4.getId());
		
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r4, r5);
		roleDao.addManagedRole(r1, r2);
		roleDao.addManagedRole(r1, r4);
		roleDao.addManagedRole(r5, r3);
		userDao.addRole(u1, r1);
		userDao.addRole(u1, r5);
		userDao.addRole(u2, r2);
		userDao.addRole(u3, r3);
		userDao.addRole(u4, r4);
		userDao.addRole(u5, r5);
		
		userDao.findManagedUsersByRole(u1, r4);
	}
	
	@Test
	public void testManagesRole() {
		
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		roleDao.addSubRole(r4, r5);
		roleDao.addManagedRole(r1, r1);
		roleDao.addManagedRole(r1, r3);
		roleDao.addManagedRole(r2, r3);
		roleDao.addManagedRole(r6, r4);
		roleDao.addManagedRole(r5, r6);
		userDao.addRole(u1, r1);
		userDao.addRole(u1, r6);
		userDao.addRole(u2, r2);
		userDao.addRole(u3, r3);
		userDao.addRole(u4, r4);
		userDao.addRole(u5, r5);
		
		assertTrue(userDao.managesRole(u1, r1));
		assertFalse(userDao.managesRole(u1, r2));
		assertTrue(userDao.managesRole(u1, r3));
		assertTrue(userDao.managesRole(u1, r4));
		assertFalse(userDao.managesRole(u1, r5));
		assertFalse(userDao.managesRole(u1, r6));
	}
	
	@Test
	public void testManagesNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to verify role management: role ID cannot be null.");
		
		userDao.managesRole(u1, null);
	}
	
	@Test
	public void testManagesRoleNotPersisted() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to verify role management: role ID cannot be null.");
		
		userDao.managesRole(u1, new Role("r1234"));
	}

	private void verifyUser(User user) {
		User u = userDao.findUserById(user.getId());
		assertEquals(user.getId(), u.getId());
		assertEquals(user.getUsername(), u.getUsername());
		assertEquals(user.getPassword(), u.getPassword());
		assertEquals(user.getName(), u.getName());
		assertEquals(user.getLastname(), u.getLastname());
		assertEquals(user.getEmail(), u.getEmail());
		assertEquals(user.getPhone(), u.getPhone());
	}

	private User createTestUser() {
		User user = new User();
		user.setUsername("username1");
		user.encryptPassword("password1");
		user.setName("name1");
		user.setLastname("lastname1");
		user.setEmail("email1");
		user.setPhone("phone1");
		return user;
	}
	
	private String stringOf(String id1, String id2) {
		return id1 + "-" + id2;
	}

}
