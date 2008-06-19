/**
 * Description: Base64 encoding algorithm. The base65(byte[] input) takes into account
 *              the endiannes of the byte stream and, by default, it turns  
 *              the byte array input to  BIG_ENDIAN order, before applying 
 *              the base64 algorithm. It uses the undocumented class
 *              BASE64Encoder of library sun.misc.*
 *              
 * @author evillalon             
 */
package edu.harvard.hmdc.vdcnet.util.unf;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;



import sun.misc.BASE64Encoder;
public class Base64Sun implements UnfCons{

	private static Logger mLog = Logger.getLogger(Base64Sun.class.getName());
	
	/** default byte order */
	private static ByteOrder border = ByteOrder.BIG_ENDIAN;
	
	public Base64Sun(){
		if(!DEBUG)
			mLog.setLevel(Level.WARNING);
		
	}
	public Base64Sun(ByteOrder ord){
		border =ord;
	}
	/**
	 * 
	 * @return ByteOrder 
	 */
	public static ByteOrder getBorder(){
	   	return border;
	}
	public static void setBorder(ByteOrder ord){
		border=ord;
	}
	/**
	 * 
	 * @param digest: byte array for encoding in base 64, 
	 * @return String encoded base64
	 */
	public static String tobase64(byte[]digest, boolean chngByteOrd){
		  
		   String tobase64 = null;
		   ByteOrder local = ByteOrder.nativeOrder();
		   String ordbyte = local.toString();
		   mLog.info("Native byte order is: "+ordbyte);
		   ByteBuffer btstream = ByteBuffer.wrap(digest);
		   btstream.order(ByteOrder.BIG_ENDIAN);
		   if(chngByteOrd){
		   byte [] revdigest = changeByteOrder(digest, local);
		   if(revdigest!=null)
			   btstream.put(revdigest);
		   else 
			   btstream.put(digest);
		   }
			
		
		    tobase64=new BASE64Encoder().encode(btstream.array());
			   
			return tobase64;
			   
		   }
	 /**
	  * Helper function to change the endianess of the byte array
	  * @param digest: byte array
	  * @param local: ByteOrder
	  * @return byte array with endianness according to getBorder()
	  */
public static byte[] changeByteOrder(byte[] digest, ByteOrder local)
{
	   byte [] revdigest = new byte[digest.length];   
	   
if((local.equals(ByteOrder.LITTLE_ENDIAN) && getBorder().equals(ByteOrder.BIG_ENDIAN))||
	  (local.equals(ByteOrder.BIG_ENDIAN) && getBorder().equals(ByteOrder.LITTLE_ENDIAN))){   
	   int ln = digest.length; 
	   for(int n=0; n < ln; ++n)
		   revdigest[n] = digest[ln-1-n];
}else{
	revdigest=digest;
}
	  return revdigest;
}

}
