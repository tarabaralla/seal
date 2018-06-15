package clast.seal.mongo.service;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.impetus.kundera.KunderaException;

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
		assertTrue( mongoRoleService.createRole(new LeafRole("lr")) );
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
	public void CreateRoleWithOccupiedName() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Cannot create role: name already assigned to another role.");
		
		Role role1 = createTestRole("role1");
		mongoRoleService.createRole(role1);
		
		Role role2 = createTestRole("role1");
		mongoRoleService.createRole(role2);
	}
	
	private Role createTestRole(String name) {
		return new LeafRole(name);
	}

}
