package edu.harvard.iq.dvn.networkData;

//import com.tinkerpop.blueprints.pgm.impls.neo4j.Neo4jGraph;

import org.neo4j.graphdb.*;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

//import java.util.Formatter;

public class GraphBatchInserterImpl implements GraphBatchInserter{
    private String neoDbName, sqlDbName, sqlConfigFileName, neoConfigFileName;

    public GraphBatchInserterImpl(String neoDbName, String sqlDbName, String sqlConfigFileName, String neoConfigFileName)
      throws ClassNotFoundException{
        this.neoDbName = neoDbName;
        this.sqlDbName = sqlDbName;
        this.sqlConfigFileName = sqlConfigFileName;
        this.neoConfigFileName = neoConfigFileName;
        Class.forName("org.sqlite.JDBC");
    }

    public void ingest(String graphmlFilename) throws SQLException, ClassNotFoundException{
        FileInputStream infile = null;
        BatchInserter inserter =
            new BatchInserterImpl(neoDbName, BatchInserterImpl.loadProperties(sqlConfigFileName));

        Connection conn = null;

        try{
            conn = DriverManager.getConnection("jdbc:sqlite:"+sqlDbName);
        }    
        catch(SQLException e){
            System.err.println("GraphBatchInserter: Couldn't open database " + sqlDbName + ". " + e.getMessage());
        }

        try{
            infile = new FileInputStream(graphmlFilename);
        }
        catch(FileNotFoundException e){
            System.err.println("GraphBatchInserter: Couldn't find input file." + e.getMessage());
            return;
        }

        GraphMLReader reader =  null;
        reader = new GraphMLReader(); 
        try{
            reader.inputGraph(inserter, conn, infile);
        } catch(XMLStreamException e){
            System.err.println("GraphBatchInserter: XML error!" + e.getMessage());
            return;
        }
        /*index.shutdown();*/
        inserter.shutdown();
        //conn.commit();
        conn.close();

        DVNGraph lg = new DVNGraphImpl(neoDbName, sqlDbName, neoConfigFileName);
        lg.initialize();
        lg.finalize();
    }
}
