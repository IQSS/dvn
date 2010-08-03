package edu.harvard.iq.dvn.networkData;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.SQLException;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 * Unit test for simple App.
 */
public class DVNGraphTest 
    extends TestCase
{

    public static final String NEO_DB = "target/test-classes/RNAi_db1";
    public static final String SQL_DB = "target/test-classes/RNAi_props.db1";
    public static final String INSERT_PROPS = "target/test-classes/graphml.props";
    public static final String NEO_PROPS = "target/test-classes/neoDB.props";

    public static final String SOURCE_XML = "target/test-classes/RNAi_sample.xml";

    private DVNGraph lg;


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DVNGraphTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DVNGraphTest.class );
    }

    protected void setUp() throws ClassNotFoundException, SQLException {
        GraphBatchInserter gbi = new GraphBatchInserter(NEO_DB, SQL_DB, INSERT_PROPS, NEO_PROPS);
        gbi.ingest(SOURCE_XML);
        lg = new DVNGraphImpl(NEO_DB, SQL_DB, NEO_PROPS);
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
