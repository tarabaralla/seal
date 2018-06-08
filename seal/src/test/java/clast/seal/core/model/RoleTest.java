package clast.seal.core.model;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class RoleTest {

	@Test
	public void testEqualRoles() {
		EqualsVerifier.forClass(Role.class).verify();
	}

}
