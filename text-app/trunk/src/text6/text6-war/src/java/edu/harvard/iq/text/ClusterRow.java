/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;


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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author ekraffmiller
 */
public class ClusterRow {
        private static final Logger logger = Logger.getLogger(ClusterRow.class.getCanonicalName());

        private String setId;
        Boolean showDocPopup = Boolean.FALSE;
        Boolean showSumPopup = Boolean.FALSE;
        ClusterInfo clusterInfo;
        ArrayList<DocumentRow> documentRows;
        private DataModel rowModel;
        private ListDataModel columnsModel;
        String summary;
        int viewDocumentIndex;

        public ClusterRow(String setId, ClusterInfo clusterInfo, ArrayList<String> summaryFields) {

            this.setId= setId;
            this.clusterInfo = clusterInfo;
            documentRows = new ArrayList<DocumentRow>();
            viewDocumentIndex = 0;  // Show the examplar document first

            for (int i=0;i<clusterInfo.getDocumentList().size();i++) {
                documentRows.add(new DocumentRow(clusterInfo.getDocumentList().get(i),i));

            }
            //
            // Initialize rowModel and columnsModel for cluster Details table
            //
            ArrayList metadata = new ArrayList();
            //
            // If the first summary field is not Title, add a File Name column
            // as the first column of the table.
            // (The first column is a link to the Document Viewer)
            //
            boolean useFilename = !summaryFields.get(0).equals(DocumentSet.TITLE_FIELD);
            for (Document doc : clusterInfo.getDocumentList()) {

                ArrayList<String> values = new ArrayList<String>();
                if (useFilename) {
                    values.add(doc.getFilename());
                }
                for (int i = 0; i < summaryFields.size(); i++) {
                    String val = doc.getMetadata().get(summaryFields.get(i));
                    if (val == null) {
                        val = "";
                    }
                    values.add(val);
                }
                metadata.add(values);
            }

            rowModel = new ListDataModel(metadata);

            ArrayList<String> headings = (ArrayList<String>)summaryFields.clone();
            if (useFilename) {
                headings.add(0, "File Name");
            }
            columnsModel = new ListDataModel(headings);

        }

        public ListDataModel getColumnsModel() {
            return columnsModel;
        }

        public void setColumnsModel(ListDataModel columnsModel) {
            this.columnsModel = columnsModel;
        }

        public DataModel getRowModel() {
            return rowModel;
        }

        public void setRowModel(DataModel rowModel) {
            this.rowModel = rowModel;
        }

        public Object getCellValue() {
            if (rowModel.isRowAvailable() && columnsModel.isRowAvailable()) {
                int col = columnsModel.getRowIndex();

                return ((List) rowModel.getRowData()).get(col).toString();
            }
            return null;
        }




       public ArrayList<DocumentRow> getDocumentRows() {
           return documentRows;
       }
       public int getRandomDocumentIndex() {
           Random ran = new Random();
           return ran.nextInt(clusterInfo.getClusterCount());
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

           viewDocumentIndex = rowModel.getRowIndex();
           showDocPopup=true;  // open the document viewer
       }

       public Document getViewDocument() {
           return clusterInfo.getDocumentList().get(this.viewDocumentIndex);
       }


       public void viewRandom(ActionEvent ae) {
           viewDocumentIndex=getRandomDocumentIndex();
       }
       public void viewFirst(ActionEvent ae) {
           viewDocumentIndex=0;
       }
       public void viewLast(ActionEvent ae) {
           viewDocumentIndex=clusterInfo.getClusterCount()-1;
       }
       public void viewNext(ActionEvent ae) {
           if (viewDocumentIndex<clusterInfo.getClusterCount()) {
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


        public String getViewDocumentPreview() {
            String title = getViewDocument().getTitle();
            if (  title !=null ) {
                return title;
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

       private FileInputStream getViewDocumentInputStream() throws IOException {
           String docRoot = System.getProperty("text.documentRoot");
           File setDir = new File(docRoot, setId);
           File docDir = new File(setDir, "docs");
           File document = new File(docDir, getViewDocument().getFilename());

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

                    File rcFile = createRCFile( clusterFile.getParentFile());
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
               FileUtils.deleteDirectory(clusterFile.getParentFile());

            } catch (java.io.IOException ex) {
                throw new ClusterException("Error reading inputStream from mead.pl process: " + ex.getMessage());

            } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
            }
        }

        private File createRCFile(File parentDir) {
            File rcFile = null;
            File keywordFile = null;
         //   File biasFile = new File(parentDir, "bias.txt");
            try{
                rcFile= new File(parentDir, "mead.rc");
                keywordFile = createKeywordFile(parentDir);

                PrintStream ps = new PrintStream(new FileOutputStream(rcFile));
                ps.println("compression_basis sentences");
                ps.println("compression_absolute 10");
                ps.println("feature QueryPhraseMatch "+ClusterUtil.getMeadDir()+"/bin/feature-scripts/keyword/QueryPhraseMatch.pl -q keywords " + keywordFile.getAbsolutePath());
             //   ps.println("feature LexRank "+ClusterUtil.getMeadDir()+"/bin/feature-scripts/lexrank/LexRank.pl -newbias " + biasFile.getAbsolutePath());
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





                PrintStream ps = new PrintStream(new FileOutputStream(clusterFile));
                ps.println("<?xml version='1.0'?>");
                ps.println("<CLUSTER LANG='ENG'>");
                for (Document doc : clusterInfo.getDocumentList()) {
                    ps.println("<D DID='" + doc.getFilename() + "' />");
                }
                ps.println("</CLUSTER>");
                ps.close();

            } catch (java.io.IOException e) {
                throw new ClusterException("Error creating clusterFile: " + e.getMessage());
            }



            return clusterFile;

        }

    public class DocumentRow {

        Document document;
        int rowIndex;
        DocumentRow( Document doc, int rowIndex) {
            this.document=doc;
            this.rowIndex=rowIndex;
        }
        public int getRowIndex() {
            return rowIndex;
        }

        public Document getDocument() {
            return document;
        }
    }
}

