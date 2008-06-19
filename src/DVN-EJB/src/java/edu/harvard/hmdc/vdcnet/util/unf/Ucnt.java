/**
 * Description: Unicodes characters 
 * @Author Elena Villalon
 *  email:evillalon@iq.harvard.edu
 *  
 */
package  edu.harvard.hmdc.vdcnet.util.unf;

public enum Ucnt {
	dot('\u002E'), //decimal separator "."
	plus('\u002b'),//"+" sign 
	min('\u002d'),//"-" 
	e('\u0065'), //"e"
	percntg('\u0025'),//"%"
	pndsgn('\u0023'), //"#"
	zero('\u0030'), //'0'
	s('\u0073'),//"s"
	nil('\u0000'), //'\0' for null terminator
	frmfeed('\u000C'), //form feed
	ls('\u2028'),//line separator
	nel('\u0085'),//next line
	psxendln('\n'); //posix end-of-line
		
	private final char ucode;
	Ucnt(char c){
		this.ucode = c;
	}
	public char getUcode(){return ucode;}
}
