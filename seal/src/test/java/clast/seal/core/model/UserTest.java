package clast.seal.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

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
		user = new User( UUID.randomUUID().toString() );
		cr1 = new CompositeRole( UUID.randomUUID().toString() );
		cr2 = new CompositeRole( UUID.randomUUID().toString() );
		cr3 = new CompositeRole( UUID.randomUUID().toString() );
		lr1 = new LeafRole( UUID.randomUUID().toString() );
		lr2 = new LeafRole( UUID.randomUUID().toString() );
	}

	@Test
	public void testEqualUsers() {
		// @formatter:off
		EqualsVerifier.forClass(User.class)
						.withOnlyTheseFields("uuid")
						.withPrefabValues(Role.class, new LeafRole("r1"), new LeafRole("r2"))
						.suppress(Warning.NONFINAL_FIELDS)
						.verify();
		// @formatter:on
	}
	
	@Test
	public void testUserFields() {
		User user = createTestUser(1l,"uuid1","usrUsername", "usrPassword", "usrName", "usrLastname", "usrEmail", "usrPhone");
		verifyUser(1l, "uuid1", user, "usrUsername", "usrPassword", "usrName", "usrLastname", "usrEmail", "usrPhone");
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
	
	private User createTestUser(Long id, String uuid, String username, String password, String name, String lastname, String email, String phone) {
		User user = new User();
		user.setId(id);
		user.setUuid(uuid);
		user.setUsername(username);
		user.setPassword(password);
		user.setName(name);
		user.setLastname(lastname);
		user.setEmail(email);
		user.setPhone(phone);
		return user;
	}
	
	private void verifyUser(Long id, String uuid, User user, String username, String password, String name, String lastname, String email, String phone) {
		assertEquals(id, user.getId());
		assertEquals(uuid, user.getUuid());
		assertEquals(username, user.getUsername());
		assertEquals(username, user.getUsername());
		assertEquals(password, user.getPassword());
		assertEquals(name, user.getName());
		assertEquals(lastname, user.getLastname());
		assertEquals(email, user.getEmail());
		assertEquals(phone, user.getPhone());
	}

}
