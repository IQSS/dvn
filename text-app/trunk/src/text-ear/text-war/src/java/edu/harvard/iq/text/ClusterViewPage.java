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
import java.math.BigDecimal;
import java.util.StringTokenizer;
import javax.annotation.PostConstruct;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ekraffmiller
 */
public class ClusterViewPage {

    private String setId;
    private BigDecimal xCoord;
    private BigDecimal yCoord;

    /** Creates a new instance of ClusterViewPage */
    public ClusterViewPage() {
        System.out.println("creating ClusterViewPage");

    }

    @PostConstruct
    public void init() {
        initConvexHullFile(setId);
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public BigDecimal getxCoord() {
        return xCoord;
    }

    public void setxCoord(BigDecimal xCoord) {
        this.xCoord = xCoord;
    }

    public BigDecimal getyCoord() {
        return yCoord;
    }

    public void setyCoord(BigDecimal yCoord) {
        this.yCoord = yCoord;
    }

    private void initConvexHullFile(String setId) {
        System.out.println("initializingConvexHull: SetId is " + setId + "!");
        ConvexHull ch;
        // Read methodpoints.text:

        // look for subfolder matching setId
        File setDir = ClusterUtil.getSetDir(setId);
        if (!setDir.exists()) {
            throw new ClusterException("setDir not found");
        }

        // look for MethodPoints.txt
        // if not found throw text exception
        File methodPoints = new File(setDir, "MethodPoints.txt");
        if (!methodPoints.exists()) {
            throw new ClusterException(methodPoints.getAbsolutePath() + " not found.");
        }


        CoordinateList methodCoords = readMethodPoints(methodPoints);

        // get the ConvexHull polygon for these method coordinates
        ConvexHull convexHull = new ConvexHull(methodCoords.toCoordinateArray(), new GeometryFactory());
        Polygon polygon = (Polygon) convexHull.getConvexHull();

        //  Create OpenLayers Polygon XML document with polygon coordinates
        
            writePolygonXMLFile(polygon);
        
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
            System.out.println("coordStr = " + coordStr);

            XMLOutputFactory xmlOutputFactory = javax.xml.stream.XMLOutputFactory.newInstance();
            fos = new FileOutputStream(new File(ClusterUtil.getSetDir(setId), "polygon.xml"));
            XMLStreamWriter xmlw = xmlOutputFactory.createXMLStreamWriter(fos);

            /*
             * <wfs:FeatureCollection xmlns:ms="http://mapserver.gis.umn.edu/mapserver" xmlns:wfs="http://www.opengis.net/wfs" xmlns:gml="http://www.opengis.net/gml" xmlns:ogc="http://www.opengis.net/ogc" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.opengis.net/wfs http://schemas.opengis.net/wfs/1.0.0/WFS-basic.xsd                         http://mapserver.gis.umn.edu/mapserver http://aneto.oco/cgi-bin/worldwfs?SERVICE=WFS&amp;VERSION=1.0.0&amp;REQUEST=DescribeFeatureType&amp;TYPENAME=polygon&amp;OUTPUTFORMAT=XMLSCHEMA">
            gml:featureMember>
            <gml:Polygon>
            <gml:outerBoundaryIs>
            <gml:LinearRing>
            <gml:coordinates>
             *
             * </gml:coordinates>
            </gml:LinearRing>
            </gml:outerBoundaryIs>
            </gml:Polygon>

            </gml:featureMember>
            </wfs:FeatureCollection>
             */

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
            throw new ClusterException("Error creating polygon.xml: " + e.getMessage());
        } catch (IOException e) {

            throw new ClusterException("Error creating polygon.xml: " + e.getMessage());
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
            int lineNumber = 0, tokenNumber = 0;

            // skip line 1 because it just contains headers
            strLine = br.readLine();
            lineNumber++;

            //read tab separated file line by line
            while ((strLine = br.readLine()) != null) {
                lineNumber++;

                //break tab separated line using "\t"
                st = new StringTokenizer(strLine, "\t");

                // first token is method name, so skip it
                st.nextToken();

                String v1 = st.nextToken();  // we assume this is x
                System.out.println("v1 = " + v1);
                double x = new Double(v1).doubleValue();

                String v2 = st.nextToken();  // we assume this is y
                System.out.println("v2 = " + v2);
                double y = new Double(v2).doubleValue();

                Coordinate methodCoord = new Coordinate(x, y);
                System.out.println("methodCoord = " + methodCoord);
                methodCoords.add(methodCoord);
                /*
                while (st.hasMoreTokens()) {
                //display csv values
                tokenNumber++;
                System.out.println("Line # " + lineNumber
                + ", Token # " + tokenNumber
                + ", Token : " + st.nextToken());
                }

                //reset token number
                tokenNumber = 0;
                 *
                 */

            }
            return methodCoords;
        } catch (java.io.IOException ex) {
            throw new ClusterException(ex.getMessage());
        }
    }
}
