package edu.harvard.iq.dvn.networkData;

import java.sql.SQLException;

public interface DVNGraph{
    public void markNodesByProperty(String query) throws SQLException;
    public void markRelationshipsByProperty(String query, boolean dropDisconnected) throws SQLException;

    public void markComponent(int nth);
    public void markNeighborhood(int nth);

    public void calcDegree();
    public void calcUniqueDegree();
    public void calcPageRank(double d);
    public void calcInLargestComponent();

    public void dumpGraphML(String xmlFilename);
    public void dumpTables(String vertexFilename, String edgeFilename, String delim);

    public boolean initialize();
    public void finalize();

    public Iterable<String> listNodeProperties(); 
    public Iterable<String> listRelationshipProperties();
    public int getVertexCount();
    public int getEdgeCount();

    //Below aren't part of the official API, but are useful for testing.
    //These will be removed soon, I think.
    public void printStatus();
    public void printUserProps();
}
