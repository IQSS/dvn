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
 * Description takes as input two dimensional arrays of generic classes 
 * <T extends Number> and <E extends CharSequence>, 
 * and calculates the unf along every column of the 
 * arrays of class T or E, according to some defaults arguments as specified 
 * in UnfCons interface, the encoding and MessageDigest.
 * To combine or add the message digests calculated from columns 
 * of data set use the method addUNFs(String[]).
 * 
 * Methods in this class are static. After invoking the overloaded methods
 * for CharSequence and Number, UnfDigest.unf, along each of the 
 * columns of the data set, one can obtain the object UnfClass as 
 * UnfClass signature = UnfDigest.getSignature();
 * 
 *  
 * After Micah Altman specifications 
 * 
 * **For unf version 3 encoding is UTF-32BE and digest is MD5
 * **for version 4 encoding is UTF-32BE and digest is SHA-256
 * **for version 4.1 encoding is UTF-8 and digest is SHA-256
 *
 * @author evillalon
 * @link   evillalon@iq.harvard.edu
 */
package edu.harvard.iq.dvn.unf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnfDigest implements UnfCons {

    private static Logger mLog = Logger.getLogger(UnfDigest.class.getName());
    /**last version of unf supported*/
    private static final double lastVersion = 4.1d;
    /** current version */
    private static float currentVersion = 3f;
    /** the MessageDigest algor associated with current version*/
    private static final String algor3 = "MD5";
    /** the MessageDigest algor associated with lastVersion*/
    private static final String algor4 = "SHA-256";
    /** encoder to use for versions 3 and 4*/
    private static final String encod3y4 = "UTF-32BE";
    /** encoder to use for version 4.1*/
    private static final String encod4p1 = "UTF-8";
    /** transpose the array of data so unf is applied along each column*/
    private static boolean trnps = transpose;
    /** List elements are arrays of fingerprints
     * for every column of input data obj*/
    private static List<List<Integer>> fingerprint = new ArrayList<List<Integer>>();
    /** for debugging */
    private boolean debug = false;
    /** unfClass type after calculating digest
     * and Base64 encoding of all columns in data matrix.
     *  Contains fingerprints, hexadecimal strings, digest and the parameters to obtain them*/
    private static UnfClass signature = null;
    /**whether to create object of type unfClass */
    private static boolean buildunfObj = new Boolean(unfObj);

    /**
     * Constructor
     */
    public UnfDigest() {
        if (!DEBUG || !debug) {
            mLog.setLevel(Level.WARNING);
        }

    }

    /**
     *
     * @return float with current unf version
     */
    public static float getVersion() {
        return currentVersion;
    }

    /**
     *
     * @param vv float to set the unf version
     */
    public void setVersion(float vv) {
        this.currentVersion = (vv <= 4.1f && vv >= 3f) ? vv : 3f;
        if (vv > lastVersion) {
            mLog.info("Unsupported version using 3");
        }
    }

    /**
     * @return boolean indicating whether to transpose array of data
     * unf is applied along each column of the data array by default
     */
    public static boolean getTrnps() {
        return trnps;
    }

    /**
     *
     * @param tt boolean indicating whether transpose array of data
     * If transpose the unf is calculated for each column
     */
    public static void setTrnps(boolean tt) {
        trnps = tt;
    }

    /**
     *
     * @return List of List<Integer> with finger-prints of input data array
     */
    public List<List<Integer>> getFingerprint() {
        return fingerprint;
    }

    /**
     *
     * @return unfClass summarizing the digest encoding of data set
     */
    public static UnfClass getSignature() {
        return signature;
    }

    /**
     *
     * @param c unfClass setting digest encoding of data set
     */
    public void setSignature(UnfClass c) {
        signature = c;
    }

    /**
     *
     * @return boolean to create object signature of class UnfClass
     */
    public static boolean getUnfObj() {
        return buildunfObj;
    }

    /**
     *
     * @param obj boolean to create UnfClass signature of data set
     */
    public static void setUnfObj(boolean obj) {
        buildunfObj = obj;
    }

    /**
     * Some warnings for consistency with Micah Alatman's code specs
     *
     * @param ndigits integer significant digits including decimal point
     * @param cdigits integer number of significant characters
     * @param version float unf version number
     */
    public static void dowarnings(int ndigits, int cdigits, float version) {
        if (!DEBUG) {
            mLog.setLevel(Level.WARNING);
        }
        if (version < 3f) {
            mLog.warning("Older versions " + version + " are not recommended use >= " + 3);
        }
        if (ndigits != 0 && (ndigits < NDGTS_BNDS[0] || ndigits > NDGTS_BNDS[1])) {
            mLog.warning("ndigts range is between " + NDGTS_BNDS[0] + " and " + NDGTS_BNDS[1]);
        }
        if (cdigits != 0 && cdigits < 1) {
            mLog.warning("cdigts can't be less than 1");
        }

    }

    /**
     * Overloaded method
     * @param <T> Generic type that extends Number
     * @param obj bi-dimensional array of generic class T that extends Number
     * @param digits optional variable arguments with Integer 
     * @return array of String with base64 encoding for each column of obj
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends Number> String[] unf(final T[][] obj, Integer... digits) throws
            NoSuchAlgorithmException, UnsupportedEncodingException,
            IOException, UnfException {
        return unf(obj, currentVersion, digits);

    }

    /**
     * Calculate UNF's for bi-dimensional array of numeric values
     * 
     * @param <T> Generic type that extends Number
     * @param vers float with the unf version number
     * @param obj bi-dimensional array of generic class T that extends Number
     * @param digits varargs with array of Integer 
     * @return array of String with base64 encoding for each column of obj
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends Number> String[] unf(final T[][] obj, float vers, Integer... digits) throws
            NoSuchAlgorithmException, UnsupportedEncodingException,
            IOException, UnfException {
        if (obj == null) {
            return null;
        }
        currentVersion = vers;
        int ln = digits.length;
        /** if only one entry it's ndigits**/
        int ndigits = (ln <= 0) ? DEF_NDGTS : (int) digits[0];
        int cdigits = (ln <= 1) ? DEF_CDGTS : (int) digits[1];
        dowarnings(ndigits, cdigits, currentVersion);
        Number[][] topass = null;

        Object[][] tmp = UnfDigestUtils.transArray(obj);
        int len = tmp[0].length;
        int nr = UnfDigestUtils.countRows(tmp);
        Number[][] tobj = new Number[nr][len];
        for (int c = 0; c < len; ++c) {
            for (int r = 0; r < nr; ++r) {
                tobj[r][c] = (Number) tmp[r][c];
            }
        }
        int nrow = 0;
        if (trnps) {
            nrow = obj[0].length;
            topass = tobj;
        } else {
            nrow = tobj[0].length;
            topass = obj;
        }
        String algor = (vers <= 3) ? algor3 : algor4;
        String enc = (vers <= 4) ? encod3y4 : encod4p1;
        if (buildunfObj) {
            signature = new UnfClass(cdigits, ndigits, getVersion(), algor, enc);
        }

        String[] res = new String[nrow];
        for (int r = 0; r < nrow; ++r) {
            res[r] = unfV(topass[r], ndigits, vers, signature);
        }
        return res;
    }

    /**
     * Overloaded method
     * @param <T> Generic array that extends CharSequence
     * @param obj bi-dimensional array of generic class T that extends CharSequence
     * @param digits varargs with array of Integer 
     * @return array of String with base64 encoding for each column of obj
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends CharSequence> String[] unf(final T[][] obj, Integer... digits) throws
            NoSuchAlgorithmException, UnsupportedEncodingException,
            IOException, UnfException {
        return unf(obj, currentVersion, digits);

    }

    /**
     * Calculate UNF's for bi-dimensional array of character values
     * 
     * @param <T> Generic array that extends CharSequence
     * @param obj bi-dimensional array of generic class T that extends CharSequence
     * @param vers float unf version number
     * @param digits varargs with array of Integer 
     * @return array of String with base64 encoding for each column of obj
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends CharSequence> String[] unf(final T[][] obj, float vers,
            Integer... digits) throws
            NoSuchAlgorithmException, UnsupportedEncodingException,
            IOException, UnfException {
        if (obj == null) {
            return null;
        }
        currentVersion = vers;
        int ln = digits.length;
        /** if only one digit entry then is cdigits*/
        int cdigits = (ln <= 0) ? DEF_CDGTS : (int) digits[0];
        int ndigits = (ln <= 1) ? DEF_NDGTS : (int) digits[1];
        dowarnings(ndigits, cdigits, currentVersion);
        CharSequence[][] topass = null;
        Object[][] tmp = UnfDigestUtils.transArray(obj);
        int nr = UnfDigestUtils.countRows(tmp);
        int len = tmp[0].length;
        CharSequence[][] tobj = new CharSequence[nr][len];
        for (int c = 0; c < len; ++c) {
            for (int r = 0; r < nr; ++r) {
                tobj[r][c] = (CharSequence) tmp[r][c];
            }
        }
        int nrow = 0;
        if (trnps) {
            nrow = obj[0].length;
            topass = tobj;
        } else {
            nrow = tobj[0].length;
            topass = obj;
        }
        String algor = (vers <= 3) ? algor3 : algor4;
        String enc = (vers <= 4) ? encod3y4 : encod4p1;
        if (buildunfObj) {
            signature = new UnfClass(cdigits, ndigits, getVersion(), algor, enc);
        }
        String[] res = new String[nrow];
        for (int r = 0; r < nrow; r++) {

            res[r] = unfV(topass[r], cdigits, vers, signature);
        }
        return res;
    }

    /**
     * Overloaded 
     * @param <T> Generic array that extends Number
     * @param obj one-dimensional array of generic class T that extends Number
     * @param ndg integer with significant digits that includes decimal point
     * @return String with base64 encoding
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends Number> String unfV(final T[] obj, int ndg) throws
            NoSuchAlgorithmException, UnsupportedEncodingException,
            IOException, UnfException {
        signature = new UnfClass(DEF_CDGTS, ndg, getVersion());
        return unfV(obj, ndg, currentVersion, signature);
    }

    /**
     * Calculates unf for one-dimensional array or column of data set
     * 
     * @param <T> Generic array that extends Number
     * @param obj one-dimensional array of generic class T that extends Number
     * @param ndg integer significant digits that includes decimal point
     * @param vers float with unf version
     * @return String with base64 encoding
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends Number> String unfV(final T[] obj,
            int ndg, float vers, UnfClass signature) throws
            NoSuchAlgorithmException, UnsupportedEncodingException,
            IOException, UnfException {
        UnfNumber<T> unfno = new UnfNumber<T>();
        currentVersion = vers;
        String init = String.format("%064d", 0);
        Character[] base64 = new Character[64];
        List<Integer> fingerp = new ArrayList<Integer>();
        StringBuilder hex = new StringBuilder();
        /**Define encoding and mdalgor according to version(vers)*/
        assnMDEnc(vers, unfno);
        String b64 = unfno.RUNF3(obj, ndg, fingerp, base64, hex);

        b64 = "UNF:" + versString() + ":" + b64;
        if (buildunfObj && signature != null) {
            buildUnfClass(fingerp, hex, b64, signature);
        }

        return b64;
    }

    /**
     * Utility method 
     *@param <T> extends Number
     *@param vers float with the unf version number
     *@param unfno unfNumber<T> class to assign the 
     * message digest algor and encoding according to unf version number 	
     */
    private static <T extends Number> void assnMDEnc(float vers, UnfNumber<T> unfno)
            throws UnfException {

        if (vers <= 3) {
            unfno.setMdalgor(algor3);
        } else if (vers > 3 && vers % 4 <= 0.1) {
            unfno.setMdalgor(algor4);
        } else {
            throw new UnfException("unfDigest:Version not supported");
        }
        if (vers <= 4) {
            unfno.setEncoding(encod3y4);
        } else if (vers > 4 & vers % 4 <= 0.1) {
            unfno.setEncoding(encod4p1);
        } else {
            throw new UnfException("unfDigest:Version not supported");
        }
    }

    /**
     * Overloaded
     * @param <T> Generic array that extends CharSequence
     * @param obj one-dimensional array of generic class T that extends CharSequence
     * @param cdg integer with number of chars to keep
     * @return String with base64 encoding
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends CharSequence> String unfV(final T[] obj, int cdg) throws
            NoSuchAlgorithmException, UnsupportedEncodingException,
            IOException, UnfException {
        signature = new UnfClass(cdg, DEF_NDGTS, getVersion());
        return unfV(obj, cdg, currentVersion, signature);
    }

    /**
     * Calculates the unf for one-dimensional array or column of data set
     * 
     * @param <T> Generic array that extends CharSequence
     * @param obj one-dimensional array of generic class T that extends CharSequence
     * @param cdg integer with number of chars to keep
     * @param vers float with unf version
     * @return String with base64 encoding
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends CharSequence> String unfV(final T[] obj, int cdg,
            float vers, UnfClass signature) throws
            NoSuchAlgorithmException, UnsupportedEncodingException,
            IOException, UnfException {
        UnfString<T> unfno = new UnfString<T>();
        currentVersion = vers;
        String init = String.format("%064d", 0);
        Character[] base64 = new Character[64];

        List<Integer> fingerp = new ArrayList<Integer>();
        boolean[] b = new boolean[obj.length];
        b = UnfDigestUtils.isna(obj);
        StringBuilder hex = new StringBuilder();
        /**Define encoding and mdalgor according to version (vers)*/
        assnMDEnc(vers, unfno);
        String b64 = unfno.RUNF3((CharSequence[]) obj, b, cdg, fingerp, base64, hex);
        fingerprint.add(fingerp);
        b64 = "UNF:" + versString() + ":" + b64;
        if (buildunfObj && signature != null) {
            buildUnfClass(fingerp, hex, b64, signature);
        }
        return b64;
    }

    /**
     * Utility helper method
     * 
     * @param <T> Generic CharSequence
     * @param vers float for unf version number
     * @param unfno unfString<T> to assign encoding 
     *  and algorithm according to unf version (vers)
     */
    private static <T extends CharSequence> void assnMDEnc(float vers, UnfString<T> unfno)
            throws UnfException {
        if (vers <= 3) {
            unfno.setMdalgor(algor3);
        } else if (vers > 3 && vers % 4 <= 0.1) {
            unfno.setMdalgor(algor4);
        } else {
            throw new UnfException("unfDigest:Version not supported");
        }
        if (vers <= 4) {
            unfno.setEncoding(encod3y4);
        } else if (vers > 4 && vers % 4 <= 0.1) {
            unfno.setEncoding(encod4p1);
        } else {
            throw new UnfException("unfDigest:Version not supported");
        }

    }

    /**
     * Creates the UnfClass from data set
     *
     * @param fingerp List of Integer contains bytes from digest
     * @param hex StringBuilder to store the hex-decimal representation
     * @param b64 String with base64 encoding
     * @param signature unfClass to store results of applying unf to a data set
     */
    private static void buildUnfClass(List<Integer> fingerp,
            StringBuilder hex, String b64, UnfClass signature) {
        int sz = fingerp.size();
        fingerprint.add(fingerp);
        Integer[] toadd = fingerp.toArray(new Integer[sz]);
        signature.getFingerprints().add(toadd);
        if (hex != null) {
            List<String> tmp = signature.getHexvalue();
            tmp.add(hex.toString());
            signature.setHexvalue(tmp);
        }

        List<String> tmp0 = signature.getB64();
        tmp0.add(b64);
        signature.setB64(tmp0);
    }

    /**
     *
     * @return String representation of unf version number
     */
    private static String versString() {
        float vrs = getVersion();
        String vch = "";
        if (vrs % Math.round(vrs) <= 0) {
            vch += (int) Math.round(vrs);
        } else {
            vch += vrs;
        }
        return vch;
    }

    /**
     * Add the combine unf's of all columns in data set
     * 
     * @param b64 String array with unf's
     * @return String with combine unf's
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static String addUNFs(String[] b64) throws
            UnsupportedEncodingException, IOException,
            NoSuchAlgorithmException, UnfException {
        if (b64.length <= 0) {
            return null;
        } else if (b64.length <= 1) {
            return b64[0];
        }
        int ln = b64.length;
        List<String> combo = new ArrayList<String>();
        for (String str : b64) {
            //String tosplit=  "UNF:"+versString()+":";
            String tosplit = ":";
            mLog.finer(tosplit);
            String res[] = str.split(tosplit);

            if (res.length >= 3 && str.startsWith("UNF:")) {
                mLog.finer("toadd..." + res[1]);
                combo.add(res[2].trim());
            } else {
                mLog.finer("toadd..." + res[0]);
                combo.add(res[0].trim());

            }
        }
        Collections.sort(combo);
        String[] sortedb64 = new String[ln];
        sortedb64 = combo.toArray(new String[ln]);
        //do not update the signature but just return the calculated unf
        String fin = unfV(sortedb64, 256, currentVersion, null);
        return fin;
    }
}
