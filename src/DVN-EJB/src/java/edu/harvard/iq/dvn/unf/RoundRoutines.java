/*
 * Dataverse Network - A web application to distribute, share and
 * analyze quantitative data.
 * Copyright (C) 2008
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *  along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/**
 * Description: Generalized Rounding Routines
 * 
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
package edu.harvard.iq.dvn.unf;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoundRoutines<T extends Number> implements UnfCons {

    public static final long serialVersionUID = 1111L;
    private static Logger mLog = Logger.getLogger(RoundRoutines.class.getName());
    /**number of digits with decimal point*/
    private int digits;
    /**the Locale language and country*/
    private Locale loc;
    /**some formatting for special numbers*/
    private FormatNumbSymbols symb = new FormatNumbSymbols();
    /**no leading and trailing 0 digits in exponent and mantissa*/
    private static final boolean nozero = true; //default is true
    /** radix for numbers*/
    private int radix = 10;
    /**unicode characters*/
    private static final char dot = Ucnt.dot.getUcode();//decimal separator "."
    private static final char plus = Ucnt.plus.getUcode(); //"+" sign
    private static final char min = Ucnt.min.getUcode(); //"-"
    private static final char e = Ucnt.e.getUcode(); //"e"
    private static final char percntg = Ucnt.percntg.getUcode(); //"%"
    private static final char pndsgn = Ucnt.pndsgn.getUcode(); //"#"
    private static final char zero = Ucnt.zero.getUcode();
    private static final char s = Ucnt.s.getUcode();//"s"
    private static final char ffeed = Ucnt.frmfeed.getUcode();
    private static final char creturn = Ucnt.psxendln.getUcode();
    /** whether to append the null byte ('\0') the end of string */
    private static boolean nullbyte = !UnfCons.nullbyte;
    /** check conversion from string to numeric for mix 
     * columns values (i.e. column can have chars and numbers)
     * */
    private static boolean convertToNumber = false;

    /**
     * Default constructor 
     */
    public RoundRoutines() {
        if (!DEBUG) {
            mLog.setLevel(Level.WARNING);
        }
        this.digits = DEF_NDGTS;
        this.symb = new FormatNumbSymbols();

    }

    /**
     *
     * @param no boolean whether to append null bytes at end of Strings
     */
    public RoundRoutines(boolean no) {
        this();
        nullbyte = no;
    }

    /**
     *
     * @param digits integer with number of decimal digits for mantissa calculations
     * @param  no boolean whether to append null bytes at end of Strings
     * Number of decimal digits including the decimal point
     */
    public RoundRoutines(int digits, boolean no) {
        this(no);
        if (digits < 1) {
            digits = 1; //count for decimal separator
        }		//upper value is limited
        this.digits = digits <= INACCURATE_SPRINTF_DIGITS ? digits : INACCURATE_SPRINTF_DIGITS;

    }

    /**
     *
     * @param digits integer number of decimal digits for mantissa calculations
     * @param loc the default locale
     * @param no boolean whether to append null bytes at end of Strings
     */
    public RoundRoutines(int digits, boolean no, Locale loc) {
        this(digits, no);
        this.loc = loc;
    }

    /**
     *
     * @return boolean indicating if null byte
     * is appended end of String
     */
    public boolean getNullbyte() {
        return nullbyte;
    }

    /**
     *
     * @param b boolean set nullbyte
     */
    public void setNullbyte(boolean b) {
        nullbyte = b;
    }

    /**
     *
     * @param obj Object of class Number and sub-classes
     * @param digits integer total decimal digits including decimal point
     * @return String with canonical formatting
     */
    public String Genround(T obj, int digits) {
        return Genround(obj, digits, nullbyte);
    }

    /**
     * It obtains the string representation of numeric values according
     * to Micah Altman specs (IEEE 754)
     *
     * @param obj Object of class Number and sub-classes
     * @param digits integer Number of decimal digits with decimal point
     * @param no boolean indicating whether null byte ('\0') is appended
     * @return String with the numeric value represented using IEEE 754
     */
    public String Genround(T obj, int digits, boolean no) {
        RoundRoutines.nullbyte = no;
        // TODO Auto-generated method stub
        BigDecimal objbint = null;
        boolean bigNumber = (obj instanceof BigDecimal) ? true : false;
        if (obj instanceof BigInteger) {
            bigNumber = true;
            objbint = new BigDecimal((BigInteger) obj);
        }

        StringBuilder build = new StringBuilder();
        String fmt, fmtu, tmp;
        //the decimal separator symbol locally
        char sep = symb.getDecimalSep();

        if (digits < 0) {
            digits = this.digits;
        }

        Double n = obj.doubleValue();

        if (sep != dot) {
            mLog.warning("RoundRoutines: Decimal separator is not " +
                    "'\u002E' or a dot:.");
            sep = '.';
        }

        //check infinity or NaN for Double inputs

        if (!(obj instanceof BigDecimal) && (objbint == null) &&
                (tmp = RoundRoutinesUtils.specialNumb(n)) != null) {
            return tmp;
        }

        char[] str = {percntg, plus, pndsgn, sep}; //{'%','+', '#', '.'}

        int dgt = (INACCURATE_SPRINTF) ? INACCURATE_SPRINTF_DIGITS : (digits - 1);

        fmt = new String("%+#." + dgt + "e");
        //using the Unicode character symbols
        build.append(str);
        build.append(dgt);
        build.append(e);
        fmtu = build.toString();
        build = null;
        if (!fmtu.equalsIgnoreCase(fmt) && loc == new Locale("en", "US")) {
            mLog.severe("RoundRoutines: Unicode & format strings do not agree");
        }

        if (obj instanceof BigDecimal) {
            tmp = String.format(loc, fmtu, obj);
        } else if (objbint != null) {
            tmp = String.format(loc, fmtu, objbint);
        } else {
            tmp = String.format(loc, fmtu, n);//double representation with full precision
        }
        //check infinity or NaN for BigDecimal
        if (tmp.equalsIgnoreCase("Infinity") ||
                tmp.equalsIgnoreCase("-Infinity") ||
                tmp.equalsIgnoreCase("NaN")) {
            mLog.warning("RoundRoutines: infinite or nan encounter");
            return tmp;
        }

        String atoms[] = tmp.split(e + "");
        //e.g., Number -2.123498e+22; atoms[0]=-2.123498 & atoms[1]=+22

        build = calcMantissa(atoms[0], sep);
        build.append(e);
        build.append(atoms[1].charAt(0)); //sign of exponent
        build.append(calcExponent(atoms[1]));
        return build.toString();
    }

    /**
     *
     * @param obj Object of class Number and sub-classes
     * @param digits integer number of decimal digits to keep
     * @param charset String with optional encoding of bytes
     * @return byte array encoded with charset
     */
    public byte[] GenroundBytes(T obj, int digits, String... charset) {
        String str = Genround(obj, digits);
        if (str == null || str.equals("")) {
            return null;
        }
        Charset original = Charset.defaultCharset();
        Charset to = original;
        if (charset.length > 0 && Charset.isSupported(charset[0])) {
            to = Charset.forName(charset[0]);
            if (!to.canEncode()) {
                to = original;
            }
        }
        return str.getBytes(to);
    }

    /**
     *
     * @param atom String with the exponent including the sign
     * @return StringBuffer representing exponent with no leading 0
     *         and appending the end of line.
     */
    private StringBuffer calcExponent(String atom) {

        StringBuffer build = new StringBuffer();

        String expnt = atom.substring(1);
        long lngmant = Long.parseLong(expnt);//remove leading 0's

        if (lngmant > 0 || (lngmant == 0 && !nozero)) {
            build.append(lngmant);
        }


        //adding end-line :"\n"

        /**
         * per specs append the end of line "\n" and
         * null terminator
         */
        build.append(creturn);
        if (nullbyte) {
            build.append(nil);
        }
        return build;
    }

    /**
     *
     * @param atom String with the mantissa
     * @param sep char the decimal point
     * @param f boolean for number between (-1,1)
     * @return StringBuilder with mantissa after removing trailing 0
     */
    private StringBuilder calcMantissa(String atom, char sep) {
        StringBuilder build = new StringBuilder();
        //sign and leading digit before decimal separator
        char mag[] = {atom.charAt(0), atom.charAt(1)};
        //canon[] :double check you have correct results
        String canon[] = atom.split("\\" + sep);
        if (!canon[0].equalsIgnoreCase(new String(mag))) {
            mLog.severe("RoundRoutines:decimal separator no in right place");
        }
        build.append(mag);//sign and leading digit
        build.append(sep);//decimal separator
        String dec = atom.substring(3);//decimal part
        if (!dec.equalsIgnoreCase(canon[1])) {
            mLog.severe("RoundRoutines: decimal separator not right");
        }

        String tmp = new StringBuffer(dec).reverse().toString();
        long tmpl = Long.parseLong(tmp); //remove trailing 0's
        tmp = new StringBuffer((Long.toString(tmpl))).reverse().toString();

        //removing trailing 0
        if (tmpl == 0 && nozero) {
            return build;
        }

        return build.append(tmp);

    }

    /**
     * @param cobj CharSequence to format
     * @param digits integer with number of characters  to keep
     * @return String formatted
     */
    public String Genround(CharSequence cobj, int digits) {
        return Genround(cobj, digits, nullbyte);
    }

    /**
     * @param cobj CharSequence to format
     * @param digits integer with number of characters  to keep
     * @param no boolean if to append nullbyte
     * @return String formatted
     */
    public static String Genround(CharSequence cobj, int digits, boolean no) {
        if ((((String) cobj).trim()).equals("")) {
            String res = null;
            if (cobj.length() > digits) {
                res = (String) cobj.subSequence(0, digits - 1);
            }
            res += creturn;
            if (no) {
                res += nil;
            }
            return res;
        }
        boolean numeric = false;
        if (convertToNumber) {
            numeric = RoundRoutinesUtils.checkNumeric(cobj);
        }

        if (numeric) {
            //only digits in obj use a BigInteger representation
            BigInteger bg = new BigInteger(cobj.toString());
            RoundRoutines<BigInteger> rout = new RoundRoutines<BigInteger>();
            return rout.Genround(bg, digits, no);
        }

        //if is not digits

        return (new RoundString().Genround((String) cobj, digits, no));
    }
}


