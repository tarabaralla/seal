package clast.census.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.InRequestScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import clast.census.BaseTest;
import clast.census.dao.RoleDao;
import clast.census.dao.UserDao;
import clast.census.model.Role;
import clast.census.model.User;
import clast.census.session.Session;

@InRequestScope
@RunWith(CdiRunner.class)
public class SessionTest extends BaseTest {

	@Inject
	private Session session;
	
	@Inject
	private Session session2;
	
	private RoleDao roleDao;
	private UserDao userDao;
	
	private Role r1;
	private Role r2;
	private Role r3;
	private Role r4;
	private Role r5;
	private Role r6;
	private Role r7;
	
	private User u1;
	private User u2;
	private User u3;
	private User u4;
	private User u5;
	private User u6;
	private User u7;
	
	private ExpectedException expectedEx = ExpectedException.none();
	
	@Rule
    public ExpectedException getExpectedEx() {
        return expectedEx;
    }
	
	@Before
	public void setUp() {
		
		roleDao = new RoleDao();
		userDao = new UserDao();
		
		r1 = new Role("role1");
		r2 = new Role("role2");
		r3 = new Role("role3");
		r4 = new Role("role4");
		r5 = new Role("role5");
		r6 = new Role("role6");
		r7 = new Role("role7");
		roleDao.createRole(r1);
		roleDao.createRole(r2);
		roleDao.createRole(r3);
		roleDao.createRole(r4);
		roleDao.createRole(r5);
		roleDao.createRole(r6);
		roleDao.createRole(r7);
		
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		roleDao.addSubRole(r5, r6);
		
		roleDao.addManagedRole(r1, r1);
		roleDao.addManagedRole(r1, r2);
		roleDao.addManagedRole(r1, r3);
		roleDao.addManagedRole(r2, r4);
		roleDao.addManagedRole(r5, r6);
		
		u1 = new User("usr1", "pwd1");
		u2 = new User("usr2", "pwd2");
		u3 = new User("usr3", "pwd3");
		u4 = new User("usr4", "pwd4");
		u5 = new User("usr5", "pwd5");
		u6 = new User("usr6", "pwd6");
		u7 = new User("usr7", "pwd7");
		
		userDao.createUser(u1);
		userDao.createUser(u2);
		userDao.createUser(u3);
		userDao.createUser(u4);
		userDao.createUser(u5);
		userDao.createUser(u6);
		userDao.createUser(u7);
		
		userDao.addRole(u1, r1);
		userDao.addRole(u2, r2);
		userDao.addRole(u3, r3);
		userDao.addRole(u4, r4);
		userDao.addRole(u5, r5);
		userDao.addRole(u6, r6);
		userDao.addRole(u7, r7);

	}
	
	@After
	public void tearDown() {
		userDao.deleteAllUsers();
		roleDao.deleteAllRoles();
	}
	
	@Test
	public void testSessionInjection() {
		assertNotNull(session);
	}
	
	@Test
	public void testLogin() {
		assertTrue(session.login("usr1", "pwd1"));
		assertEquals(u1.getId(), session.getLoggedUser().getId());
		assertEquals(u1.getUsername(), session.getLoggedUser().getUsername());
	}
	
	@Test
	public void testLoginInAlreadyLoggedInSession1() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to login: the session already contains a logged-in user.");
		
		session.login("usr1", "pwd1");
		session.login("usr1", "pwd1");
	}
	
	@Test
	public void testLoginInAlreadyLoggedInSession2() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to login: the session already contains a logged-in user.");
		
		session.login("usr1", "pwd1");
		session.login("usr2", "pwd2");
	}
	
	@Test
	public void testLoginInAlreadyLoggedInSession3() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to login: the session already contains a logged-in user.");
		
		session.login("usr1", "pwd1");
		session2.login("usr1", "pwd1");
	}
	
	@Test
	public void testLoginInAlreadyLoggedInSession4() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to login: the session already contains a logged-in user.");
		
		session.login("usr1", "pwd1");
		session2.login("usr2", "pwd2");
	}
	
	@Test
	public void testLoginWithNullUsername() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to login: username and password cannot be null.");
		
		session.login(null, "pwd1");
	}
	
	@Test
	public void testLoginWithNullPassword() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to login: username and password cannot be null.");
		
		session.login("usr1", null);
	}
	
	@Test
	public void testLoginWithNullUsernameAndPassword() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to login: username and password cannot be null.");
		
		session.login(null, null);
	}
	
	@Test
	public void testLoginWithUnexistingUsername() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to login: user not found.");
		
		session.login("user1", "pwd1");
	}
	
	@Test
	public void testLoginWithWrongPassword() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to login: wrong password.");
		
		session.login("usr1", "pwd2");
	}
	
	@Test
	public void testLogout() {
		session.login("usr1", "pwd1");
		
		assertTrue(session.logout());
		assertNull(session.getLoggedUser());
	}
	
	@Test
	public void testLogoutFromSessionWithoutLoggedUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to logout: no users logged in.");
		
		session.logout();
	}
	
	@Test
	public void testUpdateProfile() {
		session.login("usr1", "pwd1");
		u1.setUsername("mario.rossi");
		u1.encryptPassword("123");
		u1.setName("mario");
		u1.setLastname("rossi");
		u1.setEmail("mario.rossi@gmail.com");
		u1.setPhone("1234567");
		assertTrue(session.updateProfile(u1));
		assertEquals("mario.rossi", session.getLoggedUser().getUsername());
		assertTrue(session.getLoggedUser().checkPassword("123"));
		assertEquals("mario", session.getLoggedUser().getName());
		assertEquals("rossi", session.getLoggedUser().getLastname());
		assertEquals("mario.rossi@gmail.com", session.getLoggedUser().getEmail());
		assertEquals("1234567", session.getLoggedUser().getPhone());
	}
	
	@Test
	public void testUpdateProfileFromSessionWithoutLoggedUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to update profile: no users logged in.");
		
		u1.setUsername("mario.rossi");
		u1.encryptPassword("123");
		u1.setName("mario");
		u1.setLastname("rossi");
		u1.setEmail("mario.rossi@gmail.com");
		u1.setPhone("1234567");
		session.updateProfile(u1);
	}
	
	@Test
	public void testUpdateProfileOfNotLoggedUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to update profile: only logged in user data can be changed.");
		
		session.login("usr2", "pwd2");
		u1.setUsername("mario.rossi");
		u1.encryptPassword("123");
		u1.setName("mario");
		u1.setLastname("rossi");
		u1.setEmail("mario.rossi@gmail.com");
		u1.setPhone("1234567");
		session.updateProfile(u1);
	}
	
	@Test
	public void testUpdateProfileOfNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to update profile: only logged in user data can be changed.");
		
		session.login("usr1", "pwd1");
		session.updateProfile(null);
	}

	@Test
	public void testDeleteProfile() {
		session.login("usr1", "pwd1");
		
		assertTrue(session.deleteProfile());
		assertNull(session.getLoggedUser());
		assertNull(userDao.findUserByUsername("usr1"));
	}
	
	@Test
	public void testDeleteProfileFromSessionWithoutLoggedUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete profile: no users logged in.");
		
		session.deleteProfile();
	}
	
	@Test
	public void testGetAllRoles() {
		session.login("usr1", "pwd1");
		Set<String> roleNames = session.getAllRoles().stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(4, roleNames.size());
		assertTrue(roleNames.contains(r1.getName()));
		assertTrue(roleNames.contains(r2.getName()));
		assertTrue(roleNames.contains(r3.getName()));
		assertTrue(roleNames.contains(r4.getName()));
	}
	
	@Test
	public void testGetDirectRoles() {
		session.login("usr1", "pwd1");
		Set<String> roleNames = session.getDirectRoles().stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(1, roleNames.size());
		assertTrue(roleNames.contains(r1.getName()));
	}
	
	@Test
	public void testGetIndirectRoles() {
		session.login("usr1", "pwd1");
		Set<String> roleNames = session.getIndirectRoles().stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(3, roleNames.size());
		assertTrue(roleNames.contains(r2.getName()));
		assertTrue(roleNames.contains(r3.getName()));
		assertTrue(roleNames.contains(r4.getName()));
	}
	
	@Test
	public void testHasRole() {
		session.login("usr1", "pwd1");
		assertTrue(session.hasRole(r1));
		assertTrue(session.hasRole(r2));
		assertTrue(session.hasRole(r3));
		assertTrue(session.hasRole(r4));
		assertFalse(session.hasRole(r5));
		assertFalse(session.hasRole(r6));
		assertFalse(session.hasRole(r7));
	}
	
	@Test
	public void testDirectlyHasRole() {
		session.login("usr1", "pwd1");
		assertTrue(session.directlyHasRole(r1));
		assertFalse(session.directlyHasRole(r2));
		assertFalse(session.directlyHasRole(r3));
		assertFalse(session.directlyHasRole(r4));
		assertFalse(session.directlyHasRole(r5));
		assertFalse(session.directlyHasRole(r6));
		assertFalse(session.directlyHasRole(r7));
	}
	
	@Test
	public void testIndirectlyHasRole() {
		session.login("usr1", "pwd1");
		assertFalse(session.indirectlyHasRole(r1));
		assertTrue(session.indirectlyHasRole(r2));
		assertTrue(session.indirectlyHasRole(r3));
		assertTrue(session.indirectlyHasRole(r4));
		assertFalse(session.indirectlyHasRole(r5));
		assertFalse(session.indirectlyHasRole(r6));
		assertFalse(session.indirectlyHasRole(r7));
	}
	
	@Test
	public void testGetManagedRoles() {
		session.login("usr1", "pwd1");
		Set<String> roleNames = session.getManagedRoles().stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(4, roleNames.size());
		assertTrue(roleNames.contains(r1.getName()));
		assertTrue(roleNames.contains(r2.getName()));
		assertTrue(roleNames.contains(r3.getName()));
		assertTrue(roleNames.contains(r4.getName()));
	}
	
	@Test
	public void testGetManagedUsers() {
		session.login("usr1", "pwd1");
		Set<String> usernames = session.getManagedUsers().stream().map( u -> u.getUsername() ).collect(Collectors.toSet());
		assertEquals(4, usernames.size());
		assertTrue(usernames.contains(u1.getUsername()));
		assertTrue(usernames.contains(u2.getUsername()));
		assertTrue(usernames.contains(u3.getUsername()));
		assertTrue(usernames.contains(u4.getUsername()));
	}
	
	@Test
	public void testGetManagedUsersByRole() {
		session.login("usr1", "pwd1");
		Set<String> usernames = session.getManagedUsersByRole(r2).stream().map( u -> u.getUsername() ).collect(Collectors.toSet());
		assertEquals(1, usernames.size());
		assertTrue(usernames.contains(u4.getUsername()));
	}
	
	@Test
	public void testManagesRole() {
		session.login("usr1", "pwd1");
		assertTrue(session.managesRole(r1));
		assertTrue(session.managesRole(r2));
		assertTrue(session.managesRole(r3));
		assertTrue(session.managesRole(r4));
		assertFalse(session.managesRole(r5));
		assertFalse(session.managesRole(r6));
		assertFalse(session.managesRole(r7));
	}
	
	@Test
	public void testCreateUser() {
		
		session.login("usr1", "pwd1");
		Set<Role> roles = new HashSet<>();
		
		roles.add(r1);
		assertTrue(session.createUser( new User("usr8", "pwd8"), roles));
		
		roles.clear();
		roles.add(r2);
		assertTrue(session.createUser( new User("usr9", "pwd9"), roles));
		
		roles.clear();
		roles.add(r3);
		assertTrue(session.createUser( new User("usr10", "pwd10"), roles));
		
		roles.clear();
		roles.add(r4);
		assertTrue(session.createUser( new User("usr11", "pwd11"), roles));
		
		roles.clear();
		roles.add(r2);
		roles.add(r3);
		assertTrue(session.createUser( new User("usr12", "pwd12"), roles));
	}
	
	@Test
	public void testCreateUserWithLogErrors() {
		
		session.login("usr1", "pwd1");
		
		Set<Role> roles = new HashSet<>();
		roles.add(r2);
		roles.add(r3);
		roles.add(r1);
		roles.add(r4);
		session.createUser( new User("usr8", "pwd8"), roles);
	}
	
	@Test
	public void testCreateUserFromSessionWithoutLoggedUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user: no users logged in.");
		
		Set<Role> roles = new HashSet<>();
		roles.add(r1);
		session.createUser( new User("usr8", "pwd8"), roles);
	}
	
	@Test
	public void testCreateUserWithNullRolesList() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user: every user must have at least one role.");
		
		session.login("usr1", "pwd1");
		session.createUser( new User("usr8", "pwd8"), null);
	}
	
	@Test
	public void testCreateUserWithoutRoles() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user: every user must have at least one role.");
		
		session.login("usr1", "pwd1");
		session.createUser( new User("usr8", "pwd8"), new HashSet<>());
	}
	
	@Test
	public void testCreateUserWithNotManagedRoles() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user: one or more roles to assign to new user not managed from logged id user.");
		
		session.login("usr1", "pwd1");
		
		Set<Role> roles = new HashSet<>();
		roles.add(r2);
		roles.add(r5);
		session.createUser( new User("usr8", "pwd8"), roles);
	}
	
	@Test
	public void testDeleteUser() {
		session.login("usr1", "pwd1");
		assertTrue(session.deleteUser(u3.getUsername()));
		assertTrue(session.deleteUser(u4.getUsername()));
		assertTrue(session.deleteUser(u2.getUsername()));
		Set<String> usersIds = userDao.findAllUsers()
										.stream()
											.map( u -> u.getId() )
											.collect(Collectors.toSet());
		assertEquals(4, usersIds.size());
		assertTrue(usersIds.contains(u1.getId()));
		assertTrue(usersIds.contains(u5.getId()));
		assertTrue(usersIds.contains(u6.getId()));
		assertTrue(usersIds.contains(u7.getId()));
	}
	
	@Test
	public void testDeleteUserFromSessionWithoutLoggedUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user: no users logged in.");
		
		session.deleteUser( u2.getUsername() );
	}
	
	@Test
	public void testDeleteNullUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user: user not found.");
		
		session.login("usr1", "pwd1");
		session.deleteUser( "user123" );
	}
	
	@Test
	public void testDeleteLoggedUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user: logged in user cannot delete himself.");
		
		session.login("usr1", "pwd1");
		session.deleteUser( u1.getUsername() );
	}
	
	@Test
	public void testDeleteUserWithNoManagedRoles() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user: logged in user not manages one or more roles of user to delete.");
		
		userDao.addRole(u2, r7);
		
		session.login("usr1", "pwd1");
		session.deleteUser( u2.getUsername() );
	}
	
	@Test
	public void testAddRole() {
		session.login("usr1", "pwd1");
		assertTrue(session.addRole(u2, r3));
		assertTrue(session.addRole(u4, r3));
	}
	
	@Test
	public void testAddRoleFromSessionWithoutLoggedUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add role to user: no users logged in.");
		
		session.addRole(u2, r3);
	}
	
	@Test
	public void testAddNotManagedRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add role to user: logged in user not manages role to add/remove.");
		
		session.login("usr1", "pwd1");
		session.addRole(u2, r7);
	}
	
	@Test
	public void testRemoveRole() {
		session.login("usr1", "pwd1");
		assertTrue(session.removeRole(u2, r2));
		assertTrue(session.removeRole(u4, r4));
	}
	
	@Test
	public void testRemoveRoleFromSessionWithoutLoggedUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to remove role to user: no users logged in.");
		
		session.removeRole(u2, r2);
	}
	
	@Test
	public void testRemoveNotManagedRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to remove role to user: logged in user not manages role to add/remove.");
		
		session.login("usr1", "pwd1");
		session.removeRole(u2, r7);
	}
	
}
