package clast.seal.mongo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import clast.seal.core.model.User;
import clast.seal.core.service.WeldJUnit4Runner;
import clast.seal.core.service.user.US_Type;
import clast.seal.core.service.user.UserService;
import clast.seal.core.service.user.UserServiceProducer;
import clast.seal.core.service.user.UserServiceType;
import clast.seal.mongo.persistence.Database;

@RunWith(WeldJUnit4Runner.class)
public class MongoUserServiceTest {

	@Inject
	@UserServiceProducer
	@US_Type(UserServiceType.MONGO_DB_US)
	private UserService mongoUserService;
	
	private Database db;
	
	private Set<User> users;
	
	@Before
	public void setUp() {
		users = new HashSet<User>();

		db = mock(Database.class);
		when(db.findAllUsers()).thenReturn(users);
		when(db.createUser(any(User.class))).thenAnswer(new Answer<Boolean>() {
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return users.add(invocation.getArgumentAt(0, User.class));
			}
		});
		
		((MongoService) mongoUserService).setDatabase(db);
	}
	
	@Test
	public void testMongoUserServiceInjection() {
		assertTrue(mongoUserService instanceof MongoUserService);
	}
	
	@Test
	public void testMongoUserCreation() {
		assertEquals(0, users.size());

		User user1 = createTestUser("user1");
		assertTrue(mongoUserService.createUser(user1));
		verify(db, times(1)).createUser(user1);
		assertEquals(1, users.size());
		assertEquals(user1, mongoUserService.findAllUsers().iterator().next());

		User user2 = createTestUser("user2");
		assertTrue(mongoUserService.createUser(user2));
		verify(db, times(1)).createUser(user1);
		assertEquals(2, users.size());
		assertEquals(user2, mongoUserService.findAllUsers().iterator().next());

		User user3 = createTestUser("user1");
		assertFalse(mongoUserService.createUser(user3));
		verify(db, times(2)).createUser(user3);
		assertEquals(2, users.size());
	}
	

	@Test
	public void testNoUsersFound() {
		assertEquals(0, mongoUserService.findAllUsers().size());
		verify(db, times(1)).findAllUsers();
	}
	
	@Test
	public void testOneUserFound() {
		mongoUserService.createUser(createTestUser("user1"));
		assertEquals(1, mongoUserService.findAllUsers().size());
		verify(db, times(1)).findAllUsers();
	}
	
	@Test
	public void testMoreUsersFound() {
		assertEquals(0, mongoUserService.findAllUsers().size());
		verify(db, times(1)).findAllUsers();

		mongoUserService.createUser(createTestUser("user1"));
		assertEquals(1, mongoUserService.findAllUsers().size());
		verify(db, times(2)).findAllUsers();

		mongoUserService.createUser(createTestUser("user2"));
		assertEquals(2, mongoUserService.findAllUsers().size());
		verify(db, times(3)).findAllUsers();

		mongoUserService.createUser(createTestUser("user3"));
		assertEquals(3, mongoUserService.findAllUsers().size());
		verify(db, times(4)).findAllUsers();
	}
	
	private User createTestUser(String username) {
		return new User(username);
	}

}
