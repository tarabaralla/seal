package clast.seal.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class PersistenceManager {
	
	private static EntityManager em;
	
	public PersistenceManager() {
	}
	
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
		
		persistenceMap.put("hibernate.connection.url", url);
		persistenceMap.put("hibernate.connection.username", username);
		persistenceMap.put("hibernate.connection.password", password);
		persistenceMap.put("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
		persistenceMap.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		persistenceMap.put("hibernate.show_sql", "false");
		persistenceMap.put("hibernate.max_fetch_depth", "3");
		
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
