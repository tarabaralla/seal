package clast.seal.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

public class RoleTest {
	
	private Role role;
	
	private Role cr1;
	private Role cr2;
	
	@Before
	public void setUp() {
		role = new CompositeRole("role");
		cr1 = new CompositeRole("cr1");
		cr2 = new CompositeRole("cr2");
	}

	@Test
	public void testEqualRoles() {
		// @formatter:off
		EqualsVerifier.forClass(Role.class)
						.withOnlyTheseFields("id","name")
						.withPrefabValues(Role.class, createTestLeafRole("lr1", "leafRole1"), createTestCompositeRole("lr1", null))
						.suppress(Warning.NONFINAL_FIELDS)
						.verify();
		// @formatter:on
	}
	
	
	@Test
	public void testRoleFields() {
		Role role = createTestLeafRole("lr1", "leafRole1");
		verifyRole(role, "lr1", "leafRole1");
		
		role = createTestCompositeRole("cr1", "compositeRole1");
		verifyRole(role, "cr1", "compositeRole1");
	}
	
	@Test
	public void testAddManagedRole() {
		assertEquals(0, role.getDirectManagedRoles().size());
		
		assertTrue(role.addManagedRole(role));
		assertEquals(1, role.getDirectManagedRoles().size());
		
		assertTrue(role.addManagedRole(cr1));
		assertEquals(2, role.getDirectManagedRoles().size());
		
		assertFalse(role.addManagedRole(cr1));
		assertEquals(2, role.getDirectManagedRoles().size());
	}
	
	@Test
	public void testRemoveManagedRole() {
		role.addManagedRole(cr1);
		assertTrue(role.removeManagedRole(cr1));
		assertFalse(role.removeManagedRole(cr2));
	}
	
	@Test
	public void testManagesRole() {
		role.addManagedRole(cr1);
		assertTrue(role.managesRole(cr1));
		assertFalse(role.managesRole(cr2));
	}
	
	@Test
	public void testGetDirectManagedRole() {
		role.addManagedRole(role);
		role.addSubRole(cr1);
		cr1.addManagedRole(cr2);
		assertEquals(1, role.getDirectManagedRoles().size());
	}
	
	@Test
	public void testgetAllManagedRole() {
		role.addManagedRole(role);
		role.addSubRole(cr1);
		cr1.addManagedRole(cr2);
		assertEquals(2, role.getAllManagedRoles().size());
	}
	
	private Role createTestCompositeRole(String id, String name) {
		Role role = new CompositeRole();
		role.setId(id);
		role.setName(name);
		return role;
	}
	
	private Role createTestLeafRole(String id, String name) {
		Role role = new LeafRole();
		role.setId(id);
		role.setName(name);
		return role;
	}
	
	private void verifyRole(Role role, String id, String name) {
		assertEquals(id, role.getId());
		assertEquals(name, role.getName());
	}

}
