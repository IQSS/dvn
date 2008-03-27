/**
 * Description: A bunch of methods to perform conversion of byte 
 *              arrays from one encoding to another and/or toString
 *              convertnioByteToStr: byte to String with encoding 
 *              convertByteToStr: same as previous but using different buffers
 *              convertnioStrToByte: String to byte array with specified encoding
 *              StrConverter: same as previous with different buffers for conversion
 *              convertStreams: byte array to OutStream using two different encodings
 *              convertStreamToByte: wraps previous method but returns byte array 
 *              byteConverter: convert between two byte arrays using tow encodings
 *              CanonalizeUnicode: Method that takes byte array and turns it into another array
 *              It can either normalize the character sequence that the byte array  
 *              represents using java Normalizer or it can also use two different encodings 
 *              
 *   @author evillalon : Elena Villalon          
 *              
 */
package edu.harvard.iq.vdcnet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.Normalizer;
import java.util.logging.Logger;
import java.text.Normalizer.Form;


public class canonUnicode{
	private boolean normalizeText= false;
	Normalizer.Form frm = Normalizer.Form.valueOf("NFC"); 
	private static Logger mLog = Logger.getLogger(canonUnicode.class.getName());
	protected int FINAL_ENC =0;
	protected int ORG_ENC =1;
	/**
	 * 
	 * @param inbyte: byte array to be coded into String
	 * @param outset: String with name of the Charset
	 * @return String with converted bytes decoded with outset
	 */  
	public  String convertnioByteToStr(final byte[] inbyte, String outset){
		Charset original = Charset.defaultCharset();
	    Charset cset=original;
		try{
		cset = Charset.forName(outset);
        }catch(UnsupportedCharsetException err0){
        	mLog.severe("CanonUnicode: "+err0.getMessage()+ "...Using the machine default");
			err0.printStackTrace();
		} catch(IllegalCharsetNameException err1){
			mLog.severe("CanonUnicode: "+err1.getMessage()+ "...Using the machine default");
			err1.printStackTrace();
		} catch(IllegalArgumentException err2){
			mLog.severe("CanonUnicode: "+err2.getMessage()+ "...Using the machine default");
			err2.printStackTrace();
		}
		 
	   ByteBuffer bbuf = ByteBuffer.wrap(inbyte,0,inbyte.length);
	   CharBuffer cbuf; 
	   if(cset.canEncode())
	    cbuf= cset.decode(bbuf);
	   else
		cbuf= original.decode(bbuf); 
		   
		return cbuf.toString();
	}
	/**
	 * Another version of method  convertnioByteToStr using io 
	 * @param inbyte: byte array to be coded into String
	 * @param outset: String with name of the Charset
	 * @return String with converted bytes
	 */ 
	public String convertByteToStr(final byte[] inbyte, String outset){
		Charset original=Charset.defaultCharset();
		Charset to=	original;
		if(outset!=null && Charset.isSupported(outset)){	
		 to= Charset.forName(outset);
		 if(to.canEncode())
			 return new String(inbyte,to);
		}
	 return new String(inbyte,original);
	}
	/**
	 * Overloads with Charset
	 * @param inbyte: byte array to be coded into String
	 * @param cset: charset to use in the encodind
	 * @return String with converted bytes
	 */
	public  String convertByteToStr(final byte[] inbyte, Charset cset){
		if(cset.canEncode())
		return new String(inbyte,cset);
		else
		return new String(inbyte);
	}
	/**
	 * 
	 * @param input: String to be converted into byte
	 * @param inset: the Charset name for encoding
	 * @return array of byte
	 */
	public byte[] convertnioStrToByte(final String input, String inset){
		Charset original = Charset.defaultCharset();
		Charset cset = original;
		try{
		cset = Charset.forName(inset);
        }catch(UnsupportedCharsetException err0){
        	mLog.severe("CanonUnicode: "+err0.getMessage()+ " Using default");
			err0.printStackTrace();
		  
		} catch(IllegalCharsetNameException err1){
			mLog.severe("CanonUnicode: "+err1.getMessage()+" Using default");
			err1.printStackTrace();
			
		} catch(IllegalArgumentException err2){
			mLog.severe("CanonUnicode: "+err2.getMessage()+ " Using default");
			err2.printStackTrace();
			
		}
		ByteBuffer buffer=null;
		if(cset.canEncode())
		 buffer= cset.encode(input);
		else
			buffer =original.encode(input);
		byte[] bb = buffer.array(); 
		return bb;
	}
	/**
	 * Another version of previous method convertnioStrToByte
	 * @param input: String to be encoded into bytes
	 * @param cset: String with name of Charset
	 * @return byte array
	 */
	public byte[] StrConverter(final String input, String cset){
		Charset original=Charset.defaultCharset();
		Charset ccset = original;
		if(cset!=null && Charset.isSupported(cset))	
				ccset = Charset.forName(cset);
		if(ccset.canEncode())
		return input.getBytes(ccset); 
		else
			return input.getBytes(original); 	
		
	}
	/**
	 * 
	 * @param input: String to be converted into bytes
	 * @param cset:Charset for encoding
	 * @return byte array
	 */
	public byte[] StrConverter(final String input, Charset cset){
	    Charset original = Charset.defaultCharset();
		if(cset!=null && Charset.isSupported(cset.displayName()) && 
				cset.canEncode())	
				
		return input.getBytes(cset); 	
		else	
		return input.getBytes(original); 
	}
	
    /**
     * 
     * @param bin: byte array with encoding inset
     * @param inset: String with the Charset that encodes bin
     * @param outset: String with the converted encoding
     * @return OutputStream containing byte array with encoding outset 
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
	public OutputStream convertStreams(final byte[] bin, String inset, String outset) 
	throws UnsupportedEncodingException,IOException{
         //from non-unicode bytes to unicode characters
		InputStream instream = (InputStream)(new ByteArrayInputStream(bin));
		InputStreamReader inreader = new InputStreamReader(instream,inset);
		//from unicode characters to non-unicode bytes 
		OutputStream outstream = (OutputStream) new ByteArrayOutputStream(bin.length);
		OutputStreamWriter outreader = new OutputStreamWriter(outstream,outset);
		Reader r = new BufferedReader(inreader);
		Writer w = new BufferedWriter(outreader);
		StringBuffer buffer = new StringBuffer();
		int ch;
		while((ch=r.read())> -1)
			buffer.append((char) ch);
	    r.close();
	    char[] dest= new char[ch+1];
	    buffer.getChars(0, ch, dest, 0); 
	    w.write(dest,0,ch);
	    w.close();
	    return outstream;
	}
	/**
	 * 
	 * @param bin: : byte array with encoding inset
	 * @param inset: : String with the Charset that encodes bin
	 * @param outset: : String with the converted encoding
	 * @return byte array with converted encoding 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public byte [] convertStreamToByte(final byte[] bin, String inset, String outset) 
	throws UnsupportedEncodingException,IOException{
		ByteArrayOutputStream out = (ByteArrayOutputStream) convertStreams(bin, inset, outset);
		return out.toByteArray();
	}
	
	 /**
	  * @param bin: byte array to be encoded
	  * @param cset:cset String array with one or two elements
	  *             first is the conversion encoding name and, 
	  *             possibly, second the original encoding for bin
	  *             
	  * @return byte array
	  */
	public byte[] byteConverter(final byte[] bin, String... cset) throws
	UnsupportedEncodingException{
       //default Charset in JVM
		Charset original = Charset.defaultCharset();
		String strbin =null;
		//consistency checks 
		if(cset.length >2){
			mLog.severe("canonUnicode: Provide only two values for encoding");
		}else if (cset.length<= 0){
			mLog.severe("canonUnicode: Not provided encoding for array of byte");
			return bin;
		}
		//original encoding for byte array	element 1 
		if(cset.length>1){
			
			if(Charset.isSupported(cset[ORG_ENC]) ){
				Charset from = Charset.forName(cset[ORG_ENC]);
				if(from.canEncode())
			        strbin = new String(bin,cset[ORG_ENC]);
				else{
					 mLog.severe("canonUnicode: Charset "+cset[ORG_ENC]+" no encode...Using default");
				     strbin =  new String(bin,original);	
				} 
		    }else{	
		      mLog.severe("canonUnicode: Charset "+cset[ORG_ENC]+" not supported...Using default");
			  strbin =  new String(bin,original);
		   }
		}
			
		Charset to;// final encoding return for byte array element 0
		if(Charset.isSupported(cset[FINAL_ENC])){
		  to = Charset.forName(cset[FINAL_ENC]);
		  if(!to.canEncode()) to =original;
		}else{
		  mLog.severe("canonUnicode: Charset "+cset[FINAL_ENC]+" not supported...Using default");
		  to=original;
		}
	
		byte[] bout = strbin.getBytes(to);
		return bout;
	}
	
/**
 * 
 * @param bin: byte to convert or normalize UTF
 * @param form: the canonical form to choose or default "NFC'
 * @param cset: array with two elements the encoding and decoding schemes
 * @return byte array 
 * @throws UnsupportedEncodingException
 */	
	
public byte[] CanonalizeUnicode(final byte[] bin,Normalizer.Form form,String...cset)
	throws UnsupportedEncodingException{
	if(!normalizeText)
	return byteConverter(bin,cset); 
	//char[] chq =  new String(bin).toCharArray();
	 CharSequence chqn= null; 
	if(cset.length> 0 && Charset.isSupported(cset[FINAL_ENC])) 
	  chqn= new String(bin, cset[FINAL_ENC]);
	else
      chqn= new String(bin);
	if(form != null) frm = form;
	String norm = Normalizer.normalize(chqn, frm);
	return norm.getBytes(cset[FINAL_ENC]);

}
}
