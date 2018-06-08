package clast.seal.jpa.service;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import clast.seal.core.service.WeldJUnit4Runner;
import clast.seal.core.service.user.US_Type;
import clast.seal.core.service.user.UserService;
import clast.seal.core.service.user.UserServiceProducer;
import clast.seal.core.service.user.UserServiceType;

@RunWith(WeldJUnit4Runner.class)
public class JPAUserServiceTest {

	@Inject
	@UserServiceProducer
	@US_Type(UserServiceType.JPA_US)
	private UserService jpaUserService;
	

	@Test
	public void testJPAUserServiceInjection() {
		assertTrue(jpaUserService instanceof JPAUserService);
	}

}
