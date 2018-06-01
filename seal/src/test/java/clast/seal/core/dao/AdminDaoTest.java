package clast.seal.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import clast.seal.core.model.SealDb;
import clast.seal.core.model.User;
import nl.jqno.equalsverifier.EqualsVerifier;

public class AdminDaoTest {

	private AdminDao adminDao;
	private SealDb db;
	private Set<User> users;
	
	@Before
	public void setUp() {

		users = new HashSet<User>();

		db = mock(SealDb.class);
		when(db.getAllUsers()).thenReturn(users);
		when(db.addUser(any(User.class))).thenAnswer(new Answer<Boolean>() {
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return users.add(invocation.getArgumentAt(0, User.class));
			}
		});

		adminDao = new AdminDao(db);
	}

	@Test
	public void testUserCreation() {
		assertEquals(0, users.size());

		User user1 = createUser("user1", "pwd1", "name1", "lastname1", "email1", "phone1");
		assertTrue(adminDao.addUser(user1));
		verify(db, times(1)).addUser(user1);
		assertEquals(1, users.size());
		verifyUserFields(adminDao.getAllUsers().iterator().next(), "user1", "pwd1", "name1", "lastname1", "email1", "phone1");

		User user2 = createUserStepByStep("user2", "pwd2", "name2", "lastname2", "email2", "phone2");
		assertTrue(adminDao.addUser(user2));
		verify(db, times(1)).addUser(user1);
		assertEquals(2, users.size());
		verifyUserFields(adminDao.getAllUsers().iterator().next(), "user2", "pwd2", "name2", "lastname2", "email2", "phone2");

		User user3 = createUser("user1");
		assertFalse(adminDao.addUser(user3));
		verify(db, times(2)).addUser(user3);
		assertEquals(2, users.size());
	}

	@Test
	public void testNoUsers() {
		assertEquals(0, adminDao.getAllUsers().size());
	}

	@Test
	public void testOneUser() {
		adminDao.addUser(createUser("user1"));
		assertEquals(1, adminDao.getAllUsers().size());
		verify(db, times(1)).getAllUsers();
	}

	@Test
	public void testMoreUsers() {
		assertEquals(0, adminDao.getAllUsers().size());
		verify(db, times(1)).getAllUsers();

		adminDao.addUser(createUser("user1"));
		assertEquals(1, adminDao.getAllUsers().size());
		verify(db, times(2)).getAllUsers();

		adminDao.addUser(createUser("user2"));
		assertEquals(2, adminDao.getAllUsers().size());
		verify(db, times(3)).getAllUsers();

		adminDao.addUser(createUser("user3"));
		assertEquals(3, adminDao.getAllUsers().size());
		verify(db, times(4)).getAllUsers();
	}

	@Test
	public void testEqualUsers() {
		// @formatter:off
		EqualsVerifier.forClass(User.class)
						.withIgnoredFields("password", "name", "lastname", "email", "phone")
							.verify();
		// @formatter:on
	}

	private User createUser(String username) {
		return new User(username);
	}

	private User createUser(String username, String password, String name, String lastname, String email, String phone) {
		return new User(username, password, name, lastname, email, phone);
	}

	private User createUserStepByStep(String username, String password, String name, String lastname, String email, String phone) {
		User user = createUser(username);
		user.setPassword(password);
		user.setName(name);
		user.setLastname(lastname);
		user.setEmail(email);
		user.setPhone(phone);
		return user;
	}

	private void verifyUserFields(User user, String username, String password, String name, String lastname, String email, String phone) {
		assertEquals(username, user.getUsername());
		assertEquals(password, user.getPassword());
		assertEquals(name, user.getName());
		assertEquals(lastname, user.getLastname());
		assertEquals(email, user.getEmail());
		assertEquals(phone, user.getPhone());
	}

}
