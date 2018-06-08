package clast.seal.jpa.service;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import clast.seal.core.service.WeldJUnit4Runner;
import clast.seal.core.service.role.RS_Type;
import clast.seal.core.service.role.RoleService;
import clast.seal.core.service.role.RoleServiceProducer;
import clast.seal.core.service.role.RoleServiceType;

@RunWith(WeldJUnit4Runner.class)
public class JPARoleServiceTest {

	@Inject
	@RoleServiceProducer
	@RS_Type(RoleServiceType.JPA_RS)
	private RoleService jpaRoleService;
	
	@Test
	public void testJPARoleServiceInjection() {
		assertTrue(jpaRoleService instanceof JPARoleService);
	}

}
