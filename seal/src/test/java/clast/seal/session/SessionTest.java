package clast.seal.session;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import clast.seal.dao.RoleDao;
import clast.seal.dao.UserDao;
import clast.seal.model.Role;
import clast.seal.model.User;

@RunWith(WeldJUnit4Runner.class)
public class SessionTest {

	@Inject
	@SessionProducer
	private Session session;
	
	private RoleDao roleDao;
	private UserDao userDao;
	
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
	
	@Before
	public void setUp() {
		
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
		roleDao.createRole(r1);
		roleDao.createRole(r2);
		roleDao.createRole(r3);
		roleDao.createRole(r4);
		roleDao.createRole(r5);
		
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r4, r5);
		
		roleDao.addManagedRole(r1, r2);
		roleDao.addManagedRole(r1, r3);
		roleDao.addManagedRole(r4, r4);
		roleDao.addManagedRole(r4, r5);

	}
	
	@After
	public void tearDown() {
		userDao.deleteAllUsers();
		roleDao.deleteAllRoles();
	}
	
	@Test
	public void testLogin() {
		
		assertNotNull(session);
		
//		assertTrue(session.login("usr1", "pwd1"));
//		assertEquals(u1.getId(), session.getLoggedUser().getId());
//		assertEquals(u1.getUsername(), session.getLoggedUser().getUsername());
	}

}
