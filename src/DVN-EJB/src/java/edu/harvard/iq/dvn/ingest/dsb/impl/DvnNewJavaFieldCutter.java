/**
 * 
 */
package edu.harvard.iq.dvn.ingest.dsb.impl;

import java.util.*;

import org.apache.commons.lang.builder.*;
import org.apache.commons.lang.*;
import java.util.logging.*;
import java.io.*;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.BufferUnderflowException;

import static java.lang.System.*;

/**
 * @author asone
 * 
 */

public class DvnNewJavaFieldCutter {

    // static

    public int NBUFSIZ = 32767;

    private static int REC_LEN = 0;
    private static int OUT_LEN = 0;

    private static String defaultDelimitor = "\t";

    public static int[] outbounds = new int[8192];

    private static Logger dbgLog =
        Logger.getLogger(DvnNewJavaFieldCutter.class.getPackage().getName());

    public Map<Long, List<List<Integer>>> cargSet =
        new LinkedHashMap<Long, List<List<Integer>>>();

    public int colwidth;

    public int noVars;

    public DvnNewJavaFieldCutter(String list) {
        parseList(list);

    }

    public DvnNewJavaFieldCutter(Map<Long, List<List<Integer>>> cargSet) {
        this.cargSet = cargSet;
        int collength = 0;
        for (Map.Entry<Long, List<List<Integer>>> cargSeti : cargSet.entrySet()){
            for (int i= 0; i < cargSeti.getValue().size(); i++){
                collength = cargSeti.getValue().get(i).get(1) - cargSeti.getValue().get(i).get(0) +2;
                colwidth += collength;
            }
            //out.println("key = "+ cargSeti.getKey() + ":colwidth"+colwidth);
        }
        //out.println("colwidth="+colwidth);
    }


    /**
     * 
     * 
     * @param
     * @return
     */
    public void cutColumns(File fl, int noCardsPerCase, int caseLength,
        String delimitor, String tabFileName) throws FileNotFoundException {

        if (delimitor == null) {
            delimitor = defaultDelimitor;
        }

        int[] lineLength = new int[noCardsPerCase];
        InputStream in = null;
        String line = null;

        // caseLength info is not available
        // check all lines have the same length
        int columnCounter = 0;
        in = new FileInputStream(fl);
        Set<Integer> lengthSet = new LinkedHashSet<Integer>();

        if (caseLength == 0) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(in));
            try {
                for (int l = 0; l < noCardsPerCase; l++) {
                    line = rd.readLine();
                    lengthSet.add(line.length());
                    lineLength[l] = line.length();
                    dbgLog.fine(l + "th line=" + line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {

            }

            if (lengthSet.size() == 1) {
                // all lines have the same length
                columnCounter = lineLength[0];
            } else {
                // TODO
                // temporary solution
                columnCounter = lineLength[0];
            }
            dbgLog.fine("final columnCounter=" + columnCounter);
        }
        // close in
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        REC_LEN = columnCounter;
        REC_LEN++;
        dbgLog.fine("REC_LEN=" + REC_LEN);
        OUT_LEN = colwidth; // calculated by parseList
        dbgLog.fine("out_len=" + OUT_LEN);

        Boolean dottednotation = false;
	Boolean foundData = false; 
	

        // cutting a data file

        InputStream inx = new FileInputStream(fl);
        ReadableByteChannel rbc = Channels.newChannel(inx);
        // input byte-buffer size = row-length + 1(=> new line char)
        ByteBuffer inbuffer = ByteBuffer.allocate(REC_LEN);

        OutputStream outs = new FileOutputStream(tabFileName);
        WritableByteChannel outc = Channels.newChannel(outs);
        ByteBuffer outbuffer = null;

        int pos = 0;
        int offset = 0;
        int outoffset = 0;

        int begin = 0;
        int end = 0;
        int blankoffset = 0;
	int blanktail = 0; 
	int k; 

        try {
            // lc: line counter
            int lc = 0;
            while (rbc.read(inbuffer) != -1) {
                // calculate i-th card number
                lc++;
                k = lc % noCardsPerCase;
                if (k == 0) {
                    k = noCardsPerCase;
                }
                //out.println("***** " +lc+ "-th line, recod k=" + k + " *****");
                byte[] line_read = new byte[OUT_LEN];
                byte[] junk = new byte[REC_LEN];
                byte[] line_final = new byte[OUT_LEN];

                //out.println("READ: " + offset);
                inbuffer.rewind();

                offset = 0;
                outoffset = 0;
                
                // how many variables are cut from this k-th card
                int noColumns = cargSet.get(Long.valueOf(k)).size();

                //out.println("noColumns=" + noColumns);
                //out.println("cargSet k =" + cargSet.get(Long.valueOf(k)));

                for (int i = 0; i < noColumns; i++) {
                    //out.println("**** " + i +"-th col ****");
                    begin = cargSet.get(Long.valueOf(k)).get(i)
                            .get(0); // bounds[2 * i];
                    end = cargSet.get(Long.valueOf(k)).get(i).get(1); // bounds[2 * i + 1];

                    //out.println("i: begin: " + begin + "\ti: end:" + end);

                    try {
                        // throw away offect bytes
                        if (begin - offset - 1 > 0) {
                            inbuffer.get(junk, 0, (begin - offset - 1));
                        }
                        // get requested bytes
                        inbuffer.get(line_read, outoffset, (end - begin + 1));
                        // set outbound data
                        outbounds[2 * i] = outoffset;
                        outbounds[2 * i + 1] = outoffset + (end - begin);
                        // current position moved to outoffset
                        pos = outoffset;

                        dottednotation = false;
			foundData = false; 

                        blankoffset = 0;
			blanktail = 0; 

                        // as position increases
                        while (pos <= (outoffset + (end - begin))) {


                            //out.println("pos=" + pos + "\tline_read[pos]=" +
                            //    new String(line_read).replace("\000", "\052"));
                            
                            // decimal octal
                            // 48 =>0 60
                            // 46 => . 56
                            // 32 = space 40
                            
                            // dot: 
                            if (line_read[pos] == '\056') {
                                dottednotation = true;
                            }

                            // space:
                            if (line_read[pos] == '\040') {
				if ( foundData ) {
				    blanktail = blanktail > 0 ? blanktail : pos - 1; 
				} else {
				    blankoffset = pos + 1;
				}
                            } else {
				foundData = true; 
				blanktail = 0; 
			    }
			   

                            pos++;
                        }
                        // increase the outoffset by width
                        outoffset += (end - begin + 1);
                        // dot false
                        if (!dottednotation) {
                            if (blankoffset > 0) {
                                // set outbound value to blankoffset
                                outbounds[2 * i] = blankoffset;
                            }
			    if (blanktail > 0) {
				outbounds[2 * i + 1] = blanktail; 
			    }
                        }

                    } catch (BufferUnderflowException bufe) {
                        bufe.printStackTrace();
                    }
                    // set offset to the value of end-position
                    offset = end;
                }

                outoffset = 0;
                // for each var
                for (int i = 0; i < noColumns; i++) {
                    begin = outbounds[2 * i];
                    end = outbounds[2 * i + 1];
                    //out.println("begin=" + begin + "\t end=" + end);
                    for (int j = begin; j <= end; j++) {
                        line_final[outoffset++] = line_read[j];
                    }

                    if (i < (noColumns - 1)) {
                        line_final[outoffset++] = '\011'; // tab x09
                    } else {
                        if (k == cargSet.size()) {
                            line_final[outoffset++] = '\012'; // LF x0A
                        } else {
                            line_final[outoffset++] = '\011'; // tab x09
                        }
                    }
                }
                //out.println("line_final=" +
                //    new String(line_final).replace("\000", "\052"));
                outbuffer = ByteBuffer.wrap(line_final, 0, outoffset);
                outc.write(outbuffer);
                inbuffer.clear();

            } // while loop
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    
    
    /**
     * 
     *
     * @param     
     * @return    
     */
    public void parseList(String columns) {
        String[] vars = columns.split(",");
        colwidth = 0;
        for (String v : vars) {
            String[] col = v.split(":");
            String[] be = col[1].split("-");
            Long cardNo = Long.parseLong(col[0]);
            int collength = 0;
            if (cargSet.containsKey(cardNo)) {
                List<Integer> tmp = new ArrayList<Integer>();

                if (be.length == 1) {
                    tmp.add(Integer.parseInt(be[0]));
                    tmp.add(Integer.parseInt(be[0]));
                    collength = 2;
                } else {
                    tmp.add(Integer.parseInt(be[0]));
                    tmp.add(Integer.parseInt(be[1]));
                    collength =
                        Integer.parseInt(be[1]) - Integer.parseInt(be[0]) + 2;
                }

                cargSet.get(cardNo).add(tmp);
            } else {
                List<Integer> tmp = new ArrayList<Integer>();
                List<List<Integer>> lst = new ArrayList<List<Integer>>();
                if (be.length == 1) {
                    tmp.add(Integer.parseInt(be[0]));
                    tmp.add(Integer.parseInt(be[0]));
                    collength = 2;
                } else {
                    tmp.add(Integer.parseInt(be[0]));
                    tmp.add(Integer.parseInt(be[1]));
                    collength =
                        Integer.parseInt(be[1]) - Integer.parseInt(be[0]) + 2;
                }
                lst.add(tmp);
                cargSet.put(cardNo, lst);

            }
            colwidth += collength;
            //out.println("map=" + cargSet);
            //out.println("card no=" + col[0] + "\begin=" + be[0] + "\tend=" + be[1]);
            //out.println("collength=" + collength);

        }
        noVars = vars.length;
        //out.println("no vars=" + noVars);
        //out.println("no of cols required=" + colwidth);
    }


}
