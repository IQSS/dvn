/**
 * Description: CanonalizeUnicode: Method that takes byte array and turns it 
 *              into another array. It is overloaded and can 
 *              either normalize the character sequence that the byte array  
 *              represents using java Normalizer or it can also 
 *              use two different encodings and transform the bytes. 
 *              After Micah Altman code written in C
 *              
 * @author evillalon              
 * 
 */
package edu.harvard.iq.vdcnet;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.logging.Logger;

import edu.harvard.iq.vdcnet.utils.utilsConverter;



public class canonUnicode {
	private static Logger mLog = Logger.getLogger(canonUnicode.class.getName());
	protected static int FINAL_ENC =0;
	protected static int ORG_ENC =1;
	/**
	 * 
	 * @param bin: byte to convert or normalize UTF
	 * @param form: the canonical form to choose, default "NFC'
	 * @param cset: array with two elements the encoding and decoding schemes
	 * @return byte array 
	 * @throws UnsupportedEncodingException
	 */	
		public static byte[] CanonalizeUnicode(final byte[] bin, Normalizer.Form form,
				String...cset) throws UnsupportedEncodingException{
			 CharSequence chqn= null; 
				if(cset.length> 0 && Charset.isSupported(cset[FINAL_ENC])) 
				  chqn= new String(bin, cset[FINAL_ENC]);
				else
			      chqn= new String(bin);
				String norm = Normalizer.normalize(chqn, form);
				return norm.getBytes(cset[FINAL_ENC]);
		}
		/**
		 * 
		 * @param bin: byte to convert or normalize UTF
		 * @param cset: array with two elements the encoding and decoding schemes
		 * @return byte array
		 * @throws UnsupportedEncodingException
		 */
	public static byte[] CanonalizeUnicode(final byte[] bin, String...cset) 
	throws UnsupportedEncodingException{

		return utilsConverter.byteConverter(bin,cset); 
	}
}
