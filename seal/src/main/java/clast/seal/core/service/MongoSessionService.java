package clast.seal.core.service;

import clast.seal.core.service.session.SessionService;

public class MongoSessionService implements SessionService {
	
	@Override
	public String getDescription() {
		return "Mongo Session Service";
	}
}
