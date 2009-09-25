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
 * Description: Generalized Rounding Routines for Strings
 *              Implements Micah Altman code for rounding characters Strings
 *              The implementation is in method Genround
 * Input: integer with number of characters to keep  
 *        String to apply the rounding routine;  
 *        
 * Output: String representation of formatted String.
 * 
 * @Author Elena Villalon
 *  email:evillalon@iq.harvard.edu
 *  
 */
package edu.harvard.iq.dvn.unf;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoundString implements UnfCons {

    private static Logger mLog = Logger.getLogger(RoundString.class.getName());
    //ucode characters
    private static final char dot = Ucnt.dot.getUcode();//decimal separator "."
    private static final char percntg = Ucnt.percntg.getUcode(); //"%"
    private static final char s = Ucnt.s.getUcode();//"s"
    //language and country to format strings
    Locale loc = new Locale("en", "US");
    /** whether to append null byte ('\0') the end of String */
    private boolean nullbyte = true;

    /**
     * Default constructor
     */
    public RoundString() {
        if (!DEBUG) {
            mLog.setLevel(Level.WARNING);
        }
    }

    /**
     * Constructor
     * @param b boolean for null byte
     */
    public RoundString(boolean b) {
        this();
        nullbyte = b;
    }

    /**
     * Constructor
     * @param loc Locale to set character encodings
     * @param b boolean whether to append nullbyte
     */
    public RoundString(Locale loc, boolean b) {
        this(b);
        this.loc = loc;

    }

    /**
     * It truncates any string to number of characters = digits
     *
     * @param str String for formatting
     * @param digits integer for number of characters
     * @return String formatted
     */
    public String Genround(String str, int digits) {
        return Genround(str, digits, nullbyte);
    }

    /**
     * Truncates any string to number of characters = digits
     *
     * @param str String for formatting
     * @param digits integer for number of characters
     * @param no boolean whether to append null byte ('\0')
     * @return String formatted
     */
    public String Genround(String str, int digits, boolean no) {

        nullbyte = no;
        String fmtu = "" + percntg + dot + digits + s;
        String fmt = "%." + digits + "s";
        if (!fmtu.equalsIgnoreCase(fmt) && loc == new Locale("en", "US")) {
            mLog.severe("RoundString: Unicode & format strings do not agree");
        }
        String tmp = String.format(loc, fmtu, str).trim();
        tmp += creturn;
        if (nullbyte) {
            tmp += zeroscape;
        }

        return tmp;

    }

    /**
     * Truncates any string to number of characters = digits
     * @param bb Byte array from String
     * @param digits integer with  significant digits
     * @return String
     */
    public String Genround(byte[] bb, int digits) {
        byte[] str = bb;
        String fmtu = "" + percntg + dot + digits + s;
        String fmt = "%." + digits + "s";
        if (!fmtu.equalsIgnoreCase(fmt) && loc == new Locale("en", "US")) {
            mLog.severe("RoundString: Unicode & format strings do not agree");
        }
        String tmp = String.format(loc, fmtu, str).trim();
        tmp += creturn;
        if (nullbyte) {
            tmp += zeroscape;
        }

        return tmp;

    }
}
