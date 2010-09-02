package edu.harvard.iq.dvn.networkData;

import java.sql.SQLException;

public interface GraphBatchInserter {
    public void ingest(String graphmlFilename) throws SQLException, ClassNotFoundException;
}
