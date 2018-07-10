package clast.census.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import clast.census.BaseTest;
import clast.census.dao.ManagedRoleRelationDao;
import clast.census.dao.RoleDao;
import clast.census.dao.SubRoleRelationDao;
import clast.census.model.Role;

public class RoleDaoTest extends BaseTest {
	
	private RoleDao roleDao;
	private SubRoleRelationDao subRoleRelationDao;
	private ManagedRoleRelationDao managedRoleRelationDao;
	
	private Role r1;
	private Role r2;
	private Role r3;
	private Role r4;
	private Role r5;
	private Role r6;
	private Role r7;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setUp() {
		roleDao = new RoleDao();
		subRoleRelationDao = new SubRoleRelationDao(roleDao);
		managedRoleRelationDao = new ManagedRoleRelationDao(roleDao);
		r1 = new Role("role1");
		r2 = new Role("role2");
		r3 = new Role("role3");
		r4 = new Role("role4");
		r5 = new Role("role5");
		r6 = new Role("role6");
		r7 = new Role("role7");
		roleDao.createRole(r1);
		roleDao.createRole(r2);
		roleDao.createRole(r3);
		roleDao.createRole(r4);
		roleDao.createRole(r5);
		roleDao.createRole(r6);
		roleDao.createRole(r7);
	}
	
	@After
	public void tearDown() {
		roleDao.deleteAllRoles();
	}

	@Test
	public void testCreateRole() {
		Role role = new Role("role");
		assertTrue(roleDao.createRole(role));
	}
	
	@Test
	public void testCreateNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create role: Role cannot be null.");
		
		roleDao.createRole(null);
	}
	
	@Test
	public void testCreateAlreadyExistingRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create role: Role is already persisted.");
		
		Role role = new Role("role");
		roleDao.createRole(role);
		roleDao.createRole(role);
	}
	
	@Test
	public void testCreateRoleWithoutName() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create role: Role name cannot be null.");
		
		Role role = new Role();
		roleDao.createRole(role);
	}
	
	@Test
	public void testCreateRoleWithOccupiedName() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create role: Role name is already assigned to another role.");
		
		Role role = new Role("role");
		roleDao.createRole(role);
		Role role2 = new Role("role");
		roleDao.createRole(role2);
	}
	
	@Test
	public void testDeleteRole() {
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addManagedRole(r1, r1);
		roleDao.addManagedRole(r1, r2);
		roleDao.addManagedRole(r1, r3);
		roleDao.addManagedRole(r2, r2);
		roleDao.addManagedRole(r2, r3);
		roleDao.addManagedRole(r3, r3);
		
		Set<String> roleNames = roleDao.findAllRoles().stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(7, roleNames.size());
		assertTrue(roleNames.contains(r1.getName()));
		assertTrue(roleNames.contains(r2.getName()));
		assertTrue(roleNames.contains(r3.getName()));
		assertTrue(roleNames.contains(r4.getName()));
		assertTrue(roleNames.contains(r5.getName()));
		assertTrue(roleNames.contains(r6.getName()));
		assertTrue(roleNames.contains(r7.getName()));
		
		Set<String> managedRoleRelations = managedRoleRelationDao.findManagedRoleRelations(null, null).stream().map( mrr -> mrr.toString() ).collect(Collectors.toSet());
		assertEquals(6, managedRoleRelations.size());
		assertTrue(managedRoleRelations.contains(stringOf(r1.getId(), r1.getId())));
		assertTrue(managedRoleRelations.contains(stringOf(r1.getId(), r2.getId())));
		assertTrue(managedRoleRelations.contains(stringOf(r1.getId(), r3.getId())));
		assertTrue(managedRoleRelations.contains(stringOf(r2.getId(), r2.getId())));
		assertTrue(managedRoleRelations.contains(stringOf(r2.getId(), r3.getId())));
		assertTrue(managedRoleRelations.contains(stringOf(r3.getId(), r3.getId())));
		
		Set<String> subRoleRelations = subRoleRelationDao.findSubRoleRelations(null, r1.getId(), null).stream().map( mrr -> mrr.toString() ).collect(Collectors.toSet());
		assertEquals(2, subRoleRelations.size());
		assertTrue(subRoleRelations.contains(stringOf(r1.getId(), r2.getId())));
		assertTrue(subRoleRelations.contains(stringOf(r1.getId(), r3.getId())));
		
		assertTrue(roleDao.deleteRole(r1, false));
		
		roleNames = roleDao.findAllRoles().stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(6, roleNames.size());
		assertTrue(roleNames.contains(r2.getName()));
		assertTrue(roleNames.contains(r3.getName()));
		assertTrue(roleNames.contains(r4.getName()));
		assertTrue(roleNames.contains(r5.getName()));
		assertTrue(roleNames.contains(r6.getName()));
		assertTrue(roleNames.contains(r7.getName()));
		
		managedRoleRelations = managedRoleRelationDao.findManagedRoleRelations(null, null).stream().map( mrr -> mrr.toString() ).collect(Collectors.toSet());
		assertEquals(3, managedRoleRelations.size());
		assertTrue(managedRoleRelations.contains(stringOf(r2.getId(), r2.getId())));
		assertTrue(managedRoleRelations.contains(stringOf(r2.getId(), r3.getId())));
		assertTrue(managedRoleRelations.contains(stringOf(r3.getId(), r3.getId())));
		
		subRoleRelations = subRoleRelationDao.findSubRoleRelations(null, r1.getId(), null).stream().map( mrr -> mrr.toString() ).collect(Collectors.toSet());
		assertEquals(0, subRoleRelations.size());
		
	}
	
	@Test
	public void testCascadeDeleteRole() {
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		roleDao.addSubRole(r2, r5);
		roleDao.addSubRole(r4, r6);
		roleDao.addSubRole(r3, r7);
		
		Set<String> roleNames = roleDao.findAllRoles().stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(7, roleNames.size());
		assertTrue(roleNames.contains(r1.getName()));
		assertTrue(roleNames.contains(r2.getName()));
		assertTrue(roleNames.contains(r3.getName()));
		assertTrue(roleNames.contains(r4.getName()));
		assertTrue(roleNames.contains(r5.getName()));
		assertTrue(roleNames.contains(r6.getName()));
		assertTrue(roleNames.contains(r7.getName()));

		assertTrue(roleDao.deleteRole(r2, true));
		roleNames = roleDao.findAllRoles().stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(3, roleNames.size());
		assertTrue(roleNames.contains(r1.getName()));
		assertTrue(roleNames.contains(r3.getName()));
		assertTrue(roleNames.contains(r7.getName()));
	}
	
	@Test
	public void testDeleteNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete role: role not found.");
		
		roleDao.deleteRole(null, false);
	}
	
	@Test
	public void testDeleteRoleWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete role: role not found.");
		
		roleDao.deleteRole( new Role(), false );
	}
	
	@Test
	public void testDeleteNotPersistedRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete role: role with ID " + r1.getId() + " not found.");
		
		roleDao.deleteRole(r1, false);
		roleDao.deleteRole(r1, false);
	}
	
	@Test
	public void testDeleteAllRoles() {
		assertTrue(roleDao.deleteAllRoles());
		assertTrue(roleDao.findAllRoles().isEmpty());
	}
	
	@Test
	public void testUpdateRoleName() {
		assertTrue(roleDao.updateRoleName(r1.getId(), "role24"));
		assertEquals("role24", roleDao.findRoleById(r1.getId()).getName() );
	}
	
	@Test
	public void testUpdateNotPersistedRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to update role: role with ID 123 not found.");
		
		assertTrue(roleDao.updateRoleName("123", "role1"));
	}
	
	@Test
	public void testUpdateRoleWithNullName() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to update role: role name cannot be null.");
		
		assertTrue(roleDao.updateRoleName(r1.getId(), null));
	}
	
	@Test
	public void testUpdateRoleWithOccupiedName() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to update role: role name is already assigned to another role.");
		
		assertTrue(roleDao.updateRoleName(r1.getId(), "role2"));
	}
	
	@Test
	public void findAllRoles() {
		assertEquals(7,roleDao.findAllRoles().size());
		
		roleDao.createRole(new Role("role8"));
		Set<String> roleNames = roleDao.findAllRoles().stream().map( r -> r.getName()).collect(Collectors.toSet());
		assertEquals(8, roleNames.size());
		assertTrue( roleNames.contains("role1"));
		assertTrue( roleNames.contains("role2"));
		assertTrue( roleNames.contains("role3"));
		assertTrue( roleNames.contains("role4"));
		assertTrue( roleNames.contains("role5"));
		assertTrue( roleNames.contains("role6"));
		assertTrue( roleNames.contains("role7"));
		assertTrue( roleNames.contains("role8"));
		
		roleDao.createRole(new Role("role9"));
		roleDao.createRole(new Role("role10"));
		roleNames = roleDao.findAllRoles().stream().map( r -> r.getName()).collect(Collectors.toSet());
		assertEquals(10, roleNames.size());
		assertTrue( roleNames.contains("role1"));
		assertTrue( roleNames.contains("role2"));
		assertTrue( roleNames.contains("role3"));
		assertTrue( roleNames.contains("role4"));
		assertTrue( roleNames.contains("role5"));
		assertTrue( roleNames.contains("role6"));
		assertTrue( roleNames.contains("role7"));
		assertTrue( roleNames.contains("role8"));
		assertTrue( roleNames.contains("role9"));
		assertTrue( roleNames.contains("role10"));
	}
	
	@Test
	public void findRolesByFreeSearch() {
		Role r11 = new Role("role11");
		Role r21 = new Role("ro21le");
		Role r31 = new Role("31 role");
		Role r41 = new Role("41r");
		roleDao.createRole(r11);
		roleDao.createRole(r21);
		roleDao.createRole(r31);
		roleDao.createRole(r41);
		
		Set<String> roleNames = roleDao.findRolesByFreeSearch("1").stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(5, roleNames.size());
		assertTrue(roleNames.contains(r1.getName()));
		assertTrue(roleNames.contains(r11.getName()));
		assertTrue(roleNames.contains(r21.getName()));
		assertTrue(roleNames.contains(r31.getName()));
		assertTrue(roleNames.contains(r41.getName()));
		
		roleNames = roleDao.findRolesByFreeSearch("role").stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(9, roleNames.size());
		assertTrue(roleNames.contains(r1.getName()));
		assertTrue(roleNames.contains(r2.getName()));
		assertTrue(roleNames.contains(r3.getName()));
		assertTrue(roleNames.contains(r4.getName()));
		assertTrue(roleNames.contains(r5.getName()));
		assertTrue(roleNames.contains(r6.getName()));
		assertTrue(roleNames.contains(r7.getName()));
		assertTrue(roleNames.contains(r11.getName()));
		assertTrue(roleNames.contains(r31.getName()));
		
		roleNames = roleDao.findRolesByFreeSearch(" ").stream().map( r -> r.getName() ).collect(Collectors.toSet());
		assertEquals(1, roleNames.size());
		assertTrue(roleNames.contains(r31.getName()));
	}
	
	@Test
	public void findRolesByNullFreeSearch() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find roles: text of search cannot be empty.");
		
		roleDao.findRolesByFreeSearch(null);
	}
	
	@Test
	public void findRolesByEmptyFreeSearch() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find roles: text of search cannot be empty.");
		
		roleDao.findRolesByFreeSearch("");
	}
	
	@Test
	public void testFindRoleByName() {
		assertNotNull(roleDao.findRoleByName("role1"));
		assertNull(roleDao.findRoleByName("role8"));
	}
	
	@Test
	public void testFindRoleByNullName() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find role: role name cannot be null.");
		
		roleDao.findRoleByName(null);
	}
	
	@Test
	public void testFindRoleById() {
		assertNotNull(roleDao.findRoleById(r1.getId()));
		assertNull(roleDao.findRoleById("123"));
	}
	
	@Test
	public void testFindRoleByNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find role: role ID cannot be null.");
		
		roleDao.findRoleById(null);
	}

	@Test
	public void testAddSubRole() {
		assertTrue(roleDao.addSubRole(r1, r2));
	}
	
	@Test
	public void testRemoveSubRole() {
		roleDao.addSubRole(r1, r2);
		assertTrue(roleDao.removeSubRole(r1, r2));
	}
	
	@Test
	public void testFindAllSubRoles() {
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
	public void testFindAllSubRolesOfNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find sub-roles: role ID cannot be null.");
		
		roleDao.findAllSubRoles(null);
	}
	
	@Test
	public void testFindAllSubRolesOfRoleWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find sub-roles: role ID cannot be null.");
		
		roleDao.findAllSubRoles(new Role("r1234"));
	}
	
	@Test
	public void testFindDirectSubRoles() {
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		Set<String> subRolesNames = roleDao.findDirectSubRoles(r1).stream().map( sr -> sr.getName() ).collect(Collectors.toSet());
		assertEquals(2, subRolesNames.size());
		assertTrue(subRolesNames.contains(r2.getName()));
		assertTrue(subRolesNames.contains(r3.getName()));
	}
	
	@Test
	public void testFindDirectSubRolesOfNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find sub-roles: role ID cannot be null.");
		
		roleDao.findDirectSubRoles(null);
	}
	
	@Test
	public void testFindDirectSubRolesOfRoleWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find sub-roles: role ID cannot be null.");
		
		roleDao.findDirectSubRoles(new Role("r1234"));
	}
	
	@Test
	public void testFindIndirectSubRoles() {
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		Set<String> subRolesNames = roleDao.findIndirectSubRoles(r1).stream().map( sr -> sr.getName() ).collect(Collectors.toSet());
		assertEquals(1, subRolesNames.size());
		assertTrue(subRolesNames.contains(r4.getName()));
	}
	
	@Test
	public void testFindIndirectSubRolesOfNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find sub-roles: role ID cannot be null.");
		
		roleDao.findIndirectSubRoles(null);
	}
	
	@Test
	public void testFindIndirectSubRolesOfRoleWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find sub-roles: role ID cannot be null.");
		
		roleDao.findIndirectSubRoles(new Role("r1234"));
	}
	
	@Test
	public void testHasSubRole() {
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
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r1, r3);
		roleDao.addSubRole(r2, r4);
		assertFalse(roleDao.hasIndirectSubRole(r1, r2));
		assertFalse(roleDao.hasIndirectSubRole(r1, r3));
		assertTrue(roleDao.hasIndirectSubRole(r1, r4));
		assertFalse(roleDao.hasIndirectSubRole(r1, r5));
	}
	
	@Test
	public void testAddManagedRole() {
		assertTrue(roleDao.addManagedRole(r1, r2));
	}
	
	@Test
	public void testRemoveManagedRole() {
		roleDao.addManagedRole(r1, r2);
		assertTrue(roleDao.removeManagedRole(r1, r2));
	}
	
	@Test
	public void testFindAllManagedRoles() {
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r2, r4);
		roleDao.addSubRole(r4, r5);
		roleDao.addSubRole(r5, r7);
		roleDao.addManagedRole(r1, r2);
		roleDao.addManagedRole(r1, r3);
		roleDao.addManagedRole(r2, r4);
		roleDao.addManagedRole(r5, r6);
		roleDao.addManagedRole(r6, r7);
		Set<String> managedRolesNames = roleDao.findAllManagedRoles(r1).stream().map( sr -> sr.getName() ).collect(Collectors.toSet());
		assertEquals(4, managedRolesNames.size());
		assertTrue(managedRolesNames.contains(r2.getName()));
		assertTrue(managedRolesNames.contains(r3.getName()));
		assertTrue(managedRolesNames.contains(r4.getName()));
		assertTrue(managedRolesNames.contains(r6.getName()));
	}
	
	@Test
	public void testFindAllManagedRolesOfNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find managed roles: role ID cannot be null.");
		
		roleDao.findAllManagedRoles(null);
	}
	
	@Test
	public void testFindAllManagedRolesOfRoleWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find managed roles: role ID cannot be null.");
		
		roleDao.findAllManagedRoles(new Role("r1234"));
	}
	
	@Test
	public void testFindDirectManagedRoles() {
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r2, r4);
		roleDao.addSubRole(r4, r5);
		roleDao.addSubRole(r5, r7);
		roleDao.addManagedRole(r1, r2);
		roleDao.addManagedRole(r1, r3);
		roleDao.addManagedRole(r2, r4);
		roleDao.addManagedRole(r5, r6);
		roleDao.addManagedRole(r6, r7);
		Set<String> managedRolesNames = roleDao.findDirectManagedRoles(r1).stream().map( sr -> sr.getName() ).collect(Collectors.toSet());
		assertEquals(2, managedRolesNames.size());
		assertTrue(managedRolesNames.contains(r2.getName()));
		assertTrue(managedRolesNames.contains(r3.getName()));
	}
	
	@Test
	public void testFindDirectManagedRolesOfNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find direct managed roles: role ID cannot be null.");
		
		roleDao.findDirectManagedRoles(null);
	}
	
	@Test
	public void testFindDirectManagedRolesOfRoleWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find direct managed roles: role ID cannot be null.");
		
		roleDao.findDirectManagedRoles(new Role("r1234"));
	}
	
	@Test
	public void testFindIndirectManagedRoles() {
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r2, r4);
		roleDao.addSubRole(r4, r5);
		roleDao.addSubRole(r5, r7);
		roleDao.addManagedRole(r1, r2);
		roleDao.addManagedRole(r1, r3);
		roleDao.addManagedRole(r2, r4);
		roleDao.addManagedRole(r5, r6);
		roleDao.addManagedRole(r6, r7);
		Set<String> managedRolesNames = roleDao.findIndirectManagedRoles(r1).stream().map( sr -> sr.getName() ).collect(Collectors.toSet());
		assertEquals(2, managedRolesNames.size());
		assertTrue(managedRolesNames.contains(r4.getName()));
		assertTrue(managedRolesNames.contains(r6.getName()));
	}
	
	@Test
	public void testFindIndirectManagedRolesOfNullRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find indirect managed roles: role ID cannot be null.");
		
		roleDao.findIndirectManagedRoles(null);
	}
	
	@Test
	public void testFindIndirectManagedRolesOfRoleWithNullId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find indirect managed roles: role ID cannot be null.");
		
		roleDao.findIndirectManagedRoles(new Role("r1234"));
	}
	
	@Test
	public void testManagesRole() {
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r2, r4);
		roleDao.addSubRole(r4, r5);
		roleDao.addSubRole(r5, r7);
		roleDao.addManagedRole(r1, r2);
		roleDao.addManagedRole(r1, r3);
		roleDao.addManagedRole(r2, r4);
		roleDao.addManagedRole(r5, r6);
		roleDao.addManagedRole(r6, r7);
		assertTrue(roleDao.managesRole(r1, r2));
		assertTrue(roleDao.managesRole(r1, r3));
		assertTrue(roleDao.managesRole(r1, r4));
		assertFalse(roleDao.managesRole(r1, r5));
		assertTrue(roleDao.managesRole(r1, r6));
		assertFalse(roleDao.managesRole(r1, r7));
	}
	
	@Test
	public void testDirectlyManagesRole() {
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r2, r4);
		roleDao.addSubRole(r4, r5);
		roleDao.addSubRole(r5, r7);
		roleDao.addManagedRole(r1, r2);
		roleDao.addManagedRole(r1, r3);
		roleDao.addManagedRole(r2, r4);
		roleDao.addManagedRole(r5, r6);
		roleDao.addManagedRole(r6, r7);
		assertTrue(roleDao.directlyManagesRole(r1, r2));
		assertTrue(roleDao.directlyManagesRole(r1, r3));
		assertFalse(roleDao.directlyManagesRole(r1, r4));
		assertFalse(roleDao.directlyManagesRole(r1, r5));
		assertFalse(roleDao.directlyManagesRole(r1, r6));
		assertFalse(roleDao.directlyManagesRole(r1, r7));
	}
	
	@Test
	public void testIndirectlyManagesRole() {
		roleDao.addSubRole(r1, r2);
		roleDao.addSubRole(r2, r4);
		roleDao.addSubRole(r4, r5);
		roleDao.addSubRole(r5, r7);
		roleDao.addManagedRole(r1, r2);
		roleDao.addManagedRole(r1, r3);
		roleDao.addManagedRole(r2, r4);
		roleDao.addManagedRole(r5, r6);
		roleDao.addManagedRole(r6, r7);
		assertFalse(roleDao.indirectlyManagesRole(r1, r2));
		assertFalse(roleDao.indirectlyManagesRole(r1, r3));
		assertTrue(roleDao.indirectlyManagesRole(r1, r4));
		assertFalse(roleDao.indirectlyManagesRole(r1, r5));
		assertTrue(roleDao.indirectlyManagesRole(r1, r6));
		assertFalse(roleDao.indirectlyManagesRole(r1, r7));
	}

	private String stringOf(String id1, String id2) {
		return id1 + "-" + id2;
	}
	
}
