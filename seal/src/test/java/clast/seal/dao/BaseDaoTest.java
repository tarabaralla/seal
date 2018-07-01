package clast.seal.dao;

import org.junit.BeforeClass;

import clast.seal.persistence.PersistenceManager;

public class BaseDaoTest {

	@BeforeClass
	public static void setUpPersistence() {
		PersistenceManager.setUpTestConnection("mongo_pu_test");
	}

}
