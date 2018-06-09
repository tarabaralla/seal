package clast.seal.mongo.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import clast.seal.core.model.LeafRole;
import clast.seal.core.model.Role;
import clast.seal.core.service.WeldJUnit4Runner;
import clast.seal.core.service.role.RS_Type;
import clast.seal.core.service.role.RoleService;
import clast.seal.core.service.role.RoleServiceProducer;
import clast.seal.core.service.role.RoleServiceType;
import clast.seal.mongo.persistence.Database;

@RunWith(WeldJUnit4Runner.class)
public class MongoRoleServiceTest {

	@Inject
	@RoleServiceProducer
	@RS_Type(RoleServiceType.MONGO_DB_RS)
	private RoleService mongoRoleService;
	
	private Database db;
	
	private Set<Role> roles;
	
	@Before
	public void setUp() {
		roles = new HashSet<Role>();

		db = mock(Database.class);
		when(db.findAllRoles()).thenReturn(roles);
		when(db.createRole(any(Role.class))).thenAnswer(new Answer<Boolean>() {
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return roles.add(invocation.getArgumentAt(0, Role.class));
			}
		});
		
		((MongoService) mongoRoleService).setDatabase(db);
	}
	
	@Test
	public void testMongoRoleServiceInjection() {
		assertTrue(mongoRoleService instanceof MongoRoleService);
	}
	
	@Test
	public void testRoleCreation() {
		assertEquals(0, roles.size());
		
		Role role1 = createTestRole("role1");		
		assertTrue( mongoRoleService.createRole(role1) );
		verify( db, times(1) ).createRole(role1);
		assertEquals(1, roles.size());
		verifyRole(mongoRoleService.findAllRoles().iterator().next(), "role1");
	}
	
	private Role createTestRole(String name) {
		return new LeafRole(name);
	}
	
	private void verifyRole(Role role, String name) {
		assertEquals(name, role.getName());
	}

}
