// This is an experimental implementation of JCut, the 
// java port of rcut, a field-cutting command-line 
// utility written in C that the DSB has been using to 
// extract fields from tab-delimited and fixed-field files. 
//
// Please note the "experimental" part; so far I've been
// focusing on researching the speed optimizations. 
// The program doesn't support all the options and all the
// functionality rcut supports. So that still needs to 
// be added. 

import java.io.*;
import java.util.*;
import java.io.InputStream;
import java.io.BufferedReader;

import java.nio.ByteBuffer; 
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.BufferUnderflowException; 


class JCut {

    public int NBUFSIZ = 32767; 


    public static int counter = 0; 
    private static int RECLEN = 0; 
    private static int OUTLEN = 0; 

    public static Boolean debug = false; 

    private String delim = "\t"; 

    public static int[] bounds = new int[8192];
    public static int[] outbounds = new int[8192];

    private void parse_lists ( String lists ) {

	Boolean endofoptionline = false; 

	int nextRange = 0; 

	String ltok = null;
	String ntok = null;

	int begin = 0; 
	int end = 0; 

	// take apart the lists, one by one (they are seperated with commas)

	while ( ! endofoptionline ) {
	    nextRange = lists.indexOf (',') + 1;

	    if ( debug ) {
		System.out.println("entering " + (counter+1) + " list" ); 
	    }

	    if ( nextRange != 0 ) {
		ltok = lists.substring (0, nextRange-1); 
		lists = lists.substring (nextRange); 
	    } else {
		endofoptionline = true; 
		ltok = lists; 
		lists = null; 
	    }

	    if ( debug ) {
		System.out.println( "current ltok " + ltok ); 
	    }

		
	    ntok = ltok.substring(ltok.indexOf (':') + 1); 


	    if ( debug ) {
		System.out.println( "current ntok " + ntok ); 
	    }

	    begin = Integer.parseInt (ntok.substring(0, ntok.indexOf ('-'))); 

	    if ( debug ) {
		System.out.println( "begin: " + begin ); 
	    }

	    end = Integer.parseInt (ntok.substring(ntok.indexOf ('-') + 1)); 
		
	    if ( debug ) {
		System.out.println( "end: " + end ); 
	    }

	    bounds[2*counter] = begin; 
	    bounds[2*counter + 1] = end; 

	    OUTLEN += (end - begin + 2); 

	    counter++; 
	}
    }

    public static void main(String[] args) {

	String line = null; 

	int offset = 0; 
	int outoffset = 0; 
	int pos = 0; 

	Boolean dottednotation = false; 

	int i; 
	int j; 

	JCut jc = new JCut(); 
	// parse_args ( args ); 
	
	RECLEN = Integer.parseInt ( args[0].substring(2) ); 
	
	String option = args[1].substring(2); 

	jc.parse_lists ( option ); 

	InputStream in = System.in; 


	if ( RECLEN == 0 ) {
	    BufferedReader rd = new BufferedReader(new InputStreamReader(in)); 
	    try {
		line = rd.readLine (); 
	    } catch (IOException ex) {
		ex.printStackTrace();
	    }

	    RECLEN = line.length(); 
	    RECLEN++; // newline!
	}	   

	if ( debug ) {
	    System.out.println("RECLEN: " + RECLEN); 
	}

	ReadableByteChannel rbc = Channels.newChannel(in); 
	ByteBuffer inbuffer = ByteBuffer.allocate ( RECLEN ); 

	WritableByteChannel out = Channels.newChannel ( System.out );
	ByteBuffer outbuffer = null; 

	int begin = 0; 
	int end = 0;
	int blankoffset = 0; 

	
	try {
	    while ( rbc.read ( inbuffer ) != -1 ) {
		byte[] line_read = new byte[OUTLEN]; 
		byte[] junk = new byte[RECLEN]; 
		byte[] line_final = new byte[OUTLEN]; 

		String field = null; 

		//System.out.println("READ: " + offset ); 
		inbuffer.rewind(); 

		offset = 0; 
		outoffset = 0; 

		for ( i = 0; i < counter; i++ ) {
		    begin = bounds[2*i]; 
		    end = bounds[2*i+1]; 

		    //System.out.println("begin: " + begin ); 
		    //System.out.println("end: " + end ); 

		    //byte[] item = null; 

		    try { 
			if ( begin-offset-1 > 0 ) {
			    //item = new byte[begin-offset-1];
			    inbuffer.get (junk, 0, (begin-offset-1)); 
			}

			//item = new byte[end-begin+2]; 
			inbuffer.get (line_read, outoffset, (end-begin+1)); 

			//if ( i < counter - 1 ) {
			//    line_read[outoffset+end-begin+1]='\011';
			//} else {
			//    line_read[outoffset+end-begin+1]='\012';
			//}		       

			outbounds[2*i] = outoffset; 
			outbounds[2*i+1] = outoffset + (end-begin); 

			pos = outoffset; 
			dottednotation = false; 
			blankoffset = 0; 

			while ( pos <= ( outoffset + (end-begin) ) ) {

			    // dot: 

			    if ( line_read[pos] == '\056' ) {
				dottednotation = true; 
			    }

			    // space: 

			    if ( line_read[pos] == '\040' ) {
				blankoffset = pos+1; 
			    }

			    pos++;
			}

			outoffset += (end-begin+1); 


			if ( !dottednotation ) {
			    if ( blankoffset > 0 ) {
				outbounds[2*i] = blankoffset; 
			    }
			}
			    


			//field = new String ( line_read, outoffset, (end-begin+1) ); 
		    } catch ( BufferUnderflowException bufe ) {
			bufe.printStackTrace();
		    }
		    offset = end; 
		}

		outoffset = 0; 

		for ( i = 0; i < counter; i++ ) {
		    begin = outbounds[2*i]; 
		    end = outbounds[2*i+1]; 

		    for ( j = begin; j <= end; j++ ) {
			line_final [outoffset++] = line_read[j]; 
		    }
		    
		    if ( i < counter - 1 ) {
		        line_final[outoffset++]='\011';
		    } else {
		        line_final[outoffset++]='\012';
		    }

		}		

		outbuffer = ByteBuffer.wrap ( line_final, 0, outoffset ); 
		out.write ( outbuffer ); 

		inbuffer.clear(); 
	    }
	} catch (IOException ex) {
	    ex.printStackTrace();
	}

    }
}
