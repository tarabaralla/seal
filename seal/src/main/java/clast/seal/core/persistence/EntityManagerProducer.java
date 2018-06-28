package clast.seal.core.persistence;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class EntityManagerProducer {
	
	private static EntityManager em = Persistence.createEntityManagerFactory("mysqlJPAtest").createEntityManager();
	
	public EntityManagerProducer() {
	}

	public EntityManager getEntityManager() {
		return em;
	}

	
}
