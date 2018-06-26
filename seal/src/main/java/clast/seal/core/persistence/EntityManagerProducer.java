package clast.seal.core.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProducer {
	
	private static EntityManager em;
	
	static {
		//TODO esternalizzare persistence unit name
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mongo_pu");
		em = emf.createEntityManager();
	}

	public static EntityManager getEntityManager() {
		return em;
	}

	
}
