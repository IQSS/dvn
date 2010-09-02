package edu.harvard.iq.dvn.networkData;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Map;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DelimitedWriter implements GraphWriter {
    private static final String MISSING_VAL = "null";

    private OutputStreamWriter vertWriter;
    private OutputStreamWriter edgeWriter;
    private Map<String, String> nodePropTypes;
    private Map<String, String> edgePropTypes;
    private String delim;
    public static String newline = System.getProperty("line.separator");

    private enum element {
        VERTEX, EDGE
    };

    public DelimitedWriter(OutputStream vertWriter, OutputStream edgeWriter,
            Map<String, String> nodePropTypes,
            Map<String, String> edgePropTypes, String delimiter){

        this.vertWriter = new OutputStreamWriter(vertWriter);
        this.edgeWriter = new OutputStreamWriter(edgeWriter);
        this.delim = delimiter;

        this.nodePropTypes = nodePropTypes;
        this.edgePropTypes = edgePropTypes;

    }

    public void writeHeader() {
        boolean firstTime = true;
        try{
            vertWriter.write("uid");
            edgeWriter.write(String.format("source%starget%suid", delim, delim));
            for(String k : nodePropTypes.keySet()){
                vertWriter.write(delim);
                vertWriter.write(k);
            }
            for(String k : edgePropTypes.keySet()){
                edgeWriter.write(delim);
                edgeWriter.write(k);
            }
            vertWriter.write(newline);
            edgeWriter.write(newline);
        } catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void writeNode(ResultSet rs){
        String prop;
        try{
            vertWriter.write(rs.getString("uid"));
            for(String k : nodePropTypes.keySet()){
                vertWriter.write(delim);
                if(nodePropTypes.get(k).toUpperCase().equals("STRING"))
                    vertWriter.write("\"");

                prop = rs.getString(k);
                vertWriter.write(prop==null ? MISSING_VAL : prop);

                if(nodePropTypes.get(k).toUpperCase().equals("STRING"))
                    vertWriter.write("\"");

            }
            vertWriter.write(newline);
        } catch(SQLException e){
            System.err.println(e.getMessage());
        } catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void writeEdge(ResultSet rs){
        String prop;
        try{
            edgeWriter.write(rs.getString("source"));
            edgeWriter.write(delim);
            edgeWriter.write(rs.getString("target"));
            edgeWriter.write(delim);
            edgeWriter.write(rs.getString("uid"));
            for(String k : edgePropTypes.keySet()){
                edgeWriter.write(delim);
                if(edgePropTypes.get(k).toUpperCase().equals("STRING"))
                    edgeWriter.write("\"");

                prop = rs.getString(k);
                edgeWriter.write(prop==null ? MISSING_VAL : prop);

                if(edgePropTypes.get(k).toUpperCase().equals("STRING"))
                    edgeWriter.write("\"");
            }
            edgeWriter.write(newline);
        } catch(SQLException e){
            System.err.println(e.getMessage());
        } catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void writeFooter(){
        return;
    }

    public void flush(){
        try{
            vertWriter.flush();
            edgeWriter.flush();
        } catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    public void finalize(){
        try{
            vertWriter.close();
            edgeWriter.close();
        } catch(IOException e){
            System.err.println(e.getMessage());
        }
    }
}
