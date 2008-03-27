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
public class unfNumber<T extends Number>{
	
	private static Logger mLog = Logger.getLogger(unfNumber.class.getName());
	/** the staring encoding */
	private String orchars="ASSCII";
	/** the final encoding */
	private String dchars="UTF-32BE"; 
	private boolean VCPP;
	/** local formatting */ 
	Locale currentlocale = Locale.getDefault();
	/** the MessageDigest algoritm **/
	String mdalgor = "MD5";
	/** use Java Normalizer class*/
	private boolean normalizeText= false;
	private Normalizer.Form form = Normalizer.Form.valueOf("NFC"); 
	private MessageDigest md=null;
	public unfNumber(){
		try{
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
   public int mysinf(Double x){
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
   public void RUNF3(T[]v, int digits, T result)  
   throws UnsupportedEncodingException, NoSuchAlgorithmException {
	   int nv = v.length;
	   double dub =0;
	   boolean miss= false;
	   for(int k=0; k < nv; ++k ){
		   dub= v[k].doubleValue();
		   if(Double.isNaN(dub)) miss=true;
		   UNF3(v[k],digits,md,miss);
	   }
	   
	   }
		   
	/**
	 * 
	 * @param obj: Class Number or sub-classes
	 * @param digits: integer for precission arithmetic
	 * @param previous: MessageDigest 
	 * @param miss: boolean for missing values
	 * @return boolean
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */   
   
   public boolean UNF3(T obj, int digits, MessageDigest previous, boolean miss)
   throws UnsupportedEncodingException, NoSuchAlgorithmException {
	   if(!miss){
		   roundRoutines<T> rout = new roundRoutines<T>(digits, currentlocale);
		   byte[] tmps =rout.GenroundBytes(obj, digits);
		   if(tmps==null) return false;
		   canonUnicode can = new canonUnicode();
		   String dec [] = new String[2];
    	   dec[can.FINAL_ENC]=dchars;
	       dec[can.ORG_ENC]= orchars;
	       byte [] tmpu = null;
		   if(!normalizeText) 
		       tmpu = can.byteConverter(tmps, dec);
		   else
			   tmpu = can.CanonalizeUnicode(tmps,form,dec);
		   if(tmpu==null) return false;
		   md.update(tmpu);
	   } 
	  
	   if(miss){
	   }
				  
	   return true;
	   }
   }

