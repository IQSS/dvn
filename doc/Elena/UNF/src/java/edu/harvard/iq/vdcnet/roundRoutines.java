/**
 * Description: Generalized Rounding Routines
 *              Implements Micah Altman code for rounding Numbers
 *              The implementation is in method Genround
 * Input: int with number of digits including the  decimal point, 
 *        Object obj to apply the rounding routine;  
 *        obj is of class Number and any of its derived sub-classes.
 *        
 * Output: String representation of Object obj in canonical form. 
 * Example :/*
		 * Canonical form:
		 *                -leading + or -
		 *                -leading digit
		 *                -decimal point
		 *                -up to digits-1 no trailing 0
		 *                -'e'
		 *                -sign either + or -
		 *                -exponent digits no leading 0
		 * Number -2.123498e+22, +1.56e+1, -1.3456e-, +3.4222e+
         * mantissa= digits after the decimal point & decimal point
         * exponent= digits after 'e' and the sign that follows
         *
 * Usage: For Number, e.g  Double number and int digits
         *  	roundRoutines<Double> rout = new roundRoutines<Double>();
	     *      rout.Genround(number,digits);
	     * 	    For BigDecimal number,
	     * 	    roundRoutines<BigDecimal> routb = new roundRoutines<BigDecimal>();
		 *      routb.Genround(new BigDecimal(number),digits);
		 *      
		 * For String of chars, e.g. String ss = "news from ado";
		 *      roundRoutines.Genround(ss,digits);
		 *  
 * @Author: Elena Villalon
 * <a heref= email: evillalon@iq.harvard.edu/>
 *       
 */
package edu.harvard.iq.vdcnet;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.logging.Logger;

import edu.harvard.iq.vdcnet.utils.formatNumbSymbols;
import edu.harvard.iq.vdcnet.utils.roundRoutinesUtils;

public class roundRoutines<T extends Number>
implements generalizedRound<T> {
public static final long serialVersionUID=1111L;
    /**default number of digits with and after decimal point*/
    private static final int DMAX=7;
    /**for INACCURATE_SPRINTF digits with and after decimal point*/ 
    private static final int INACCURATE_SPRINTF_DIGITS=14;
    private boolean INACCURATE_SPRINTF=false; 
    /**number of digits with decimal point*/
    private int digits; 
    /**the Locale language and country*/ 
    private Locale loc; 
    /**some formatting for special numbers*/
    private formatNumbSymbols symb = new formatNumbSymbols();
    /**not show 0 digit of exponent and mantissa if they are =0*/
    private boolean nozero=true; //default is true
    /** radix for numbers*/
    private int radix=10; 
    /**unicode characters*/ 
    private static final char dot=ucnt.dot.getUcode();//decimal separator "."
    private static final char plus=ucnt.plus.getUcode(); //"+" sign 
    private static final char min=ucnt.min.getUcode(); //"-" 
    private static final char e=ucnt.e.getUcode(); //"e"
    private static final char percntg= ucnt.percntg.getUcode(); //"%"
    private static final char pndsgn=ucnt.pndsgn.getUcode(); //"#"
    private static final char zero =ucnt.zero.getUcode();
    private static final char s =ucnt.s.getUcode();//"s"
    
    private static Logger mLog = Logger.getLogger(roundRoutines.class.getName());
    /**
     * Default constructor 
     */
   
	public roundRoutines(){
		super();
		this.digits=DMAX;
		this.symb = new formatNumbSymbols();
		nozero = true;
		
	}
	/**
	 * 
	 * @param digits:int 
	 * Number of decimal digits including the decimal point
	 */
	public roundRoutines(int digits){
		this();
		if(digits < 1) digits=1; //count for decimal separator
		//upper value is limited 
		this.digits = digits <= INACCURATE_SPRINTF_DIGITS ? digits : INACCURATE_SPRINTF_DIGITS;
	
	}
	/**
	 * 
	 * @param digits:int
	 * @param loc: the default locale
	 */
	public roundRoutines(int digits, Locale loc){
		this(digits);
		this.loc = loc; 
	}
	/**
	 * 
	 * @param digits:int 
	 * @param nozero: boolean show the 0 digit for mantissa & exponent if they are 0 
	 * Number of decimal digits including the decimal point
	 */
	public roundRoutines(int digits, boolean nozero){
		this(digits);
		this.nozero= nozero;
	}
	/**
	 * 
	 * @param digits:int.  
	 * Number of decimal digits including the decimal point
	 * @param loc: Locale. For country and language
	 * @param nozero: boolean show the 0 for mantissa & exponent if they are 0 
	 */
	public roundRoutines(int digits, boolean nozero, Locale loc){
	    this(digits, nozero);
		symb = new formatNumbSymbols(loc);
		
	}
	@Override
	/**
	 * @param obj: Object of class Number and sub-classes 
	 * @param digits: int 
	 * Number of decimal digits including the decimal point
	 * @return String: with canonical formatting
	 */
	public String Genround(T obj, int digits) {	
		
		// TODO Auto-generated method stub
		BigDecimal objbint=null; 
		boolean bigNumber = (obj instanceof BigDecimal) ? true:false; 
		if(obj instanceof BigInteger){
		    bigNumber = true;
			objbint = new BigDecimal((BigInteger) obj);
		}
		
		if(obj instanceof Byte){
			Double objd=null;
			CharSequence objcs = null;
			boolean b= roundRoutinesUtils.checkNumeric((Byte) obj,radix);
			
			if(b) {
			  objd = obj.doubleValue();
			  return Genround((T) objd, digits);
			}else{ 
				objcs = obj.toString();
				return Genround(objcs,digits);
		    }
		}
		StringBuilder build = new StringBuilder(); 
		String fmt, fmtu, tmp; 
		//the decimal separator symbol locally 
		char sep = symb.getDecimalSep();
	  
		if(digits< 0) digits = this.digits;
		
		Double n = obj.doubleValue();
		
		if(sep != dot)
			mLog.info("RoundRoutines: Decimal separator is not " +
					"'\u002E' or a dot:.");
			
		//check infinity or NaN for Double inputs 
	    
	    if(!(obj instanceof BigDecimal) && (objbint==null) &&
	         (tmp = roundRoutinesUtils.specialNumb(n)) != null)
	    	return tmp;
			 
		char[] str= {percntg, plus,pndsgn,sep}; //{'%','+', '#', '.'}	
		
		int dgt=(INACCURATE_SPRINTF)?INACCURATE_SPRINTF_DIGITS:(digits-1); 
			
		fmt= new String("%+#."+dgt+"e"); 
		//using the Unicode character symbols 
		build.append(str);
		build.append(dgt);
		build.append(e);
		fmtu = build.toString();
		build = null;
		if(!fmtu.equalsIgnoreCase(fmt)&& loc==new Locale("en", "US"))
			mLog.severe("RoundRoutines: Unicode & format strings do not agree"); 
		
		if(obj instanceof BigDecimal)
			tmp = String.format(loc, fmtu, obj);
		else if (objbint != null)
		    tmp= String.format(loc, fmtu, objbint);
		else
			tmp = String.format(loc, fmtu, n);//double representation with full precision
		
		//check infinity or NaN for BigDecimal 	
		if(tmp.equalsIgnoreCase("Infinity") ||
		   tmp.equalsIgnoreCase("-Infinity") ||
		   tmp.equalsIgnoreCase("NaN")){
			mLog.severe("RoundRoutines: infinite or nan encounter");
			return tmp;
		}
	
		String atoms [] =tmp.split(e+"");
		//e.g., Number -2.123498e+22; atoms[0]=-2.123498 & atoms[1]=+22
		
		
		build= calcMantissa(atoms[0],sep); 
		build.append(e);
		build.append(atoms[1].charAt(0)); //sign of exponent
		build.append(calcExponent(atoms[1])); 
		return build.toString();
	}
	/**
	 * 
	 * @param obj: : Object of class Number and sub-classes 
	 * @param digits: int number of decimal digits to keep
	 * @param charset:String with optional charset to encode bytes
	 * @return byte array encoded with charset
	 */
	
	public byte[] GenroundBytes(T obj, int digits, String... charset){
		String str = Genround(obj, digits);
		if(str==null || str.equals(""))
			return null;
		Charset original= Charset.defaultCharset();
		Charset to = original;
		if(charset.length>0 && Charset.isSupported(charset[0])){
			to = Charset.forName(charset[0]);
			if(!to.canEncode()) to=original;
		}
		return str.getBytes(to); 
	}
			
	/**
	 * 
	 * @param atom: String with the exponent including the sign
	 * @return StringBuffer representing exponent with no leading 0
	 *         and appending the end of line. 
	 */
		private StringBuffer calcExponent(String atom){
			
			StringBuffer build = new StringBuffer();
			
			String expnt = atom.substring(1); 
			long lngmant = Long.parseLong(expnt);//remove leading 0's
		
			if(lngmant > 0 || (lngmant == 0 && !nozero))
			build.append(lngmant);
		    
		    	
			//adding end-line :"\n"
			String nl = System.getProperty("line.separator");
			build.append(nl);  //end of line
			return build;
		}
	/**
	 * 
	 * @param atom: String with the mantissa 
	 * @param sep: char the decimal point 
	 * @param f: boolean for number between (-1,1)
	 * @return StringBuilder with mantissa after removing trailing 0
	 */
	private StringBuilder calcMantissa(String atom, char sep){
		StringBuilder build = new StringBuilder();
    //sign and leading digit before decimal separator
	char mag[] = {atom.charAt(0), atom.charAt(1)};
	//canon[] :double check you have correct results 
	String canon[] = atom.split("\\"+sep);
	if(!canon[0].equalsIgnoreCase(new String(mag)))
			mLog.severe("RoundRoutines:decimal separator no in right place");	
	build.append(mag);//sign and leading digit
	build.append(sep);//decimal separator
	String dec = atom.substring(3);//decimal part
	if(!dec.equalsIgnoreCase(canon[1]))
		mLog.severe("RoundRoutines: decimal separator not right");

	String tmp= new StringBuffer(dec).reverse().toString();
	long tmpl = Long.parseLong(tmp); //remove trailing 0's 
	tmp = new StringBuffer((Long.toString(tmpl))).reverse().toString();
	
	//removing trailing 0
	if(tmpl== 0 && nozero) return build;
	
	return build.append(tmp);
	
}
	
	
	
	/**
	 * @param cobj: CharSequence to format
	 * @param digits:int number of characters  to keep
	 * @return String formatted
	 */
	
	public static String Genround(CharSequence cobj, int digits){
		boolean numeric =roundRoutinesUtils.checkNumeric(cobj);
	
		if(numeric){
			//only digits in obj use a BigInteger representation
			BigInteger bg = new BigInteger(cobj.toString());
			roundRoutines<BigInteger> rout = new roundRoutines<BigInteger>();
		    return rout.Genround(bg, digits);
		}
		
		//if is not digits
		mLog.info("roundRoutines: Enter string of chars:");
		return (new roundString().Genround(cobj.toString(), digits));
		}
		 
	//checking some inputs.  Need some JUnit Tests 
	public static void main(String args[]){
		
		roundRoutines<Double> rout = new roundRoutines<Double>();
		mLog.info("*********************");
		mLog.info(rout.Genround(3344556677.786549,15));
		roundRoutines<BigDecimal> routb = new roundRoutines<BigDecimal>();
		mLog.info("**********************");
		mLog.info(routb.Genround(new BigDecimal(3344556677.786549),15));
		roundRoutines<BigInteger> routn = new roundRoutines<BigInteger>();
		mLog.info("**********************");
		mLog.info(routn.Genround(new BigInteger("334455667788991122"),15));
		rout = new roundRoutines<Double>();
		mLog.info("**********************");
		mLog.info(rout.Genround(23.78,7));
		rout = new roundRoutines<Double>();
		mLog.info("***********************");
		mLog.info(rout.Genround(3.78,7));
		mLog.info("***********************");
		mLog.info(rout.Genround(3d,7));
		mLog.info("**************************");
		mLog.info(rout.Genround(0.000345,7));
		mLog.info("**************************");
		mLog.info(rout.Genround(-0.000345,7));
		mLog.info("**************************");
		mLog.info(rout.Genround(-1.0,7));
		mLog.info("**************************");
		mLog.info(rout.Genround(1.0,-1));
		String ss = "news from ado";
		mLog.info(roundRoutines.Genround(ss,7));
		mLog.info(rout.Genround(1.0,-1));
		mLog.info("**************************");
		ss = "1122334455 6677";
		mLog.info(ss);
		mLog.info(roundRoutines.Genround(ss,7));
		
		byte[] issb ={'\u0073', 101, 119, 115, 32};
		byte [] nb = {111,(byte) 222,(byte) 333,(byte) 444};
		
		System.out.println(""+ new String(issb));
		System.out.println(""+ new String(nb));
		String str = "mmmm ggggg hhhh jjj";
		mLog.info("*********************");
		BigDecimal bd = new BigDecimal(12345678.12345678);
		mLog.info("Eng String: "+ bd.toEngineeringString());
		mLog.info("String: "+ bd.unscaledValue());
		mLog.info("*********************");
		MathContext mth = new MathContext(7);
		mLog.info("Mth Context: "+ mth.getRoundingMode());
		mLog.info("*********************");
		String strt ="000012345678";
		mLog.info(roundRoutinesUtils.trimZeros(strt,true));
		mLog.info(roundRoutinesUtils.trimZeros(strt,false));
		mLog.info("*********************");
		String strr ="123456780000";
		mLog.info(roundRoutinesUtils.trimZeros(strr,true));
		mLog.info(roundRoutinesUtils.trimZeros(strr,false));
	
	}
	}


