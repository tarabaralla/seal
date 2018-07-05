package clast.seal.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Embeddable;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

@Embeddable
public class Password {
	
	private String value;
	
	public Password() {}
	
	public Password(String plainPassword) {
		encryptPassword(plainPassword);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void encryptPassword(String plainPassword) {
		if (plainPassword == null) {
			throw new IllegalArgumentException("Unable to encrypt password: plainpassword cannot be null.");
		}
		value = StringUtils.isBlank(plainPassword) ? "" : encrypt(plainPassword);
	}

	public boolean checkPassword(String plainPassword) {
		
		if ( plainPassword == null ) {
			return false;
		}
		
		if (StringUtils.isBlank(plainPassword) && StringUtils.isBlank(value) && value != null) {
			return true;
		}
		
		return value.equals(encrypt(plainPassword));
	}

	private String encrypt(String plainPassword) {
		
		try {
			
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(plainPassword.getBytes());

			return new String(Base64.encodeBase64(md.digest()));
			
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Unable to encrypt: encryption algorithm not found");
		}
	}

}
