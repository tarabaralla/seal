package clast.seal.dao;

import javax.persistence.EntityManager;

import clast.seal.persistence.PersistenceManager;

public abstract class BaseDao {
	
	EntityManager getEntityManager() {
		return PersistenceManager.getEntityManager();
	}

}
