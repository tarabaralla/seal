package clast.seal.core.model;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class UserTest {

	@Test
	public void testEqualUsers() {
		// @formatter:off
		EqualsVerifier.forClass(User.class)
						.withIgnoredFields("password", "name", "lastname", "email", "phone")
							.verify();
		// @formatter:on
	}

}
