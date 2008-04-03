/**
 * Description: Generalized Rounding Routines for Strings
 *              Implements Micah Altman code for rounding characters Strings
 *              The implementation is in method Genround
 * Input: int with number of characters to keep  
 *        String obj to apply the rounding routine;  
 *        
 * Output: String representation of formatted String.
 * 
 * @Author Elena Villalon
 *  email:evillalon@iq.harvard.edu
 *  
 */
package edu.harvard.iq.vdcnet;

import java.text.Normalizer;
import java.util.Locale;
import java.util.logging.Logger;

public class roundString {
	//ucode characters
	   private static final char dot=ucnt.dot.getUcode();//decimal separator "."
	    private static final char percntg=ucnt.percntg.getUcode(); //"%"
	    private static final char s =ucnt.s.getUcode();//"s"
	    //language and country to format strings
	    Locale loc = new Locale("en", "US");
	    private static Logger mLog = Logger.getLogger(roundRoutines.class.getName());
	    public roundString(){}
	    /**
	     * 
	     * @param loc: Locale to set character encodings
	     */
	    
	    public roundString(Locale loc){this.loc = loc;}
	    /**
	     * 
	     * @param str: String for formatting
	     * @param digits: int for number of characters 
	     * @return String formatted
	     */
	    public String Genround(String str, int digits){
	    	String nl = System.getProperty("line.separator");
	    	String fmtu= ""+ percntg + dot + digits + s + nl;
	    	String fmt = "%."+digits + "s" + nl;
	    	if(!fmtu.equalsIgnoreCase(fmt)&& loc==new Locale("en", "US"))
				mLog.severe("RoundString: Unicode & format strings do not agree"); 
	    	String tmp = String.format(loc, fmtu, str).trim();
	    	
	        
	    	return tmp; 
			
	    }
	    public String Genround(byte[] str, int digits){
	    	String nl = System.getProperty("line.separator");
	    	String fmtu= ""+ percntg + dot + digits + s + nl;
	    	String fmt = "%."+digits + "s" + nl;
	    	if(!fmtu.equalsIgnoreCase(fmt)&& loc==new Locale("en", "US"))
				mLog.severe("RoundString: Unicode & format strings do not agree"); 
	    	String tmp = String.format(loc, fmtu, str).trim();
	    	
	        
	    	return tmp; 
			
	    }
		public static void main(String args[]){
			
			roundString rout = new roundString();
			mLog.info("*********************");
			String str = "aaaa bbbb cccc dddd gggg";
			//'\u0680','\u21E2','\u0204',
			char [] data = {'\u00F1','\u00E1','\u00E3','\u017F'};
			mLog.info("*********************");
			
			str = new String(data);
	
			System.out.println(""+Normalizer.normalize((CharSequence)(new String(data)), Normalizer.Form.valueOf("NFC")));
			
			mLog.info(str);
			mLog.info(rout.Genround(str, 5));
			mLog.info(rout.Genround(str, 3));
}
}
