package clast.seal.core.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import clast.seal.core.model.Role;
import clast.seal.core.model.SealDb;
import nl.jqno.equalsverifier.EqualsVerifier;

public class RoleDaoTest {
	
	private RoleDao roleDao;
	private SealDb db;
	private Set<Role> roles;
	
	@Before
	public void setUp() {
		roles = new HashSet<Role>();
		
		db = mock(SealDb.class);
		when(db.getAllRoles()).thenReturn(roles);
		when(db.addRole(any(Role.class))).thenAnswer(new Answer<Boolean>() {
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return roles.add(invocation.getArgumentAt(0, Role.class));
			}
		});
		
		roleDao = new RoleDao(db);
	}

	@Test
	public void testRoleCreation() {
		assertEquals(0, roles.size());
		
		Role role1 = createRole("role1");		
		assertTrue( roleDao.addRole(role1) );
		verify( db, times(1) ).addRole(role1);
		assertEquals(1, roles.size());
		verifyRole(roleDao.getAllRoles().iterator().next(), "role1");
	}
	
	@Test
	public void testEqualRoles() {
		EqualsVerifier.forClass(Role.class).verify();
	}

	private Role createRole(String name) {
		return new Role(name);
	}
	
	private void verifyRole(Role role, String name) {
		assertEquals(name, role.getName());
	}

}
