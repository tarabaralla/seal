package clast.seal.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PasswordTest {
	
	private Password password;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	@Before
	public void setUp() {
		password = new Password("123");
	}
	
	@Test
	public void testValue() {
		password = new Password();
		password.setValue("123");
		assertEquals("123",password.getValue());
	}

	@Test
	public void testEncryptPassword() {
		password.encryptPassword("password");
		assertTrue(password.checkPassword("password"));
	}
	
	@Test
	public void testEncryptNullPassword() {
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Unable to encrypt password: plainpassword cannot be null.");
		
		password.encryptPassword(null);
	}
	
	@Test
	public void testEncryptBlankPassword() {
		password.encryptPassword("");
		assertTrue(password.checkPassword(""));
	}
	
	@Test
	public void testCheckNullPassword() {
		assertFalse(password.checkPassword(null));
	}
	
	@Test
	public void testCheckEmptyPassword() {
		password.encryptPassword("");
		assertTrue(password.checkPassword(""));
	}

}
