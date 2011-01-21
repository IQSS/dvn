package edu.harvard.iq.text;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import edu.harvard.iq.text.ClusterInfo.DocInfo;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.apache.commons.io.FileUtils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ekraffmiller
 */
public class ClusteringSpacePage {
    private static final Logger logger = Logger.getLogger(ClusteringSpacePage.class.getCanonicalName());

    private String setId;
    private Double xCoord;
    private Double yCoord;
    
    private DocumentSet documentSet;
    private ClusterSolution clusterSolution;
    private String clusterLabelParam;
    private String solutionLabelParam;  //
    
    private ArrayList<ClusterSolution> savedSolutions = new ArrayList<ClusterSolution>();
    private Integer clusterNum;
    private HtmlDataTable clusterTable;
    private ArrayList<ClusterRow> clusterTableModel = new ArrayList<ClusterRow>();
    private int solutionIndex;  // This is passed from the form to indicate that we need to display a saved solution rather than calculate a new solution
    private Boolean discoverable = Boolean.FALSE;

    public String getHost() {
        try {
        return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new ClusterException(e.getMessage());
        }

    }

    public Boolean getDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }  

    /** Creates a new instance of ClusterViewPage */
    public ClusteringSpacePage() {
      
    }

    @PostConstruct
    public void init() {
        logger.fine("initializing page");

        // Initialize inputs to Cluster calculation

        if (xCoord==null) {
        xCoord = new Double(0);
        }
        if (yCoord==null) {
            yCoord = new Double(0);
        } if (clusterNum==null) {
            clusterNum = 5;
        }
        if (setId==null) {
            throw new ClusterException("missing setId parameter.");
        }
        documentSet = new DocumentSet(setId);
        solutionIndex=-1;

        // Do calculation

        calculateClusterSolution(true);

        // Update cluster solution with labels
        // from request, if necessary

        clusterSolution.setLabel(solutionLabelParam);
        if (clusterLabelParam!=null) {
            clusterSolution.initClusterLabels(clusterLabelParam);
        }

        // If we have labels for this solution, added to the saved
        // solutions list

        if (clusterLabelParam!=null || solutionLabelParam !=null) {
            saveSolution(clusterSolution);
        }
    }



  
    // This is called either when the user clicks a point on the map,
    // or is browsing thru the history of points on the map.
    public void updateClusterSolutionListener(ActionEvent ae) {
        if (solutionIndex < 0) {
            // This is not a saved solution, so calculate it.
            calculateClusterSolution(true);
        } else {

            // the request is for a saved solution, so update the page
            // with the saved solution data.
            clusterSolution = savedSolutions.get(solutionIndex);
            populateClusterTableModel();
        }
    }

   
    /**
     *  This is called when the user wants to get a new solution based on the
     * existing point, but a different cluster number
     */
    public void changeClusterNumberListener(ActionEvent ae) {
        calculateClusterSolution(false);
    }




    private void calculateClusterSolution(boolean newPoint) {
        try {
            if (newPoint) {
                // calculate a new solution from scratch
                if (discoverable) {
                    clusterSolution = new ClusterSolution(documentSet, xCoord, yCoord);
                } else {
                    clusterSolution = new ClusterSolution(documentSet, xCoord, yCoord, clusterNum);
                }
            } else {
                // Calculate solution based on the existing solution,
                // and the new clusterNum (or discoverable)
                if (discoverable) {
                    clusterSolution = new ClusterSolution(clusterSolution);
                } else {
                    clusterSolution = new ClusterSolution(clusterSolution, clusterNum);
                }
            }
        } catch (Exception e) {
            DecimalFormat form = new DecimalFormat("#.#####");
            String errMessage =  "Could not calculate solution for ("
                    + form.format(this.xCoord) + "," + form.format(this.yCoord) + "), number of clusters = " + this.clusterNum+".";
            logger.warning(errMessage);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Clustering Error- ", errMessage+" Please try fewer clusters or a point closer to the convex hull."));
        }

        populateClusterTableModel();
       
    }
    
    
    public void saveClusterLabel(ActionEvent ae) {
        saveSolution(clusterSolution);
    }


    public void saveSolutionLabel(ActionEvent ae) {
           saveSolution(clusterSolution);
    }

    private void saveSolution(ClusterSolution cs) {
        if (!savedSolutions.contains(cs)) {
               // The id corresponds to the index in the savedSolutions list
               cs.setId(savedSolutions.size());
               savedSolutions.add(cs);
           }
    }
    public int getSolutionIndex() {
        return solutionIndex;
    }

    public void setSolutionIndex(int solutionIndex) {
        this.solutionIndex = solutionIndex;
    }


    public ArrayList<ClusterSolution> getSavedSolutions() {
        return savedSolutions;
    }

    public void setSavedSolutions(ArrayList<ClusterSolution> savedSolutions) {
        this.savedSolutions = savedSolutions;
    }

    
    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public Double getxCoord() {
        return xCoord;
    }

    public void setxCoord(Double xCoord) {
        this.xCoord = xCoord;
    }

    public Double getyCoord() {
        return yCoord;
    }

    public void setyCoord(Double yCoord) {
        this.yCoord = yCoord;
    }

    public String getClusterLabelParam() {
        return clusterLabelParam;
    }

    public void setClusterLabelParam(String clusterLabelParam) {
        this.clusterLabelParam = clusterLabelParam;
    }

    public String getSolutionLabelParam() {
        return solutionLabelParam;
    }

    public void setSolutionLabelParam(String solutionLabelParam) {
        this.solutionLabelParam = solutionLabelParam;
    }

    

    public ClusterSolution getClusterSolution() {
        return clusterSolution;
    }

    public int getDocumentCount() {
        return documentSet.getDocumentCount();
    }

    // Run this whenever the ClusterSolution changes
    private void populateClusterTableModel() {
        clusterTableModel.clear();
        for (ClusterInfo ci: clusterSolution.getClusterInfoList()) {
            clusterTableModel.add(new ClusterRow(ci));
        }
      
    }

    public Integer getClusterNum() {
        return clusterNum;
    }

    public void setClusterNum(Integer newClusterNum) {
       
            this.clusterNum = newClusterNum;
           
         
    }

    public HtmlDataTable getClusterTable() {
        return clusterTable;
    }

    public void setClusterTable(HtmlDataTable clusterTable) {
        this.clusterTable = clusterTable;
    }

    public ArrayList<ClusterRow> getClusterTableModel() {
        return clusterTableModel;
    }

    public void setClusterTableModel(ArrayList<ClusterRow> clusterTableModel) {
        this.clusterTableModel = clusterTableModel;
    }



    public class ClusterRow {
       
        PanelTabSet panelTabSet;
        Boolean showDocPopup = Boolean.FALSE;
        Boolean showSumPopup = Boolean.FALSE;
        ClusterInfo clusterInfo;
        String summary;
        int viewDocumentIndex;

        public ClusterRow(ClusterInfo clusterInfo) {          
            this.clusterInfo = clusterInfo;
            viewDocumentIndex = 0;  // Show the examplar document first
        }
       

        public PanelTabSet getPanelTabSet() {
            return panelTabSet;
        }

        public void setPanelTabSet(PanelTabSet panelTabSet) {
            this.panelTabSet = panelTabSet;
        }

       public int getRandomDocumentIndex() {
           Random ran = new Random();
           return ran.nextInt(clusterInfo.getFileIndices().size());
       }

       public int getViewDocumentIndex() {
           return this.viewDocumentIndex;
       }

       public void setViewDocumentIndex(int i) {
           viewDocumentIndex = i;
       }

       /**
        * ActionListener that is called when user clicks on a document in the ViewList tab
        * @param ae
        */
       public void viewDocumentInList(ActionEvent ae) {
           UIComponent comp = ae.getComponent().getParent();
           while(!(comp instanceof HtmlDataTable)) {
               comp = comp.getParent();
           }
           HtmlDataTable table = (HtmlDataTable)comp;
           comp = comp.getParent();
           while(!(comp instanceof PanelTabSet)) {
               comp = comp.getParent();
           }
           PanelTabSet tabSet = (PanelTabSet)comp;
           viewDocumentIndex = table.getRowIndex();
           tabSet.setSelectedIndex(0);  // Select the Document tab
       }


       public void viewRandom(ActionEvent ae) {
           viewDocumentIndex=getRandomDocumentIndex();
       }
       public void viewFirst(ActionEvent ae) {
           viewDocumentIndex=0;
       }
       public void viewLast(ActionEvent ae) {
           viewDocumentIndex=clusterInfo.getFileIndices().size()-1;
       }
       public void viewNext(ActionEvent ae) {
           if (viewDocumentIndex<clusterInfo.getFileIndices().size()) {
                viewDocumentIndex++;
           }
       }

       public void viewPrevious(ActionEvent ae) {
           if (viewDocumentIndex>0) {
                viewDocumentIndex--;
           }
       }

       public void openDocPopup(ActionEvent ae) {
           
            showDocPopup = Boolean.TRUE;

        }

        public void closeDocPopup(ActionEvent ae) {
            showDocPopup=Boolean.FALSE;
        }

        public Boolean getShowDocPopup() {
            return showDocPopup;
        }

        public void setShowDocPopup(Boolean showPopup) {
            this.showDocPopup = showPopup;
        }

        public void openSumPopup(ActionEvent ae) {
            if (summary == null) {
                generateSummary();
            }
            showSumPopup = Boolean.TRUE;
        }

        public void closeSumPopup(ActionEvent ae) {
            showSumPopup=Boolean.FALSE;
        }

        public Boolean getShowSumPopup() {
            return showSumPopup;
        }

        public void setShowSumPopup(Boolean showPopup) {
            this.showSumPopup = showPopup;
        }
        public String getViewDocumentName() {
            String docName = clusterInfo.getDocInfoList().get(this.viewDocumentIndex).getDocId();
            if (setId.equals("1")) {
                    docName += "Bush02.txt";
            }
            return  docName;
        }

        public String getViewDocumentPreview() {
            if (  clusterInfo.getDocInfoList().get(this.viewDocumentIndex).title !=null ) {
                return clusterInfo.getDocInfoList().get(this.viewDocumentIndex).title;
            } else {
                int previewLength = 300;
                String temp;
                String preview = null;
                byte[] byteArray = new byte[previewLength];
                try {
                    FileInputStream fin = getViewDocumentInputStream();
                    BufferedInputStream bis = new BufferedInputStream(fin);
                    bis.read(byteArray,0, previewLength);
                    temp = new String(byteArray, "utf-8");
                    // remove leading white space
                    temp = temp.trim();
                    // remove everything after the carriage return
                    int retIndex = temp.indexOf("\n");
                    if (retIndex !=-1) {
                        preview = temp.substring(0, retIndex);
                    }
                } catch (IOException e) {
                    throw new ClusterException(e.getMessage());
                }
            return preview;
            }
        }

       public String getViewDocumentText() {

           ByteArrayOutputStream baos = new ByteArrayOutputStream();

           try {
               FileInputStream fin = getViewDocumentInputStream();
               BufferedInputStream bis = new BufferedInputStream(fin);

               // Now read the buffered stream.
               while (bis.available() > 0) {
                   baos.write(bis.read());                   
               }
               fin.close();
           } catch (IOException e) {
               throw new ClusterException(e.getMessage());
           }
           return baos.toString();

       }
       // This is just a dummy setter so we can use viewDocumentText
       // as the value of an inputTextarea component.
       public void setViewDocumentText(String s) {

       }
       private FileInputStream getViewDocumentInputStream() throws IOException {
           String docRoot = System.getProperty("text.documentRoot");
           File setDir = new File(docRoot, setId);
           File docDir = new File(setDir, "docs");
           File document = new File(docDir, getViewDocumentName());

           return new FileInputStream(document);

       }

        public ClusterInfo getClusterInfo() {
            return clusterInfo;
        }

        public void setClusterInfo(ClusterInfo clusterInfo) {
            this.clusterInfo = clusterInfo;
        }

        public String getSummary() {
            
            return summary;
        }

        private String getDocsentDir()
        {
            return ClusterUtil.getDocRoot() + "/" + setId + "/docs/docsent";
        }

        public boolean getSummaryAvailable() {
            return new File(getDocsentDir()).exists();
        }

        private void generateSummary() {
            boolean useKeywords=true;
            try {
                // Create temp Cluster file
                File clusterFile = createMeadCluster();

                // remove ".cluster" extension from name of clusterFile
                // for mead clusterName parameter.
                String clusterName = clusterFile.getName().substring(0, clusterFile.getName().indexOf(".cluster"));
                ArrayList<String> cmdArray = new ArrayList<String>();

                // call mead with the cluster directory and docsent directory
                String cmd = ClusterUtil.getMeadDir()+"/bin/mead.pl -cluster_dir " + clusterFile.getParent()
                        + " -docsent_dir " + getDocsentDir() +" "
                     //   + " -sentences -absolute 10 "
                        ;
                cmdArray.add(ClusterUtil.getMeadDir()+"/bin/mead.pl");
                cmdArray.add("-cluster_dir");
                cmdArray.add(clusterFile.getParent());
                cmdArray.add("-docsent_dir");
                cmdArray.add(getDocsentDir());
              //  cmdArray.add("-sentences");
              //  cmdArray.add("-absolute");
              //  cmdArray.add("10");


                if (useKeywords) {
                    File keywordFile = createKeywordFile(clusterFile.getParentFile());
                    File rcFile = createRCFile(keywordFile.getAbsolutePath(), clusterFile.getParentFile());
                    /*
                    cmd += " -feature QueryPhraseMatch '"+ClusterUtil.getMeadDir()+"/bin/feature-scripts/keyword/QueryPhraseMatch.pl "
                                +"-q keywords "+keywordFile.getAbsolutePath()+"' ";
                    cmdArray.add("-feature");
                    cmdArray.add("QueryPhraseMatch");
                    cmdArray.add("'"+ClusterUtil.getMeadDir()+"/bin/feature-scripts/keyword/QueryPhraseMatch.pl "
                                +"-q keywords "+keywordFile.getAbsolutePath()+"'");
                     *
                     */
                    cmd += " -rc "+ rcFile.getAbsolutePath();
                    cmdArray.add(" -rc "+ rcFile.getAbsolutePath());
                }
                cmd += " "+ clusterName;
                cmdArray.add(clusterName);
                logger.fine("mead command: "+cmd);                
                Runtime r = Runtime.getRuntime();
                String[] cmdStrings = new String[cmdArray.size()];
                for (int i=0;i<cmdStrings.length; i++) {
                    cmdStrings[i] = cmdArray.get(i);
                }
              //  Process p = r.exec(cmdStrings);
                Process p = r.exec(cmd);
              //  ProcessBuilder pb = new ProcessBuilder(cmd);
              //  logger.fine(pb.command().toString());
             //   Process p = pb.start();
                int exitValue = p.waitFor();


                // read result into summary string
                InputStream is = p.getInputStream();
                InputStream err = p.getErrorStream();
                String line;
                InputStreamReader esr = new InputStreamReader(err);
                BufferedReader ebr = new BufferedReader(esr);

                //
                // mead sends informational messages, as well as
                // error messages to errorstream.
                while ((line = ebr.readLine()) != null) {
                    if (exitValue==0) {
                        logger.fine(line);
                    } else {
                        logger.severe(line);
                    }
                }

                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);


                summary = "";
                while ((line = br.readLine()) != null) {
                    summary += line + "\n";
                }
                // delete temp directory and files
              //  FileUtils.deleteDirectory(clusterFile.getParentFile());

            } catch (java.io.IOException ex) {
                throw new ClusterException("Error reading inputStream from mead.pl process: " + ex.getMessage());
            
            } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
            }
        }

        private File createRCFile(String keywordFilePath, File parentDir) {
            File rcFile = null;
            try{
                rcFile= new File(parentDir, "mead.rc");
                PrintStream ps = new PrintStream(new FileOutputStream(rcFile));
                ps.println("compression_basis sentences");
                ps.println("compression_absolute 10");
                ps.println("feature QueryPhraseMatch "+ClusterUtil.getMeadDir()+"/bin/feature-scripts/keyword/QueryPhraseMatch.pl -q keywords " + keywordFilePath);
                ps.close();
            } catch (java.io.IOException e) {
                throw new ClusterException(e.getMessage());

            }

            return rcFile;
        }
        private File createKeywordFile(File parentDir) {
            File keywordFile =null;
            try {
                keywordFile= new File(parentDir, "keywords.xml");
                PrintStream ps = new PrintStream(new FileOutputStream(keywordFile));
                ps.println("<?xml version='1.0'?>");
                ps.println("<QUERY QID='KF' QNO='1' TRANSLATED='NO'>");
                ps.println("<TITLE>");
                ps.println("</TITLE>");
                ps.println("<NARRATIVE>");
                ps.println("</NARRATIVE>");
                ps.println("<DESCRIPTION>");
                ps.println("</DESCRIPTION>");
                ps.println("<KEYWORDS>");
                String keywords = "";
                for (int i=0;i<10;i++) {
                    keywords+="\\b"+clusterInfo.getWordList().get(i).title+"\\b;1;";
                }
                ps.println(keywords);
                ps.println("</KEYWORDS>");
                ps.println("</QUERY>");
                ps.close();
            } catch (java.io.IOException e) {
                throw new ClusterException(e.getMessage());

            }
            return keywordFile;

        }

        private File createMeadCluster() {
            File clusterFile;
            File clusterDir;
            try {
                // Create a tmpdir to put the file in.
                // (mead only allows us to specify the
                // directory that contains the cluster file,
                // not the cluster file itself.)
                // First create a file to get a unique path name,
                // than delete it and create a directory
                // of that name.

               clusterDir = File.createTempFile("mead", "tmp");
              if (!(clusterDir.delete())) {
                    throw new ClusterException("Could not delete temp file: " + clusterDir.getAbsolutePath());
                }

                if (!(clusterDir.mkdir())) {
                    throw new ClusterException("Could not create temp directory: " + clusterDir.getAbsolutePath());
                }

                // Now create a cluster file within the temp directory
                clusterFile = new File(clusterDir, "temp.cluster");




                // Annoying: We can't use the XMLStreamWriter
                // because mead.pl won't accept standard XML.
                // It requires a carriage return before the <CLUSTER> element

                /*
                // Write the XML contents to the file
                XMLStreamWriter xmlw = null;
                try {
                XMLOutputFactory xmlOutputFactory = javax.xml.stream.XMLOutputFactory.newInstance();


                xmlw = xmlOutputFactory.createXMLStreamWriter(new FileOutputStream(clusterFile));
                xmlw.writeStartDocument();
                xmlw.writeStartElement("CLUSTER");
                xmlw.writeAttribute("LANG", "ENG" );
                for ( DocInfo doc : this.docInfoList) {
                xmlw.writeStartElement("D");
                xmlw.writeAttribute("DID", doc.docId);
                xmlw.writeEndElement();
                }
                xmlw.writeEndElement(); // cluster
                xmlw.writeEndDocument();
                } catch (Exception ex) {
                throw new ClusterException("ERROR occurred writing cluster file: "+ex.getMessage());
                }finally {
                try {
                if (xmlw != null) {
                xmlw.close();
                }
                } catch (XMLStreamException ex) {
                }

                 */
                PrintStream ps = new PrintStream(new FileOutputStream(clusterFile));
                ps.println("<?xml version='1.0'?>");
                ps.println("<CLUSTER LANG='ENG'>");
                for (DocInfo doc : clusterInfo.getDocInfoList()) {
                    ps.println("<D DID='" + doc.docId + "' />");
                }
                ps.println("</CLUSTER>");
                ps.close();

            } catch (java.io.IOException e) {
                throw new ClusterException("Error creating clusterFile: " + e.getMessage());
            }



            return clusterFile;

        }
       

      

    }

    
}
