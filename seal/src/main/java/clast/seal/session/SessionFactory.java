package clast.seal.session;

import javax.enterprise.inject.Produces;

public class SessionFactory {
	
	@Produces
	public Session createSession() {
		return new SessionImpl();
	}

}
