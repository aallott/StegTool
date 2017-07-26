package manipulation;
/**
 * Exception used to indicate the an error in the BitStream class
 * 
 * @author Ashley Allott
 */
public class BitStreamException extends Exception{
	
	private static final long serialVersionUID = 1L;
	
	public BitStreamException(String message){
		super(message);
	}
	public String getMessage(){
		return super.getMessage();
	}
	
}
