package edu.harvard.iq.vdcnet;
import java.io.Serializable;


public interface generalizedRound<T extends Number>{
	
	public String Genround(T obj, int digits); 
	//public String Genround(String obj, int digits); 

}
