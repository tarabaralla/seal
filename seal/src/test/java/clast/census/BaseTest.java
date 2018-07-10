package clast.census;

import org.junit.BeforeClass;

import clast.census.persistence.PersistenceManager;

public class BaseTest {

	@BeforeClass
	public static void setUpPersistence() {
		PersistenceManager.setUpTestConnection("hsql_pu_test");
	}

}
