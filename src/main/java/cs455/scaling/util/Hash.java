package cs455.scaling.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	
	//Computes the SHA-1 hash of the given byte[]
	public String getHash(byte[] data) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Could not find algorithm SHA1: " + e);
			return null;
		}
		byte[] hashArray = digest.digest(data);
		BigInteger hash = new BigInteger(1, hashArray);
		
		return hash.toString(16);
	}
	
}

