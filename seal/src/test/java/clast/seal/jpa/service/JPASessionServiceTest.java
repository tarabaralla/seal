package clast.seal.jpa.service;

import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import clast.seal.core.service.WeldJUnit4Runner;
import clast.seal.core.service.session.SS_Type;
import clast.seal.core.service.session.SessionService;
import clast.seal.core.service.session.SessionServiceProducer;
import clast.seal.core.service.session.SessionServiceType;

@RunWith(WeldJUnit4Runner.class)
public class JPASessionServiceTest {

	@Inject
	@SessionServiceProducer
	@SS_Type(SessionServiceType.JPA_SS)
	private SessionService jpaSessionService;

	@Test
	public void testJPASessionServiceInjection() {
		assertTrue(jpaSessionService instanceof JPASessionService);
	}

}
