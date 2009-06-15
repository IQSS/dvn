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
 * Description complements the methods in class UnfDigest
 * @author evillalon@iq.harvard.edu
 */
package edu.harvard.iq.dvn.unf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnfDigestUtils implements UnfCons {

    /** the following symbols represent missing values*/
    private static final String nasymb[] = nastrings;
    private static Logger mLog = Logger.getLogger(UnfDigestUtils.class.getName());

    /**
     * Constructor
     */
    public UnfDigestUtils() {
        if (!DEBUG) {
            mLog.setLevel(Level.WARNING);
        }
    }

    /**
     * Finds the length first index (rows) of two-dimensional array
     * Second index length may be obtained as data[0].length;
     *
     * @param <T> Object of generic class
     * @param data two-dimensional array of class T
     * @return integer number of rows (first index) of two-dimensional array
     */
    public static <T extends Object> int countRows(T[][] data) {

        List<T[]> lst = new ArrayList<T[]>();
        lst = Arrays.asList(data);
        return lst.size();
    }

    /**
     * Another method to count the rows in two-dimensional array
     *
     * @param <T> Object of generic class
     * @param data bi-dimensional array of class T
     * @return integer number of rows (first index) of two-dimensional array
     */
    public static <T extends Object> int countRows1(T[][] data) {
        int rw = 0;
        T dat = null;

        for (int n = 0;; ++n) {
            try {
                dat = data[n][0];
                rw++;
            } catch (ArrayIndexOutOfBoundsException err) {
                break;
            }
        }
        return rw;
    }

    /**
     * Transpose two-dimensional array
     * 
     * @param <T> Object of generic class
     * @param obj bi-dimensional array of class T
     * @return transpose array 
     */
    public static <T extends Object> Object[][] transArray(final T[][] obj) {
        if (!DEBUG) {
            mLog.setLevel(Level.WARNING);
        }
        int ncol = obj[0].length;
        int nrow = countRows(obj);
        Object[][] objtrans = new Object[ncol][nrow];
        mLog.finer("rows= " + nrow + "; cols= " + ncol);
        for (int c = 0; c < ncol; ++c) {
            for (int r = 0; r < nrow; ++r) {
                objtrans[c][r] = obj[r][c];
            }
        }
        return objtrans;
    }

    /**
     * 
     * @param in Transform byte array to remove zero trailing
     * @param or  Original byte array
     * @return byte array with only one null byte
     */
    public static byte[] eliminateZeroPadding(byte[] in, byte[] or) {
        int ln = in.length;
        if (ln == or.length) {
            return in;
        }
        int cnt = 0;
        //byte end = or[or.length-1];

        for (int k = (ln - 1); k >= 0; k--) {
            if (in[k] == 0) {
                cnt++;
            } else {
                break;
            }
        }
        //leave one zero byte
        byte[] out = new byte[ln - cnt + 1];
        out = Arrays.copyOfRange(in, 0, ln - cnt + 1);
        return out;

    }

    /**
     * 
     * @param obj array of class Object
     * @return boolean array whether the elements in T are missed
     */
    public static boolean[] isna(final Object[] obj) {
        int ln = obj.length;
        boolean[] res = new boolean[ln];
        for (int k = 0; k < ln; ++k) {
            res[k] = false;

            String tmp = ((String) obj[k]).trim();
            for (int n = 0; n < nasymb.length; ++n) {
                if (tmp.equals(nasymb[n])) {
                    res[k] = true;
                    break;
                }
            }
        }
        return res;
    }
}
