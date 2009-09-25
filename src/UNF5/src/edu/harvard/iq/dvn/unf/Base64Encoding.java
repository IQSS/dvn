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
 * Description: Base64 encoding algorithm. The base64(byte[] input) takes into account
 *              the Endianes of the byte stream and, by default, it turns  
 *              the byte array input to  BIG_ENDIAN order, before applying 
 *              the base64 algorithm. It uses  the classes in org.apache.commons.codec.*
 *              
 * @author evillalon@iq.harvard.edu            
 */
package edu.harvard.iq.dvn.unf;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.BCodec;

public class Base64Encoding implements UnfCons {

    private static String DEFAULT_CHAR_ENCODING = "UTF-8";
    private static Logger mLog = Logger.getLogger(Base64Encoding.class.getName());
    /** default byte order */
    private static ByteOrder border = ByteOrder.BIG_ENDIAN;

    public Base64Encoding() {
        if (!DEBUG) {
            mLog.setLevel(Level.WARNING);
        }
    }

    public Base64Encoding(ByteOrder ord) {
        border = ord;
    }

    /**
     *
     * @return ByteOrder
     */
    public static ByteOrder getBorder() {
        return border;
    }

    public static void setBorder(ByteOrder ord) {
        border = ord;
    }

    /**
     *
     * @param digest byte array for encoding in base 64,
     * @param  chngByteOrd boolean indicating if to change byte order
     * @return String the encoded base64 of digest
     */
    public static String tobase64(byte[] digest, boolean chngByteOrd) {

        byte[] tobase64 = null;
        ByteOrder local = ByteOrder.nativeOrder();
        String ordbyte = local.toString();
        mLog.finer("Native byte order is: " + ordbyte);
        ByteBuffer btstream = ByteBuffer.wrap(digest);
        btstream.order(ByteOrder.BIG_ENDIAN);
        byte[] revdigest = null;
        if (chngByteOrd) {
            revdigest = changeByteOrder(digest, local);
        }
        if (revdigest != null) {
            btstream.put(revdigest);
        } else {
            btstream.put(digest);
        }



        tobase64 = Base64.encodeBase64(btstream.array());

        return new String(tobase64);

    }

    /**
     *
     * @param digest byte array
     * @param enc String with final encoding
     * @return String the encoded base64 of digest
     * @throws UnsupportedEncodingException
     */
    public static String tobase64(byte[] digest, String enc)
            throws UnsupportedEncodingException {

        ByteArrayOutputStream btstream = new ByteArrayOutputStream();
        //this make sure is written in big-endian

        DataOutputStream stream = new DataOutputStream(btstream);

        byte[] tobase64 = null;
        byte[] revdigest = new byte[digest.length];
        revdigest = changeByteOrder(digest, ByteOrder.nativeOrder());
        try {

            stream.write(revdigest);
            stream.flush();

            tobase64 = Base64.encodeBase64(btstream.toByteArray());

        } catch (IOException io) {
            tobase64 = Base64.encodeBase64(digest);
        }

        return new String(tobase64, enc);
    }

    /**
     * Alternative tobase64 method.Encodes the String(byte[]) instead of the array
     *
     * @param digest byte array for encoding in base 64,
     * @param cset String with name of charset
     * @return String base 64 the encoded base64 of digest
     */
    public static String tobase641(byte[] digest, String cset) {
        byte[] revdigest = changeByteOrder(digest, ByteOrder.nativeOrder());
        String str = null;

        str = new String(revdigest);
        ByteArrayOutputStream btstream = new ByteArrayOutputStream();
        //this make sure is written in big-endian

        DataOutputStream stream = new DataOutputStream(btstream);
        String tobase64 = null;
        //use a charset for encoding
        if (cset == null) {
            cset = DEFAULT_CHAR_ENCODING;
        }
        BCodec bc = new BCodec(cset);
        try {


            tobase64 = (String) bc.encode(str);


        } catch (EncoderException err) {
            mLog.info("base64Encoding: exception" + err.getMessage());
        }
        return tobase64;
    }

    /**
     * Helper function to change the endianess of the byte array
     *
     * @param digest byte array
     * @param local ByteOrder
     * @return byte array with endianness according to getBorder()
     */
    public static byte[] changeByteOrder(byte[] digest, ByteOrder local) {
        byte[] revdigest = new byte[digest.length];

        if ((local.equals(ByteOrder.LITTLE_ENDIAN) && getBorder().equals(ByteOrder.BIG_ENDIAN)) ||
                (local.equals(ByteOrder.BIG_ENDIAN) && getBorder().equals(ByteOrder.LITTLE_ENDIAN))) {
            int ln = digest.length;
            for (int n = 0; n < ln; ++n) {
                revdigest[n] = digest[ln - 1 - n];
            }
        } else {
            revdigest = digest;
        }
        return revdigest;
    }
}
