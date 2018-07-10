package clast.census.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionAlgorithmImpl implements EncryptionAlgorithm {

	@Override
	public MessageDigest getMessageDigest(String plainPassword) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(plainPassword.getBytes());
		return md;
	}
}
