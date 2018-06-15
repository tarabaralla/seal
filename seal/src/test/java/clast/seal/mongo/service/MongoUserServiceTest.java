package clast.seal.mongo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.impetus.kundera.KunderaException;

import clast.seal.core.model.User;
import clast.seal.core.service.WeldJUnit4Runner;
import clast.seal.core.service.user.US_Type;
import clast.seal.core.service.user.UserService;
import clast.seal.core.service.user.UserServiceProducer;
import clast.seal.core.service.user.UserServiceType;

@RunWith(WeldJUnit4Runner.class)
public class MongoUserServiceTest {
	
	@Inject
	@UserServiceProducer
	@US_Type(UserServiceType.MONGO_DB_US)
	private UserService mongoUserService;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testMongoUserServiceInjection() {
		assertTrue(mongoUserService instanceof MongoUserService);
	}
	
	@Test
	public void testCreateUser() {
		assertTrue( mongoUserService.createUser( createTestUser("user1", "password1") ) );
	}
	
	@Test
	public void testCreateUserWithoutUsername() {
		expectedEx.expect(KunderaException.class);
		
		User user = new User();
		user.setPassword("password1");
		mongoUserService.createUser(user);
	}
	
	@Test
	public void testCreateUserWithoutPassword() {
		expectedEx.expect(KunderaException.class);
		
		User user = new User();
		user.setUsername("user1");
		mongoUserService.createUser(user);
	}
	
	@Test
	public void testCreateExistingUser() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Cannot create user: a user with same id already exist.");
		
		User user = createTestUser("user1", "password1");
		
		mongoUserService.createUser(user);
		mongoUserService.createUser(user);
	}
	
	@Test
	public void testCreateUserWithOccupiedUsername() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Cannot create user: username already assigned to another user.");
		
		User user1 = createTestUser("user1", "password1");
		mongoUserService.createUser(user1);

		User user2 = createTestUser("user1", "password2");
		mongoUserService.createUser(user2);
	}
	
	@Test
	public void testfindAllUsers() {
		
		assertEquals(0, mongoUserService.findAllUsers().size());
		
		mongoUserService.createUser( createTestUser("user1", "pwd1") );
		Set<User> users = mongoUserService.findAllUsers();
		assertEquals(1, users.size());
		assertEquals("user1", users.iterator().next().getUsername());

		mongoUserService.createUser( createTestUser("user2", "pwd2") );
		users = mongoUserService.findAllUsers();
		Set<String> usernames = users.stream()
				.map( un -> un.getUsername() )
				.collect( Collectors.toSet() );
		assertEquals(2, users.size());
		assertTrue(usernames.contains("user1"));
		assertTrue(usernames.contains("user2"));
	}
	
	private User createTestUser(String username, String password) {
		return new User(username, password);
	}

}
