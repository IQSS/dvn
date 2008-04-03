/**
 * @author evillalon
 * Description: Calculate MessageDigest for a vector with any of the 
 *              sub-classes that extend Number. The algorithm can 
 *              apply any of the MessageDigest algorithms available in java.
 *              The class of the input data is Number and any of the sub-classes.
 *              It can either calculate a different encoding for text
 *              or use class Normalizer of java.text
 *              In development...
 *              
 */
package edu.harvard.iq.vdcnet;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Locale;
import java.util.logging.Logger;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import edu.harvard.iq.vdcnet.utils.utilsConverter;
public class unfNumber<T extends Number>{
	
	private static Logger mLog = Logger.getLogger(unfNumber.class.getName());
	/** the staring encoding */
	private String orchars="ASCII";
	/** the final encoding */
	private String dchars="UTF-32BE"; 
	private static boolean VCPP;
	/** local specific formatting */ 
	Locale currentlocale = Locale.getDefault();
	/** the MessageDigest algorithm **/
	String mdalgor = "MD5";
	/** use Java Normalizer class*/
	private final static char zeroscape = (ucnt.nil).getUcode();
	private static final char[] missv={zeroscape, zeroscape, zeroscape}; 
	//  private byte[] missv= {'\0','\0','\0'};
	protected static int FINAL_ENC =0;
	protected static int ORG_ENC =1;
	private boolean normalizeText= false;
	private Normalizer.Form form = Normalizer.Form.valueOf("NFC"); 
	private MessageDigest md=null;
	public unfNumber(){
		
		try{
			//md5_init in Micah code
		md = MessageDigest.getInstance(mdalgor);
		}catch(NoSuchAlgorithmException err){
			err.getMessage();
		}
	}
	/**
	 * 
	 * @param algor: String with the name of algorithm to 
	 * use with the MessageDigest
	 * @exception: NoSuchAlgorithmException 
	 */
	public unfNumber(String algor){
		mdalgor = algor;
		try{
		//another algor different form md5
		md = MessageDigest.getInstance(algor);
		}catch(NoSuchAlgorithmException err){
			err.getMessage();
		}
	}
	/**
	 * @param dch: String with name of final encoding
	 * @param or: String with name of original encoding  
	 * 
	 */
	public unfNumber(String dch, String or){
		this();
		dchars=dch;
		orchars=or;
	}
	
	public boolean getNormalizeText(){
		return normalizeText;
	}
	/**
	 * 
	 * @param norm: boolean whether to use class java.text.Normalizer
	 */
	
	public void setNormalizeText(boolean norm){
		normalizeText=norm;
	}
	public  Normalizer.Form  getNormalizerForm(){
		return form;
	}
	/**
	 * 
	 * @param norm: Normalizer.for to use with java.text.Normalizer
	 */
	public void setNormalizerForm(Normalizer.Form norm){
		form=norm;
	}
	/**
	 * 
	 * @param x: double
	 * @return int to indicate if x is a special number
	 */
   public static int mysinf(Double x){
	   if(VCPP){
		   if(!x.isInfinite()|| !x.isNaN()) return 0;
		   if(x.isNaN()) return 0;
		   if(x>0) return 1;
		   return -1;
	   }else{
		   Boolean b=  x.isInfinite();
		   if(b) return 1;else return 0;
	   }
   }
   
   /**
    * 
    * @param v: vecor of class Number or its sub-classes
    * @param digits: int with the numebers of digits for precission
    * @param result : of Class Number 
    * @throws UnsupportedEncodingException
    * @throws NoSuchAlgorithmException
    */
   public byte[] RUNF3(T[]v, int digits, int [] result)//, String[] resultBase64)  
   throws UnsupportedEncodingException, NoSuchAlgorithmException {
	   int nv = v.length;
	   double dub =0;
	   boolean miss= false;
	   int k=0;
	   for(k=0; k < nv; ++k ){
		   dub= v[k].doubleValue();
		   if(Double.isNaN(dub)) miss=true;
		   //md5_append is called with UNF3
		   md = UNF3(v[k],digits,md,miss);
	   }
	   /**produces, by default, 16 byte digest: equivalent to md5_finish**/
	   byte [] hash = md.digest();
	   if((hash.length > 16) && mdalgor.equals("MD5") )
		   mLog.info("unfNumber: hash has more than 16 bytes.."+hash.length); 
	   for(k=0; k < hash.length; ++k)
		   result[k]= ((Byte) hash[k]).intValue();
	   Base64 bas64 = new Base64();
	   String tobase64 =bas64.encode(hash);
	   byte[] base64b = tobase64.getBytes();
	   System.out.println(tobase64); 
	   return hash;
	   }
		   
	/**
	 * 
	 * @param obj: Class Number or sub-classes
	 * @param digits: integer for precision arithmetic
	 * @param previous: MessageDigest 
	 * @param miss: boolean for missing values
	 * @return boolean
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */   
   
   public MessageDigest UNF3(T obj, int digits, MessageDigest previous, boolean miss)
   throws UnsupportedEncodingException, NoSuchAlgorithmException {
	   if(!miss){
		   roundRoutines<T> rout = new roundRoutines<T>(digits, currentlocale);
		  
		   String tmps = rout.Genround(obj,digits); 
		   if(tmps==null) {
			   mLog.severe("UNF3: Genroun returns null");
			   return previous;
		   }
		   
		   String dec [] = new String[2];
    	   dec[FINAL_ENC]=dchars;
	       dec[ORG_ENC]= orchars;
	     
           byte[] bt = tmps.getBytes(orchars);
	       
	       byte [] tmpu = null;
		  
		   if(!normalizeText) 
		       tmpu = utilsConverter.byteConverter(bt, dec);
		   else
			   tmpu = canonUnicode.CanonalizeUnicode(bt,form,dec);
		   if(tmpu==null) {
			   mLog.severe("UNF3: CanonalizeUnicode returns null");
			   return previous;
		   }
		   //md5_append in Micah code
		   md.update(tmpu);
	   } 
	  
	   if(miss){
		  byte [] topass= utilsConverter.getBytes(missv,null); 
		  md.update(topass);
	   }
				  
	   return previous;
	   }
   public static void main(String args[]) throws Exception{
	   unfNumber<Double> unf1 = new unfNumber<Double>();
	   int [] result = new int[16];
	   Double [] dub = {1.0, 2.0, 3.0, 4.0};
	   byte [] b= unf1.RUNF3(dub,7,result);
	   for(int n=0; n <16; ++n){
		   mLog.info("n= "+ n+"; result= "+result[n]);
		   mLog.info("n= "+ n+"; hash= "+b[n]);
		   
		   
	   }
	   unfString<String> unf2 = new unfString<String>();
	   String [] str ={"ab", "cd", "fg"};
	   
   }
   
   }

