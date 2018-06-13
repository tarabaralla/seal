package clast.seal.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

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
		role = new CompositeRole( UUID.randomUUID().toString() );
		cr1 = new CompositeRole( UUID.randomUUID().toString() );
		cr2 = new CompositeRole( UUID.randomUUID().toString() );
	}

	@Test
	public void testEqualRoles() {
		// @formatter:off
		EqualsVerifier.forClass(Role.class)
						.withOnlyTheseFields("uuid")
						.withPrefabValues(Role.class, new LeafRole("lr1"), new LeafRole("lr2"))
						.suppress(Warning.NONFINAL_FIELDS)
						.verify();
		// @formatter:on
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
	public void testgetDirectManagedRole() {
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

}
