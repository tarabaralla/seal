package clast.seal.core.service;

import clast.seal.core.service.session.SessionService;

public class JPASessionService implements SessionService {

	@Override
	public String getDescription() {
		return "JPA Session Service";
	}
}
