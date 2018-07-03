package clast.seal;

import org.junit.BeforeClass;

import clast.seal.persistence.PersistenceManager;

public class BaseTest {

	@BeforeClass
	public static void setUpPersistence() {
		PersistenceManager.setUpTestConnection("hsql_pu_test");
	}

}
