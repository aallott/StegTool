package manipulation;

import javax.crypto.BadPaddingException;

/**
 * <p>Defines required methods for an encoder class
 * @author Ashley Allott
 *
 */
public interface Encoder {
	/**
	 * <p>Embeds a given hidden message into the file, producing a stegotext.
	 * 
	 * @param file			the WorkFile containing the covertext
	 * @param message		the message to be embedded
	 * @param degradation	a value to change the degradation level of the embedding
	 * @param password		a key which can be used in the embedding
	 * @return				byte array containing the stegotext
	 * @throws MessageTooLargeException
	 */
	public abstract byte[] encode_msg(WorkFile file, byte[] message, short degradation, String password) throws MessageTooLargeException;
	
	/**
	 * <p>Recovers a message from a stegotext file.
	 * 
	 * @param file			the WorkFile containing the stegotext
	 * @param password		a key which can be used in the recovery
	 * @return				the recovered message
	 * @throws BadPaddingException
	 * @throws Exception
	 */
	public abstract byte[] decode_msg(WorkFile file, String password) throws BadPaddingException, Exception;
}
