package edu.harvard.iq.dvn.ingest.dsb.impl;

import java.io.*;
import java.util.logging.*;
import static java.lang.System.*;
import edu.harvard.iq.dvn.ingest.dsb.*;
/**
 */
public class RcutDatasetCutter implements DatasetCutter{

    private static String[] PROGRAM = new String[3];
    private static String RCUT;
    static {
        PROGRAM[0] = "/bin/sh";
        PROGRAM[1] = "-c";

        String rcut_dir = System.getProperty("dvn.dsb.rcut.home");
        if (rcut_dir == null) {
            // temporary test measure later an FileNotFoundException will be
            // thrown
            RCUT = "/usr/local/VDC/bin/rcut";
        } else {
            RCUT = rcut_dir + File.separator + "rcut";
        }
    }

    private static Logger dbgLog = Logger.getLogger(RcutDatasetCutter.class.getPackage().getName());


    /** Set to true to end the loop */
    static boolean done = false;

    public RcutDatasetCutter() {
    }

    String subsetcriteria;
    String infilename;
    String outfilename;

    public RcutDatasetCutter(String subsetcriteria, String infilename) {
        this.subsetcriteria = subsetcriteria;
        this.infilename = infilename;
    }

    public RcutDatasetCutter(String subsetcriteria, String infilename,
        String outfilename) {
        this.subsetcriteria = subsetcriteria;
        this.infilename = infilename;
        this.outfilename = outfilename;
    }

    public void run() {

        final Process p;
        BufferedReader is; // reader for output of process
        String line;

        /*
         * -c must (if !-f) -d optional (default=tab) -s optional -f must (if
         * !-c) -o optional (default=tab) -r optional (default=1) -n optional
         * (default=na) -m optional (default=?) -g optional (default=all)
         */

        // java -cp .: DatasetCutter 1:710-713,1:714-717 inputfilename
        String cmnd = RCUT + " " + subsetcriteria + " < " + infilename;
        if (outfilename != null) {
            PROGRAM[2] = cmnd + " > " + outfilename;
        } else {
            PROGRAM[2] = cmnd;
        }
        dbgLog.fine("command fragment=" + PROGRAM[2]);

        try {
            p = Runtime.getRuntime().exec(PROGRAM);

            Thread waiter = new Thread() {
                @Override
                public void run() {
                    try {
                        p.waitFor();
                    } catch (InterruptedException ex) {
                        // OK, just quit.
                        return;
                    }
                    dbgLog.fine("rcut has been terminated!");
                    done = true;
                }
            };
            waiter.start();

            is = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while (!done && ((line = is.readLine()) != null)) {
                dbgLog.fine(line);
            }
            // Debug.println("exec", "In Main after EOF");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
