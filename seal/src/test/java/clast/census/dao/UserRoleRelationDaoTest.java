package clast.census.dao;

import static org.junit.Assert.assertEquals;
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
import clast.census.model.UserRoleRelation;

public class UserRoleRelationDaoTest extends BaseTest {

	private UserRoleRelationDao userRoleRelationDao;
	private UserDao userDao;
	private RoleDao roleDao;
	
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
	private Role r7;
	
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
	}
	
	@After
	public void tearDown() {
		userDao.deleteAllUsers();
		roleDao.deleteAllRoles();
	}
	
	@Test
	public void createUserRoleRelation() {
		assertTrue(userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r4.getId())));
		assertTrue(userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r3.getId())));
		assertTrue(userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r1.getId())));
		assertTrue(userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r6.getId())));
		assertTrue(userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r5.getId())));
		assertTrue(userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r7.getId())));

		assertTrue(userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r4.getId())));
		assertTrue(userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r3.getId())));

		assertTrue(userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r3.getId())));
		assertTrue(userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r1.getId())));
	
		Set<String> roleIds = userRoleRelationDao.findUserRoleRelations(u1.getId(), null).stream().map( urr -> urr.getRoleId() ).collect(Collectors.toSet());
		assertEquals(3, roleIds.size());
		assertTrue(roleIds.contains(r1.getId()));
		assertTrue(roleIds.contains(r5.getId()));
		assertTrue(roleIds.contains(r7.getId()));

		roleIds = userRoleRelationDao.findUserRoleRelations(u2.getId(), null).stream().map( urr -> urr.getRoleId() ).collect(Collectors.toSet());
		assertEquals(2, roleIds.size());
		assertTrue(roleIds.contains(r3.getId()));
		assertTrue(roleIds.contains(r4.getId()));
		
		roleIds = userRoleRelationDao.findUserRoleRelations(u3.getId(), null).stream().map( urr -> urr.getRoleId() ).collect(Collectors.toSet());
		assertEquals(0, roleIds.size());
		
		roleIds = userRoleRelationDao.findUserRoleRelations(u4.getId(), null).stream().map( urr -> urr.getRoleId() ).collect(Collectors.toSet());
		assertEquals(1, roleIds.size());
		assertTrue(roleIds.contains(r1.getId()));
		
	}
	
	
	@Test
	public void testCreateNullUserRoleRelation() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user-role relation: IDs of user and role to associate cannot be null.");
		
		userRoleRelationDao.createUserRoleRelation(null);
	}
	
	@Test
	public void testCreateUserRoleRelationWithNullUserId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user-role relation: IDs of user and role to associate cannot be null.");
		
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(null, r1.getId()));
	}
	
	@Test
	public void testCreateUserRoleRelationWithNullRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user-role relation: IDs of user and role to associate cannot be null.");
		
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), null));
	}
	
	@Test
	public void testCreateUserRoleRelationWithNullFields() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user-role relation: IDs of user and role to associate cannot be null.");
		
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(null, null));
	}
	
	@Test
	public void testCreatePersistedUserRoleRelation() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user-role relation: UserRoleRelation is already persisted.");
		
		UserRoleRelation urr = createTestUserRoleRelation(u1.getId(), r1.getId());
		userRoleRelationDao.createUserRoleRelation(urr);
		userRoleRelationDao.createUserRoleRelation(urr);
	}
	
	@Test
	public void testCreateUserRoleRelationWithUnexistingUserId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user-role relation: User and Role to associate must already be persisted.");
		
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), "123"));
	}
	
	@Test
	public void testCreateUserRoleRelationWithUnexistingRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user-role relation: User and Role to associate must already be persisted.");
		
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation("123", r1.getId()));
	}
	
	@Test
	public void testCreateUserRoleRelationWithUnexistingUserIdAndRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user-role relation: User and Role to associate must already be persisted.");
		
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation("123", "456"));
	}
	
	@Test
	public void testCreateExistingUserRoleRelation() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user-role relation: Role: " + r1.getId() + " is already assigned to User: " + u1.getId());
		
		userRoleRelationDao.createUserRoleRelation( createTestUserRoleRelation(u1.getId(), r1.getId()) );
		userRoleRelationDao.createUserRoleRelation( createTestUserRoleRelation(u1.getId(), r1.getId()) );
	}
	
	@Test
	public void testCreateUserSubRoleRelation() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create user-role relation: Role: " + r2.getId() + " is SubRole of Role: " + r1.getId() + ", already assigned to User: " + u1.getId());
		
		userRoleRelationDao.createUserRoleRelation( createTestUserRoleRelation(u1.getId(), r1.getId()) );
		userRoleRelationDao.createUserRoleRelation( createTestUserRoleRelation(u1.getId(), r2.getId()) );
	}
	
	@Test
	public void testDeleteUserRoleRelation() {
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r3.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r4.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r2.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r6.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r1.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r5.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r7.getId()));
		
		userRoleRelationDao.deleteUserRoleRelation(createTestUserRoleRelation(u1.getId(), r3.getId()));
		userRoleRelationDao.deleteUserRoleRelation(createTestUserRoleRelation(u2.getId(), r6.getId()));
		userRoleRelationDao.deleteUserRoleRelation(createTestUserRoleRelation(u4.getId(), r7.getId()));
		
		Set<String> roleIds = userRoleRelationDao.findUserRoleRelations(u1.getId(), null).stream().map( urr -> urr.getRoleId() ).collect(Collectors.toSet());
		assertEquals(1, roleIds.size());
		assertTrue(roleIds.contains(r4.getId()));

		roleIds = userRoleRelationDao.findUserRoleRelations(u2.getId(), null).stream().map( urr -> urr.getRoleId() ).collect(Collectors.toSet());
		assertEquals(1, roleIds.size());
		assertTrue(roleIds.contains(r2.getId()));
		
		roleIds = userRoleRelationDao.findUserRoleRelations(u4.getId(), null).stream().map( urr -> urr.getRoleId() ).collect(Collectors.toSet());
		assertEquals(2, roleIds.size());
		assertTrue(roleIds.contains(r1.getId()));
		assertTrue(roleIds.contains(r5.getId()));
	}
	
	@Test
	public void testDeleteNullUserRoleRelation() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user-role relation: IDs of user and role cannot be null.");
		
		userRoleRelationDao.deleteUserRoleRelation(null);
	}
	
	@Test
	public void testDeleteUserRoleRelationWithNullUserId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user-role relation: IDs of user and role cannot be null.");
		
		userRoleRelationDao.deleteUserRoleRelation( createTestUserRoleRelation(null, r1.getId()));
	}
	
	@Test
	public void testDeleteUserRoleRelationWithNullRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user-role relation: IDs of user and role cannot be null.");
		
		userRoleRelationDao.deleteUserRoleRelation( createTestUserRoleRelation(u1.getId(), null));
	}
	
	@Test
	public void testDeleteUserRoleRelationWithNullUserIdAndRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user-role relation: IDs of user and role cannot be null.");
		
		userRoleRelationDao.deleteUserRoleRelation( createTestUserRoleRelation(null, null));
	}
	
	@Test
	public void testDeleteUnexistingUserRoleRelation1() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user-role relation: Relation between User: " + u1.getId() + " and Role: " + r1.getId() + " not exist.");
		
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r3.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r4.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r2.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r6.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r1.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r5.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r7.getId()));
		
		userRoleRelationDao.deleteUserRoleRelation(createTestUserRoleRelation(u1.getId(), r1.getId()));
	}
	
	@Test
	public void testDeleteUnexistingUserRoleRelation2() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user-role relation: Relation between User: " + u2.getId() + " and Role: " + r5.getId() + " not exist.");
		
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r3.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r4.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r2.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r6.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r1.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r5.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r7.getId()));
		
		userRoleRelationDao.deleteUserRoleRelation(createTestUserRoleRelation(u2.getId(), r5.getId()));
	}
	
	@Test
	public void testDeleteUnexistingUserRoleRelation3() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user-role relation: Relation between User: " + u3.getId() + " and Role: " + r1.getId() + " not exist.");
		
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r3.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r4.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r2.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r6.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r1.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r5.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r7.getId()));
		
		userRoleRelationDao.deleteUserRoleRelation(createTestUserRoleRelation(u3.getId(), r1.getId()));
	}
	
	@Test
	public void testDeleteUnexistingUserRoleRelation4() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete user-role relation: Relation between User: " + u4.getId() + " and Role: " + r4.getId() + " not exist.");
		
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r3.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r4.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r2.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r6.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r1.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r5.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r7.getId()));
		
		userRoleRelationDao.deleteUserRoleRelation(createTestUserRoleRelation(u4.getId(), r4.getId()));
	}
	
	@Test
	public void testFindUserRoleRelations() {
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r3.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u1.getId(), r4.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r4.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r6.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u2.getId(), r7.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u3.getId(), r7.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r1.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r5.getId()));
		userRoleRelationDao.createUserRoleRelation(createTestUserRoleRelation(u4.getId(), r7.getId()));
		
		Set<String> roleIds = userRoleRelationDao.findUserRoleRelations(null, null).stream().map( urr -> urr.toString() ).collect(Collectors.toSet());
		assertTrue(roleIds.contains(stringOf(u1.getId(), r3.getId())));
		assertTrue(roleIds.contains(stringOf(u1.getId(), r4.getId())));
		assertTrue(roleIds.contains(stringOf(u2.getId(), r4.getId())));
		assertTrue(roleIds.contains(stringOf(u2.getId(), r6.getId())));
		assertTrue(roleIds.contains(stringOf(u2.getId(), r7.getId())));
		assertTrue(roleIds.contains(stringOf(u3.getId(), r7.getId())));
		assertTrue(roleIds.contains(stringOf(u4.getId(), r1.getId())));
		assertTrue(roleIds.contains(stringOf(u4.getId(), r5.getId())));
		assertTrue(roleIds.contains(stringOf(u4.getId(), r7.getId())));
		assertEquals(9, roleIds.size());
		
		roleIds = userRoleRelationDao.findUserRoleRelations(null, r7.getId()).stream().map( urr -> urr.toString() ).collect(Collectors.toSet());
		assertEquals(3, roleIds.size());
		assertTrue(roleIds.contains(stringOf(u2.getId(), r7.getId())));
		assertTrue(roleIds.contains(stringOf(u3.getId(), r7.getId())));
		assertTrue(roleIds.contains(stringOf(u4.getId(), r7.getId())));
		
		roleIds = userRoleRelationDao.findUserRoleRelations(u1.getId(), null).stream().map( urr -> urr.toString() ).collect(Collectors.toSet());
		assertEquals(2, roleIds.size());
		assertTrue(roleIds.contains(stringOf(u1.getId(), r3.getId())));
		assertTrue(roleIds.contains(stringOf(u1.getId(), r4.getId())));
		
		roleIds = userRoleRelationDao.findUserRoleRelations(u2.getId(), r6.getId()).stream().map( urr -> urr.toString() ).collect(Collectors.toSet());
		assertEquals(1, roleIds.size());
		assertTrue(roleIds.contains(stringOf(u2.getId(), r6.getId())));
		
		roleIds = userRoleRelationDao.findUserRoleRelations(u2.getId(), r5.getId()).stream().map( urr -> urr.toString() ).collect(Collectors.toSet());
		assertEquals(0, roleIds.size());
	}

	private UserRoleRelation createTestUserRoleRelation(String userId, String roleId) {
		UserRoleRelation urr = new UserRoleRelation();
		urr.setUserId(userId);
		urr.setRoleId(roleId);
		return urr;
	}

	private String stringOf(String userId, String roleId) {
		return userId + "-" + roleId;
	}

}
