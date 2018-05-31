package clast.seal.core.model;

import java.util.Set;

public interface SealDb {

	public abstract Set<User> getAllUsers();

	public abstract boolean addUser(User user);

}
