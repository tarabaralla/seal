package clast.seal.core.service;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import clast.seal.core.service.role.RS_Type;
import clast.seal.core.service.role.RoleService;
import clast.seal.core.service.role.RoleServiceProducer;
import clast.seal.core.service.role.RoleServiceType;
import clast.seal.core.service.session.SS_Type;
import clast.seal.core.service.session.SessionService;
import clast.seal.core.service.session.SessionServiceProducer;
import clast.seal.core.service.session.SessionServiceType;
import clast.seal.core.service.user.US_Type;
import clast.seal.core.service.user.UserService;
import clast.seal.core.service.user.UserServiceProducer;
import clast.seal.core.service.user.UserServiceType;

@RunWith(WeldJUnit4Runner.class)
public class ServiceFactoryTest {
	
	@Inject
	@SessionServiceProducer
	@SS_Type(SessionServiceType.MONGO_DB_SS)
	private SessionService mongoSessionService;
	
	@Inject
	@SessionServiceProducer
	@SS_Type(SessionServiceType.JPA_SS)
	private SessionService jpaSessionService;
	
	@Inject
	@UserServiceProducer
	@US_Type(UserServiceType.MONGO_DB_US)
	private UserService mongoUserService;

	@Inject
	@UserServiceProducer
	@US_Type(UserServiceType.JPA_US)
	private UserService jpaUserService;

	@Inject
	@RoleServiceProducer
	@RS_Type(RoleServiceType.MONGO_DB_RS)
	private RoleService mongoRoleService;

	@Inject
	@RoleServiceProducer
	@RS_Type(RoleServiceType.JPA_RS)
	private RoleService jpaRoleService;
	
	@Test
	public void testMongoSessionServiceCreation() {
		assertEquals("Mongo Session Service", mongoSessionService.getDescription());
	}
	
	@Test
	public void testJPASessionServiceCreation() {
		assertEquals("JPA Session Service", jpaSessionService.getDescription());
	}
	
	@Test
	public void testMongoUserService() {
		assertEquals("Mongo User Service", mongoUserService.getDescription());
	}
	
	@Test
	public void testJPAUserService() {
		assertEquals("JPA User Service", jpaUserService.getDescription());
	}
	
	@Test
	public void testMongoRoleService() {
		assertEquals("Mongo Role Service", mongoRoleService.getDescription());
	}
	
	@Test
	public void testJPARoleService() {
		assertEquals("JPA Role Service", jpaRoleService.getDescription());
	}
	
	

}
