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
 * Complements the class RounRoutines.java with some utility methods
 * @author evillalon@iq.harvard.edu
 */
package edu.harvard.iq.dvn.unf;

import java.util.Locale;
import java.util.logging.Logger;

public class RoundRoutinesUtils {

    public static final long serialVersionUID = 2222L;
    private static Logger mLog = Logger.getLogger(RoundRoutinesUtils.class.getName());
    private static final Locale currentLocale = Locale.getDefault();
    /**some formatting for special numbers*/
    private static FormatNumbSymbols symb = new FormatNumbSymbols(currentLocale);

    /**
     * @param obj Byte with either numeric or character values
     * @return boolean indicating if it's numeric
     */
    public static boolean checkNumeric(Byte obj, int radix) {
        String str = obj.toString();
        boolean numeric = true;
        try {
            Byte.parseByte(str, radix);
        } catch (NumberFormatException err) {
            numeric = false;
        }
        return numeric;
    }

    /**
     * Checks if only number are in the strings. It does not
     * check for either decimal points or other numeric symbols
     * (use with care)
     * @param cobj CharSequence to check for numeric values
     * @return boolean indicating if it is all numeric
     */
    public static boolean checkNumeric(CharSequence cobj) {

        String obj = cobj.toString();
        int ln = cobj.length();
        char[] objarro = new char[ln];
        boolean numeric = true;
        int cnt = 0;
        for (int n = 0; n < ln; ++n) {
            objarro[n] = cobj.charAt(n);
            if (!Character.isDigit(objarro[n])) {
                numeric = false;
                cnt++;
            }
        }
        if (!numeric && cnt < ln) {
            mLog.warning("roundRoutines: mixing digits and chars treated as string");
        }
        return numeric;
    }

    /**
     *
     * @param str String to be trimmed from 0
     * @param atfront boolean whether 0 are at front or back ends of str
     * @return String trimmed from zeros either at front or back
     */
    public static String trimZeros(String str, boolean atfront) {
        char[] strchar = str.toCharArray();
        int ln = strchar.length;
        int cnt = 0;
        String str0 = str;
        if (atfront) {
            for (int i = 0; i < ln; ++i) {
                if (strchar[i] == '0') {
                    cnt++;
                } else {
                    break;
                }
            }

            if (cnt > 0) {
                str0 = str.substring(cnt, str.length());
            }
        } else {
            for (int i = (ln - 1); i >= 0; --i) {
                if (strchar[i] == '0') {
                    cnt++;
                } else {
                    break;
                }
            }

            if (cnt > 0) {
                str0 = str.substring(0, ln - cnt);
            }
        }


        return str0;
    }

    /**
     *
     * @param n double to check for infinity and NaN
     * @return String with special symbols or null if n is finite
     */
    public static String specialNumb(Double n) {
        boolean bnan = Double.isNaN(n);
        boolean binfty = Double.isInfinite(n);
        String tmp = null;
        if ((n - n + 0) == 0 && n == n && !bnan && !binfty) {
            return tmp;
        }

        //check for infinity or NaN
        if (bnan) {
            mLog.warning("RoundRoutines: nan encounter");
            return tmp = symb.getNan(); //"+nan"

        } else if (binfty) {

            mLog.warning("RoundRoutines: infinite encounter");
            if (n > 0.0d) {
                return tmp = symb.getPlusInf(); //"+inf"
            } else {
                return tmp = symb.getMinusInf(); //"-inf"
            }
        } else {
            mLog.severe("RoundRoutines: Genround: Unknown input" + n);
            return n.toString();
        }
    }

    /**
     *
     * @param obj Byte to be converted to numeric or chars
     * @param radix integer for the basis of numeric conversion
     * @return Object with numeric or String value of obj
     */
    public static Object byteToValue(Byte obj, int radix) {

        Double objd = null;
        CharSequence objcs = null;
        boolean b = RoundRoutinesUtils.checkNumeric((Byte) obj, radix);

        if (b) {
            objd = obj.doubleValue();
            return (Object) objd;

        }
        if (!b) {
            objcs = obj.toString();
            return (Object) objcs;

        }

        return (Object) obj;
    }
}
