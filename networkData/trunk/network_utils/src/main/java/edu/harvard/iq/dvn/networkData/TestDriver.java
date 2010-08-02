package edu.harvard.iq.dvn.networkData;

import java.sql.SQLException;
//import java.util.ArrayList;

public class TestDriver {
    public static void main(String[] args) throws ClassNotFoundException, SQLException{
        //GraphBatchInserter gbi = new GraphBatchInserter("full_db", "full_props.db", "graphml.props");
        GraphBatchInserter gbi = new GraphBatchInserter("RNAi_db1", "RNAi_props.db1", "graphml.props", "neodb.props");
        //gbi.ingest("big_one.xml");
        gbi.ingest("RNAi_sample.xml");
        
        //DVNGraph lg = new DVNGraphImpl("full_db", "full_props.db");
        DVNGraph lg = new DVNGraphImpl("RNAi_db", "RNAi_props.db", "neoDB.props");
        try{
            lg.initialize();
            lg.printStatus();
            
            lg.markNodesByProperty("\"loc\"=\"MA\"");
            lg.printStatus();
            lg.initialize();

            lg.markRelationshipsByProperty("h_city = \"CAMBRIDGE\" or t_city = \"CAMBRIDGE\"",true);

            lg.calcPageRank(0.85);
            lg.calcDegree();
            lg.calcUniqueDegree();
            lg.calcInLargestComponent();

            lg.printUserProps();
            
            lg.markComponent(1);

            lg.calcPageRank(0.85);

            lg.printUserProps();

            lg.dumpGraphML("RNAi_subset.xml");
            lg.dumpTables("RNAi_verts.tab", "RNAi_edge.tab", ",");

            System.out.println("Vertex Properties:");
            for(String p : lg.listNodeProperties())
                System.out.println("\t" + p);
            System.out.print("\n");

            System.out.println("Edge Properties:");
            for(String p : lg.listRelationshipProperties())
                System.out.println("\t"+p);
            System.out.print("\n");


        }
        finally{
            lg.finalize();
        }
        
    }
}
