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
	
	AdminDao adminDao;
	
	SealDb db;
	
	Set<User> users;
	
	User user1;
	
	@Before
	public void setUp() {
		
		users = new HashSet<User>();
		
		db = mock(SealDb.class);
		when(db.getAllUsers()).thenReturn(users);
		when(db.addUser(any(User.class))).thenAnswer(new Answer<Boolean>() {
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return !users.contains(invocation.getArgumentAt(0, User.class));
			}
		});
		
		adminDao = new AdminDao(db);
	}
	
	@Test
	public void testUserCreation() {
		assertEquals( 0, users.size() );

		User user1 = createUser("user1");
		assertTrue( adminDao.addUser(user1) );
		users.add( user1 );
		verify( db, times(1) ).addUser(user1);
		assertEquals(1, users.size());
		
		User user2 = createUser("user2");
		assertTrue( adminDao.addUser(user2) );
		verify( db, times(1) ).addUser(user1);
		users.add( user2 );
		assertEquals(2, users.size());
		
		User user3 = createUser("user1");
		assertFalse( adminDao.addUser(user3) );
		verify( db, times(2) ).addUser(user3);
		users.add( user3 );
		assertEquals( 2, users.size() );
	}

	@Test
	public void testNoUsers() {
		assertEquals( 0, adminDao.getAllUsers().size() );
	}
	
	@Test
	public void testOneUser() {
		users.add( createUser("user1") );
		assertEquals( 1, adminDao.getAllUsers().size() );
		verify( db, times(1) ).getAllUsers();
		assertEquals("user1", adminDao.getAllUsers().iterator().next().getUsername() );
	}
	
	@Test
	public void testMoreUsers() {
		assertEquals( 0, adminDao.getAllUsers().size() );
		verify( db, times(1) ).getAllUsers();
		
		users.add( createUser("user1") );
		assertEquals( 1, adminDao.getAllUsers().size() );
		verify( db, times(2) ).getAllUsers();
		
		users.add( createUser("user2") );
		assertEquals( 2, adminDao.getAllUsers().size() );
		verify( db, times(3) ).getAllUsers();
		
		users.add( createUser("user3") );
		assertEquals( 3, adminDao.getAllUsers().size() );
		verify( db, times(4) ).getAllUsers();
	}
	
	@Test
	public void testEqualUsers() {
		EqualsVerifier.forClass(User.class).verify();
	}
	
	private User createUser(String username) {
		return new User(username);
	}

}
