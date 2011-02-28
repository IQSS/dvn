/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;

import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author ekraffmiller
 */
public class DocumentSet {
    private static final Logger logger = Logger.getLogger(DocumentSet.class.getCanonicalName());
    private String setId;
    private File setDir;
    
    private String description;
    private ArrayList<String> summaryFields;  // Metadata fields that will appear in Document details table
    private MethodPoint[] methodPoints;
    private int[][] clusterMembership;   //
    private int[][] wordDocumentMatrix;  // word rows and doc columns (each cell is word count)
    private ArrayList<String> wordList;  // all words in the set (column headers in WordDocumentMatrix)
    private ArrayList<String> matrixIds; // DocumentID's in the order they appear in WordDocumentMatrix and ClusterMembershipMatrix
    private HashMap<String, Document> documents;  // list of Documents, mapped to their DocumentIDs
    private final static String POLYGON_FILE = "polygon.xml";
    private final static String METHOD_POINTS_FILE = "MethodPoints.csv";
    private final static String CLUSTER_MEMBERSHIP_FILE = "ClusterMembershipMatrix.csv";
    private final static String WORD_DOC_MATRIX_FILE = "WordDocumentMatrix.csv";
    private final static String FILELIST_FILE = "FileList.csv";
    private final static String METADATA_FILE = "Metadata.csv";
    private final static String SUMMARY_FIELDS_FILE = "SummaryFields.csv";
    private final static String DOCUMENT_ID_FIELD = "DocumentID";

    public DocumentSet(String setId) {
        this.setId=setId;
        initializeSet();
        
    }

    
   
   


   public Document getDocumentByIndex(int index) {
       return documents.get(matrixIds.get(index));
   }

    public ArrayList<String> getSummaryFields() {
        return summaryFields;
    }

    public void setSummaryFields(ArrayList<String> summaryFields) {
        this.summaryFields = summaryFields;
    }
  


    public int[][] getClusterMembership() {

        return clusterMembership;
    }

    public void setClusterMembership(int[][] clusterMembership) {
        this.clusterMembership = clusterMembership;
    }

    public MethodPoint[] getMethodPoints() {
        return methodPoints;
    }

    public void setMethodPoints(MethodPoint[] methodPoints) {
        this.methodPoints = methodPoints;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int[][] getWordDocumentMatrix() {
        return wordDocumentMatrix;
    }

    public void setWordDocumentMatrix(int[][] wordDocumentMatrix) {
        this.wordDocumentMatrix = wordDocumentMatrix;
    }

    public ArrayList<String> getWords() {
        return wordList;

    }
    public int getDocumentCount() {
        return wordDocumentMatrix.length;
    }
    private void initializeSet() {
        setDir = ClusterUtil.getSetDir(setId);
        if (!setDir.exists()) {
            throw new ClusterException("setDir not found");
        }
        initDescription();
        initSummaryFields();
        initMethodPointsAndPolygon();
        initClusterMembership();
        initWordDocumentMatrix();
        boolean uploadMetadata = true;
        initDocuments(uploadMetadata);

        logger.fine("complete initializeSet");
        
       
            for (int i=0;i< matrixIds.size(); i++) {
                logger.fine( i + "\t\t "+documents.get(matrixIds.get(i)));
            }
        
    }

    private void initDocuments(boolean uploadMetadata) {
        documents = new HashMap<String, Document>();
        if (uploadMetadata) {
            try {
                //
                // Read file list - populate Document Hashmap
                //
                File fileListFile = new File(setDir, this.FILELIST_FILE);
                if (!fileListFile.exists()) {
                    throw new ClusterException(fileListFile.getAbsolutePath() + " not found.");
                }
                BufferedReader br = new BufferedReader(new FileReader(fileListFile));
                String strLine;
                StringTokenizer st;
                while ((strLine = br.readLine()) != null) {
                    st = new StringTokenizer(strLine, ",");
                    Document doc = new Document(this);
                    doc.setId(st.nextToken());
                    doc.setFilename(st.nextToken());
                    this.documents.put(doc.getId(), doc);
                }
                //
                // read metadata - update Document Hashmap.
                // if  id in metadata doesn't exist, throw exception
                //
                 File metadataFile = new File(setDir, this.METADATA_FILE);
                if (!metadataFile.exists()) {
                    throw new ClusterException(metadataFile.getAbsolutePath() + " not found.");
                }
                br = new BufferedReader(new FileReader(metadataFile));

                // read headers - they are the metadata field names
                //
                st = new StringTokenizer(br.readLine(),",");             
                ArrayList<String> metadataFields = new ArrayList<String>();
                while(st.hasMoreTokens()) {
                    metadataFields.add(st.nextToken());
                }
                // One of the field names must refer to the document ID
                if (!metadataFields.contains(DOCUMENT_ID_FIELD)) {
                    throw new ClusterException("Error reading "+ METADATA_FILE+ ", missing "+this.DOCUMENT_ID_FIELD+ " column");
                }
                //
                // Read metadata values

                while ((strLine = br.readLine()) != null) {
                    LinkedHashMap<String, String> values = new LinkedHashMap();
                    st = new StringTokenizer(strLine,",");
                    int col = 0;
                    String docId = null;
                    while(st.hasMoreTokens()) {
                        
                        String value = st.nextToken();
                        if (metadataFields.get(col).equals(DOCUMENT_ID_FIELD)) {
                            docId = value;
                        } else {
                            values.put(metadataFields.get(col),value);
                        }
                        col++;
                    }
    
                    Document doc = this.documents.get(docId);
                    if (doc==null) {
                        throw new ClusterException("Error reading "+ this.METADATA_FILE+", unknown id: "+ docId);
                    }
                    if (doc.getMetadata()!=null && doc.getMetadata().size()>0) {
                        throw new ClusterException("Error reading "+this.METADATA_FILE+", duplicate id: "+ docId);
                    }
                    doc.setMetadata(values);

                }
                //
                // Test that all ids in matrixIds table exist in the documents Hashmap
                //
                for (String id: matrixIds) {
                    if (documents.get(id)==null) {
                        throw new ClusterException("Error loading metadata and fileList - no data found for document id: "+id);
                    }
                }
            } catch (IOException ex) {
                throw new ClusterException(ex.getMessage());
            }

          

            // load docs into mongo db!
        } else {
            // get docs from mongo db!
        }

    }
    
    private void initDescription() {
        description = "";
        File desc = new File(setDir, "description.txt");
        if (desc.exists()) {
            byte[] buffer = new byte[(int) desc.length()];
            BufferedInputStream bi = null;
            try {
                bi = new BufferedInputStream(new FileInputStream(desc));
                bi.read(buffer);
            } catch (IOException ignored) {
            } finally {
                if (bi != null) {
                    try {
                        bi.close();
                    } catch (IOException ignored) {
                    }
                }
            }
            description = new String(buffer);
        }
    }

    private void initClusterMembership() {
        ArrayList<ArrayList> table = new ArrayList<ArrayList>();
        
        File clusterMembershipFile = new File(setDir, this.CLUSTER_MEMBERSHIP_FILE );
        if ( !clusterMembershipFile.exists()) {
            throw new ClusterException(clusterMembershipFile.getAbsolutePath() + " not found.");
        }
          try {
            BufferedReader br = new BufferedReader(new FileReader(clusterMembershipFile));
            String strLine = "";
            StringTokenizer st = null;
            int lineNumber = 0;
          

            String tokenSeparator;
            if (setId.equals("3") ) {
                tokenSeparator=" ";
            } else if (setId.equals("1")) {
                tokenSeparator="\t";
            } else {
                tokenSeparator = ",";
            }
            // populate matrixIds from the column headers
            matrixIds = new ArrayList<String>();
            strLine = br.readLine();
            st = new StringTokenizer(strLine, tokenSeparator);
            while(st.hasMoreTokens()) {
                matrixIds.add(st.nextToken());
            }

            // Calculating this just for test purposes
            int maxClusters = 0;
            String maxClusterMethod = "";

            //read tab separated file line by line
            while ((strLine = br.readLine()) != null) {
                 Set uniqueClusters = new HashSet();
                 String clusterMethod="";
                //break tab separated line using "\t"
                st = new StringTokenizer(strLine, tokenSeparator);

                // first token is method name
                if (!setId.equals("3") ) {
                    clusterMethod = st.nextToken();
                }

                // create object for holding this row's values
                ArrayList<Integer> row = new ArrayList<Integer>();

                while(st.hasMoreTokens()) {
                    Integer clusterNumber = new Integer(st.nextToken());
                    row.add(clusterNumber);
                    uniqueClusters.add(clusterNumber);
                }

                // we are at the end of the row so add it to the table
                table.add(row);
                
                // Get the number of unique clusters in this row, and add it to the MethodPoint object
                // for this method.

                methodPoints[lineNumber].numberOfClusters = uniqueClusters.size();
                logger.fine("numberOfClusters = "+methodPoints[lineNumber].numberOfClusters+", method="+ clusterMethod);
                // This is just for testing
                
                if (uniqueClusters.size() > maxClusters) {
                    maxClusters = uniqueClusters.size();
                    maxClusterMethod = clusterMethod;
                }

                lineNumber++;
            }
            logger.fine("maxClusters = "+maxClusters+", method="+ maxClusterMethod);

            // Convert our List object to two-dim array
            this.clusterMembership = new int[table.size()][];
                for (int i=0; i< table.size(); i++) {
                    clusterMembership[i] = new int[table.get(i).size()];
                    for (int j=0; j<table.get(i).size(); j++) {
                        clusterMembership[i][j] = (Integer)table.get(i).get(j);
                    }
                }
          } catch (java.io.IOException ex) {
            throw new ClusterException(ex.getMessage());
        }
        logger.fine("ClusterMembership, methodSize = "+table.size()+" docSize = "+table.get(0).size());
    }

    private void initSummaryFields() {
        summaryFields = new ArrayList<String>();
         File sumFile = new File(setDir, this.SUMMARY_FIELDS_FILE);
        if ( sumFile.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(sumFile));
               //read the file line by line
                String strLine =null;
                while ((strLine = br.readLine()) != null) {
                    summaryFields.add(strLine);
                }
            }  catch (java.io.IOException ex) {
                throw new ClusterException(ex.getMessage());
            }
        }
        logger.fine("SummaryFields, size = "+this.summaryFields.size());
    }
 
    private void initWordDocumentMatrix(){
        ArrayList<ArrayList> table = new ArrayList<ArrayList>();
        wordList = new ArrayList<String>();
      
        File wordDocumentFile = new File(setDir, this.WORD_DOC_MATRIX_FILE );
        if ( !wordDocumentFile.exists()) {
            throw new ClusterException(wordDocumentFile.getAbsolutePath() + " not found.");
        }
          try {
            String tokenSeparator;
            if (setId.equals("3") ) {
                tokenSeparator=" ";
            } else if (setId.equals("1")){
                tokenSeparator="\t";
            } else {
                tokenSeparator=",";
            }

            BufferedReader br = new BufferedReader(new FileReader(wordDocumentFile));
            String strLine = "";
            StringTokenizer st = null; 
            

            // line 1 contains headers (ie word list), so save them in separate list
            strLine = br.readLine();
            wordList = new ArrayList<String>();
            st = new StringTokenizer(strLine, tokenSeparator);
            while (st.hasMoreTokens() ) {
                wordList.add(st.nextToken());
            }
           

            //read the rest of the tab separated file line by line
            while ((strLine = br.readLine()) != null) {
               

                //break tab separated line using "\t"
                st = new StringTokenizer(strLine, tokenSeparator);

                // first token is documentId, so add it to our list
                String documentId = st.nextToken();
                if (documentId.contains("\\")) {
                    // If this id is a path, get the file name
                    documentId = documentId.substring(documentId.lastIndexOf("\\")+1);

                }
               

                // create object for holding this row's values
                ArrayList<Integer> row = new ArrayList<Integer>();

                while(st.hasMoreTokens()) {
                    row.add(new Integer(st.nextToken()));
                }

                // we are at the end of the row so add it to the table
                table.add(row);

            }
            // Convert our List object to two-dim array
            this.wordDocumentMatrix = new int[table.size()][];
                for (int i=0; i< table.size(); i++) {
                    wordDocumentMatrix[i] = new int[table.get(i).size()];
                    for (int j=0; j<table.get(i).size(); j++) {
                        wordDocumentMatrix[i][j] = (Integer)table.get(i).get(j);
                    }
                }
          } catch (java.io.IOException ex) {
            throw new ClusterException(ex.getMessage());
        }
        logger.fine("WordDocumentMatrix, docSize = "+wordDocumentMatrix.length+" wordSize = "+table.get(0).size());

    }

    /**
     *  Read methodPoints.txt, and if necessary, create polygon.xml
     */
    private void initMethodPointsAndPolygon() {
        logger.fine("initializingConvexHull: SetId is " + setId + "!");
        ConvexHull ch;
        // Read methodpoints.text:

        // look for subfolder matching setId
        

        // look for MethodPoints.txt
        // if not found throw text exception
        File methodPoints = new File(setDir, METHOD_POINTS_FILE);
        if (!methodPoints.exists()) {
            throw new ClusterException(methodPoints.getAbsolutePath() + " not found.");
        }


        CoordinateList methodCoords = readMethodPoints(methodPoints);

        File polygonFile = new File(setDir, POLYGON_FILE);
        if (!polygonFile.exists()) {
            // get the ConvexHull polygon for these method coordinates
            ConvexHull convexHull = new ConvexHull(methodCoords.toCoordinateArray(), new GeometryFactory());
            Polygon polygon = (Polygon) convexHull.getConvexHull();
            //  Create OpenLayers Polygon XML document with polygon coordinates
            writePolygonXMLFile(polygon);
        }

    }

    private void writePolygonXMLFile(Polygon polygon) {
        FileOutputStream fos = null;
        try {
            // First convert the coordinates to a string that we can insert into the XML doc.
            Coordinate[] coords = polygon.getCoordinates();
            String coordStr = "";
            for (int i = 0; i < coords.length; i++) {
                coordStr += " " + coords[i].x + "," + coords[i].y;
            }
            logger.fine("coordStr = " + coordStr);

            XMLOutputFactory xmlOutputFactory = javax.xml.stream.XMLOutputFactory.newInstance();
            fos = new FileOutputStream(new File(ClusterUtil.getSetDir(setId), POLYGON_FILE));
            XMLStreamWriter xmlw = xmlOutputFactory.createXMLStreamWriter(fos);


            xmlw.writeStartDocument();
            xmlw.writeStartElement("wfs:FeatureCollection");
            xmlw.writeNamespace("wfs", "http://mapserver.gis.umn.edu/mapserver");
            xmlw.writeNamespace("gml", "http://www.opengis.net/gml");
            xmlw.writeStartElement("gml:featureMember");
            xmlw.writeStartElement("gml:Polygon");
            xmlw.writeStartElement("gml:LinearRing");
            xmlw.writeStartElement("gml:coordinates");
            xmlw.writeCharacters(coordStr);
            xmlw.writeEndElement();  //coordinates
            xmlw.writeEndElement(); //LinearRing
            xmlw.writeEndElement(); //Polygon
            xmlw.writeEndElement(); //featureMemeber
            xmlw.writeEndElement(); //featureCollection
            xmlw.writeEndDocument();
            xmlw.flush();
            xmlw.close();
        } catch (XMLStreamException e) {
            throw new ClusterException("Error creating "+POLYGON_FILE+": " + e.getMessage());
        } catch (IOException e) {
            throw new ClusterException("Error creating "+POLYGON_FILE+": " + e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                }
            }
        }



    }

   

    private CoordinateList readMethodPoints(File strFile) {

        CoordinateList methodCoords = new CoordinateList();
        //create BufferedReader to read csv file
        try {
            BufferedReader br = new BufferedReader(new FileReader(strFile));
            String strLine = "";
            StringTokenizer st = null;
        
            ArrayList<MethodPoint> methodPointList = new ArrayList<MethodPoint>();
            //read comma separated file line by line
            String tokenSeparator =",";
           
            logger.fine("reading method points");
            while ((strLine = br.readLine()) != null) {
                st = new StringTokenizer(strLine, tokenSeparator);

                // first token is method name
                String method = st.nextToken();

                String v1 = st.nextToken();  // we assume this is x
                logger.fine("v1 = " + v1);
                double x = new Double(v1).doubleValue();

                String v2 = st.nextToken();  // we assume this is y
                logger.fine("v2 = " + v2);
                double y = new Double(v2).doubleValue();

                Coordinate methodCoord = new Coordinate(x, y);
                logger.fine("methodCoord = " + methodCoord);
                methodCoords.add(methodCoord);
                methodPointList.add(new MethodPoint(x,y,method));
              

            }
            this.methodPoints = new MethodPoint[methodPointList.size()];
            for (int i=0; i< methodPointList.size(); i++) {
                methodPoints[i] = methodPointList.get(i);
            }
            return methodCoords;
        } catch (java.io.IOException ex) {
            throw new ClusterException(ex.getMessage());
        }
    }

}
