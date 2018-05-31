package clast.seal.core.dao;

import java.util.Set;

import clast.seal.core.model.SealDb;
import clast.seal.core.model.User;

public class AdminDao {
	
	private SealDb db;
	
	public AdminDao(SealDb db) {
		super();
		this.db = db;
	}

	public Set<User> getAllUsers() {
		return db.getAllUsers();
	}

	public boolean addUser(User user) {
		return db.addUser(user);
		
	}

}
