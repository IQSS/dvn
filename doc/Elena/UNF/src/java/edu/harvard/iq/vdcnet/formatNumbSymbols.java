package edu.harvard.iq.vdcnet;
import java.util.*;
import java.text.*;

public class formatNumbSymbols {
	
	 private final Locale currentLocale; 
	 private final DecimalFormatSymbols decimalFmtSymb; 
	
	public formatNumbSymbols() {
		currentLocale =new Locale("en", "US"); 
		decimalFmtSymb = new DecimalFormatSymbols(currentLocale);
	}
	public formatNumbSymbols( Locale currentLocale){
		this.currentLocale =currentLocale ; 
		decimalFmtSymb = new DecimalFormatSymbols(currentLocale);
		
	}
	public Locale getCurrentLocale(){
		return currentLocale; 
	}
	public Locale get(){
		return currentLocale; 
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
