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
 * Description: This class is a wrapper for unfDigest and uses its static methods
 *  It contains only one static method calculateUNF, which is overloaded 
 *  and accept one-dimensional primitives array types or array of String.
 *  It also accepts two-dimensional array of primitive type double and 
 *  of class String. It calls the appropriate method unfDigest.unf 
 *  depending on whether the array is of primitives, after converting them 
 *  to class Number, or if the array is of String, and return the unf.
 *  It can also accept an array of unf's and add them into a single unf.
 *  The main difference between this class and UnfDigest is that for this 
 *  class only primitive types or String, but for UnfDigest arrays are 
 *  of class Number or CharSequence.   
 *              
 * @author evillalon@iq.harvard.edu
 */
 
package edu.harvard.iq.dvn.unf;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class UNFUtil {

    /**
     * Obtains the unf of a one dimensional array of double
     * by calling the methods in UnfDigest
     *
     * @param numb one dimensional array of double
     * @param version String unf version
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws UnfException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String calculateUNF(final double[] numb, String version)
            throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException {

        float vers = (float) Double.parseDouble(version);
        Double mat[][] = new Double[1][numb.length];
        for (int n = 0; n < numb.length; ++n) {
            mat[0][n] = numb[n];
        }
        UnfDigest.setTrnps(false);
        String[] res = UnfDigest.unf(mat, vers);

        return res[0];
    }

    /**
     * Overloaded method
     * @param numb one dimensional array of float
     * @param vers String unf version
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws UnfException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String calculateUNF(final float[] numb, String vers)
            throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException {
        double[] toret = new double[numb.length];
        for (int k = 0; k < numb.length; ++k) {
             toret[k] =  numb[k] == Float.NaN ? Double.NaN : (double) numb[k];
        }
        String res = calculateUNF(toret, vers);
        return res;
    }

    /**
     * Overloaded method
     * @param numb one dimensional array of short
     * @param vers String unf version
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws UnfException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String calculateUNF(final short[] numb, String vers)
            throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException {
        double[] toret = new double[numb.length];
        for (int k = 0; k < numb.length; ++k) {
           if (numb[k] == Short.MAX_VALUE){
                toret[k] = Double.NaN;
            } else {
                toret[k] = (double) numb[k];
           }
        }
        String res = calculateUNF(toret, vers);
        return res;

    }

    /**
     * Overloaded method
     * @param numb one dimensional array of byte
     * @param vers String unf version
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws UnfException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String calculateUNF(final byte[] numb, String vers)
            throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException {
        double[] toret = new double[numb.length];
        for (int k = 0; k < numb.length; ++k) {
           if (numb[k] == Byte.MAX_VALUE){
                toret[k] = Double.NaN;
            } else {
                toret[k] = (double) numb[k];
           }
        }
        String res = calculateUNF(toret, vers);
        return res;

    }

    /**
     * Overloaded method
     * @param numb one dimensional array of long
     * @param vers String unf version
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws UnfException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String calculateUNF(final long[] numb, String vers)
            throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException {

        double[] toret = new double[numb.length];
        for (int k = 0; k < numb.length; ++k) {
           if (numb[k] == Long.MAX_VALUE){
                toret[k] = Double.NaN;
            } else {
                toret[k] = (double) numb[k];
           }
        }
        String res = calculateUNF(toret, vers);
        return res;
    }

    /**
     * Overloaded method
     * @param numb one dimensional array of integer
     * @param vers String unf version
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws UnfException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String calculateUNF(final int[] numb, String vers)
            throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException {

        double[] toret = new double[numb.length];
        for (int k = 0; k < numb.length; ++k) {
           if (numb[k] == Integer.MAX_VALUE){
                toret[k] = Double.NaN;
            } else {
                toret[k] = (double) numb[k];
           }
        }
        String res = calculateUNF(toret, vers);
        return res;
    }

    /**
     * Overloaded method Converts boolean to 1 (true) or 0 (false).
     * @param numb one dimensional array of boolean
     * @param vers String unf version
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws UnfException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String calculateUNF(final boolean[] numb, String vers)
            throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException {

        double[] toret = new double[numb.length];
        for (int k = 0; k < numb.length; ++k) {

           if (Boolean.valueOf(numb[k]) == null){
                toret[k] = Double.NaN;
            } else {
                toret[k] = (numb[k] == true) ? 1d : 0d;
           }
        }
        String res = calculateUNF(toret, vers);
        return res;
    }

    /**
     * Overloaded method
     * @param numb List with generics types
     * @param version String unf version
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws UnfException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static <T> String calculateUNF(final List<T> numb, String version)
            throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException {
        if (numb.get(0) instanceof Number) {
            double[] arr = new double[numb.size()];
            int cnt = 0;
            for (T obj : numb) {
                arr[cnt] = (Double) obj;
                cnt++;
            }
            return calculateUNF(arr, version);
        }
        String[] topass = new String[numb.size()];
        topass = numb.toArray(new String[numb.size()]);
        return calculateUNF(topass, version);

    }

    /**
     * Overloaded method
     * @param chr one dimensional array of String
     * @param version String unf version
     * @return String with unf calculation
     * @throws NumberFormatException
     * @throws UnfException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String calculateUNF(final String[] chr, String version)
            throws UnfException, NoSuchAlgorithmException, IOException {
        float vers = (float) Double.parseDouble(version);
        String tosplit = ":";
        String spres[] = chr[0].split(tosplit);
        if (spres.length >= 3 && chr[0].startsWith("UNF:")) {
            return UnfDigest.addUNFs(chr);
        }
        if (spres.length > 1) {
            //throw new UnfException("UNFUtil: Malformed unf");
        }
        CharSequence[][] chseq = new CharSequence[1][chr.length];
        UnfDigest.setTrnps(false);
        int cnt = 0;
        for (String str : chr) {
            chseq[0][cnt] = (CharSequence) str;
            cnt++;
        }
        String[] res = UnfDigest.unf(chseq, vers);
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
     * @param version unf version
     * @return String unf for data set
     * @throws NumberFormatException
     * @throws UnfException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String calculateUNF(final double[][] numb, String version)
            throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException {
        int ncol = numb[0].length;
        List<double[]> lst = Arrays.asList(numb);
        int nrw = lst.size();
        Number[][] pass = new Number[nrw][ncol];
        for (int r = 0; r < nrw; ++r) {
            for (int c = 0; c < ncol; ++c) {
                pass[r][c] = numb[r][c];
            }
        }
        float v = (float) Double.parseDouble(version);
        String[] unfs = UnfDigest.unf(pass, v);
        return calculateUNF(unfs, version);
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
     * @param version unf version
     * @return String unf for data set
     * @throws NumberFormatException
     * @throws UnfException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String calculateUNF(final String[][] str, String version)
            throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException {
        int ncol = str[0].length;
        List<String[]> lst = Arrays.asList(str);
        int nrw = lst.size();
        CharSequence[][] pass = new CharSequence[nrw][ncol];
        for (int r = 0; r < nrw; ++r) {
            for (int c = 0; c < ncol; ++c) {
                pass[r][c] = str[r][c];
            }
        }
        float v = (float) Double.parseDouble(version);
        String[] unfs = UnfDigest.unf(pass, v);
        return calculateUNF(unfs, version);
    }
}
