package clast.census.dao;

import static org.junit.Assert.assertEquals;
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
import clast.census.model.ManagedRoleRelation;
import clast.census.model.Role;

public class ManagedRoleRelationDaoTest extends BaseTest {

	private ManagedRoleRelationDao managedRoleRelationDao;
	private RoleDao roleDao;
	
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
	public void testCreateManagedRoleRelation() {
		assertTrue(managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r1.getId())));
		assertTrue(managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r2.getId())));
		assertTrue(managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r3.getId())));
		assertTrue(managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r2.getId())));
		assertTrue(managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r1.getId())));
		assertTrue(managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r3.getId())));
		assertTrue(managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r2.getId())));
		assertTrue(managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r4.getId())));
	}
	
	@Test
	public void testCreateNullManagedRoleRelation() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create managedRole relation: IDs of role and managedRole cannot be null.");
		
		managedRoleRelationDao.createManagedRoleRelation(null);
	}
	
	@Test
	public void testCreateManagedRoleRelationWithNullRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create managedRole relation: IDs of role and managedRole cannot be null.");
		
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(null, r2.getId()));
	}
	
	@Test
	public void testCreateManagedRoleRelationWithNullManagedRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create managedRole relation: IDs of role and managedRole cannot be null.");
		
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), null));
	}
	
	@Test
	public void testCreateManagerRoleRelationWithNullFields() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create managedRole relation: IDs of role and managedRole cannot be null.");
		
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(null, null));
	}
	
	@Test
	public void testCreatePersistedManagedRoleRelation() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create managedRole relation: Passed ManagedRoleRelation already persisted.");
		
		ManagedRoleRelation dmrr = createTestManagedRoleRelation(r1.getId(), r2.getId());
		managedRoleRelationDao.createManagedRoleRelation(dmrr);
		managedRoleRelationDao.createManagedRoleRelation(dmrr);
	}
	
	@Test
	public void testCreateManagedRoleRelationWithUnexistingRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create managedRole relation: Role and ManagedRole must be already persisted.");
		
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation("123", r2.getId()));
	}
	
	@Test
	public void testCreateManagedRoleRelationWithUnexistingManagedRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create managedRole relation: Role and ManagedRole must be already persisted.");
		
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), "123"));
	}
	
	@Test
	public void testCreateManagedRoleRelationWithUnexistingUserIdAndManagedRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create managedRole relation: Role and ManagedRole must be already persisted.");
		
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation("123", "456"));
	}
	
	@Test
	public void testCreateExistingManagedRoleRelation() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create managedRole relation: Role: " + r2.getId() + " is already managed from Role: " + r1.getId());
		
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r2.getId()));
	}
	
	@Test
	public void testDeleteManagedRoleRelation1() {
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r4.getId()));
		
		managedRoleRelationDao.deleteManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r1.getId()));
		
		Set<String> managedRoles = managedRoleRelationDao.findManagedRoleRelations(null, null).stream().map( mrr -> mrr.toString() ).collect(Collectors.toSet());
		assertEquals(7, managedRoles.size());
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r3.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r1.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r3.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r3.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r3.getId(), r4.getId()) ) );
	}
	
	@Test
	public void testDeleteManagedRoleRelation2() {
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r4.getId()));
		
		managedRoleRelationDao.deleteManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r2.getId()));
		
		Set<String> managedRoles = managedRoleRelationDao.findManagedRoleRelations(null, null).stream().map( mrr -> mrr.toString() ).collect(Collectors.toSet());
		assertEquals(7, managedRoles.size());
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r1.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r3.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r1.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r3.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r3.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r3.getId(), r4.getId()) ) );
	}
	
	@Test
	public void testDeleteManagedRoleRelation3() {
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r4.getId()));
		
		managedRoleRelationDao.deleteManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r1.getId()));
		
		Set<String> managedRoles = managedRoleRelationDao.findManagedRoleRelations(null, null).stream().map( mrr -> mrr.toString() ).collect(Collectors.toSet());
		assertEquals(7, managedRoles.size());
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r1.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r3.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r3.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r3.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r3.getId(), r4.getId()) ) );
	}
	
	@Test
	public void testDeleteManagedRoleRelation4() {
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r4.getId()));
		
		managedRoleRelationDao.deleteManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r4.getId()));
		
		Set<String> managedRoles = managedRoleRelationDao.findManagedRoleRelations(null, null).stream().map( mrr -> mrr.toString() ).collect(Collectors.toSet());
		assertEquals(7, managedRoles.size());
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r1.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r3.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r1.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r3.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r3.getId(), r2.getId()) ) );
	}
	
	@Test
	public void testDeleteNullManagedRelation() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete managedRole relation: IDs of role and managedRole cannot be null.");
		
		managedRoleRelationDao.deleteManagedRoleRelation(null);
	}
	
	@Test
	public void testDeleteManagedRelationWithNullRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete managedRole relation: IDs of role and managedRole cannot be null.");
		
		managedRoleRelationDao.deleteManagedRoleRelation( createTestManagedRoleRelation(null, r2.getId()));
	}
	
	@Test
	public void testDeleteManagedRelationWithNullManagedRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete managedRole relation: IDs of role and managedRole cannot be null.");
		
		managedRoleRelationDao.deleteManagedRoleRelation( createTestManagedRoleRelation(r1.getId(), null));
	}
	
	@Test
	public void testDeleteManagedRelationWithNullRoleIdAndManagedRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete managedRole relation: IDs of role and managedRole cannot be null.");
		
		managedRoleRelationDao.deleteManagedRoleRelation( createTestManagedRoleRelation(null, null));
	}
	
	@Test
	public void testDeleteUnexistingManagedRoleRelation1() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete managedRole relation: Relation between Role: " + r1.getId() + "and ManagedRole:" + r4.getId() + " not exist.");
	
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r4.getId()));
		
		managedRoleRelationDao.deleteManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r4.getId()));
	}
	
	@Test
	public void testDeleteUnexistingManagedRoleRelation2() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete managedRole relation: Relation between Role: " + r1.getId() + "and ManagedRole:" + r5.getId() + " not exist.");
		
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r4.getId()));
		
		managedRoleRelationDao.deleteManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r5.getId()));
	}
	
	@Test
	public void testFindManagedRoleRelations() {
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r1.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r1.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r2.getId(), r3.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r2.getId()));
		managedRoleRelationDao.createManagedRoleRelation(createTestManagedRoleRelation(r3.getId(), r4.getId()));
		
		Set<String> managedRoles = managedRoleRelationDao.findManagedRoleRelations(null, null).stream().map( mrr -> mrr.toString() ).collect(Collectors.toSet());
		assertEquals(8, managedRoles.size());
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r1.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r3.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r1.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r3.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r3.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r3.getId(), r4.getId()) ) );
		
		managedRoles = managedRoleRelationDao.findManagedRoleRelations(null, r2.getId()).stream().map( mrr -> mrr.toString() ).collect(Collectors.toSet());
		assertEquals(3, managedRoles.size());
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r2.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r3.getId(), r2.getId()) ) );
		
		managedRoles = managedRoleRelationDao.findManagedRoleRelations(r1.getId(), null).stream().map( mrr -> mrr.toString() ).collect(Collectors.toSet());
		assertEquals(3, managedRoles.size());
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r1.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r2.getId()) ) );
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r3.getId()) ) );
		
		managedRoles = managedRoleRelationDao.findManagedRoleRelations(r1.getId(), r2.getId()).stream().map( mrr -> mrr.toString() ).collect(Collectors.toSet());
		assertEquals(1, managedRoles.size());
		assertTrue( managedRoles.contains( stringOf(r1.getId(), r2.getId()) ) );
	}

	private ManagedRoleRelation createTestManagedRoleRelation(String idRole, String idManagedRole) {
		ManagedRoleRelation dmrr = new ManagedRoleRelation();
		dmrr.setRoleId(idRole);
		dmrr.setManagedRoleId(idManagedRole);
		return dmrr;
	}
	
	private String stringOf(String roleId, String managedRoleId) {
		return roleId + "-" + managedRoleId;
	}
	
}
