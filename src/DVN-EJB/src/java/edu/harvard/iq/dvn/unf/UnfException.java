/**
 * Exception class of type UNF
 */
package  edu.harvard.iq.dvn.unf;

public class UnfException extends Exception{
	/**
	 * Constructor
	 */
	public UnfException(){
		super();
	}
	/**
	 * Constructor
	 * @param mess String message for Exception 
	 */
	
	public UnfException(String mess){
		super(mess);
	}

}
