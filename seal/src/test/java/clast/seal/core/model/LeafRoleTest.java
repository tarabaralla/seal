package clast.seal.core.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LeafRoleTest {
	
	private Role role;
	private Role son;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setUp() {
		role = new LeafRole("role");
		son = new CompositeRole("son");
	}

	@Test
	public void testAddSubRole() {
		expectedEx.expect(UnsupportedOperationException.class);
		expectedEx.expectMessage("Only CompositeRole can manage sub-roles");
		role.addSubRole( son );
	}
	
	@Test
	public void testRemoveSubRole() {
		expectedEx.expect(UnsupportedOperationException.class);
		expectedEx.expectMessage("Only CompositeRole can manage sub-roles");
		role.removeSubRole( son );
	}
	
	@Test
	public void testHasSubRole() {
		
		expectedEx.expect(UnsupportedOperationException.class);
		expectedEx.expectMessage("Only CompositeRole can manage sub-roles");
		assertFalse( role.hasSubRole(role));
		role.addSubRole( son );
		assertFalse( role.hasSubRole(role));
	}
	
	@Test
	public void testGetDirectSubRoles() {
		assertTrue( role.getDirectSubRoles().isEmpty() );
	}
	
	@Test
	public void testGetSubRoles() {
		assertTrue( role.getAllSubRoles().isEmpty() );
	}

}
