package clast.seal.core.service.user;

import java.util.Set;

import clast.seal.core.model.User;
import clast.seal.core.service.Service;

public interface UserService extends Service {

	public abstract boolean createUser(User user);

	public abstract Set<User> findAllUsers();

}
