package clast.census.dao;

import javax.persistence.EntityManager;

import clast.census.persistence.PersistenceManager;

public abstract class BaseDao {
	
	EntityManager getEntityManager() {
		return PersistenceManager.getEntityManager();
	}

}
