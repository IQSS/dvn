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
 * Description: Holds relevant parameters and values from calculating digest
 * in a data set with specified number of rows and columns. 
 * The Collection with the fingerprints, the arrays with 
 * base64 encoding, and the hexadecimal strings representations each of these  
 * values have being calculated for every column of data set. Following the 
 * definitation of class unf given by Micah Altman in his C code
 * 
 * @author evillalon
 * {@link evillalon@iq.harvard.edu} 
 */
package edu.harvard.iq.dvn.unf;

import java.util.ArrayList;
import java.util.List;

public class UnfClass {

    /** approximate with cdigits number of characters */
    private int cdigits = 128;
    /** approximate with (ndigits-1) after decimal point*/
    private int ndigits = 7;
    /**
     * contains the fingerprint (byte array) from MessageDigest
     * for every column of data matrix
     * */
    private List<Integer[]> fingerprints = new ArrayList<Integer[]>();
    /** the unf version*/
    private float version = 3f;
    /**
     * the hexadecimal string for columns of data matrix as obtained
     * from the byte arrays of every column
     */
    private List<String> hexvalue = new ArrayList<String>();
    /**
     * array with strings after encoding with Base64 the
     * byte arrays of the messageDigest for
     * every column of data matrix
     */
    private List<String> b64 = new ArrayList<String>();
    /**message digest algorithm */
    private String mdalgor = null;
    /** the encoding to apply message digest*/
    private String encoding = null;

    /**
     * Constructor
     */
    public UnfClass() {
    }

    /**
     * Constructor
     * @param cd integer with number of characters
     * @param nd integer with number decimal digits
     * @param vers float the unf version
     */
    public UnfClass(int cd, int nd, float vers) {
        cdigits = cd;
        ndigits = nd;
        version = vers;
    }

    /**
     * Constructor
     * @param cd integer with number of characters
     * @param nd integer with number decimal digits
     * @param vers float the unf version
     * @param md String with MessageDigest algor
     * @param enc String with encoding
     */
    public UnfClass(int cd, int nd, float vers, String md, String enc) {
        this(cd, nd, vers);
        mdalgor = md;
        encoding = enc;
    }

    /**
     *
     * @return integer with approximated of characters
     */
    public int getCdigits() {
        return cdigits;
    }

    /**
     *
     * @param d integer for number of chars
     */
    public void setCdigits(int d) {
        cdigits = d;
    }

    /**
     *
     * @return integer with number of digits including decimal point
     */
    public int getNdigits() {
        return ndigits;
    }

    /**
     *
     * @param d integer with digits including decimal point
     */
    public void setNdigits(int d) {
        ndigits = d;
    }

    /**
     *
     * @return float with unf version
     */
    public float getVersion() {
        return version;
    }

    /**
     *
     * @param v float for unf version
     */
    public void setVersion(float v) {
        version = v;
    }

    /**
     *
     * @return String array with hexadecimal representation
     * of every column in data set after applying digest
     */
    public List<String> getHexvalue() {
        return hexvalue;
    }

    /**
     *
     * @param s String array with hexadecimal representation of
     * each column in data set after calculating digest
     */
    public void setHexvalue(List<String> s) {
        hexvalue = s;
    }

    /***
     *
     * @return String array with base64 encoding for every column
     * in data set obtained from bytes arrays of digest
     */
    public List<String> getB64() {
        return b64;
    }

    /**
     *
     * @param b String array with base64 encoding
     * of each column in data set
     */
    public void setB64(List<String> b) {
        b64 = b;
    }

    /**
     *
     * @return Collection of fingerprints from digest
     */
    public List<Integer[]> getFingerprints() {
        return fingerprints;
    }

    /**
     *
     * @param fg List of Integer arrays with fingerprints
     * of data set as obtained from digest
     */
    public void setFingerprints(List<Integer[]> fg) {
        fingerprints = fg;
    }

    /**
     *
     * @return String with the message digest algorithm
     */
    public String getMdalgor() {
        return mdalgor;
    }

    /**
     *
     * @param algor String for message digest
     */
    public void setMdalgor(String algor) {
        mdalgor = algor;
    }

    /**
     *
     * @return String with encode to apply message digest
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param enc String for encoding
     */
    public void setEncoding(String enc) {
        encoding = enc;
    }
}