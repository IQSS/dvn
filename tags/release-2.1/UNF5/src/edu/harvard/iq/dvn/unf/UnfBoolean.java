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
 * @author evillalon
 * After Micah Altman code written in C
 * rtreacy adapted evillalon code for unf version 5 adding Boolean calculation
 * 
 * Description: Calculate MessageDigest for a vector of Booleans. The algorithm can
 *              apply any of the MessageDigest algorithms available in java.
 *              The class of the input data set is Number and any of sub-classes.
 *              It canonicalizes the values of array to possibly  a different encoding.
 *              Uses the original encoding in UnfCons.textencoding to read 
 *              the bytes in the original array of Number.
 *              It also obtained the Base64 string representation of the 
 *              bytes that are returned with the digest
 *              
 * **for version 5 encoding is UTF-8 and digest is SHA-256
 * **Original encoding defaults to UTF-8
 *
 *              
 */
package edu.harvard.iq.dvn.unf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnfBoolean<T extends Number> implements UnfCons {

    private static Logger mLog = Logger.getLogger(UnfBoolean.class.getName());
    /** the starting encoding */
    private String orencoding = null;
    /** the final encoding */
    private String encoding = "UTF-8";//";
    /** relevant to C code only*/
    private static boolean VCPP;
    /** local specific formatting */
    private Locale currentlocale = Locale.getDefault();
    /** the MessageDigest algorithm **/
    private String mdalgor = "SHA-256";
    private MessageDigest md = null;

    /**
     * Constructor
     */
    public UnfBoolean() {
        if (!DEBUG) {
            mLog.setLevel(Level.WARNING);
        }
        if (orencoding == null) {
            orencoding = textencoding;
        }
        try {
            //md5_init in Micah code
            md = MessageDigest.getInstance(mdalgor);
        } catch (NoSuchAlgorithmException err) {
            throw new RuntimeException(err.getMessage());
        }
    }

    /**
     * Constructor
     * @param algor String with the name of algorithm to
     * use with the MessageDigest
     * @exception NoSuchAlgorithmException
     */
    public UnfBoolean(String algor) {
        if (!DEBUG) {
            mLog.setLevel(Level.WARNING);
        }
        mdalgor = algor;
        try {
            //another algor different form md5
            md = MessageDigest.getInstance(algor);
        } catch (NoSuchAlgorithmException err) {
            throw new RuntimeException(err.getMessage());
        }
    }

    /**
     * Constructor
     * @param dch String with name of final encoding
     * @param or String with name of original encoding
     *
     */
    public UnfBoolean(String dch, String or) {
        this();
        encoding = dch;
        orencoding = or;

    }

    /**
     * Constructor
     * @param dch String with name of final encoding
     * @param or String with name of original encoding
     * @param algor messageDigest algorithm
     */
    public UnfBoolean(String algor, String dch, String or) {
        this(algor);
        encoding = dch;
        orencoding = or;

    }

    /**
     * @return String with the default final encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param fenc String with final encoding
     */
    public void setEncoding(String fenc) {
        encoding = fenc;
    }

    /**
     *
     * @return String with MessageDigest
     */
    public String getMdalgor() {
        return mdalgor;
    }

    /**
     * @param aa String with the digest algorithm
     */
    public void setMdalgor(String aa) {
        mdalgor = aa;
        MessageDigest mdm = null;
        try {
            //another algor different form md5
            mdm = MessageDigest.getInstance(aa);
            this.md = mdm;
        } catch (NoSuchAlgorithmException err) {
            throw new RuntimeException(err.getMessage());
        }

    }

    /**
     *
     * @return boolean indicating if '\0' is appended end of String
     */
    public boolean getNullbyte() {
        return nullbyte;
    }

    /**
     *
     * @param x double
     * @return integer to indicate if x is a special number
     */
    public static int mysinf(Double x) {
        if (VCPP) {
            if (!x.isInfinite() || !x.isNaN()) {
                return 0;
            }
            if (x.isNaN()) {
                return 0;
            }
            if (x > 0) {
                return 1;
            }
            return -1;
        } else {
            Boolean b = x.isInfinite();
            if (b) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public String RUNF5(final Boolean[] v, List<Integer> result, Character[] base64, StringBuilder hex)//, String[] resultBase64)
            throws UnsupportedEncodingException, IOException {
        int nv = v.length;
        double dub = 0;
        boolean miss = false;
        int k = 0;
        int [] v5bitsize = {128, 192, 256}; // TODO: is it supposed to be 192 rather than 196?

        for (k = 0; k < nv; ++k) {
            miss = (v[k] == null);

            //md5_append is called with UNF5
            md = UNF5(v[k], md, miss);
        }
        /**produces, by default, 16 byte digest: equivalent to md5_finish**/
        byte[] hash = md.digest();
        md.reset();
        byte[] v5hash = truncateHash(hash,v5bitsize[0]);
        byte[] inthash = new byte[v5hash.length];
        for (k = 0; k < v5hash.length; ++k) {
            int h = (int) ((v5hash[k] & 0xFF));
            inthash[k] = (byte) h;
            result.add((Integer) (h + 0));
        }
        Integer[] res = result.toArray(new Integer[16]);

        //make sure is in big-endian order
        mLog.finer("Base64 encoding in BIG-ENDIAN");
        String tobase64 = Base64Encoding.tobase64(inthash, false);
        String hexstr = UtilsConverter.getHexStrng(v5hash);
        hex.append(hexstr);
        mLog.finer("hex " + hex);
        if ((hash.length > 16) && mdalgor.equals("MD5")) {
            mLog.finer("unfNumber: hash has more than 16 bytes.." + hash.length);
        }


        for (int n = 0; n < tobase64.length(); ++n) {
            base64[n] = new Character(tobase64.charAt(n));
        }

        return tobase64;
    }

    //TODO: this was cut&pasted from UnfString refactoring need to be done
    byte[] truncateHash(byte[] hash,int n){
        int bits = n/8;
        byte[] rhash = new byte[bits];
        for (int x=0;x<bits;x++){
            rhash[x] = hash[x];
        }
        return rhash;
    }

    /**
     *  Feeds the bytes of a String to MessageDigest algorithm
     *
     * @param obj Class Number or sub-classes
     * @param digits integer for precision arithmetic
     * @param previous MessageDigest
     * @param miss boolean for missing values
     * @return MessageDigest after updating with data in obj
     * @throws UnsupportedEncodingException
     */
    public MessageDigest UNF5(final Boolean obj, MessageDigest previous, boolean miss)
            throws UnsupportedEncodingException, IOException {
        if (!miss) {

            String tmps = obj == true? "1":"0";

            if (tmps == null) {
                mLog.severe("UNF3: Genround returns null");
                return previous;
            } else {
                mLog.finer("UNF3: Genround: " + tmps);
            }
            /** add the null byte */
            int sz = tmps.length();
            if (nullbyte && !(tmps.charAt(sz - 1) == zeroscape)) {
                tmps += zeroscape;
            }
            String dec[] = new String[2];
            dec[FINAL_ENC] = encoding;
            dec[ORG_ENC] = (orencoding != null) ? orencoding : Charset.defaultCharset().name();
            byte bt[] = null;
            if (orencoding != null) {
                bt = tmps.getBytes(orencoding);
            } else {
                bt = tmps.getBytes();
            }
            String tmp = "";

            byte[] tmpu = null;


            tmpu = UtilsConverter.byteConverter(bt, dec);

            if (tmpu == null) {
                mLog.severe("UNF3: CanonalizeUnicode returns null");
                return previous;
            }

            byte[] bint = tmpu;
            if (nullbyte) {
                bint = UnfDigestUtils.eliminateZeroPadding(tmpu, bt);
            }
            String tmp0 = "";
            for (int n = 0; n < bint.length; ++n) {
                tmp0 = tmp0 + "\t" + bint[n];
            }
            mLog.finer("after " + tmp0);
            previous.update(bint);
        }

        if (miss) {
            byte[] tpu = UtilsConverter.getBytes(missv, null);

            previous.update(tpu);
        }

        return previous;
    }
}
   
   
