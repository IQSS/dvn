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
import edu.harvard.iq.dvn.ingest.dsb.impl.DvnRGraphServiceImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author gdurand
 */
@Stateless
public class NetworkDataServiceBean implements NetworkDataServiceLocal, java.io.Serializable {
    private static Logger dbgLog = Logger.getLogger(NetworkDataServiceBean.class.getPackage().getName());
    @EJB VariableServiceLocal varService;

    public String initAnalysis() {
        return null;
    }

    public NetworkDataSubsetResult runManualQuery(String RDataFileName, String attributeSet, String query, Boolean eliminateDisconnectedVertices) {
        /*
        Map<String, Object> mpl = new HashMap<String, Object>();
        Map<String, String> resultInfo = new HashMap<String, String>();

        mpl.put("rFunction","manualQuery");
        mpl.put("attributeSet",attributeSet);
        mpl.put("query", query);
        mpl.put("eliminate",eliminateDisconnectedVertices);

        DvnRJobRequest rjr = new DvnRJobRequest(RDataFileName, mpl);
        DvnRGraphServiceImpl dgs = new DvnRGraphServiceImpl();
        resultInfo = dgs.execute(rjr);

        if (resultInfo.get("RexecError").equals("true")){
            // throw exception
            // resultInfo.get("RexecErrorDescription") -- error condition;
            // resultInfo.get("RexecErrorMessage") -- more detailed error
            //					  message, if available
        }
        */
        NetworkDataSubsetResult result = new NetworkDataSubsetResult();
        //result.setVertices( Long.parseLong( resultInfo.get("numVertices") ) );
        //result.setEdges( Long.parseLong( resultInfo.get("numEdges") ) );
        return result;
    }

    public NetworkDataSubsetResult runAutomaticQuery() {
        NetworkDataSubsetResult result = new NetworkDataSubsetResult();
        //result.setVertices( Long.parseLong( resultInfo.get("numVertices") ) );
        //result.setEdges( Long.parseLong( resultInfo.get("numEdges") ) );
        return result;
    }

    public String runNetworkMeasure() {
        return null;
    }



    public void ingest(StudyFileEditBean editBean)  {
        dbgLog.fine("Begin ingest() ");
        // Initialize NetworkDataFile with new DataTable objects
        NetworkDataFile ndf = (NetworkDataFile)editBean.getStudyFile();
        DataTable vertexTable = new DataTable();
        vertexTable.setDataVariables(new ArrayList<DataVariable>());
        ndf.setVertexDataTable(vertexTable);
        DataTable edgeTable = new DataTable();
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

        // Convert the GraphML file to an RData File, and save it in "ingested" location so it can be loaded later for subsetting
        saveRDataFile(editBean);

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
    /**
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
        } else {
            ndf.getEdgeDataTable().getDataVariables().add(dataVariable);
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


    private void saveRDataFile(StudyFileEditBean editBean) {
        DvnRGraphServiceImpl dgs = new DvnRGraphServiceImpl();


        Map<String, String> resultInfo = new HashMap<String, String>();
        File temploc  = new File(editBean.getTempSystemFileLocation());
        File tempDir = temploc.getParentFile();
        File ingestedDir = new File(tempDir, "ingested");
        if (!ingestedDir.exists()) {
            ingestedDir.mkdirs();
        }
        String rDataFileName = FileUtil.replaceExtension(temploc.getName(),"RData");
        File rDataFile = new File(ingestedDir,rDataFileName);
        resultInfo = dgs.ingestGraphML(editBean.getTempSystemFileLocation(), rDataFile.getAbsolutePath());

        // for cachedRDataFileName we may use the name of the graphML file in
        // the study directory, with the ".RData" extension?

        // error diagnostics:

        if (resultInfo!=null && resultInfo.get("RexecError")!=null && resultInfo.get("RexecError").equals("true")) {
            String err = resultInfo.get("RexecErrorDescription");// -- error condition;
            err += " "+ resultInfo.get("RexecErrorMessage"); //more detailed error message, if available
            throw new EJBException(err);
         
        }
    }

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
