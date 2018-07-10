package clast.census.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class PersistenceManager {
	
	private static EntityManager em;
	
	private PersistenceManager() {}
	
	public static void setUpMongoConnection(String host, String port, String dbName) {
		
		Map<String, String> persistenceMap = new HashMap<>();

		persistenceMap.put("kundera.nodes", host);
		persistenceMap.put("kundera.port", port);
		persistenceMap.put("kundera.keyspace", dbName);
		persistenceMap.put("kundera.dialect", "mongodb");
		persistenceMap.put("kundera.ddl.auto.prepare", "update");
		persistenceMap.put("kundera.client.lookup.class", "com.impetus.client.mongodb.MongoDBClientFactory");
		
		em = Persistence.createEntityManagerFactory("mongo_pu",persistenceMap).createEntityManager();
	}
	
	public static void setUpMySQLConnection(String url, String username, String password) {
		
		Map<String, String> persistenceMap = new HashMap<>();
		
		persistenceMap.put("javax.persistence.jdbc.url", url);
		persistenceMap.put("javax.persistence.jdbc.user", username);
		persistenceMap.put("javax.persistence.jdbc.password", password);
		persistenceMap.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
		persistenceMap.put("hibernate.show_sql", "false");
		persistenceMap.put("hibernate.format_sql", "true");
		persistenceMap.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
		persistenceMap.put("hibernate.hbm2ddl.auto", "update");
		persistenceMap.put("hibernate.c3p0.min_size", "5");
		persistenceMap.put("hibernate.c3p0.max_size", "20");
		persistenceMap.put("hibernate.c3p0.timeout", "500");
		persistenceMap.put("hibernate.c3p0.max_statements", "50");
		persistenceMap.put("hibernate.c3p0.idle_test_period", "2000");
		
		em = Persistence.createEntityManagerFactory("mysql_pu",persistenceMap).createEntityManager();
	}
	
	public static void setUpTestConnection( String testPU) {
		em = Persistence.createEntityManagerFactory(testPU).createEntityManager();
	}

	public static EntityManager getEntityManager() {
		return em;
	}
	
	public static void tearDownDbConnection() {
		em.close();
	}
	
}
