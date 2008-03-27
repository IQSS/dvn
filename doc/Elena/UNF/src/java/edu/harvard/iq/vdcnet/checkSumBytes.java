/**
 * Description: Fingerprinting methods. Takes stream of bytes
 *              After Micah Altman code in C
 *              Overloaded method CheckSumBytes, sequence can be
 *              sequence of int, short, byte
 *               
 * @Author Elena Villalon
 *  email:evillalon@iq.harvard.edu
 *  
 */
 package edu.harvard.iq.vdcnet;

import java.math.BigInteger;
import java.util.logging.Logger;


public class checkSumBytes<T extends Number> {
	  private static Logger mLog = Logger.getLogger(roundRoutines.class.getName());
public 	checkSumBytes(){}
/**
 * 
 * @param previous: Number
 * @param sequence: int array to adjust previous
 * @param len: int number of elements in sequence
 * @return Number adjusted value of previous
 */
public Number CheckSumBytes(T previous,int[]sequence, int len){
	mLog.info("Input: "+previous.getClass().getCanonicalName());
		
	if(len <0)
		return ((Number)previous);
	
	Number r = previous;
	long lr;
	BigInteger br;
	long res=0;
	len=(len <= sequence.length ? len: sequence.length);
	 for(int i=0;i<len; i++)
		 res +=  sequence[i];
	if(previous instanceof Long)
	    return (lr=r.longValue()+res);
	
	if(previous instanceof BigInteger)
	    return (br=((BigInteger)r).add(BigInteger.valueOf(res)));
    if(previous instanceof Byte){
     return	(previous.longValue()+res);
    }
    	
	mLog.severe("CheckSumBytes: input must be BigInteger long, byte");
	return previous;
}
/**
 * 
 * @param previous: Number
 * @param sequence: short array to adjust previous
 * @param len: int number of elements in sequence
 * @return Number adjusted value of previous
 */
public Number CheckSumBytes(T previous,short[]sequence, int len){
	int [] seqint = new int[sequence.length];
  for(int n=0; n <sequence.length; ++n)
	  seqint[n] = sequence[n];
  return CheckSumBytes(previous,seqint, len);
	  
}
/**
 * 
 * @param previous: Number
 * @param sequence: char array to adjust previous
 * @param len: int number of elements in sequence
 * @return Number adjusted value of previous
 */
public Number CheckSumBytes(T previous,char[]sequence, int len){
	if(!Character.isDigit(sequence[0])){
		mLog.severe("CheckSumBytes: sequence must contain only numeric values");
	    return(previous);
	}
	int [] seqint = new int[sequence.length];
  for(int n=0; n <sequence.length; ++n)
	  seqint[n] = Character.digit(sequence[n],10);
  return CheckSumBytes(previous,seqint, len);
	  
}
/**
 * 
 * @param previous: Number
 * @param sequence: byte array to adjust previous
 * @param len: int number of elements in sequence
 * @return Number adjusted value of previous
 */
public Number CheckSumBytes(T previous,byte[]sequence, int len){
	if(!Character.isDigit(sequence[0])){
		mLog.severe("CheckSumBytes: sequence must contain only numeric values");
	    return(previous);
	}
	int [] seqint = new int[sequence.length];
  for(int n=0; n <sequence.length; ++n)
	  seqint[n] =((Byte) sequence[n]).intValue();
  return CheckSumBytes(previous,seqint, len);
	  
}
//need some JUnit tests
public static void main(String args[]){
	checkSumBytes<Long> bt = new checkSumBytes();
	int seq[]= {1,2,3,9,7};
	int len=3;  
	System.out.println(""+bt.CheckSumBytes(3456789234L, seq, len));
	int seq1[] = {100,200,300,90,70};
	System.out.println(""+bt.CheckSumBytes(3456789234L, seq1, 4));
	checkSumBytes<BigInteger> bn = new checkSumBytes();
	System.out.println(""+bn.CheckSumBytes(new BigInteger("3456789234"), seq, len));
	System.out.println(""+bn.CheckSumBytes(new BigInteger("3456789234"), seq1, 4));
	
	
}
}

