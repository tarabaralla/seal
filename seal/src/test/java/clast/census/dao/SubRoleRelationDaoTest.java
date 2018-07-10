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
import clast.census.dao.RoleDao;
import clast.census.dao.SubRoleRelationDao;
import clast.census.model.DirectSubRoleRelation;
import clast.census.model.Role;
import clast.census.model.SubRoleRelationType;

public class SubRoleRelationDaoTest extends BaseTest {
	
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
		roleDao = new RoleDao();
		subRoleRelationDao = new SubRoleRelationDao(roleDao);
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
	public void testCreateSubRoleRelation() {
		assertTrue(subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r2.getId())));
		assertTrue(subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r3.getId())));
		assertTrue(subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r4.getId(), r7.getId())));
		assertTrue(subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r5.getId())));
		assertTrue(subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r6.getId())));
		assertTrue(subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r4.getId())));
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
		
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(null, r2.getId()) );
	}
	
	@Test
	public void testCreateSubRoleRelationWithNullSubRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: IDs of role and subRole cannot be null.");
		
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r1.getId(), null));
	}
	
	@Test
	public void testCreateSubRoleRelationWithNullFields() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: IDs of role and subRole cannot be null.");
		
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(null, null));
	}
	
	@Test
	public void testCreateExistingSubRoleRelation() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: Passed SubRoleRelation already persisted.");
		
		DirectSubRoleRelation dsrr = createTestSubRoleRelation(r1.getId(), r2.getId());
		subRoleRelationDao.createSubRoleRelation(dsrr);
		subRoleRelationDao.createSubRoleRelation(dsrr);
	}
	
	@Test
	public void testCreateSubRoleRelationWithUnexistingRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: Role and SubRole must be already persisted.");
		
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation("123", r2.getId()));
	}
	
	@Test
	public void testCreateSubRoleRelationWithUnexistingSubRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: Role and SubRole must be already persisted.");
		
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r1.getId(), "123"));
	}
	
	@Test
	public void testCreateSubRoleRelationWithUnexistingRoleIdAndSubRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: Role and SubRole must be already persisted.");
		
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation("123", "456"));
	}
	
	@Test
	public void testCreateSubRoleRelationWhereRoleIsSubRoleOfItself() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: A role cannot be sub-role of itself.");
		
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r1.getId(), r1.getId()) );
	}
	
	@Test
	public void testCreateAlreadyExistingSubRoleRelation1() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: SubRoleRelation: " + r2.getId() + " is already subRole of another role.");
		
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r1.getId(), r2.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r1.getId(), r2.getId()) );
	}
	
	@Test
	public void testCreateAlreadyExistingSubRoleRelation2() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: SubRoleRelation: " + r3.getId() + " is already subRole of another role.");
		
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r1.getId(), r2.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r2.getId(), r3.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r1.getId(), r3.getId()) );
	}
	
	@Test
	public void testCreateAlreadyExistingSubRoleRelation3() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: SubRoleRelation: " + r2.getId() + " is already subRole of another role.");
		
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r3.getId(), r2.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r1.getId(), r2.getId()) );
	}
	
	@Test
	public void testCreateAlreadyExistingSubRoleRelation4() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: SubRoleRelation: " + r2.getId() + " is already subRole of another role.");
		
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r3.getId(), r4.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r4.getId(), r2.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r1.getId(), r2.getId()) );
	}
	
	@Test
	public void testCreateCyclicSubRoleRelation1() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: Cyclic relations are not allowed.");
		
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r1.getId(), r2.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r2.getId(), r1.getId()) );
	}
	
	@Test
	public void testCreateCyclicSubRoleRelation2() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to create subRole relation: Cyclic relations are not allowed.");
		
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r1.getId(), r2.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r2.getId(), r3.getId()) );
		subRoleRelationDao.createSubRoleRelation( createTestSubRoleRelation(r3.getId(), r1.getId()) );
	}
	
	@Test
	public void testDeleteSubRoleRelation1() {
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r2.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r3.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r4.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r5.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r6.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r4.getId(), r7.getId()));
		
		subRoleRelationDao.deleteSubRoleRelation(createTestSubRoleRelation(r1.getId(), r3.getId()));
		
		Set<String> subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.DIRECT, null, null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(5, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r2.getId())) );
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
	}
	
	@Test
	public void testDeleteSubRoleRelation2() {
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r2.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r3.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r4.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r5.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r6.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r4.getId(), r7.getId()));
		
		subRoleRelationDao.deleteSubRoleRelation(createTestSubRoleRelation(r1.getId(), r2.getId()));
		
		Set<String> subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.DIRECT, null, null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(5, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r3.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r4.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r6.getId())) );
		assertTrue( subRoles.contains( stringOf(r4.getId(), r7.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.INDIRECT, null, null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(1, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r2.getId(), r7.getId())) );
	}
	
	@Test
	public void testDeleteSubRoleRelation3() {
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r2.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r3.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r4.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r5.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r6.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r4.getId(), r7.getId()));
		
		subRoleRelationDao.deleteSubRoleRelation(createTestSubRoleRelation(r2.getId(), r4.getId()));
		
		Set<String> subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.DIRECT, null, null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(5, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r2.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r3.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r6.getId())) );
		assertTrue( subRoles.contains( stringOf(r4.getId(), r7.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.INDIRECT, null, null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(2, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r6.getId())) );
	}
	
	@Test
	public void testDeleteSubRoleRelation4() {
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r2.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r3.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r4.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r5.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r6.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r4.getId(), r7.getId()));
		
		subRoleRelationDao.deleteSubRoleRelation(createTestSubRoleRelation(r4.getId(), r7.getId()));
		
		Set<String> subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.DIRECT, null, null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(5, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r2.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r3.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r4.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r2.getId(), r6.getId())) );
		
		subRoles = subRoleRelationDao.findSubRoleRelations(SubRoleRelationType.INDIRECT, null, null).stream().map( sr -> sr.toString() ).collect(Collectors.toSet());
		assertEquals(3, subRoles.size());
		assertTrue( subRoles.contains( stringOf(r1.getId(), r4.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r5.getId())) );
		assertTrue( subRoles.contains( stringOf(r1.getId(), r6.getId())) );
	}
	
	@Test
	public void testDeleteNullSubRoleRelation() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete subRole relation: IDs of role and subRole cannot be null.");
		
		subRoleRelationDao.deleteSubRoleRelation(null);
	}
	
	@Test
	public void testDeleteSubRoleRelationWithNullRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete subRole relation: IDs of role and subRole cannot be null.");
		
		subRoleRelationDao.deleteSubRoleRelation( createTestSubRoleRelation(null, r2.getId()));
	}
	
	@Test
	public void testDeleteSubRoleRelationWithNullSubRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete subRole relation: IDs of role and subRole cannot be null.");
		
		subRoleRelationDao.deleteSubRoleRelation( createTestSubRoleRelation(r1.getId(), null));
	}
	
	@Test
	public void testDeleteSubRoleRelationWithNullRoleIdAndSubRoleId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete subRole relation: IDs of role and subRole cannot be null.");
		
		subRoleRelationDao.deleteSubRoleRelation( createTestSubRoleRelation(null, null));
	}
	
	@Test
	public void testDeleteUnexistingSubRoleRelation1() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete subRole relation: Direct relation between Role: " + r3.getId() + "and SubRole:" + r7.getId() + " not exist.");
		
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r2.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r3.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r4.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r5.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r6.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r4.getId(), r7.getId()));
		
		subRoleRelationDao.deleteSubRoleRelation(createTestSubRoleRelation(r3.getId(), r7.getId()));
	}
	
	@Test
	public void testDeleteUnexistingSubRoleRelation2() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to delete subRole relation: Direct relation between Role: " + r1.getId() + "and SubRole:" + r4.getId() + " not exist.");
		
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r2.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r3.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r4.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r5.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r6.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r4.getId(), r7.getId()));
		
		subRoleRelationDao.deleteSubRoleRelation(createTestSubRoleRelation(r1.getId(), r4.getId()));
	}
	
	@Test
	public void testFindSubRoleRelations() {
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r2.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r3.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r4.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r5.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r6.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r4.getId(), r7.getId()));
		
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
		
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r4.getId(), r7.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r6.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r5.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r2.getId(), r4.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r3.getId()));
		subRoleRelationDao.createSubRoleRelation(createTestSubRoleRelation(r1.getId(), r2.getId()));
		
		subRoleRelationDao.findSubRoleRelations(null, null, null);		
	}
	
	@Test
	public void testSubRoleRelationType() {

		assertEquals(SubRoleRelationType.DIRECT, SubRoleRelationType.valueOf(SubRoleRelationType.DIRECT.toString()));
		assertEquals(SubRoleRelationType.INDIRECT, SubRoleRelationType.valueOf(SubRoleRelationType.INDIRECT.toString()));
	}
	
	private DirectSubRoleRelation createTestSubRoleRelation(String idRole, String idSubRole) {
		DirectSubRoleRelation dsrr = new DirectSubRoleRelation();
		dsrr.setRoleId(idRole);
		dsrr.setSubRoleId(idSubRole);
		return dsrr;
	}
	
	private String stringOf(String roleId, String subRoleId) {
		return roleId + "-" + subRoleId;
	}

}
