package clast.seal.mongo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.impetus.kundera.KunderaException;

import clast.seal.core.model.CompositeRole;
import clast.seal.core.model.LeafRole;
import clast.seal.core.model.Role;
import clast.seal.core.service.WeldJUnit4Runner;
import clast.seal.core.service.role.RS_Type;
import clast.seal.core.service.role.RoleService;
import clast.seal.core.service.role.RoleServiceProducer;
import clast.seal.core.service.role.RoleServiceType;

@RunWith(WeldJUnit4Runner.class)
public class MongoRoleServiceTest {

	@Inject
	@RoleServiceProducer
	@RS_Type(RoleServiceType.MONGO_DB_RS)
	private RoleService mongoRoleService;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testMongoRoleServiceInjection() {
		assertTrue(mongoRoleService instanceof MongoRoleService);
	}
	
	@Test
	public void testCreateRole() {
		
		Role lr1 = new LeafRole("lr1");
		
		Role lr2 = new LeafRole("lr2");
		lr2.addManagedRole(lr1);
		
		Role cr1 = new CompositeRole("cr1");
		cr1.checkSubRoleRelation(lr2);
		
		Role role = new CompositeRole("role1");
		role.checkSubRoleRelation(cr1);
		role.checkSubRoleRelation(lr1);
		role.addManagedRole(lr2);
		
		assertTrue( mongoRoleService.createRole(role) );
		
		assertNotNull( mongoRoleService.findByName("lr1") );
		
		Role role1 = mongoRoleService.findByName("lr2");
		assertNotNull(role1);
		assertEquals("lr1", role1.getDirectManagedRoles().iterator().next().getName());
		
		Role role2 = mongoRoleService.findByName("cr1");
		assertNotNull(role2);
		assertEquals(1, role2.getDirectSubRoles().size());
		assertEquals(1, role2.getAllSubRoles().size());
		assertEquals("lr2", role2.getDirectSubRoles().iterator().next().getName());
		assertEquals(0, role2.getDirectManagedRoles().size());
		assertEquals(1, role2.getAllManagedRoles().size());
		assertEquals("lr1", role2.getAllManagedRoles().iterator().next().getName());
		
		Role role3 = mongoRoleService.findByName("role1");
		assertNotNull(role3);
		assertEquals(2, role3.getDirectSubRoles().size());
		assertEquals(3, role3.getAllSubRoles().size());
		assertEquals(1, role3.getDirectManagedRoles().size());
		assertEquals(2, role3.getAllManagedRoles().size());
		
	}
	
	@Test
	public void testCreateRoleWithoutName() {
		expectedEx.expect(KunderaException.class);
		
		mongoRoleService.createRole( new LeafRole() );
	}
	
	@Test
	public void testCreateExistingRole() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Cannot create role: a role with same id already exist.");
	
		Role role = createTestRole("role1");
		
		mongoRoleService.createRole(role);
		mongoRoleService.createRole(role);
	}
	
	@Test
	public void testDeleteRole() {
		Role role = createTestRole("role1");
		mongoRoleService.createRole(role);
		
		assertTrue( mongoRoleService.deleteRole(role));
	}
	
	@Test
	public void testDeleteRoleWithoutId() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Cannot delete role: role id cannot be null.");
		
		Role role = createTestRole("role1");
		mongoRoleService.deleteRole(role);
	}
	
	@Test
	public void testDeleteRoleNotPersisted() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Cannot delete role: role with id 123 not found.");
		
		Role role = createTestRole("role1");
		role.setId("123");
		mongoRoleService.deleteRole(role);
	}
	
	@Test
	public void testCreateRoleWithOccupiedName() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Cannot create role: name already assigned to another role.");
		
		Role role1 = createTestRole("role1");
		mongoRoleService.createRole(role1);
		
		Role role2 = createTestRole("role1");
		mongoRoleService.createRole(role2);
	}
	
	@Test
	public void testFindAllRoles() {
		
		assertEquals(0, mongoRoleService.findAll().size());
		
		mongoRoleService.createRole( createTestRole("role1") );
		Set<Role> roles = mongoRoleService.findAll();
		assertEquals(1, roles.size());
		assertEquals("role1", roles.iterator().next().getName());
		
		mongoRoleService.createRole( createTestRole("role2") );
		roles = mongoRoleService.findAll();
		Set<String> roleNames = roles.stream()
										.map( r -> r.getName() )
										.collect( Collectors.toSet() );
		assertEquals(2, roles.size());
		assertTrue(roleNames.contains("role1"));
		assertTrue(roleNames.contains("role2"));
		
	}
	
	private Role createTestRole(String name) {
		return new LeafRole(name);
	}

}
