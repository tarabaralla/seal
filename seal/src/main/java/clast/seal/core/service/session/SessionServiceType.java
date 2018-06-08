package clast.seal.core.service.session;

import clast.seal.jpa.service.JPASessionService;
import clast.seal.mongo.service.MongoSessionService;

public enum SessionServiceType {
	
	MONGO_DB_SS (MongoSessionService.class), JPA_SS (JPASessionService.class);
	
	private Class<? extends SessionService> sessionService;
	
	private SessionServiceType (Class<? extends SessionService> sessionService) {
		this.sessionService = sessionService;
	}

	public Class<? extends SessionService> getService() {
		return sessionService;
	}

}
