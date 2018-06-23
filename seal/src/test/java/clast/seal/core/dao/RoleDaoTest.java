package clast.seal.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import clast.seal.core.model.Role;

public class RoleDaoTest {
	
	private RoleDao roleDao;
	
	private Role r1;
	private Role r2;
	private Role r3;
	private Role r4;
	private Role r5;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setUp() {
		roleDao = new RoleDao();
		r1 = new Role("role1");
		r2 = new Role("role2");
		r3 = new Role("role3");
		r4 = new Role("role4");
		r5 = new Role("role5");
	}

	@Test
	public void testCreateRole() {
		Role role = new Role("role");
		assertTrue(roleDao.createRole(role));
	}
	
	@Test
	public void testCreateAlreadyExistingRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create role: it already has an ID.");
		
		Role role = new Role("role");
		roleDao.createRole(role);
		roleDao.createRole(role);
	}
	
	@Test
	public void testCreateRoleWithoutName() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create role: role name cannot be null.");
		
		Role role = new Role();
		roleDao.createRole(role);
	}
	
	@Test
	public void testCreateRoleWithOccupiedName() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create role: name is already assigned to another role.");
		
		Role role = new Role("role");
		roleDao.createRole(role);
		Role role2 = new Role("role");
		roleDao.createRole(role2);
	}
	
	@Test
	public void testDeleteRole() {
		roleDao.createRole(new Role("role"));
		Role role = roleDao.findRoleByName("role");
		assertTrue(roleDao.deleteRole(role));
	}
	
	@Test
	public void testDeleteNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete role: role not found.");
		
		roleDao.deleteRole(null);
	}
	
	@Test
	public void testDeleteRoleWithNullName() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete role: role not found.");
		
		roleDao.deleteRole( new Role() );
	}
	
	@Test
	public void testDeleteNotPersistedRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete role: role with ID 123 not found.");
		
		Role role = new Role("role");
		role.setId("123");
		roleDao.deleteRole(role);
	}
	
	@Test
	public void testUpdateRoleName() {
		Role role = new Role("role");
		roleDao.createRole(role);
		
		assertTrue(roleDao.updateRoleName(role.getId(), "role1"));
		assertEquals("role1", roleDao.findRoleById(role.getId()).getName() );
	}
	
	@Test
	public void testUpdateNotPersistedRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to update role: role with ID 123 not found.");
		
		assertTrue(roleDao.updateRoleName("123", "role1"));
		assertEquals("role1", roleDao.findRoleById("123").getName() );
	}
	
	@Test
	public void testFindRoleByName() {
		roleDao.createRole( new Role("role") );
		assertNotNull(roleDao.findRoleByName("role"));
		assertNull(roleDao.findRoleByName("role1"));
	}
	
	@Test
	public void testFindRoleByNullName() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find role: role name cannot be null.");
		
		roleDao.findRoleByName(null);
	}
	
	@Test
	public void testFindRoleById() {
		Role role = new Role("role");
		roleDao.createRole(role);
		assertNotNull(roleDao.findRoleById(role.getId()));
		assertNull(roleDao.findRoleById("123"));
	}
	
	@Test
	public void testFindRoleByNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find role: role ID cannot be null.");
		
		roleDao.findRoleById(null);
	}
	
	@Test
	public void findAllRoles() {
		assertTrue(roleDao.findAllRoles().isEmpty());
		
		roleDao.createRole(new Role("r1"));
		Set<String> roleNames = roleDao.findAllRoles().stream().map( r -> r.getName()).collect(Collectors.toSet());
		assertEquals(1, roleNames.size());
		assertTrue( roleNames.contains("r1"));
		
		roleDao.createRole(new Role("r2"));
		roleDao.createRole(new Role("r3"));
		roleNames = roleDao.findAllRoles().stream().map( r -> r.getName()).collect(Collectors.toSet());
		assertEquals(3, roleNames.size());
		assertTrue( roleNames.contains("r1"));
		assertTrue( roleNames.contains("r2"));
		assertTrue( roleNames.contains("r3"));
		
	}

	@Test
	public void testAddSubRole() {
		roleDao.createRole(r1);
		roleDao.createRole(r2);
		assertTrue(roleDao.addSubRole(r1, r2));
	}
	
	@Test
	public void testRemoveSubRole() {
		roleDao.createRole(r1);
		roleDao.createRole(r2);
		roleDao.addSubRole(r1, r2);
		assertTrue(roleDao.removeSubRole(r1, r2));
	}
	
	@Test
	public void testFindAllSubroles() {
		roleDao.createRole(r1);
		roleDao.createRole(r2);
		roleDao.createRole(r3);
		roleDao.createRole(r4);
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		Set<String> subRolesNames = roleDao.findAllSubRoles(r1).stream().map( sr -> sr.getName() ).collect(Collectors.toSet());
		assertEquals(3, subRolesNames.size());
		assertTrue(subRolesNames.contains(r2.getName()));
		assertTrue(subRolesNames.contains(r3.getName()));
		assertTrue(subRolesNames.contains(r4.getName()));
	}
	
	@Test
	public void testFindDirectSubRoles() {
		roleDao.createRole(r1);
		roleDao.createRole(r2);
		roleDao.createRole(r3);
		roleDao.createRole(r4);
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		Set<String> subRolesNames = roleDao.findDirectSubRoles(r1).stream().map( sr -> sr.getName() ).collect(Collectors.toSet());
		assertEquals(2, subRolesNames.size());
		assertTrue(subRolesNames.contains(r2.getName()));
		assertTrue(subRolesNames.contains(r3.getName()));
	}
	
	@Test
	public void testFindIndirectSubRoles() {
		roleDao.createRole(r1);
		roleDao.createRole(r2);
		roleDao.createRole(r3);
		roleDao.createRole(r4);
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		Set<String> subRolesNames = roleDao.findIndirectSubRoles(r1).stream().map( sr -> sr.getName() ).collect(Collectors.toSet());
		assertEquals(1, subRolesNames.size());
		assertTrue(subRolesNames.contains(r4.getName()));
	}
	
	@Test
	public void testHasSubRole() {
		roleDao.createRole(r1);
		roleDao.createRole(r2);
		roleDao.createRole(r3);
		roleDao.createRole(r4);
		roleDao.createRole(r5);
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		assertTrue(roleDao.hasSubRole(r1, r2));
		assertTrue(roleDao.hasSubRole(r1, r3));
		assertTrue(roleDao.hasSubRole(r1, r4));
		assertFalse(roleDao.hasSubRole(r1, r5));
	}
	
	@Test
	public void testHasDirectSubRole() {
		roleDao.createRole(r1);
		roleDao.createRole(r2);
		roleDao.createRole(r3);
		roleDao.createRole(r4);
		roleDao.createRole(r5);
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		assertTrue(roleDao.hasDirectSubRole(r1, r2));
		assertTrue(roleDao.hasDirectSubRole(r1, r3));
		assertFalse(roleDao.hasDirectSubRole(r1, r4));
		assertFalse(roleDao.hasDirectSubRole(r1, r5));
	}
	
	@Test
	public void testHasIndirectSubRole() {
		roleDao.createRole(r1);
		roleDao.createRole(r2);
		roleDao.createRole(r3);
		roleDao.createRole(r4);
		roleDao.createRole(r5);
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		assertFalse(roleDao.hasIndirectSubRole(r1, r2));
		assertFalse(roleDao.hasIndirectSubRole(r1, r3));
		assertTrue(roleDao.hasIndirectSubRole(r1, r4));
		assertFalse(roleDao.hasIndirectSubRole(r1, r5));
	}
}
