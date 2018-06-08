package clast.seal.mongo.service;

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
public class MongoSessionServiceTest {

	@Inject
	@SessionServiceProducer
	@SS_Type(SessionServiceType.MONGO_DB_SS)
	private SessionService mongoSessionService;

	@Test
	public void testMongoSessionServiceInjection() {
		assertTrue(mongoSessionService instanceof MongoSessionService);
	}
	
}
