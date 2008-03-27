/**
 * Description rounds a vector or matrix of numbers to some 
 * significant digits, using round-toward-zero rounding. 
 * After Micah Altman code (not used in UNF computations 
 * per se, the C code) but in R
 * INPUT : bi-dimensional array of doubles or a vector 
 *         digits, integer with precision
 * OUTPUT : array or vector rounded toward zeo.
 * 
 * @author evillalon@iq.harvard.edu
 * Elena Villalon
 *         
 */
package edu.harvard.iq.vdcnet;
import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.log10;
import static java.lang.Math.pow;
import static java.lang.Math.signum;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealMatrixImpl;

public class matUtils <T extends Number>{
/**
 * 
 * @param arr: double bi-dimensional array
 * @param digits: int for rounding elements in arr
 * @return bi-dimnesional array of doubles
 * Same as Micah Altman code in R
 */
public double[][] signifz(double arr[][],int digits){
	

	int nc = arr[0].length; //first index in arr
	int nr = calNoRows(arr);
	double [] vec=null;
	
    //length of second index
    double[][] ret=new double[nr][nc];
   
	if(nc <= 1){
	  
	  for(int n=0; n < nr;++n)
		  vec[n]=arr[n][0];
	  ret[0] =signifz(vec,digits);
	  return ret;
	}  
	if(nr <= 1){
	
		vec=arr[0];
		ret[0] =signifz(vec,digits);
		return ret;
	}
	  
	  for(int j=0;j<nr;++j)
		  ret[j]= signifz(arr[j],digits);
	  
	 
	  return ret;
}
/**
 * @param arr: double bi-dimensional array
 * @param digits: int for rounding elements in arr
 * @return bi-dimnesional array of doubles
 * Same as signifz but using matrices
 * 
 */
public double[][] signifzMat(double arr[][],int digits){
	
	//create real matrix dim= [nr][nc]
	RealMatrix mat = new RealMatrixImpl(arr);
	int nc = mat.getColumnDimension(); 
	int nr = mat.getRowDimension();
	
    //length of second index
    double[][] ret=new double[nr][nc];
   
	if(nc <= 1){
	  
	  for(int n=0; n < nr;++n)
	  ret[0] =signifz(mat.getColumn(0),digits);
	  
	  return ret;
	}  
	if(nr <= 1){
		ret[0] =signifz(mat.getRow(0),digits);
		
		return ret;
	}
	  
	  for(int j=0;j<nr;++j)
		  ret[j]= signifz(arr[j],digits);
	  
	 
	  return ret;
}
public int calNoRows(double data[][]){
	int rw=0;
	for(int n=0; ; ++n){
	try{
		double	dat=data[n][0];
		    rw++;
	}catch(ArrayIndexOutOfBoundsException err){
		break;
	}
	}
	return rw;
}
/**
 * 
 * @param mat: RealMatrix a matrix 
 * @param digits: int for precission
 * @return a bi-dimensional array of double 
 */
public double[][] signifz(RealMatrix mat ,int digits){
	double[][] arr = mat.getData();
	return signifz(arr ,digits); 
	
}
/**
 * 
 * @param vec: one dimensional array of double
 * @param digits: int with precission
 * @return one dimensional array of double
 */
public double[] signifz(double[]vec,int digits){
	double magnitude [] = new double[vec.length];
	double scale[]= new double[vec.length];
	float signs[] = new float[vec.length];
	int n=0;
	double [] ret= vec;
	for(n=0; n<vec.length;++n){
		magnitude[n]=floor(log10(abs(vec[n])));
		scale[n]=pow(10d,(digits-magnitude[n]-1));
		signs[n] = signum((float)vec[n]);
		if(signs[n] >0){
			ret[n] = floor(vec[n]*scale[n])/scale[n];
		}else{
			ret[n] = ceil(vec[n]*scale[n])/scale[n];
		}
			
	}
	return ret;
		
	}
/**
 * Uses Apache Math software
 * @param arr: double bi-dimensional array
 * @return bi-dimensional matrix, rows and columns along first and second indexes of arr
 */
public RealMatrix asMatrix(double[][]arr) {
	return new RealMatrixImpl(arr);
	
}
/**
 * Uses Apache Math software
 * @param mat: RealMatrix matrix with double
 * @return double array 
 */
public double[][] asDouble(RealMatrix mat){
	return mat.getData();
}
public static void main(String args[]){
	double arr [][]={{1,2,3,4},{11,12,13,14},{21,22,23,24},{31,32,33,34},{51,52,53,54}};
	new matUtils().signifz(arr, 2);
	
}
}

