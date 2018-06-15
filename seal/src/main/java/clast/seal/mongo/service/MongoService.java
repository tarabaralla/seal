package clast.seal.mongo.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;



public abstract class MongoService {
	
	EntityManager em;
	
	static final Logger logger = Logger.getLogger(MongoService.class);
	
	public MongoService() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("mongo_pu");
	    em = emf.createEntityManager();
	}

}
