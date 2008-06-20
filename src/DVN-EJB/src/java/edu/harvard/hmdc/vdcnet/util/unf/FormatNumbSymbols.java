/**
 * Description For special numbers (infinity, -infinity,NaN)
 * represents them as in Micah's code
 * @author evillalon
 */
package edu.harvard.hmdc.vdcnet.util.unf;
import java.util.*;
import java.text.*;

public class FormatNumbSymbols {
	
	 private final Locale currentLocale; 
	 private final DecimalFormatSymbols decimalFmtSymb; 
	
	
	public FormatNumbSymbols() {
		currentLocale =new Locale("en", "US"); 
		decimalFmtSymb = new DecimalFormatSymbols(currentLocale);
	}
	public FormatNumbSymbols( Locale currentLocale){
		this.currentLocale =currentLocale ; 
		decimalFmtSymb = new DecimalFormatSymbols(currentLocale);
		
	}
	public Locale getCurrentLocale(){
		return currentLocale; 
	}
	/**
	 * Micah algor defining +infinity
	 * @return String
	 */
	 public String getPlusInf(){
		 return "+inf";
	 }
	 /**
	  * Micah algor defining -infinity
	  * @return String
	  */
	 public String getMinusInf(){
		 return "-inf";
	 }
	 /**
	  * Micah algor defining nan
	  * @return String
	  */
	 public String getNan(){
		 return "+nan"; 
	 }
	 /**
	  * Micah decimal separator
	  * @return char
	  */
	 public char getDecSep(){
		 return '.';
	 }
    public String getPlusInfinity(){
     
     return decimalFmtSymb.getInfinity();
    }
    public String getMinusInfinity(){
       
        return new String(decimalFmtSymb.getMinusSign()+ 
        		decimalFmtSymb.getInfinity());
       }
    
    public String getNaN(){
      
        return (decimalFmtSymb.getNaN());
    
       }
    public char getDecimalSep(){
    	return(decimalFmtSymb.getDecimalSeparator());
    }
	 
    public void setDecimalSep(char dot){
    	decimalFmtSymb.setDecimalSeparator(dot);
    }
}
