package clast.seal.core.dao;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import clast.seal.core.model.Role;
import clast.seal.core.model.User;
import clast.seal.core.model.UserRoleRelation;

public class UserRoleRelationDaoTest {

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
		
		userRoleRelationDao = new UserRoleRelationDao();
		userDao = new UserDao();
		roleDao = new RoleDao();
		
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
		expectedEx.expectMessage("UserRoleRelation is already persisted.");
		
		UserRoleRelation urr = createTestUserRoleRelation(u1.getId(), r1.getId());
		userRoleRelationDao.createUserRoleRelation(urr);
		userRoleRelationDao.createUserRoleRelation(urr);
	}
	
	//TODO completare i test. VEDI ANCHE TODO SUL QUADERNO !!

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
