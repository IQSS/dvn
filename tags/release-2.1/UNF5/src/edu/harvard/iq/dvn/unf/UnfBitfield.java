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
 * rtreacy adapted evillalon code for unf version 5 adding bitfield calculation
 * 
 * Description: Calculate MessageDigest for a vector of bitfields. The algorithm can
 *              apply any of the MessageDigest algorithms available in java.
 *              The class of the input data is of type String or any related classes.
 *              It can either calculate a different encoding for text
 *              from the original encoding in UnfCons.textencoding
 *              It also obtains the Base64 string representation of the 
 *              bytes that are returned with the digest
 *              
 * **for version 5 encoding is UTF-8 and digest is SHA-256
 * **Original encoding defaults to UTF-8
 *              
 */
package edu.harvard.iq.dvn.unf;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnfBitfield<T extends CharSequence> implements UnfCons {

    private static Logger mLog = Logger.getLogger(UnfBitfield.class.getName());
    /** the staring encoding */
    private String orencoding = null;
    /** the final encoding */
    private String encoding = "UTF-8";//endencoder;
    /** local specific formatting */
    Locale currentlocale = Locale.getDefault();
    /** the MessageDigest algorithm **/
    private String mdalgor = "SHA-256";//mdalgorithm;
    private MessageDigest md = null;

    /**
     * Constructor
     */
    public UnfBitfield() {
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
    public UnfBitfield(String algor) {
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
    public UnfBitfield(String dch, String or) {
        this();
        encoding = dch;
        orencoding = or;

    }

    /**
     * Constructor
     * @param digest String with the name of messageDigest algor
     * @param dch String with name of final encoding
     * @param or String with name of original encoding
     *
     */
    public UnfBitfield(String digest, String dch, String or) {
        this(digest);
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
     * @return String with MessageDigest algorithm
     */
    public String getMdalgor() {
        return mdalgor;
    }

    /**
     * @param aa String to set the MessageDigest
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
     * Feeds the elements of v to MessageDigest and updates it
     *
     * @param v vector of class CharSequence or its sub-classes:
     *           CharBuffer, StringBuilder, StringBuffer, String, Segment
     * @param digits int with the numbers of digits for precision
     * @param result  of Class Number
     * @param miss array of booleans for missing values
     * @throws UnsupportedEncodingException
     */
    public String RUNF5(final CharSequence[] v, boolean miss[], List<Integer> result,
            Character[] base64, StringBuilder hex)
            throws UnsupportedEncodingException {

        int nv = v.length;

        int [] v5bitsize = {128, 192, 256}; // TODO: is it supposed to be 192 rather than 196?
        int k = 0;
        for (k = 0; k < nv; ++k) {
            //md5_append is called with UNF3
            mLog.finer("" + v[k] + ";");
            boolean m;
            if (miss == null) {
                md = UNF3(v[k],  md, false);
            } else {
                md = UNF3(v[k], md, (boolean) miss[k]);
            }
        }
        /**SHA256 produces, by default, 32 byte digest**/
        byte[] hash = md.digest();
        md.reset();
        if (hash.length > 32) {
            mLog.finer("unfString: hash has more than 32 bytes.." + hash.length);
        }

        // most significant 128 bits are used by dvn in UNFv5
        byte[] v5hash = truncateHash(hash,v5bitsize[0]);
        for (k = 0; k < v5hash.length; ++k) {
            result.add(v5hash[k] & 0xFF);
        }
        String rtobase64 = Base64Encoding.tobase64(v5hash, false);
        String hexstr = UtilsConverter.getHexStrng(v5hash);
        hex.append(hexstr);
        for (int n = 0; n < rtobase64.length(); ++n) {
            base64[n] = new Character(rtobase64.charAt(n));
        }
        mLog.finer(rtobase64);
        return rtobase64;
    }

    byte[] truncateHash(byte[] hash,int n){
        //TODO: are all the truncation values on byte boundaries
        assert n%8 == 0;
        int bits = n/8;
        byte[] rhash = new byte[bits];
        for (int x=0;x<bits;x++){
            rhash[x] = hash[x];
        }
        return rhash;
    }

    /**
     * Updates the MessageDigest previous with the bytes in obj
     *
     * @param obj Class Number or sub-classes
     * @param digits integer for precision arithmetic
     * @param previous MessageDigest
     * @param miss boolean for missing values
     * @return updated MessageDigest
     * @throws UnsupportedEncodingException
     */
    public MessageDigest UNF3(CharSequence obj, 
            MessageDigest previous, boolean miss)
            throws UnsupportedEncodingException {
        if (!miss) {
            mLog.finer(obj.toString());
            String res = "";
            if (!obj.equals("")) {
                res = (String) obj.toString();
            } else {
                res += creturn;
                if (nullbyte) {
                    res += zeroscape;
                }
            }
            mLog.finer(res);
            if (res == null || (res.equals("") && !obj.equals(obj))) {
                mLog.severe("UNF3: Genround returns null or empty String");
                return previous;
            }
            int sz = res.length();
            if (nullbyte && !(res.charAt(sz - 1) == zeroscape)) {
                res += zeroscape;
            }
            String dec[] = new String[2];
            dec[FINAL_ENC] = encoding;
            dec[ORG_ENC] = orencoding;
            byte[] bt = res.getBytes(orencoding);

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
            int cnt = 0;
            for (int n = 0; n < bint.length; ++n) {
                tmp0 = tmp0 + "\t" + bint[n];
                cnt++;
                if (cnt % 10 == 0) {
                    tmp0 += "\n";
                }
            }
            mLog.finer("after " + tmp0);
            //md5_append in Micah code
            md.update(bint);
        }

        if (miss) {
            byte[] topass = UtilsConverter.getBytes(missv, null);
            md.update(topass);
        }

        return previous;
    }
}
