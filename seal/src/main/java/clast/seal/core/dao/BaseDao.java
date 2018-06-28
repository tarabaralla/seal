package clast.seal.core.dao;

import javax.persistence.EntityManager;

import clast.seal.core.persistence.EntityManagerProducer;

public abstract class BaseDao {
	
	private EntityManagerProducer emp = new EntityManagerProducer();
	
	EntityManager getEntityManager() {
		return emp.getEntityManager();
	}

}
