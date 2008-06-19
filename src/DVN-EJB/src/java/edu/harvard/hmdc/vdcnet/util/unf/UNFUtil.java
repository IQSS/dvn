/**
 * Description: This class is a wrapper for unfDigest and uses its static methods
 * 				It contains only one method calculateUNF, which is overloaded 
 *              and accept one-dimensional primitives array types or array of String;  
 *              it calls the appropriate method unfDigest.unf depending on 
 *              whether the array is of primitives, after converting them to Number,
 *              or if the array is of String, and return the unf.
 *              
 * @author evillalon@iq.harvard.edu               
 */
package edu.harvard.hmdc.vdcnet.util.unf;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UNFUtil {
/**
 * 
 * @param numb: one dimensional array of double 
 * @param version: String unf version
 * @return String with unf calculation 
 * @throws NumberFormatException
 * @throws UnfException
 * @throws IOException
 * @throws NoSuchAlgorithmException
 */

public static String calculateUNF(final double[] numb, String version) 
throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException{
	
	float vers = (float) Double.parseDouble(version);
	Double mat[][] = new Double[1][numb.length];
	for(int n=0; n < numb.length; ++n)
		mat[0][n] = numb[n];
	UnfDigest.setTrnps(false);
	String [] res = UnfDigest.unf(mat, vers);
	
	return res[0];
}
/**
 * Overloaded method 
 * @param numb: one dimensional array of float
 * @param version: String unf version
 * @return String with unf calculation 
 * @throws NumberFormatException
 * @throws UnfException
 * @throws IOException
 * @throws NoSuchAlgorithmException
 */

public static String calculateUNF(final float[] numb, String vers) 
throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException{
	double [] toret = new double[numb.length];
	for(int k=0; k < numb.length; ++k)
		toret[k] = (double) numb[k];
	String res = calculateUNF(toret, vers);
	return res;
}
/**
 * Overloaded method
 * @param numb: one dimensional array of short 
 * @param version: String unf version
 * @return String with unf calculation 
 * @throws NumberFormatException
 * @throws UnfException
 * @throws IOException
 * @throws NoSuchAlgorithmException
 */

public static String calculateUNF(final short[] numb, String vers)
throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException{
	double [] toret = new double[numb.length];
	for(int k=0; k < numb.length; ++k)
		toret[k] = (double) numb[k];
	String res = calculateUNF(toret, vers);
	return res;
	
}
/**
 * Overloaded method
 * @param numb: one dimensional array of byte
 * @param version: String unf version
 * @return String with unf calculation 
 * @throws NumberFormatException
 * @throws UnfException
 * @throws IOException
 * @throws NoSuchAlgorithmException
 */

public static String calculateUNF(final byte[] numb, String vers)
throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException{
	double [] toret = new double[numb.length];
	for(int k=0; k < numb.length; ++k)
		toret[k] = (double) numb[k];
	String res = calculateUNF(toret, vers);
	return res;
	
}
/**
 * Overloaded method
 * @param numb: one dimensional array of long 
 * @param version: String unf version
 * @return String with unf calculation 
 * @throws NumberFormatException
 * @throws UnfException
 * @throws IOException
 * @throws NoSuchAlgorithmException
 */

public static String calculateUNF(final long[] numb, String vers)
throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException{
	
	double [] toret = new double[numb.length];
	for(int k=0; k < numb.length; ++k)
		toret[k] = (double) numb[k];
	String res = calculateUNF(toret, vers);
	return res;
}
/**
 * Overloaded method
 * @param numb: one dimensional array of integer 
 * @param version: String unf version
 * @return String with unf calculation 
 * @throws NumberFormatException
 * @throws UnfException
 * @throws IOException
 * @throws NoSuchAlgorithmException
 */

public static String calculateUNF(final int[] numb, String vers)
throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException{
	
	double [] toret = new double[numb.length];
	for(int k=0; k < numb.length; ++k)
		toret[k] = (double) numb[k];
	String res = calculateUNF(toret, vers);
	return res;
}
/**
 * Overloaded method: Converts boolean to 1 (true) or 0 (false). 
 * @param numb: one dimensional array of boolean 
 * @param version: String unf version
 * @return String with unf calculation 
 * @throws NumberFormatException
 * @throws UnfException
 * @throws IOException
 * @throws NoSuchAlgorithmException
 */

public static String calculateUNF(final boolean[] numb, String vers)
throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException{
	
	double [] toret = new double[numb.length];
	for(int k=0; k < numb.length; ++k)
		toret[k] = (numb[k]==true)?1d:0d;
	String res = calculateUNF(toret, vers);
	return res;
}
/**
 * Overloaded method
 * @param numb: List with generics types
 * @param version: String unf version
 * @return String with unf calculation 
 * @throws NumberFormatException
 * @throws UnfException
 * @throws IOException
 * @throws NoSuchAlgorithmException
 */

public static <T> String calculateUNF(final List<T> numb, String version) 
throws NumberFormatException, UnfException, IOException, NoSuchAlgorithmException{
	if(numb.get(0) instanceof Number){
		double[] arr = new double[numb.size()];
		int cnt  = 0;
		for(T obj: numb){
			arr[cnt] =(Double) obj;
			cnt++;
		}
		return calculateUNF(arr, version);
	}
	String [] topass = new String[numb.size()];
	topass = numb.toArray(new String[numb.size()]);
	return calculateUNF(topass, version);
		
}
/**
 * Overloaded method
 * @param numb: one dimensional array of String 
 * @param version: String unf version
 * @return String with unf calculation 
 * @throws NumberFormatException
 * @throws UnfException
 * @throws IOException
 * @throws NoSuchAlgorithmException
 */

public static String calculateUNF(final String[] chr, String version)
throws UnfException, NoSuchAlgorithmException, IOException{
	float vers = (float) Double.parseDouble(version);
	String tosplit=  ":";
	String spres[] =  chr[0].split(tosplit);
	if(spres.length >= 3 && chr[0].startsWith("UNF:")){
	  return UnfDigest.addUNFs(chr);
	}
	if(spres.length>1)throw new UnfException("UNFUtil: Malformed unf");
	CharSequence[][] chseq = new CharSequence[1][chr.length];
	UnfDigest.setTrnps(false);
	int cnt = 0;
	for(String str:chr){
		chseq[0][cnt]= (CharSequence) str;
		cnt++;
	}
	String [] res = UnfDigest.unf(chseq, vers);
	return res[0];
}



}
