/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.analysis;


import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.DataVariable;
import edu.harvard.iq.dvn.core.study.NetworkDataFile;
import edu.harvard.iq.dvn.core.study.StudyFileEditBean;
import edu.harvard.iq.dvn.core.study.VariableServiceLocal;
import edu.harvard.iq.dvn.core.util.FileUtil;
import edu.harvard.iq.dvn.networkData.DVNGraph;
import edu.harvard.iq.dvn.core.util.StringUtil;
import edu.harvard.iq.dvn.networkData.GraphBatchInserter;
import edu.harvard.iq.dvn.networkData.DVNGraphFactory;
import edu.harvard.iq.dvn.networkData.GraphBatchInserterFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author gdurand
 */
@Stateful
public class NetworkDataServiceBean implements NetworkDataServiceLocal, java.io.Serializable {

    private static Logger dbgLog = Logger.getLogger(NetworkDataServiceBean.class.getCanonicalName());
    private static final String SQLITE_CONFIG_FILE ="graphml.props";
    private static final String NEO4J_CONFIG_FILE = "neodb.props";
    public static final String SQLITE_EXTENSION = "sqliteDB";
    public static final String NEO4J_EXTENSION = "neo4jDB";


    @EJB VariableServiceLocal varService;

     private static DVNGraphFactory dvnGraphFactory = null;
     private static GraphBatchInserterFactory batchInserterFactory = null;
     
     DVNGraph dvnGraph;
     GraphBatchInserter dvnGBI;
     String fileSystemLocation;
     private static final String baseTempPath = System.getProperty("java.io.tmpdir");
     private static final String LIB_PATH = System.getProperty("dvn.networkData.libPath");
        
    public void initAnalysis(String fileSystemLocation, String sessionId) {
       
        try {
            File fileLocation = new File(fileSystemLocation);
            String neoDirName = FileUtil.replaceExtension(fileLocation.getName(), NEO4J_EXTENSION);
            File neoDir = new File(fileLocation.getParent(), neoDirName);
            // Coy neoDB directory to a temporary location, so this bean can have exclusive access to it.
            File tempNeoDir = new File(baseTempPath, sessionId+new Long(new Date().getTime()).toString());
            tempNeoDir.mkdir();
            tempNeoDir.deleteOnExit();
            FileUtils.copyDirectory(neoDir, tempNeoDir);

            // File copyNeoDB = FileUtils.
            File sqliteFile = new File(fileLocation.getParent(), FileUtil.replaceExtension(fileLocation.getName(), SQLITE_EXTENSION));
            try {
                if (dvnGraphFactory == null) {
                    dvnGraphFactory = new DVNGraphFactory(LIB_PATH);
                }
                //dvnGraph = new DVNGraphFactory(LIB_PATH).
                dvnGraph = dvnGraphFactory.newInstance(tempNeoDir.getAbsolutePath(), sqliteFile.getAbsolutePath(), NEO4J_CONFIG_FILE);
                //dvnGraph = new DVNGraphImpl(tempNeoDir.getAbsolutePath(), sqliteFile.getAbsolutePath(), NEO4J_CONFIG_FILE);
            } catch (ClassNotFoundException e) {
                throw new EJBException(e);
            }
            //dvnGraph.initialize();
            this.fileSystemLocation = fileSystemLocation;
        } catch (IOException e) {
            throw new EJBException(e);
        }
    }

    @PreDestroy
    public void finalizeGraph() {
        if (dvnGraph!=null) {
            dvnGraph.finalize();
        }
    }

   

    public NetworkDataSubsetResult runManualQuery( String attributeSet, String query, boolean eliminateDisconnectedVertices) throws SQLException {
       
        if (DataTable.TYPE_VERTEX.equals(attributeSet)) {
            dvnGraph.markNodesByProperty(query);
        } else if (DataTable.TYPE_EDGE.equals(attributeSet)) {
            dvnGraph.markRelationshipsByProperty(query, eliminateDisconnectedVertices);
        }
       
        NetworkDataSubsetResult result = new NetworkDataSubsetResult();
        result.setVertices( dvnGraph.getVertexCount() );
        result.setEdges(dvnGraph.getEdgeCount() );
        return result;
    }

    public NetworkDataSubsetResult runAutomaticQuery( String automaticQuery, int nValue) {
        
        if (automaticQuery.equals(AUTOMATIC_QUERY_NTHLARGEST)) {
            dvnGraph.markComponent(nValue);
        } else if (automaticQuery.equals(AUTOMATIC_QUERY_NEIGHBORHOOD)) {
            dvnGraph.markNeighborhood(nValue);
        }
       
        NetworkDataSubsetResult result = new NetworkDataSubsetResult();
        result.setVertices(dvnGraph.getVertexCount());
        result.setEdges( dvnGraph.getEdgeCount());
        
        return result;
    }

    public String runNetworkMeasure( String networkMeasure, List<NetworkMeasureParameter> parameters){
        
        String newMeasure=null;
        if (networkMeasure.equals(this.NETWORK_MEASURE_DEGREE)) {
            newMeasure=dvnGraph.calcDegree();
        } else if (networkMeasure.equals(this.NETWORK_MEASURE_UNIQUE_DEGREE)) {
            newMeasure=dvnGraph.calcUniqueDegree();
        } else if (networkMeasure.equals(this.NETWORK_MEASURE_RANK)) {
            String strVal =parameters.get(0).getValue();
            if (StringUtil.isEmpty(strVal)) {
                strVal =parameters.get(0).getDefaultValue();
            }
            newMeasure=dvnGraph.calcPageRank(Double.parseDouble(strVal));
        } else if (networkMeasure.equals(this.NETWORK_MEASURE_IN_LARGEST)) {
            newMeasure=dvnGraph.calcInLargestComponent();
        }
        


        return newMeasure;
    }

    public void undoLastEvent() {
        dvnGraph.undo();
    }

    public void resetAnalysis()  {
        dvnGraph.initialize();
        
    }

    public File getSubsetExport() {
        return getSubsetExport(true, true);
    }

    public File getSubsetExport(boolean getGraphML, boolean getTabular) {

        if (!getGraphML && !getTabular) {
            throw new IllegalArgumentException("At least one download type must be set to true.");
        }

        //  Map<String, String> resultInfo = dgs.liveConnectionExport(rWorkspace);
        //return new File( resultInfo.get(DvnRGraphServiceImpl.GRAPHML_FILE_EXPORTED) );
        File xmlFile;
        File vertsFile;
        File edgesFile;
        File zipOutputFile;
        ZipOutputStream zout;
        try {

            xmlFile = File.createTempFile("graphml", "xml");
            vertsFile = File.createTempFile("vertices", "tab");
            edgesFile = File.createTempFile("edges", "tab");


            String delimiter = "\t";
            if (getGraphML) {
                dvnGraph.dumpGraphML(xmlFile.getAbsolutePath());
            }
            if (getTabular) {
                dvnGraph.dumpTables(vertsFile.getAbsolutePath(), edgesFile.getAbsolutePath(), delimiter);
            }

            // Create zip file
            String exportTimestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss").format(new Date());
            zipOutputFile = File.createTempFile("subset_" + exportTimestamp, "zip");
            zout = new ZipOutputStream((OutputStream) new FileOutputStream(zipOutputFile));

            if (getGraphML) {
                addZipEntry(zout, xmlFile.getAbsolutePath(), "graphml_" + exportTimestamp + ".xml");
            }
            if (getTabular) {
                addZipEntry(zout, vertsFile.getAbsolutePath(), "vertices_" + exportTimestamp + ".tab");
                addZipEntry(zout, edgesFile.getAbsolutePath(), "edges_" + exportTimestamp + ".tab");
            }

            zout.close();


        } catch (IOException e) {
            throw new EJBException(e);
        }

        return zipOutputFile;
    }

    private void addZipEntry(ZipOutputStream zout, String inputFileName, String outputFileName) throws IOException{
        FileInputStream tmpin = new FileInputStream(inputFileName);
        byte[] dataBuffer = new byte[8192];
        int i = 0;

        ZipEntry e = new ZipEntry(outputFileName);
        zout.putNextEntry(e);

        while ((i = tmpin.read(dataBuffer)) > 0) {
            zout.write(dataBuffer, 0, i);
            zout.flush();
        }
        tmpin.close();
        zout.closeEntry();
     }

    @Remove
    public void ingest(StudyFileEditBean editBean) {
        dbgLog.fine("Begin ingest() ");
        // Initialize NetworkDataFile with new DataTable objects
        NetworkDataFile ndf = (NetworkDataFile)editBean.getStudyFile();
        
        DataTable vertexTable = new DataTable();
        vertexTable.setStudyFile(ndf);
        vertexTable.setDataVariables(new ArrayList<DataVariable>());
        ndf.setVertexDataTable(vertexTable);

        DataTable edgeTable = new DataTable();
        edgeTable.setStudyFile(ndf);
        edgeTable.setDataVariables(new ArrayList<DataVariable>());
        ndf.setEdgeDataTable(edgeTable);

        try {
            // Populate DataTables using GraphML file
            processXML(editBean.getTempSystemFileLocation(),ndf );

            //Copy GraphML file to "ingested" location
            copyFile(editBean);
        } catch(Exception e) {
            throw new EJBException(e);
        }

        // Convert the GraphML file to a Neo4j DB File (for the qraph stucture, and a SQLite set of files (for the property values), and
        // save it in "ingested" location so it can be loaded later for subsetting
        saveNetworkDBFiles(editBean);
        
    }

    private void processXML(String fileName, NetworkDataFile ndf) throws XMLStreamException, IOException{

        File file = new File(fileName);
        FileReader fileReader = new FileReader(file);
        javax.xml.stream.XMLInputFactory xmlif = javax.xml.stream.XMLInputFactory.newInstance();
        xmlif.setProperty("javax.xml.stream.isCoalescing", java.lang.Boolean.TRUE);

        XMLStreamReader xmlr = xmlif.createXMLStreamReader(fileReader);
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                
                if (xmlr.getLocalName().equals("key")) processKey(xmlr, ndf);
                else if (xmlr.getLocalName().equals("graph")) processGraph(xmlr, ndf);


            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("graphml")) return;
            }
        }

        // If #nodes and #edges is not set, then go thru list to count them
    }
    /**"
     * If this key element is for a node, add a DataVariable to the
     * vertexDataTable, else add a DataVariable to edgeDataTable
     * @param xmlr
     */
    private void processKey(XMLStreamReader xmlr,  NetworkDataFile ndf) {
        DataVariable dataVariable = new DataVariable();

        String attrName = xmlr.getAttributeValue(null, "attr.name");
        dataVariable.setName(attrName);
        dataVariable.setLabel(attrName);

        String attrType = xmlr.getAttributeValue(null, "attr.type");
        if (attrType.equals("string") || attrType.equals("boolean")) {
            dbgLog.fine("attrType = "+attrType);
            dataVariable.setVariableFormatType( varService.findVariableFormatTypeByName( "character" ) );
        } else {
            dataVariable.setVariableFormatType( varService.findVariableFormatTypeByName( "numeric" ) );

        }

        if (xmlr.getAttributeValue(null, "for").equals("node")) {
            ndf.getVertexDataTable().getDataVariables().add(dataVariable);
            dataVariable.setDataTable(ndf.getVertexDataTable());
        } else {
            ndf.getEdgeDataTable().getDataVariables().add(dataVariable);
            dataVariable.setDataTable(ndf.getEdgeDataTable());

        }
    }

    private void processGraph(XMLStreamReader xmlr,  NetworkDataFile ndf)throws XMLStreamException {
        for (int event = xmlr.next(); event != XMLStreamConstants.END_DOCUMENT; event = xmlr.next()) {
            if (event == XMLStreamConstants.START_ELEMENT) {
                if (xmlr.getLocalName().equals("node")) {
                    Long caseQuantity = ndf.getVertexDataTable().getCaseQuantity();
                    if (caseQuantity == null) {
                        caseQuantity = new Long(0);
                    }
                    caseQuantity++;
                    ndf.getVertexDataTable().setCaseQuantity(caseQuantity);
                } else if (xmlr.getLocalName().equals("edge")) {
                    Long caseQuantity = ndf.getEdgeDataTable().getCaseQuantity();
                    if (caseQuantity == null) {
                        caseQuantity = new Long(0);
                    }
                    caseQuantity++;
                    ndf.getEdgeDataTable().setCaseQuantity(caseQuantity);
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (xmlr.getLocalName().equals("graph")) {
                    ndf.getEdgeDataTable().setVarQuantity(new Long(ndf.getEdgeDataTable().getDataVariables().size()));
                    ndf.getVertexDataTable().setVarQuantity(new Long(ndf.getVertexDataTable().getDataVariables().size()));
                    return;
                }
            }
        }
    }

    private void copyFile(StudyFileEditBean editBean) throws IOException {
        File tempFile = new File(editBean.getTempSystemFileLocation());
        dbgLog.fine("begin copyFile()");
        // create a sub-directory "ingested"
        File newDir = new File(tempFile.getParentFile(), "ingested");

        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        dbgLog.fine("newDir: abs path:\n" + newDir.getAbsolutePath());


        File newFile = new File(newDir, tempFile.getName());

        FileInputStream fis = new FileInputStream(tempFile);
        FileOutputStream fos = new FileOutputStream(newFile);
        FileChannel fcin = fis.getChannel();
        FileChannel fcout = fos.getChannel();
        fcin.transferTo(0, fcin.size(), fcout);
        fcin.close();
        fcout.close();
        fis.close();
        fos.close();

        dbgLog.fine("newFile: abs path:\n" + newFile.getAbsolutePath());

        // store the tab-file location
        editBean.setIngestedSystemFileLocation(newFile.getAbsolutePath());


    }

    private void saveNetworkDBFiles(StudyFileEditBean editBean) {
        File temploc  = new File(editBean.getTempSystemFileLocation());
        File tempDir = temploc.getParentFile();
        File ingestedDir = new File(tempDir, "ingested");
        if (!ingestedDir.exists()) {
            ingestedDir.mkdirs();
        }
        String neo4jDirName = FileUtil.replaceExtension(temploc.getName(),this.NEO4J_EXTENSION);
        File neo4jDir = new File(ingestedDir,neo4jDirName);
        neo4jDir.mkdirs();
        String sqliteFileName = FileUtil.replaceExtension(temploc.getName(),this.SQLITE_EXTENSION);
        File sqliteFile = new File(ingestedDir, sqliteFileName);

        try {

            if (batchInserterFactory == null) {
                batchInserterFactory = new GraphBatchInserterFactory(LIB_PATH);
            }
            dvnGBI = batchInserterFactory.
                newInstance(neo4jDir.getAbsolutePath(), sqliteFile.getAbsolutePath(),  SQLITE_CONFIG_FILE, NEO4J_CONFIG_FILE);
            dvnGBI.ingest(editBean.getTempSystemFileLocation());
        } catch (Exception e) {
            throw new EJBException(e);
        }


    }
    /*

    private void saveRDataFile(StudyFileEditBean editBean){
        dbgLog.fine("begin saveRDataFile");

        Map<String, String> resultInfo = new HashMap<String, String>();
        File temploc  = new File(editBean.getTempSystemFileLocation());
        File tempDir = temploc.getParentFile();
        File ingestedDir = new File(tempDir, "ingested");
        if (!ingestedDir.exists()) {
            ingestedDir.mkdirs();
        }
        String rDataFileName = FileUtil.replaceExtension(temploc.getName(),"RData");
        File rDataFile = new File(ingestedDir,rDataFileName);
        dbgLog.fine("before call to ingestGraphML");
        resultInfo = dgs.ingestGraphML(editBean.getTempSystemFileLocation(), rDataFile.getAbsolutePath());

        // for cachedRDataFileName we may use the name of the graphML file in
        // the study directory, with the ".RData" extension?

        // error diagnostics:
        dbgLog.fine("return from ingestGraphML, resultInfo ="+resultInfo);
        checkForError(resultInfo);
    }

    private void checkForError(Map<String, String> resultInfo) {
        if (resultInfo.get("RexecError") != null && resultInfo.get("RexecError").equals("true")){
            String errorMessage = resultInfo.get("RexecErrorDescription");
            errorMessage += resultInfo.get("RexecErrorMessage") != null ? ": " + resultInfo.get("RexecErrorMessage") : "";

            throw new RuntimeException(errorMessage);
        }
    }

*/
   public static void main(String args[]) throws Exception{


       NetworkDataFile ndf = new NetworkDataFile();
       StudyFileEditBean editBean = new StudyFileEditBean(ndf);

       editBean.setTempSystemFileLocation( "C:\\download\\network data\\alex examples\\boston_1.xml");
       NetworkDataServiceBean ndr = new NetworkDataServiceBean();
       ndr.ingest(editBean);
       DataTable nodeTable = ndf.getVertexDataTable();
       DataTable edgeTable = ndf.getEdgeDataTable();

       System.out.println("done!");

  

   }

}
