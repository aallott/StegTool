package manipulation;
/**
 * <p>Exception used to indicate the given hidden message is too large to be
 * able to be encoding in a given stegotext file.
 * 
 * @author Ashley Allott
 */
public class MessageTooLargeException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public MessageTooLargeException(String message){
		super(message);
	}
	public String getMessage(){
		return super.getMessage();
	}
	
}
