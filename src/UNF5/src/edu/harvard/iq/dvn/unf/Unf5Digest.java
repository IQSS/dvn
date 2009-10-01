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
 * columns of the data set, one can obtain the object Unf5Class as 
 * Unf5Class signature = Unf5Digest.getSignature();
 * 
 *  
 * After Micah Altman specifications 
 * 
 * **for version 5 encoding is UTF-8 and digest is SHA-256
 *
 * @author evillalon
 * @link   evillalon@iq.harvard.edu
 * rtreacy adapted evillalon code for unf version 5
 */
package edu.harvard.iq.dvn.unf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Unf5Digest implements UnfCons {

    private static Logger mLog = Logger.getLogger(Unf5Digest.class.getName());
    /**last version of unf supported*/
    /** current version */
   //private static float currentVersion = 3f;
    private static String currentVersion = "5";
    /** the MessageDigest algor associated with lastVersion*/
    private static final String SHA256 = "SHA-256";
    /** encoder to use for version 4.1*/
    private static final String UTF8 = "UTF-8";
    /** transpose the array of data so unf is applied along each column*/
    private static boolean trnps = transpose;
    /** List elements are arrays of fingerprints
     * for every column of input data obj*/
    private static List<List<Integer>> fingerprint = new ArrayList<List<Integer>>();

    private static String getExtensions(Unf5Class signature) {
        StringBuffer retValBuf = new StringBuffer();
        if (signature != null){
            String extensions = signature.getExtensions();
            if (extensions.length() > 0){
                retValBuf.append(":"+extensions);
            }
        }
        return retValBuf.toString();
    }
    /** for debugging */
    private boolean debug = false;
    /** Unf5Class type after calculating digest
     * and Base64 encoding of all columns in data matrix.
     *  Contains fingerprints, hexadecimal strings, digest and the parameters to obtain them*/
    private static Unf5Class signature = null;
    /**whether to create object of type Unf5Class */
    private static boolean buildunfObj = new Boolean(unfObj);

    /**
     * Constructor
     */
    public Unf5Digest() {
        if (!DEBUG || !debug) {
            mLog.setLevel(Level.WARNING);
        }

    }

    /**
     *
     * @return String with current unf version
     */
    public static String getVersion() {
        return currentVersion;
    }

    /**
     *
     * @param vv String to set the unf version
     */
    public void setVersion(String vv){
        this.currentVersion = vv;
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
     * @return Unf5Class summarizing the digest encoding of data set
     */
    public static Unf5Class getSignature() {
        return signature;
    }

    /**
     *
     * @param c Unf5Class setting digest encoding of data set
     */
    public void setSignature(Unf5Class c) {
        signature = c;
    }

    /**
     *
     * @return boolean to create object signature of class Unf5Class
     */
    public static boolean getUnfObj() {
        return buildunfObj;
    }

    /**
     *
     * @param obj boolean to create Unf5Class signature of data set
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
    public static void dowarnings(int ndigits, int cdigits) {
        if (!DEBUG) {
            mLog.setLevel(Level.WARNING);
        }
        if (ndigits != 0 && (ndigits < NDGTS_BNDS[0] || ndigits > NDGTS_BNDS[1])) {
            mLog.warning("ndigts range is between " + NDGTS_BNDS[0] + " and " + NDGTS_BNDS[1]);
        }
        if (cdigits != 0 && cdigits < 1) {
            mLog.warning("cdigts can't be less than 1");
        }

    }

    /**
     * Calculate UNF's for bi-dimensional array of numeric values
     * 
     * @param <T> Generic type that extends Number
     * @param obj bi-dimensional array of generic class T that extends Number
     * @param digits varargs with array of Integer 
     * @return array of String with base64 encoding for each column of obj
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
     public static <T extends Number> String[] unf(final T[][] obj, Integer... digits) throws
            UnsupportedEncodingException, IOException {
        if (obj == null) {
            return null;
        }
        int ln = digits.length;
        /** if only one entry it's ndigits**/
        int ndigits = (ln <= 0) ? DEF_NDGTS : (int) digits[0];
        int cdigits = (ln <= 1) ? DEF_CDGTS : (int) digits[1];
        dowarnings(ndigits, cdigits);
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
        String algor = SHA256;
        String enc = UTF8;
        int hsz = 128; // TODO default for dvn only
        if (buildunfObj) {
            signature = new Unf5Class(cdigits, ndigits, hsz);
        }

        String[] res = new String[nrow];
        for (int r = 0; r < nrow; ++r) {
            res[r] = unfV(topass[r], ndigits, signature);
        }
        return res;
    }

    public static String [] unf(BitString[] b) throws UnsupportedEncodingException, IOException{
        int nrow = b.length;
        int hsz = 128; // TODO default for dvn only
        if (buildunfObj) {
            signature = new Unf5Class(DEF_CDGTS, DEF_NDGTS, DEF_HSZ);
        }
        String[] res = new String[nrow];
        for (int r = 0; r < nrow; r++) {

            res[r] = unfV(b[r],  signature);
        }
        return res;
    }

    public static String [] unf(Boolean[] b) throws UnsupportedEncodingException, IOException{
        int nrow = b.length;
        int hsz = 128; // TODO default for dvn only
        if (buildunfObj) {
            signature = new Unf5Class(DEF_CDGTS, DEF_NDGTS, hsz);
        }
        String[] res = new String[nrow];
        for (int r = 0; r < nrow; r++) {

            res[r] = unfV(b[r],  signature);
        }
        return res;
    }

    /**
     * Overloaded method
     * @param <T> Generic array that extends CharSequence
     * @param obj bi-dimensional array of generic class T that extends CharSequence
     * @param digits varargs with array of Integer 
     * @return array of String with base64 encoding for each column of obj
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends CharSequence> String[] unf(final T[][] obj, Integer... digits) throws
            UnsupportedEncodingException,
            IOException {
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
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends CharSequence> String[] unf(final T[][] obj, String vers,
            Integer... digits) throws
            UnsupportedEncodingException,
            IOException {
        if (obj == null) {
            return null;
        }
        currentVersion = vers;
        int ln = digits.length;
        /** if only one digit entry then is cdigits*/
        int cdigits = (ln <= 0) ? DEF_CDGTS : (int) digits[0];
        int ndigits = (ln <= 1) ? DEF_NDGTS : (int) digits[1];
        dowarnings(ndigits, cdigits);
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
        int hsz = 128; // TODO default for dvn only
        if (buildunfObj) {
            signature = new Unf5Class(cdigits, ndigits, hsz);
        }
        String[] res = new String[nrow];
        for (int r = 0; r < nrow; r++) {

            res[r] = unfV(topass[r], cdigits, signature);
        }
        return res;
    }

    /**
     * Overloaded 
     * @param <T> Generic array that extends Number
     * @param obj one-dimensional array of generic class T that extends Number
     * @param ndg integer with significant digits that includes decimal point
     * @return String with base64 encoding
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends Number> String unfV(final T[] obj, int ndg) throws
            UnsupportedEncodingException,
            IOException {
        int hsz = 128;
        signature = new Unf5Class(DEF_CDGTS, ndg, hsz);
        return unfV(obj, ndg, signature);
    }

    /**
     * Calculates unf for one-dimensional array or column of data set
     * 
     * @param <T> Generic array that extends Number
     * @param obj one-dimensional array of generic class T that extends Number
     * @param ndg integer significant digits that includes decimal point
     * @param vers float with unf version
     * @return String with base64 encoding
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends Number> String unfV(final T[] obj,
            int ndg, Unf5Class signature) throws
            UnsupportedEncodingException,
            IOException {
        UnfNumber<T> unfno = new UnfNumber<T>();
        Character[] base64 = new Character[64];
        List<Integer> fingerp = new ArrayList<Integer>();
        StringBuilder hex = new StringBuilder();
        /**Define encoding and mdalgor according to version(vers)*/
        String b64 = unfno.RUNF5(obj, ndg, fingerp, base64, hex);
        boolean buildclass=false;
        if (signature != null){
            buildclass = true;
        } else{
            int hsz = 128;
            signature = new Unf5Class(DEF_CDGTS, ndg, hsz);
        }

        b64 = "UNF:" + getVersion() + getExtensions(signature) + ":" + b64;
        if (buildunfObj && buildclass) {
            buildUnf5Class(fingerp, hex, b64, signature);
        }

        return b64;
    }

    /**
     * Overloaded
     * @param <T> Generic array that extends CharSequence
     * @param obj one-dimensional array of generic class T that extends CharSequence
     * @param cdg integer with number of chars to keep
     * @return String with base64 encoding
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends CharSequence> String unfV(final T[] obj, int cdg) throws
            UnsupportedEncodingException,
            IOException {
        int hsz = 128;
        signature = new Unf5Class(cdg, DEF_NDGTS, hsz);
        return unfV(obj, cdg, signature);
    }

    /**
     * Calculates the unf for one-dimensional array or column of data set
     * 
     * @param <T> Generic array that extends CharSequence
     * @param obj one-dimensional array of generic class T that extends CharSequence
     * @param cdg integer with number of chars to keep
     * @param vers String with unf version
     * @return String with base64 encoding
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static <T extends CharSequence> String unfV(final T[] obj, int cdg,
            Unf5Class signature) throws
            UnsupportedEncodingException,
            IOException{
        UnfString<T> unfno = new UnfString<T>();
        String init = String.format("%064d", 0);
        Character[] base64 = new Character[64];

        List<Integer> fingerp = new ArrayList<Integer>();
        boolean[] b = new boolean[obj.length];
        b = UnfDigestUtils.isna(obj);
        StringBuilder hex = new StringBuilder();
        /**Define encoding and mdalgor according to version (vers)*/
        String b64 = unfno.RUNF5((CharSequence[]) obj, b, cdg, fingerp, base64, hex);
        fingerprint.add(fingerp);
        boolean buildclass=false;
        if (signature != null){
            buildclass = true;
        } else{
            int hsz = 128;
            signature = new Unf5Class(cdg,DEF_NDGTS,hsz);
        }
        b64 = "UNF:" + getVersion() + getExtensions(signature) + ":" + b64;
        if (buildunfObj && buildclass) {
            buildUnf5Class(fingerp, hex, b64, signature);
        }
        return b64;
    }



    /**
     * Creates the Unf5Class from data set
     *
     * @param fingerp List of Integer contains bytes from digest
     * @param hex StringBuilder to store the hex-decimal representation
     * @param b64 String with base64 encoding
     * @param signature Unf5Class to store results of applying unf to a data set
     */
    private static void buildUnf5Class(List<Integer> fingerp,
            StringBuilder hex, String b64, Unf5Class signature) {
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
     * Add the combine unf's of all columns in data set
     * 
     * @param b64 String array with unf's
     * @return String with combine unf's
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static String addUNFs(String[] b64) throws
            UnsupportedEncodingException, IOException
            {
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
                combo.add(res[res.length - 1].trim());
            } else {
                mLog.finer("toadd..." + res[0]);
                combo.add(res[0].trim());

            }
        }
        Collections.sort(combo);
        String[] sortedb64 = new String[ln];
        sortedb64 = combo.toArray(new String[ln]);
        //do not update the signature but just return the calculated unf
        String fin = unfV(sortedb64, DEF_CDGTS, null);
        return fin;
    }
    public static String unfV(final Boolean obj, Unf5Class signature) throws
            UnsupportedEncodingException,
            IOException {
        UnfBoolean unfno = new UnfBoolean();
        Character[] base64 = new Character[64];
        List<Integer> fingerp = new ArrayList<Integer>();
        StringBuilder hex = new StringBuilder();
        Boolean[] cobj = new Boolean[1];
        cobj[0] = obj;
        String b64 = unfno.RUNF5(cobj, fingerp, base64, hex);
        boolean buildclass=false;
        if (signature != null){
            buildclass = true;
        } else{
            signature = new Unf5Class(DEF_CDGTS, DEF_NDGTS, DEF_HSZ);
        }

        b64 = "UNF:" + getVersion() + getExtensions(signature) + ":" + b64;
        if (buildunfObj && buildclass) {
            buildUnf5Class(fingerp, hex, b64, signature);
        }

        return b64;
    }

    public static String unfV(final BitString obj,
            Unf5Class signature) throws
            UnsupportedEncodingException, IOException{
        UnfBitfield unfno = new UnfBitfield();
        String init = String.format("%064d", 0);
        Character[] base64 = new Character[64];

        List<Integer> fingerp = new ArrayList<Integer>();
        boolean[] b = new boolean[1];
        b[0] = obj != null? false: true;
        StringBuilder hex = new StringBuilder();
        CharSequence[] cobj = new CharSequence[1];
        cobj[0] = obj.getBits();
        /**Define encoding and mdalgor according to version (vers)*/
        String b64 = unfno.RUNF5((CharSequence[]) cobj, b,  fingerp, base64, hex);
        fingerprint.add(fingerp);
        boolean buildclass=false;
        if (signature != null){
            buildclass = true;
        } else{
            int hsz = 128;
            signature = new Unf5Class(DEF_CDGTS,DEF_NDGTS,hsz);
        }
        b64 = "UNF:" + getVersion() + getExtensions(signature) + ":" + b64;
        if (buildunfObj && buildclass) {
            buildUnf5Class(fingerp, hex, b64, signature);
        }
        return b64;
    }

}
