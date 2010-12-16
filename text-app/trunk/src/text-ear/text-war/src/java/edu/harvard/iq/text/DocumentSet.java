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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author ekraffmiller
 */
public class DocumentSet {
    private String setId;
    private File setDir;
    private MethodPoint[] methodPoints;
    private int[][] clusterMembership;   //
    private int[][] wordDocumentMatrix;  // word rows and doc columns (each cell is word count)
    private ArrayList<String> wordList;
    private ArrayList<String> docIdList;
    private final static String POLYGON_FILE = "polygon.xml";
    private final static String METHOD_POINTS_FILE = "MethodPoints.txt";
    private final static String CLUSTER_MEMBERSHIP_FILE = "ClusterMembershipMatrix.txt";
    private final static String WORD_DOC_MATRIX_FILE = "WordDocumentMatrix.txt";

    public DocumentSet(String setId) {
        this.setId=setId;
        initializeSet();
        System.out.println("DocumentSet initialize complete."); 
    }

    public ArrayList<String> getDocIdList() {
        return docIdList;
    }

    public void setDocIdList(ArrayList<String> docIdList) {
        this.docIdList = docIdList;
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
        initMethodPointsAndPolygon();
        initClusterMembership();
        initWordDocumentMatrix();

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
            int lineNumber = 0, tokenNumber = 0;
          

            String tokenSeparator;
            if (setId.equals("3") ) {
                tokenSeparator=" ";
            } else if (setId.equals("1")) {
                tokenSeparator="\t";
            } else {
                tokenSeparator = ",";
            }
            // skip line 1 because it just contains headers
            strLine = br.readLine();

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
           //     System.out.println("numberOfClusters = "+methodPoints[lineNumber].numberOfClusters+", method="+ clusterMethod);
                // This is just for testing
                if (uniqueClusters.size()>maxClusters) {
                    maxClusters = uniqueClusters.size();
                    maxClusterMethod = clusterMethod;
                }
                lineNumber++;
            }
        //    System.out.println("maxClusters = "+maxClusters+", method="+ maxClusterMethod);

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
     //   System.out.println("ClusterMembership, methodSize = "+table.size()+" docSize = "+table.get(0).size());
    }

    private void initWordDocumentMatrix(){
        ArrayList<ArrayList> table = new ArrayList<ArrayList>();
        wordList = new ArrayList<String>();
        docIdList = new ArrayList<String>();

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
            //    System.out.println("documentId is "+documentId);
                docIdList.add(documentId);

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
      //  System.out.println("WordDocumentMatrix, docSize = "+wordDocumentMatrix.length+" wordSize = "+table.get(0).size());

    }

    /**
     *  Read methodPoints.txt, and if necessary, create polygon.xml
     */
    private void initMethodPointsAndPolygon() {
     //   System.out.println("initializingConvexHull: SetId is " + setId + "!");
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
         //   System.out.println("coordStr = " + coordStr);

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
            int lineNumber = 0;

            // skip line 1 because it just contains headers
            strLine = br.readLine();
            lineNumber++;
            ArrayList<MethodPoint> methodPointList = new ArrayList<MethodPoint>();
            //read tab separated file line by line
            String tokenSeparator;
            if (setId.equals("3") ) {
                tokenSeparator=",";
            } else if (setId.equals("1")) {
                tokenSeparator="\t";
            } else {
                tokenSeparator = ",";
            }
            while ((strLine = br.readLine()) != null) {
                lineNumber++;

                

                st = new StringTokenizer(strLine, tokenSeparator);

                // first token is method name
                String method = st.nextToken();

                String v1 = st.nextToken();  // we assume this is x
             //   System.out.println("v1 = " + v1);
                double x = new Double(v1).doubleValue();

                String v2 = st.nextToken();  // we assume this is y
             //   System.out.println("v2 = " + v2);
                double y = new Double(v2).doubleValue();

                Coordinate methodCoord = new Coordinate(x, y);
             //   System.out.println("methodCoord = " + methodCoord);
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
