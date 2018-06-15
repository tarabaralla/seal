package clast.seal.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class UserTest {
	
	private User user;
	
	private Role cr1;
	private Role cr2;
	private Role cr3;
	private Role lr1;
	private Role lr2;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setUp() {
		user = new User("usrUsername", "usrPassword");
		cr1 = new CompositeRole("cr1");
		cr2 = new CompositeRole("cr2");
		cr3 = new CompositeRole("cr3");
		lr1 = new LeafRole("lr1");
		lr2 = new LeafRole("lr2");
	}

	@Test
	public void testEqualUsers() {
		// @formatter:off
		EqualsVerifier.forClass(User.class)
						.withOnlyTheseFields("id","username")
						.withPrefabValues(User.class, createTestUser(null, "usrUsername1", "usrPassword1", "usrName1", "usrLastname1", "usrEmail1", "usrPhone1"), createTestUser("usr2", null, "usrPassword2", "usrName2", "usrLastname2", "usrEmail2", "usrPhone2"))
						.withPrefabValues(Role.class, createTestLeafRole(null, "leafRole1"), createTestLeafRole("lr1", null))
						.suppress(Warning.NONFINAL_FIELDS)
						.verify();
		// @formatter:on
	}
	
	@Test
	public void testUserFields() {
		User user = createTestUser("usr1", "usrUsername", "usrPassword", "usrName", "usrLastname", "usrEmail", "usrPhone");
		verifyUser(user, "usr1", "usrUsername", "usrPassword", "usrName", "usrLastname", "usrEmail", "usrPhone");
	}
	
	@Test
	public void testAddAlreadyAssignedRole () {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Passed role already assigned to this user");
		user.addRole(cr1);
		user.addRole(cr1);
	}
	
	@Test
	public void testAddAlreadyAssignedRoleAsSubRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Passed role already assigned to this user");
		user.addRole(cr1);
		cr1.addSubRole(lr1);
		user.addRole(lr1);
	}
	
	@Test
	public void testAddParentRoleOfAnAlreadyAssignedRole() {
		user.addRole(lr1);
		user.addRole(cr2);
		user.addRole(lr2);
		cr1.addSubRole(lr1);
		cr1.addSubRole(cr3);
		cr3.addSubRole(lr2);
		
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
		user.addRole(lr2);
		cr1.addSubRole(lr1);
		assertTrue( user.removeRole(cr1));
		assertTrue( user.removeRole(cr2));
		assertTrue( user.removeRole(lr2));
	}
	
	@Test
	public void testRemoveNotAssignedRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage( "Role " + lr2.getName() + " to remove isn't assigned to user " + user.getName() );
		user.addRole(cr1);
		user.addRole(cr2);
		cr1.addSubRole(lr1);
		user.removeRole(lr2);
	}
	
	@Test
	public void testRemoveSubRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage( "Only direct role can be removed from users. Role " + lr1.getName() + " is a sub-role for User " + user.getName() );
		user.addRole(cr1);
		user.addRole(cr2);
		cr1.addSubRole(lr1);
		user.removeRole(lr1);
	}
	
	@Test
	public void testHasRole() {
		user.addRole(cr2);
		user.addRole(cr1);
		cr1.addSubRole(lr1);
		assertTrue( user.hasRole(cr2));
		assertTrue( user.hasRole(cr1));
		assertTrue( user.hasRole(lr1));
		assertFalse( user.hasRole(lr2));
	}
	
	@Test
	public void testHasDirectRole() {
		user.addRole(lr1);
		user.addRole(cr1);
		cr1.addSubRole(cr2);
		cr1.addSubRole(cr3);
		cr3.addSubRole(lr2);
		assertTrue(user.hasDirectRole(lr1));
		assertTrue(user.hasDirectRole(cr1));
		assertFalse(user.hasDirectRole(cr2));
		assertFalse(user.hasDirectRole(cr3));
		assertFalse(user.hasDirectRole(lr2));
	}
	
	@Test
	public void testGetDirectRoles() {
		user.addRole(lr1);
		user.addRole(cr1);
		cr1.addSubRole(cr2);
		cr1.addSubRole(cr3);
		cr3.addSubRole(lr2);
		assertEquals(2, user.getDirectRoles().size());
		assertTrue( user.hasRole(lr1));
		assertTrue( user.hasRole(cr1));
		assertTrue( user.hasRole(cr2));
		assertTrue( user.hasRole(cr3));
		assertTrue( user.hasRole(lr2));
	}
	
	@Test
	public void testGetAllRoles() {
		user.addRole(lr1);
		user.addRole(cr1);
		cr1.addSubRole(cr2);
		cr1.addSubRole(cr3);
		cr3.addSubRole(lr2);
		assertEquals(5, user.getAllRoles().size());
		assertTrue( user.hasRole(lr1));
		assertTrue( user.hasRole(cr1));
		assertTrue( user.hasRole(cr2));
		assertTrue( user.hasRole(cr3));
		assertTrue( user.hasRole(lr2));
	}
	
	private User createTestUser(String id, String username, String password, String name, String lastname, String email, String phone) {
		User user = new User();
		user.setId(id);
		user.setUsername(username);
		user.setPassword(password);
		user.setName(name);
		user.setLastname(lastname);
		user.setEmail(email);
		user.setPhone(phone);
		return user;
	}
	
	private void verifyUser(User user, String id, String username, String password, String name, String lastname, String email, String phone) {
		assertEquals(id, user.getId());
		assertEquals(username, user.getUsername());
		assertEquals(username, user.getUsername());
		assertEquals(password, user.getPassword());
		assertEquals(name, user.getName());
		assertEquals(lastname, user.getLastname());
		assertEquals(email, user.getEmail());
		assertEquals(phone, user.getPhone());
	}
	
	private Role createTestLeafRole(String id, String name) {
		Role role = new LeafRole();
		role.setId(id);
		role.setName(name);
		return role;
	}

}
