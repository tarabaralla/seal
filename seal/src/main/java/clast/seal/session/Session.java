package clast.seal.session;

import clast.seal.model.User;

public interface Session {

	public boolean login(String username, String password);

	public User getLoggedUser();

}
