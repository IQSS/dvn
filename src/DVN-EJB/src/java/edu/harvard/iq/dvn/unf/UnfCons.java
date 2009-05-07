/**
 * Global constants used in unf classes
 * 
 */
package  edu.harvard.iq.dvn.unf;

import java.nio.ByteOrder;
import java.util.logging.Logger;

public interface UnfCons {
	/**number decimal digits with decimal point included*/
	static int DEF_NDGTS= 7;
	/**number of characters approximation*/
	static int DEF_CDGTS= 128;
	/**bounds for min and max decimal digits and decimal point */
	static int [] NDGTS_BNDS={1,15};
	/**finger-print array length */
	static int FINFP_LNGTH = 32;
	/**index for unf-encoding in bi-dimensional array*/
	static int FINAL_ENC =0;
	/**index for the original encoding */
	static int ORG_ENC=1;
	/** the starting encoding */
	static String textencoding="UTF-8";
	/**representing missing values*/
	static String miss = ""+Double.NaN;
	/** array with na's values */
	static String[] nastrings ={"NA", "N/A", miss, ""};
	/** constant used in the C code*/
	static int INACCURATE_SPRINTF_DIGITS=14;
	/** whether to use the constant above */
	boolean INACCURATE_SPRINTF= false;
	/** the null byte '\0'*/
    static char zeroscape = Ucnt.nil.getUcode();
    /** alias for null byte*/
    static char nil = zeroscape; 
    /** char array representation for missing values in unf calculations*/
    static char [] missv ={zeroscape, zeroscape, zeroscape};
    /** posix end of line*/
    static char creturn = Ucnt.psxendln.getUcode();
	 /** whether to append the null byte ('\0') at the end of each vector 
	  * element and at the end of string representing the vector*/
    static boolean nullbyte=true;
    /** to control verbose output */
    static boolean DEBUG = false;
    /**whether to create the object unfClass 
     * when the static method unf is invoked*/
    static boolean unfObj = true;
    /** to transpose matices for unf calculations */
    static boolean transpose = true;
    
    

}
