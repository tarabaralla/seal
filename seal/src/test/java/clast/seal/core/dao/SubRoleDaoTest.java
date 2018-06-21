package clast.seal.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import clast.seal.core.model.DirectSubRole;
import clast.seal.core.model.IndirectSubRole;
import clast.seal.core.model.Role;
import clast.seal.core.model.SubRoleType;

public class SubRoleDaoTest {
	
	private SubRoleDao subRoleDao;
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
		subRoleDao = new SubRoleDao();
		roleDao = new RoleDao();
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

	@Test
	public void testAddSubRole() {
		assertTrue(subRoleDao.addSubRole(createTestDirectSubRole(r1.getId(), r2.getId())));
		assertTrue(subRoleDao.addSubRole(createTestDirectSubRole(r1.getId(), r3.getId())));
		assertTrue(subRoleDao.addSubRole(createTestDirectSubRole(r2.getId(), r4.getId())));
		assertTrue(subRoleDao.addSubRole(createTestDirectSubRole(r2.getId(), r5.getId())));
		assertTrue(subRoleDao.addSubRole(createTestDirectSubRole(r2.getId(), r6.getId())));
		assertTrue(subRoleDao.addSubRole(createTestDirectSubRole(r4.getId(), r7.getId())));
		
		assertTrue(subRoleDao.addSubRole(createTestIndirectSubRole(r1.getId(), r4.getId())));
		assertTrue(subRoleDao.addSubRole(createTestIndirectSubRole(r1.getId(), r5.getId())));
		assertTrue(subRoleDao.addSubRole(createTestIndirectSubRole(r1.getId(), r6.getId())));
		assertTrue(subRoleDao.addSubRole(createTestIndirectSubRole(r1.getId(), r7.getId())));
		assertTrue(subRoleDao.addSubRole(createTestIndirectSubRole(r2.getId(), r7.getId())));
	}
	
	@Test
	public void testAddNullSubRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add subRole: IDs of role and subRole cannot be null.");
		
		subRoleDao.addSubRole(null);
	}
	
	@Test
	public void testAddSubRoleWithNullRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add subRole: IDs of role and subRole cannot be null.");
		
		subRoleDao.addSubRole( createTestDirectSubRole(null, r2.getId()));
	}
	
	@Test
	public void testAddSubRoleWithNullSubRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add subRole: IDs of role and subRole cannot be null.");
		
		subRoleDao.addSubRole( createTestDirectSubRole(r1.getId(), null));
	}
	
	@Test
	public void testAddSubRoleWithNullFields() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add subRole: IDs of role and subRole cannot be null.");
		
		subRoleDao.addSubRole( createTestDirectSubRole(null, null));
	}
	
	@Test
	public void testAddExistingSubRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add subRole: it already has an ID.");
		
		DirectSubRole dsr = createTestDirectSubRole(r1.getId(), r2.getId());
		subRoleDao.addSubRole(dsr);
		subRoleDao.addSubRole(dsr);
	}
	
	@Test
	public void testAddRoleAsSubRoleOfItself() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add subRole: a role cannot be sub-role of itself.");
		
		subRoleDao.addSubRole( createTestDirectSubRole(r1.getId(), r1.getId()) );
	}
	
	@Test
	public void testAddAlreadyExistingDirectSubRole1() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add subRole: subRole: " + r2.getId() + " is already subRole of another role.");
		
		subRoleDao.addSubRole( createTestDirectSubRole(r1.getId(), r2.getId()) );
		subRoleDao.addSubRole( createTestDirectSubRole(r1.getId(), r2.getId()) );
	}
	
	@Test
	public void testAddAlreadyExistingDirectSubRole2() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add subRole: subRole: " + r2.getId() + " is already subRole of another role.");
		
		subRoleDao.addSubRole( createTestIndirectSubRole(r1.getId(), r2.getId()) );
		subRoleDao.addSubRole( createTestDirectSubRole(r1.getId(), r2.getId()) );
	}
	
	@Test
	public void testAddAlreadyExistingDirectSubRole3() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add subRole: subRole: " + r2.getId() + " is already subRole of another role.");
		
		subRoleDao.addSubRole( createTestDirectSubRole(r3.getId(), r2.getId()) );
		subRoleDao.addSubRole( createTestDirectSubRole(r1.getId(), r2.getId()) );
	}
	
	@Test
	public void testAddAlreadyExistingDirectSubRole4() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add subRole: subRole: " + r2.getId() + " is already subRole of another role.");
		
		subRoleDao.addSubRole( createTestIndirectSubRole(r3.getId(), r2.getId()) );
		subRoleDao.addSubRole( createTestDirectSubRole(r1.getId(), r2.getId()) );
	}
	
	@Test
	public void testAddAlreadyExistingIndirectSubRole1() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add subRole: a relation between role: " + r1.getId() + " and subRole: " + r2.getId() + " already exist.");
		
		subRoleDao.addSubRole( createTestIndirectSubRole(r1.getId(), r2.getId()) );
		subRoleDao.addSubRole( createTestIndirectSubRole(r1.getId(), r2.getId()) );
	}
	
	@Test
	public void testAddAlreadyExistingIndirectSubRole2() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to add subRole: a relation between role: " + r1.getId() + " and subRole: " + r2.getId() + " already exist.");
		
		subRoleDao.addSubRole( createTestDirectSubRole(r1.getId(), r2.getId()) );
		subRoleDao.addSubRole( createTestIndirectSubRole(r1.getId(), r2.getId()) );
	}
	
	@Test
	public void testFindSubRoles() {
		subRoleDao.addSubRole(createTestDirectSubRole(r1.getId(), r2.getId()));
		subRoleDao.addSubRole(createTestDirectSubRole(r1.getId(), r3.getId()));
		subRoleDao.addSubRole(createTestDirectSubRole(r2.getId(), r4.getId()));
		subRoleDao.addSubRole(createTestDirectSubRole(r2.getId(), r5.getId()));
		subRoleDao.addSubRole(createTestDirectSubRole(r2.getId(), r6.getId()));
		subRoleDao.addSubRole(createTestDirectSubRole(r4.getId(), r7.getId()));
		
		subRoleDao.addSubRole(createTestIndirectSubRole(r1.getId(), r4.getId()));
		subRoleDao.addSubRole(createTestIndirectSubRole(r1.getId(), r5.getId()));
		subRoleDao.addSubRole(createTestIndirectSubRole(r1.getId(), r6.getId()));
		subRoleDao.addSubRole(createTestIndirectSubRole(r1.getId(), r7.getId()));
		subRoleDao.addSubRole(createTestIndirectSubRole(r2.getId(), r7.getId()));
		
		Set<String> subRoles = subRoleDao.findSubRoles(SubRoleType.DIRECT, null, null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(6, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r2.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r3.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r4.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r6.getId())) );
		assertTrue( subRoles.contains( stringOf(r4.getId(), r7.getId())) );
		
		subRoles = subRoleDao.findSubRoles(SubRoleType.INDIRECT, null, null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(5, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r4.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r6.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r7.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r7.getId())) );
		
		subRoles = subRoleDao.findSubRoles(SubRoleType.DIRECT, null, r7.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(1, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r4.getId(), r7.getId())) );
		
		subRoles = subRoleDao.findSubRoles(SubRoleType.INDIRECT, null, r7.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(2, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r7.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r7.getId())) );
		
		subRoles = subRoleDao.findSubRoles(SubRoleType.DIRECT, null, r7.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(1, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r4.getId(), r7.getId())) );
		
		subRoles = subRoleDao.findSubRoles(SubRoleType.INDIRECT, null, r7.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(2, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r7.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r7.getId())) );
		
		subRoles = subRoleDao.findSubRoles(SubRoleType.DIRECT, r1.getId(), null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(2, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r2.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r3.getId())) );
		
		subRoles = subRoleDao.findSubRoles(SubRoleType.INDIRECT, r1.getId(), null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(4, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r4.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r6.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r7.getId())) );
		
		subRoles = subRoleDao.findSubRoles(SubRoleType.DIRECT, r1.getId(), r5.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(0, subRoles.size());
		
		subRoles = subRoleDao.findSubRoles(SubRoleType.INDIRECT, r1.getId(), r5.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(1, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r5.getId())) );
		
		subRoles = subRoleDao.findSubRoles(null, null, r5.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(2, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r5.getId())) );
		
		subRoles = subRoleDao.findSubRoles(null, r2.getId(), null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(4, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r2.getId(), r4.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r6.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r7.getId())) );
		
		subRoles = subRoleDao.findSubRoles(null, r2.getId(), r6.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(1, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r2.getId(), r6.getId())) );

	}
	
	@Test
	public void testFindSubRolesWithNoSearchParmas() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find subRoles: at least one search field must be specified.");
		
		subRoleDao.addSubRole(createTestDirectSubRole(r1.getId(), r2.getId()));
		subRoleDao.addSubRole(createTestDirectSubRole(r1.getId(), r3.getId()));
		subRoleDao.addSubRole(createTestDirectSubRole(r2.getId(), r4.getId()));
		subRoleDao.addSubRole(createTestDirectSubRole(r2.getId(), r5.getId()));
		subRoleDao.addSubRole(createTestDirectSubRole(r2.getId(), r6.getId()));
		subRoleDao.addSubRole(createTestDirectSubRole(r4.getId(), r7.getId()));
		
		subRoleDao.addSubRole(createTestIndirectSubRole(r1.getId(), r4.getId()));
		subRoleDao.addSubRole(createTestIndirectSubRole(r1.getId(), r5.getId()));
		subRoleDao.addSubRole(createTestIndirectSubRole(r1.getId(), r6.getId()));
		subRoleDao.addSubRole(createTestIndirectSubRole(r1.getId(), r7.getId()));
		subRoleDao.addSubRole(createTestIndirectSubRole(r2.getId(), r7.getId()));
		
		subRoleDao.findSubRoles(null, null, null);		
	}
	
	private String stringOf(String roleId, String subRoleId) {
		return roleId + "-" + subRoleId;
	}

	private DirectSubRole createTestDirectSubRole(String idRole, String idSubRole) {
		DirectSubRole dsr = new DirectSubRole();
		dsr.setRoleId(idRole);
		dsr.setSubRoleId(idSubRole);
		return dsr;
	}
	
	private IndirectSubRole createTestIndirectSubRole(String idRole, String idSubRole) {
		IndirectSubRole isr = new IndirectSubRole();
		isr.setRoleId(idRole);
		isr.setSubRoleId(idSubRole);
		return isr;
	}

}
