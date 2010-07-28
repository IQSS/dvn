package edu.harvard.iq.dvn.networkData;

import java.sql.ResultSet;

public interface GraphWriter{
    public void writeHeader();
    public void writeNode(ResultSet rs);
    public void writeEdge(ResultSet rs);
    public void writeFooter();
    public void flush();
    public void finalize();
}

