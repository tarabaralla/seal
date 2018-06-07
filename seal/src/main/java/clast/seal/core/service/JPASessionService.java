package clast.seal.core.service;

import clast.seal.core.service.session.SessionService;

public class JPASessionService implements SessionService {

	private JPASessionService() {}
	
	@Override
	public String getDescription() {
		return "JPA Session Service";
	}
}
