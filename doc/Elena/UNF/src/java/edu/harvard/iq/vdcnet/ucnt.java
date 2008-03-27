/**
 * Description: Unicodes characters 
 * @Author Elena Villalon
 *  email:evillalon@iq.harvard.edu
 *  
 */
package edu.harvard.iq.vdcnet;

public enum ucnt {
	dot('\u002E'), //decimal separator "."
	plus('\u002b'),//"+" sign 
	min('\u002d'),//"-" 
	e('\u0065'), //"e"
	percntg('\u0025'),//"%"
	pndsgn('\u0023'), //"#"
	zero('\u0030'), //'0'
	s('\u0073');//"s"
	private final char ucode;
	ucnt(char c){
		this.ucode = c;
	}
	public char getUcode(){return ucode;}
}
