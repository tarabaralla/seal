package clast.seal.core.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public abstract class BaseDao {
	
	EntityManager em;
	
	public BaseDao() {
		//TODO esternalizzare persistence unit name
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mongo_pu");
	    em = emf.createEntityManager();
	}

}
