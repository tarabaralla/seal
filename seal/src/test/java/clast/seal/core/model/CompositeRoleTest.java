package clast.seal.core.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CompositeRoleTest {

	private Role role;
	
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
		role = new CompositeRole("compositeRole");
		cr1 = new CompositeRole("role");
		cr2 = new CompositeRole("cr2");
		cr3 = new CompositeRole("cr3");
		lr1 = new LeafRole("role");
		lr2 = new LeafRole("lr2");
		lr3 = new LeafRole("lr3");
	}
	
	@Test
	public void testAddSubRole() {
		role.addSubRole(cr1);
		role.addSubRole(cr2);
		cr1.addSubRole(cr3);
		cr1.addSubRole(lr2);
		cr3.addSubRole(lr3);
		assertEquals(5, role.getAllSubRoles().size());
	}

	@Test
	public void testAddItself() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("A role cannot be sub-role of him self");
		role.addSubRole(role);	
	}
	
	@Test
	public void testAddAlreadyPresentSubRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("A role cannot add a sub-role that is already present in its sub-roles tree");
		role.addSubRole(cr1);
		role.addSubRole(lr1);
	}
	
	@Test
	public void testAddRoleAlreadyPresentInItsSubRolesTree() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("A role cannot add a sub-role that is already present in its sub-roles tree");
		role.addSubRole(cr1);
		cr1.addSubRole(lr2);
		role.addSubRole(lr2);
	}
	
	@Test
	public void testAddRoleThatContainsSubRolesAlreadyPresentInItsSubRolesTree() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("A role cannot add a sub-role that has one or more sub-roles those are already present in its sub-roles tree");
		role.addSubRole(cr1);
		cr1.addSubRole(lr2);
		cr3.addSubRole(lr2);
		role.addSubRole(cr3);
	}
	
	@Test
	public void testAddParentRoleAsSubRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Cyclic relations between roles aren't admitted");
		role.addSubRole(cr1);
		cr1.addSubRole(cr2);
		cr2.addSubRole(role);
	}
	
	@Test
	public void testRemoveSubRole() {
		role.addSubRole(cr1);
		role.addSubRole(lr3);
		cr1.addSubRole(lr2);
		cr1.addSubRole(cr2);
		
		assertEquals(4, role.getAllSubRoles().size());

		assertTrue( role.removeSubRole(cr2) );
		assertEquals(3, role.getAllSubRoles().size());
		
		assertFalse( role.removeSubRole(cr3) );
		assertEquals(3, role.getAllSubRoles().size());
		
		assertTrue( role.removeSubRole(cr1) );
		assertEquals(1, role.getAllSubRoles().size());
	}
	
	@Test
	public void testHasSubRole() {
		role.addSubRole(cr1);
		cr1.addSubRole(lr2);
		cr1.addSubRole(cr2);
		
		assertTrue( role.hasSubRole(cr2) );
		assertFalse( role.hasSubRole(cr3) );
	}
	
	@Test
	public void testGetDirectSubRoles() {
		
		role.addSubRole(cr1);
		assertEquals(1, role.getDirectSubRoles().size());
		assertTrue(role.hasSubRole(cr1));
		assertEquals("role", role.getAllSubRoles().iterator().next().getName());
		
		cr1.addSubRole(lr2);
		
		assertEquals(1, cr1.getDirectSubRoles().size());
		assertTrue(cr1.hasSubRole(lr2));
		
		assertEquals(1, role.getDirectSubRoles().size());
		assertTrue(role.hasSubRole(cr1));
		assertTrue(role.hasSubRole(lr2));
	}

	@Test
	public void testGetAllSubRoles() {

		role.addSubRole(cr1);
		assertEquals(1, role.getAllSubRoles().size());
		assertTrue(role.hasSubRole(cr1));
		assertEquals("role", role.getAllSubRoles().iterator().next().getName());

		cr1.addSubRole(lr2);

		assertEquals(1, cr1.getAllSubRoles().size());
		assertTrue(cr1.hasSubRole(lr2));

		assertEquals(2, role.getAllSubRoles().size());
		assertTrue(role.hasSubRole(cr1));
		assertTrue(role.hasSubRole(lr2));
	}

}
