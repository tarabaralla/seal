package clast.seal.core.dao;

import javax.persistence.EntityManager;

import clast.seal.core.persistence.EntityManagerProducer;

public abstract class BaseDao {
	
	EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

}
