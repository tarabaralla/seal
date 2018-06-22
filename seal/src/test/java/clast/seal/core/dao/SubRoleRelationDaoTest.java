package clast.seal.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import clast.seal.core.model.DirectSubRoleRelation;
import clast.seal.core.model.Role;
import clast.seal.core.model.SubRoleRelationType;

public class SubRoleRelationDaoTest {
	
	private SubRoleRelationDao subRoleRelationDao;
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
		subRoleRelationDao = new SubRoleRelationDao();
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
	public void testCreateSubRoleRelation() {
		assertTrue(subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r1.getId(), r2.getId())));
		assertTrue(subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r1.getId(), r3.getId())));
		assertTrue(subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r2.getId(), r4.getId())));
		assertTrue(subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r2.getId(), r5.getId())));
		assertTrue(subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r2.getId(), r6.getId())));
		assertTrue(subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r4.getId(), r7.getId())));
	}
	
	@Test
	public void testCreateNullSubRoleRelation() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: IDs of role and subRole cannot be null.");
		
		subRoleRelationDao.createSubRoleRelation(null);
	}
	
	@Test
	public void testCreateSubRoleRelationWithNullRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: IDs of role and subRole cannot be null.");
		
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(null, r2.getId()) );
	}
	
	@Test
	public void testCreateSubRoleRelationWithNullSubRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: IDs of role and subRole cannot be null.");
		
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r1.getId(), null));
	}
	
	@Test
	public void testCreateSubRoleRelationWithNullFields() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: IDs of role and subRole cannot be null.");
		
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(null, null));
	}
	
	@Test
	public void testCreateExistingSubRoleRelation() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: It already has an ID.");
		
		DirectSubRoleRelation dsr = createTestDirectSubRoleRelation(r1.getId(), r2.getId());
		subRoleRelationDao.createSubRoleRelation(dsr);
		subRoleRelationDao.createSubRoleRelation(dsr);
	}
	
	@Test
	public void testCreateSubRoleRelationWhereRoleIsSubRoleOfItself() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: A role cannot be sub-role of itself.");
		
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r1.getId(), r1.getId()) );
	}
	
	@Test
	public void testCreateAlreadyExistingSubRoleRelation1() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: SubRoleRelation: " + r2.getId() + " is already subRole of another role.");
		
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r1.getId(), r2.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r1.getId(), r2.getId()) );
	}
	
	@Test
	public void testCreateAlreadyExistingSubRoleRelation2() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: SubRoleRelation: " + r3.getId() + " is already subRole of another role.");
		
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r1.getId(), r2.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r2.getId(), r3.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r1.getId(), r3.getId()) );
	}
	
	@Test
	public void testCreateAlreadyExistingSubRoleRelation3() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: SubRoleRelation: " + r2.getId() + " is already subRole of another role.");
		
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r3.getId(), r2.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r1.getId(), r2.getId()) );
	}
	
	@Test
	public void testCreateAlreadyExistingSubRoleRelation4() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: SubRoleRelation: " + r2.getId() + " is already subRole of another role.");
		
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r3.getId(), r4.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r4.getId(), r2.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r1.getId(), r2.getId()) );
	}
	
	@Test
	public void testCreateCyclicSubRoleRelation1() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: Cyclic relations are not allowed.");
		
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r1.getId(), r2.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r2.getId(), r1.getId()) );
	}
	
	@Test
	public void testCreateCyclicSubRoleRelation2() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: Cyclic relations are not allowed.");
		
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r1.getId(), r2.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r2.getId(), r3.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestDirectSubRoleRelation(r3.getId(), r1.getId()) );
	}
	
	@Test
	public void testFindSubRoleRelations() {
		subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r1.getId(), r2.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r1.getId(), r3.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r2.getId(), r4.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r2.getId(), r5.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r2.getId(), r6.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r4.getId(), r7.getId()));
		
		Set<String> subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.DIRECT, null, null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(6, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r2.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r3.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r4.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r6.getId())) );
		assertTrue( subRoles.contains( stringOf(r4.getId(), r7.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.INDIRECT, null, null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(5, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r4.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r6.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r7.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r7.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.DIRECT, null, r7.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(1, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r4.getId(), r7.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.INDIRECT, null, r7.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(2, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r7.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r7.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.DIRECT, null, r7.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(1, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r4.getId(), r7.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.INDIRECT, null, r7.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(2, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r7.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r7.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.DIRECT, r1.getId(), null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(2, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r2.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r3.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.INDIRECT, r1.getId(), null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(4, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r4.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r6.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r7.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.DIRECT, r1.getId(), r5.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(0, subRoles.size());
		
		subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.INDIRECT, r1.getId(), r5.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(1, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r5.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(null, null, r5.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(2, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r5.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(null, r2.getId(), null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(4, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r2.getId(), r4.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r6.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r7.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(null, r2.getId(), r6.getId()).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(1, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r2.getId(), r6.getId())) );

	}
	
	@Test
	public void testFindSubRoleRelationsWithNoSearchParmas() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to find subRole relations: At least one search field must be specified.");
		
		subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r1.getId(), r2.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r1.getId(), r3.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r2.getId(), r4.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r2.getId(), r5.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r2.getId(), r6.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestDirectSubRoleRelation(r4.getId(), r7.getId()));
		
		subRoleRelationDao.findSubRoleRelations(null, null, null);		
	}
	
	private DirectSubRoleRelation createTestDirectSubRoleRelation(String idRole, String idSubRole) {
		DirectSubRoleRelation dsr = new DirectSubRoleRelation();
		dsr.setRoleId(idRole);
		dsr.setSubRoleId(idSubRole);
		return dsr;
	}
	
	private String stringOf(String roleId, String subRoleId) {
		return roleId + "-" + subRoleId;
	}

}
