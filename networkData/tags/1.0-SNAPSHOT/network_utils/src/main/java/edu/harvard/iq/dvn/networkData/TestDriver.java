package edu.harvard.iq.dvn.networkData;

import java.sql.SQLException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.io.File;
import org.apache.commons.io.FileUtils;

//import java.util.ArrayList;

public class TestDriver {
    public static final String NEO_DB = "RNAi_db1";
    public static final String SQL_DB = "RNAi_props.db1";
    public static final String INSERT_PROPS = "graphml.props";
    public static final String NEO_PROPS = "neoDB.props";

    public static final String SOURCE_XML = "RNAi_sample.xml";


    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException{
        long startTimeMs = System.currentTimeMillis();
        String LIB_PATH = args[0];

        /*
        File libDir = new File(LIB_PATH);
        URL[] urls = (URL[])FileUtils.toURLs(
                        (File[])FileUtils.listFiles(
                            libDir, new String[]{"jar"}, false).
                            toArray(new File[0]));

        RestrictedURLClassLoader rucl = new RestrictedURLClassLoader(urls,
                                        TestDriver.class.getClassLoader());
                            */
        RestrictedURLClassLoader rucl = new RestrictedURLClassLoader(LIB_PATH,
                                            TestDriver.class.getClassLoader());
        
        //GraphBatchInserter gbi = new GraphBatchInserterImpl(NEO_DB, SQL_DB, INSERT_PROPS, NEO_PROPS);
        GraphBatchInserter gbi = new GraphBatchInserterFactory(rucl).newInstance(NEO_DB, SQL_DB, INSERT_PROPS, NEO_PROPS);
        gbi.ingest(SOURCE_XML);

        //gbi.ingest("big_one.xml");
        
        //DVNGraph lg = new DVNGraphImpl(NEO_DB, SQL_DB, NEO_PROPS);
        DVNGraph lg = new DVNGraphFactory(rucl).newInstance(NEO_DB, SQL_DB, NEO_PROPS);
        
        
        try{
            //lg.initialize();
            
//            ((DVNGraphImpl)lg).tagComponents();
//            ((DVNGraphImpl)lg).printComponents();
            
            System.out.println("\nComponent marking test.");
            lg.markComponent(1);
            lg.printStatus();
            lg.undo();
            lg.printStatus();
            
            
            System.out.println("\nVertex subset test.");
            lg.markNodesByProperty("\"loc\"=\"MA\"");
            lg.printStatus();
            lg.undo();
            lg.printStatus();
            lg.initialize();
            

            System.out.println("\nRelationship subset test with dropped nodes.");
            lg.markRelationshipsByProperty("h_city = \"CAMBRIDGE\" or t_city = \"CAMBRIDGE\"", true);
            lg.printStatus();

            System.out.println("\nNeighborhood test.");
            lg.markNeighborhood(3);
            lg.printStatus();
            lg.initialize();
            
            System.out.println("\nRelationship subset test without dropped nodes.");
            lg.initialize();
            lg.markRelationshipsByProperty("h_city = \"CAMBRIDGE\" or t_city = \"CAMBRIDGE\"", false);
            lg.printStatus();

            System.out.println("\nNetwork measures test.");
            lg.calcPageRank(0.85);
            lg.calcDegree();
            lg.calcUniqueDegree();
            lg.calcInLargestComponent();
            lg.printUserProps();

            lg.markComponent(1);
            lg.calcPageRank(0.85);
            lg.printUserProps();

            lg.markNeighborhood(1);
            lg.printStatus();

            System.out.println("\nGraphML dump test.");
            lg.dumpGraphML("RNAi_subset.xml");

            System.out.println("\nTabular dump test.");
            lg.dumpTables("RNAi_verts.tab", "RNAi_edge.tab", ",");

            System.out.println("\nProperty dump test:");
            System.out.println("Vertex Properties:");
            for(String p : lg.listNodeProperties())
                System.out.println("\t" + p);

            System.out.println("Edge Properties:");
            for(String p : lg.listRelationshipProperties())
                System.out.println("\t"+p);
            System.out.print("\n");
            
        }
        finally{
             System.out.println("Entire test took " + (((double)(System.currentTimeMillis()-startTimeMs))/1000) + " seconds.");
           //System.out.println("hello!");
            lg.finalize();

        }
    }
}
