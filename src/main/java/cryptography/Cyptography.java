package cryptography;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class handles the cryptographic operations of the applications.
 * 
 * @author Ashley Allott
 *
 */
public class Cyptography {
	private static final String CIPHER_ALGORITHM_KEY = "AES";
	private static final String CIPHER_ALGORITHM_CIPHER  = "AES";
	public static final int CIPHER_BLOCK_SIZE = 16;
	/**
	 * <p>Hashes a given string password using SHA-512, returning the result in a byte array of size 64
	 * 
	 * @param password		the string password to be hashed
	 * @return				the 64 size byte array containing the hashed password
	 * @throws NoSuchAlgorithmException
	 */
	public static byte[] hashKey(String password) throws NoSuchAlgorithmException{
		MessageDigest digest = MessageDigest.getInstance("SHA-512");
		byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
		return hash;
	}
	
	/**
	 * <p>Generates a long seed from a hashedKey.
	 * 
	 * <p>The method performs multiple bitwise ORs ad ANDs between the long array and
	 * the key array, with insertion of 'random' bytes, to create an apparent random
	 * seed.
	 * 
	 * @param hashedKey		a byte array containing the key, this will be used to 
	 * 						construct the long seed.
	 * @return				an apparent random seed, created from the key.
	 */
	public static long genSeed(byte[] hashedKey){
		ByteBuffer bb = ByteBuffer.allocate(Long.BYTES);
		bb.putLong(Long.MAX_VALUE);
		byte[] longValue = bb.array();
		
		Random rand = new Random();
		rand.setSeed(Long.MAX_VALUE);
		for(int i=0; i<8; i++){
			for(int j=0; j<hashedKey.length; j++){
				if((j)%(hashedKey.length / 8) == i){
					if(i%2 == 0){
						longValue[i] = (byte)(longValue[i] | hashedKey[j]);
					}else{
						longValue[i] = (byte)(longValue[i] & hashedKey[j]);
						longValue[i] = (byte)(longValue[i] | rand.nextLong());
					}
				}
			}
		}
		
		bb = ByteBuffer.wrap(longValue);
		return bb.getLong();
	}
	
	/**
	 * <p>Encrypts a given plaintext using a specified encryption key.
	 * 
	 * @param key		the key to be used for the encryption
	 * @param plaintext	the plaintext to be encrypted
	 * @return			the produced ciphertext
	 */
	public static byte[] cipherEncrypt(byte[] key, byte[] plaintext){
		try {
			key = Arrays.copyOf(key, 16);
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, CIPHER_ALGORITHM_KEY);
			
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CIPHER);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			byte[] cipherBytes = cipher.doFinal(plaintext);
			return cipherBytes;
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>Decrypts a given ciphertext using a specified encryption key
	 * @param key			the key to be used for the encryption
	 * @param ciphertext	the ciphertext to be decrypted
	 * @return				the produced plaintext
	 */
	public static byte[] cipherDecrypt(byte[] key, byte[] ciphertext){
		try {    
			//Resize the supplied key array to the required size (128 bits)
			key = Arrays.copyOf(key, 16);
			//Using the supplied key, create a new key in format required by the cipher algorithm
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, CIPHER_ALGORITHM_KEY);
			
			//Create a Cipher and initialise it to the specified cipher algorithm
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM_CIPHER);
			
			//Set the cipher to Decrypt mode and supply the decryption key
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			
			//Decrypt the data, and return the result
			byte[] plainBytesDecrypted = cipher.doFinal(ciphertext);
			return plainBytesDecrypted;
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return null;
	}
}
