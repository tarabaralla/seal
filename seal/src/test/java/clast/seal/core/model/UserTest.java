package clast.seal.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import nl.jqno.equalsverifier.EqualsVerifier;

public class UserTest {
	
	private User user;
	
	private Role cr1;
	private Role cr2;
	private Role cr3;
	private Role lr1;
	private Role lr2;
	private Role lr3;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setUp() {
		user = new User("user");
		cr1 = new CompositeRole("cr1");
		cr2 = new CompositeRole("cr2");
		cr3 = new CompositeRole("cr3");
		lr1 = new LeafRole("cr1");
		lr2 = new LeafRole("lr2");
		lr3 = new LeafRole("lr3");
	}

	@Test
	public void testEqualUsers() {
		// @formatter:off
		EqualsVerifier.forClass(User.class)
						.withIgnoredFields("password", "name", "lastname", "email", "phone", "roles")
							.withPrefabValues(Role.class, new LeafRole("r1"), new LeafRole("r2"))
								.verify();
		// @formatter:on
	}
	
	@Test
	public void testUserFields() {
		User user = createTestUser("usrUsername", "usrPassword", "usrName", "usrLastname", "usrEmail", "usrPhone");
		verifyUser(user, "usrUsername", "usrPassword", "usrName", "usrLastname", "usrEmail", "usrPhone");
	}
	
	@Test
	public void testAddAlreadyAssignedRole () {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Passed role already assigned to this user");
		user.addRole(cr1);
		user.addRole(lr1);
	}
	
	@Test
	public void testAddAlreadyAssignedRoleAsSubRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Passed role already assigned to this user");
		user.addRole(cr1);
		cr1.addSubRole(lr2);
		user.addRole(lr2);
	}
	
	@Test
	public void testAddParentRoleOfAnAlreadyAssignedRole() {
		user.addRole(lr2);
		user.addRole(cr2);
		user.addRole(lr3);
		cr1.addSubRole(lr2);
		cr1.addSubRole(cr3);
		cr3.addSubRole(lr3);
		
		assertEquals(3, user.getDirectRoles().size());
		assertEquals(3, user.getAllRoles().size());
		assertTrue(user.addRole(cr1));
		assertEquals(2, user.getDirectRoles().size());
		assertEquals(5, user.getAllRoles().size());
	}
	
	@Test
	public void testRemoveDirectRole() {
		user.addRole(cr1);
		user.addRole(cr2);
		user.addRole(lr3);
		cr1.addSubRole(lr2);
		assertTrue( user.removeRole(cr1));
		assertTrue( user.removeRole(cr2));
		assertTrue( user.removeRole(lr3));
	}
	
	@Test
	public void testRemoveNotAssignedRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage( "Role " + lr3.getName() + " to remove isn't assigned to user " + user.getName() );
		user.addRole(cr1);
		user.addRole(cr2);
		cr1.addSubRole(lr2);
		user.removeRole(lr3);
	}
	
	@Test
	public void testRemoveSubRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage( "Only direct role can be removed from users. Role " + lr2.getName() + " is a sub-role for User " + user.getName() );
		user.addRole(cr1);
		user.addRole(cr2);
		cr1.addSubRole(lr2);
		user.removeRole(lr2);
	}
	
	@Test
	public void testHasRole() {
		user.addRole(cr2);
		user.addRole(cr1);
		cr1.addSubRole(lr2);
		assertTrue( user.hasRole(cr2));
		assertTrue( user.hasRole(cr1));
		assertTrue( user.hasRole(lr2));
		assertFalse( user.hasRole(lr3));
	}
	
	@Test
	public void testGetDirectRoles() {
		user.addRole(lr2);
		user.addRole(cr1);
		cr1.addSubRole(cr2);
		cr1.addSubRole(cr3);
		cr3.addSubRole(lr3);
		assertEquals(2, user.getDirectRoles().size());
		assertTrue( user.hasRole(lr2));
		assertTrue( user.hasRole(cr1));
		assertTrue( user.hasRole(cr2));
		assertTrue( user.hasRole(cr3));
		assertTrue( user.hasRole(lr3));
	}
	
	@Test
	public void testGetAllRoles() {
		user.addRole(lr2);
		user.addRole(cr1);
		cr1.addSubRole(cr2);
		cr1.addSubRole(cr3);
		cr3.addSubRole(lr3);
		assertEquals(5, user.getAllRoles().size());
		assertTrue( user.hasRole(lr2));
		assertTrue( user.hasRole(cr1));
		assertTrue( user.hasRole(cr2));
		assertTrue( user.hasRole(cr3));
		assertTrue( user.hasRole(lr3));
	}
	
	private User createTestUser(String username, String password, String name, String lastname, String email, String phone) {
		User user = new User(username);
		user.setPassword(password);
		user.setName(name);
		user.setLastname(lastname);
		user.setEmail(email);
		user.setPhone(phone);
		return user;
	}
	
	private void verifyUser(User user, String username, String password, String name, String lastname, String email, String phone) {
		assertEquals(username, user.getUsername());
		assertEquals(password, user.getPassword());
		assertEquals(name, user.getName());
		assertEquals(lastname, user.getLastname());
		assertEquals(email, user.getEmail());
		assertEquals(phone, user.getPhone());
	}

}
