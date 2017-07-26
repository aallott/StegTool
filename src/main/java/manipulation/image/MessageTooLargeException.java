package manipulation.image;

/**
 * <p>Exception for indicating that a given message is too large to be hidden in
 * a given covertext file.
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
