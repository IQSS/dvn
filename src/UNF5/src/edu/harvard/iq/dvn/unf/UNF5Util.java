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
 * Description: This class is a wrapper for Unf5Digest and uses its static methods
 *  It contains only one static method calculateUNF, which is overloaded 
 *  and accept one-dimensional primitives array types or array of String.
 *  It also accepts two-dimensional array of primitive type double and 
 *  of class String. It calls the appropriate method Unf5Digest.unf 
 *  depending on whether the array is of primitives, after converting them 
 *  to class Number, or if the array is of String, and return the unf.
 *  It can also accept an array of unf's and add them into a single unf.
 *  The main difference between this class and Unf5Digest is that for this 
 *  class only primitive types or String, but for Unf5Digest arrays are 
 *  of class Number or CharSequence.   
 *              
 * @author evillalon@iq.harvard.edu
 * rtreacy adapted evillalon code for unf version 5 adding date,time,duration,
 * boolean and bitfield calculations
 */
 
package edu.harvard.iq.dvn.unf;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UNF5Util {

    /**
     * Obtains the unf of a one dimensional array of double
     * by calling the methods in Unf5Digest
     *
     * @param numb one dimensional array of double
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws IOException
     */
    public static String calculateUNF(final double[] numb)
            throws NumberFormatException, IOException {

        Double mat[][] = new Double[1][numb.length];
        for (int n = 0; n < numb.length; ++n) {
            mat[0][n] = numb[n];
        }
        Unf5Digest.setTrnps(false);
        String[] res = Unf5Digest.unf(mat);

        return res[0];
    }


    /**
     * Overloaded method
     * @param numb one dimensional array of float
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws IOException
     */
    public static String calculateUNF(final float[] numb)
            throws NumberFormatException, IOException {
        double[] toret = new double[numb.length];
        for (int k = 0; k < numb.length; ++k) {
             toret[k] =  numb[k] == Float.NaN ? Double.NaN : (double) numb[k];
        }
        String res = calculateUNF(toret);
        return res;
    }

    /**
     * Overloaded method
     * @param numb one dimensional array of short
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws IOException
     */
    public static String calculateUNF(final short[] numb)
            throws NumberFormatException, IOException {
        double[] toret = new double[numb.length];
        for (int k = 0; k < numb.length; ++k) {
           if (numb[k] == Short.MAX_VALUE){
                toret[k] = Double.NaN;
            } else {
                toret[k] = (double) numb[k];
           }
        }
        String res = calculateUNF(toret);
        return res;

    }

    /**
     * Overloaded method
     * @param numb one dimensional array of byte
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws IOException
     */
    public static String calculateUNF(final byte[] numb)
            throws NumberFormatException, IOException {
        double[] toret = new double[numb.length];
        for (int k = 0; k < numb.length; ++k) {
           if (numb[k] == Byte.MAX_VALUE){
                toret[k] = Double.NaN;
            } else {
                toret[k] = (double) numb[k];
           }
        }
        String res = calculateUNF(toret);
        return res;

    }

    /**
     * Overloaded method
     * @param numb one dimensional array of long
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws IOException
     */
    public static String calculateUNF(final long[] numb)
            throws NumberFormatException, IOException {

        double[] toret = new double[numb.length];
        for (int k = 0; k < numb.length; ++k) {
           if (numb[k] == Long.MAX_VALUE){
                toret[k] = Double.NaN;
            } else {
                toret[k] = (double) numb[k];
           }
        }
        String res = calculateUNF(toret);
        return res;
    }

    /**
     * Overloaded method
     * @param numb one dimensional array of integer
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws IOException
     */
    public static String calculateUNF(final int[] numb)
            throws NumberFormatException, IOException {

        double[] toret = new double[numb.length];
        for (int k = 0; k < numb.length; ++k) {
           if (numb[k] == Integer.MAX_VALUE){
                toret[k] = Double.NaN;
            } else {
                toret[k] = (double) numb[k];
           }
        }
        String res = calculateUNF(toret);
        return res;
    }

    /**
     * Overloaded method Converts boolean to 1 (true) or 0 (false).
     * @param numb one dimensional array of boolean
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws IOException
     */
    public static String calculateUNF(final boolean[] numb)
            throws NumberFormatException, IOException {

        Boolean[] toret = new Boolean[numb.length];
        for (int k = 0; k < numb.length; ++k) {
            toret[k] = Boolean.valueOf(numb[k]);
        }
        String[] res = Unf5Digest.unf(toret);
        return res[0];
    }

    /**
     * Overloaded method
     * @param numb List with generics types
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws IOException
     */
    public static <T> String calculateUNF(final List<T> numb)
            throws NumberFormatException, IOException {
        if (numb.get(0) instanceof Number) {
            double[] arr = new double[numb.size()];
            int cnt = 0;
            for (T obj : numb) {
                arr[cnt] = (Double) obj;
                cnt++;
            }
            return calculateUNF(arr);
        }
        String[] topass = new String[numb.size()];
        topass = numb.toArray(new String[numb.size()]);
        return calculateUNF(topass);

    }

    /**
     * Overloaded method
     * @param chr one dimensional array of String
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws IOException
     */
    public static String calculateUNF(final String[] chr)
            throws IOException {
        String tosplit = ":";
        if (chr[0] != null) {
            String spres[] = chr[0].split(tosplit);
            if (spres.length >= 3 && chr[0].startsWith("UNF:")) {
                return Unf5Digest.addUNFs(chr);
            }
            if (spres.length > 1) {
                //throw new UnfException("UNFUtil: Malformed unf");
            }
        }
        CharSequence[][] chseq = new CharSequence[1][chr.length];
        Unf5Digest.setTrnps(false);
        int cnt = 0;
        for (String str : chr) {
            chseq[0][cnt] = (CharSequence) str;
            cnt++;
        }
        String[] res = Unf5Digest.unf(chseq);
        return res[0];
    }

     public static String calculateUNF(final String[] chr, final String[] sdfFormat)
            throws  IOException {
        String tosplit = ":";
         if (chr[0] != null) {
             String spres[] = chr[0].split(tosplit);
             if (spres.length >= 3 && chr[0].startsWith("UNF:")) {
                 return Unf5Digest.addUNFs(chr);
             }

             if (spres.length > 1) {
                 //throw new UnfException("UNFUtil: Malformed unf");
             }
         }
        CharSequence[][] chseq = new CharSequence[1][chr.length];
        Unf5Digest.setTrnps(false);
        int cnt = 0;
        for (String str : chr) {
            if (sdfFormat[cnt] != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(sdfFormat[cnt]);
                try {
                    Date d = sdf.parse(str);
                    UnfDateFormatter udf = new UnfDateFormatter(sdfFormat[cnt]);
                    SimpleDateFormat unfSdf = new SimpleDateFormat(udf.getUnfFormatString().toString());
                    if (udf.isTimeZoneSpecified()){
                        unfSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    }
                    str = unfSdf.format(d);
                    // remove any trailing 0s from milliseconds
                    if (sdfFormat[cnt].indexOf("S")> -1 && str.endsWith("0")){
                        while (str.endsWith("0")){
                            str = str.substring(0,str.length()-1);
                        }
                        // if all trailing milliseconds were 0s, there will now be a trailing . to remove
                        if (str.endsWith(".")) {
                            str = str.substring(0, str.length() - 1);
                        }
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(UNF5Util.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            chseq[0][cnt] = (CharSequence) str;
            cnt++;
        }
        String[] res = Unf5Digest.unf(chseq);
        return res[0];
    }

   /**
     * Calculates unf's of two-dimensional array of double
     * along second index (columns) and add them
     * Note that if data set contains number and String this method
     * cannot be used. Instead used the one-dimensional arrays of double and
     * combine with the String unfs.
     *
     * @param numb double bi-dimensional array
     * to obtain unf along columns second index
     * @return String unf for data set
     * @throws NumberFormatException
     * @throws IOException
     */
    public static String calculateUNF(final double[][] numb)
            throws NumberFormatException, IOException {
        int ncol = numb[0].length;
        List<double[]> lst = Arrays.asList(numb);
        int nrw = lst.size();
        Number[][] pass = new Number[nrw][ncol];
        for (int r = 0; r < nrw; ++r) {
            for (int c = 0; c < ncol; ++c) {
                pass[r][c] = numb[r][c];
            }
        }
        String[] unfs = Unf5Digest.unf(pass);
        return calculateUNF(unfs);
    }

    /**
     * Calculates unf's of two-dimensional array of String
     * along second index (columns) and add them
     * Note that if data set contains number and String this method
     * cannot be used. Instead used the one-dimensional arrays and
     * combine with the numeric unfs.
     *
     * @param str String bi-dimensional array
     * to obtain unf along columns second index
     * @return String unf for data set
     * @throws NumberFormatException
     * @throws IOException
     */
    public static String calculateUNF(final String[][] str)
            throws NumberFormatException, IOException {
        int ncol = str[0].length;
        List<String[]> lst = Arrays.asList(str);
        int nrw = lst.size();
        CharSequence[][] pass = new CharSequence[nrw][ncol];
        for (int r = 0; r < nrw; ++r) {
            for (int c = 0; c < ncol; ++c) {
                pass[r][c] = str[r][c];
            }
        }
        String[] unfs = Unf5Digest.unf(pass);
        return calculateUNF(unfs);
    }

    public static String calculateUNF(final Number[] numb) throws IOException {
        Double mat[][] = new Double[1][numb.length];
        for (int n = 0; n < numb.length; ++n) {
            mat[0][n] = numb[n] != null ? Double.valueOf(numb[n].doubleValue()) : null;
        }
        Unf5Digest.setTrnps(false);
        String[] res = Unf5Digest.unf(mat);

        return res[0];
    }

    public static String calculateUNF(final BitString[] numb) throws IOException {
        Unf5Digest.setTrnps(false);
        String[] res = Unf5Digest.unf(numb);

        return res[0];
    }
}
