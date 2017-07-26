package manipulation;

/**
 * <p>Defines methods for a Codec class
 * @author Ashley Allott
 */
public interface Codec {
	
	/**
	 * <p>Reads a file from a byte array.
	 * 
	 * @param fileBytes	the array containing the file
	 * @throws Exception
	 */
	public abstract void decodeStream(byte[] fileBytes) throws Exception;
	
	/**
	 * <p>Embeds a given hidden message into the file structure.
	 * 
	 * @param message		the message to be embedded
	 * @param passwordKey	a key which can be used in the embedding
	 * @param degradation	a value to change the degradation level of the embedding
	 * @throws MessageTooLargeException
	 */
	public abstract void encode_msg(byte[] message, String passwordKey, int degradation) throws MessageTooLargeException;
	
	/**
	 * <p>Recovers a message from the file structure.
	 * 
	 * <p>Must be able to extract the same message embedding in encode_msg.
	 * 
	 * @param passwordKey	a key which can be used in the recovery
	 * @return	byte array of the hidden message
	 * @throws MessageTooLargeException
	 * @throws Exception
	 */
	public abstract byte[] decode_msg(String passwordKey) throws MessageTooLargeException, Exception;
	
	/**
	 * <p>Produces a byte array containing the file (with any modifications made).
	 * 
	 * <p>The returned file must be of the same format of the one the Codec reads,
	 * and must include any embedded hidden messages.
	 * 
	 * @return	byte array containing the file
	 * @throws Exception
	 */
	public abstract byte[] genStego() throws Exception;
}
