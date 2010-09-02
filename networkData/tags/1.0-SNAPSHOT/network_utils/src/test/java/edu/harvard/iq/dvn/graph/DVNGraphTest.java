package edu.harvard.iq.dvn.networkData;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.SQLException;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.commons.io.FileUtils;

/**
 * Unit test for simple App.
 */
public class DVNGraphTest 
    extends TestCase
{

    public static final String NEO_DB = "RNAi_db1";
    public static final String SQL_DB = "RNAi_props.db1";
    public static final String INSERT_PROPS = "graphml.props";
    public static final String NEO_PROPS = "neoDB.props";

    public static final String SOURCE_XML = "RNAi_sample.xml";
    public static final String LIB_CONF_FILE = "lib.conf";

    private final String LIB_DIR;
    private final DVNGraphFactory GraphFac;
    private final GraphBatchInserterFactory GraphInserterFac;

    private DVNGraph lg;


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DVNGraphTest( String testName ) throws IOException, FileNotFoundException
    {
        super( testName );
        File f = new File(LIB_CONF_FILE);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        LIB_DIR = br.readLine();
        br.close();

        GraphFac = new DVNGraphFactory(LIB_DIR);
        GraphInserterFac = new GraphBatchInserterFactory(LIB_DIR);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DVNGraphTest.class );
    }

    protected void setUp() throws ClassNotFoundException, SQLException {
        GraphBatchInserter gbi = GraphInserterFac.newInstance(NEO_DB, SQL_DB, INSERT_PROPS, NEO_PROPS);
        gbi.ingest(SOURCE_XML);
        lg = GraphFac.newInstance(NEO_DB, SQL_DB, NEO_PROPS);
    }

    protected void tearDown() throws IOException {
        lg.finalize();
        File f = new File(SQL_DB);
        f.delete();
        f = new File(NEO_DB);
        FileUtils.deleteDirectory(f);
        f.delete();
    }

    public void testNodeSubset() throws SQLException {
        lg.markNodesByProperty("zipcode=\"02138\"");
        System.out.println(lg.getVertexCount());
        assertTrue(lg.getVertexCount()==10);
        System.out.println(lg.getEdgeCount());
        assertTrue(lg.getEdgeCount()==10);
    }

    public void testEdgeSubsetWithDrops() throws SQLException {
        lg.markRelationshipsByProperty("h_city=\"CAMBRIDGE\" or t_city=\"CAMBRIDGE\"", true);
        System.out.println(lg.getVertexCount());
        assertTrue(lg.getVertexCount()==70);
        assertTrue(lg.getEdgeCount()==294);
    }

    public void testEdgeSubsetWithoutDrops() throws SQLException {
        lg.markRelationshipsByProperty("h_city=\"CAMBRIDGE\" or t_city=\"CAMBRIDGE\"", false);
        System.out.println(lg.getVertexCount());
        assertTrue(lg.getVertexCount()==1210);
        assertTrue(lg.getEdgeCount()==294);
    }

    public void testComponent(){
        lg.markComponent(1);
        System.out.println(lg.getVertexCount());
        assertTrue(lg.getVertexCount()==1106);
        assertTrue(lg.getEdgeCount()==11563);
        assertTrue(true);
    }

    public void testNeighborhood() throws SQLException {
        lg.markNodesByProperty("loc=\"MA\"");
        lg.markNeighborhood(2);
        System.out.println(lg.getVertexCount());
        assertTrue(lg.getVertexCount()==702);
        assertTrue(lg.getEdgeCount()==8606);
    }

    public void testDirtyInitialize() throws SQLException {
        lg.markNodesByProperty("\"loc\"=\"MA\"");
        lg.initialize();
        System.out.println(lg.getVertexCount());
        assertTrue(lg.getVertexCount()==1210);
        assertTrue(lg.getEdgeCount()==12269);
    }
}
