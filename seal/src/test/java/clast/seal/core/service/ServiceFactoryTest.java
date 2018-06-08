package clast.seal.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import clast.seal.core.service.session.SS_Type;
import clast.seal.core.service.session.SessionService;
import clast.seal.core.service.session.SessionServiceProducer;
import clast.seal.core.service.session.SessionServiceType;

@RunWith(Arquillian.class)
public class ServiceFactoryTest {
	
	@Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClasses(Service.class, SessionService.class, MongoSessionService.class, SessionServiceProducer.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

	@Inject
	@SessionServiceProducer
	@SS_Type(SessionServiceType.MONGO_DB_SS)
	private Service mongoSessionService;

//	@Inject
//	@SessionServiceProducer
//	@SS_Type(SessionServiceType.JPA_SS)
//	private Service jpaSessionService;
//
//	@Inject
//	@UserServiceProducer
//	@US_Type(UserServiceType.MONGO_DB_US)
//	private Service mongoUserService;
//
//	@Inject
//	@UserServiceProducer
//	@US_Type(UserServiceType.JPA_US)
//	private Service jpaUserService;
//
//	@Inject
//	@RoleServiceProducer
//	@RS_Type(RoleServiceType.MONGO_DB_RS)
//	private Service mongoRoleService;
//
//	@Inject
//	@RoleServiceProducer
//	@RS_Type(RoleServiceType.JPA_RS)
//	private Service jpaRoleService;

	@Test
	public void testMongoSessionServiceCreation() {
		assertTrue(mongoSessionService instanceof MongoSessionService);
		assertEquals("Mongo Session Service", mongoSessionService.getDescription());
	}

//	@Test
//	public void testJPASessionServiceCreation() {
//		assertTrue(jpaSessionService instanceof JPASessionService);
//		assertEquals("JPA Session Service", jpaSessionService.getDescription());
//	}
//
//	@Test
//	public void testMongoUserService() {
//		assertTrue(mongoUserService instanceof MongoUserService);
//		assertEquals("Mongo User Service", mongoUserService.getDescription());
//	}
//
//	@Test
//	public void testJPAUserService() {
//		assertTrue(jpaUserService instanceof JPAUserService);
//		assertEquals("JPA User Service", jpaUserService.getDescription());
//	}
//
//	@Test
//	public void testMongoRoleService() {
//		assertTrue(mongoRoleService instanceof MongoRoleService);
//		assertEquals("Mongo Role Service", mongoRoleService.getDescription());
//	}
//
//	@Test
//	public void testJPARoleService() {
//		assertTrue(jpaRoleService instanceof JPARoleService);
//		assertEquals("JPA Role Service", jpaRoleService.getDescription());
//	}

}
