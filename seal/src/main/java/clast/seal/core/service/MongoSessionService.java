package clast.seal.core.service;

import clast.seal.core.service.session.SessionService;

public class MongoSessionService implements SessionService {
	
	private MongoSessionService() {}
	
	@Override
	public String getDescription() {
		return "Mongo Session Service";
	}
}
