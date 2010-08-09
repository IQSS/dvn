package edu.harvard.iq.dvn.networkData;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Alex D'Amour (adapted from Marko A. Rodriguez)
 */
public class GraphMLWriter implements GraphWriter {
    private XMLOutputFactory outputFactory;
    private XMLStreamWriter writer;
    private Map<String, String> nodePropTypes;
    private Map<String, String> edgePropTypes;

    public GraphMLWriter(OutputStream graphMLOutputStream,
            Map<String, String> nodePropTypes, Map<String, String> edgePropTypes){
        try{
            this.outputFactory = XMLOutputFactory.newInstance();
            this.writer = outputFactory.createXMLStreamWriter(graphMLOutputStream);
        } catch(XMLStreamException e) {
            System.err.println(e.getMessage());
        }
        this.nodePropTypes = nodePropTypes;
        this.edgePropTypes = edgePropTypes;
    }

    public void writeHeader() { 
        try{
            writer.writeStartDocument();
            writer.writeStartElement(GraphMLTokens.GRAPHML);
            writer.writeAttribute(GraphMLTokens.XMLNS, GraphMLTokens.GRAPHML_XMLNS);
            writer.writeAttribute(GraphMLTokens.XMLNS_XSI, GraphMLTokens.GRAPHML_XMLNS_XSI);
            writer.writeAttribute(GraphMLTokens.XMLNS_SCHEMA, GraphMLTokens.GRAPHML_XMLNS_SCHEMA);
            //<key id="weight" for="edge" attr.name="weight" attr.type="float"/>
            for (Map.Entry<String, String> entry : nodePropTypes.entrySet()) {
                writer.writeStartElement(GraphMLTokens.KEY);
                writer.writeAttribute(GraphMLTokens.ID, entry.getKey());
                writer.writeAttribute(GraphMLTokens.FOR, GraphMLTokens.NODE);
                writer.writeAttribute(GraphMLTokens.ATTR_NAME, entry.getKey());
                writer.writeAttribute(GraphMLTokens.ATTR_TYPE, entry.getValue());
                writer.writeEndElement();
            }
            for (Map.Entry<String, String> entry : edgePropTypes.entrySet()) {
                writer.writeStartElement(GraphMLTokens.KEY);
                writer.writeAttribute(GraphMLTokens.ID, entry.getKey());
                writer.writeAttribute(GraphMLTokens.FOR, GraphMLTokens.EDGE);
                writer.writeAttribute(GraphMLTokens.ATTR_NAME, entry.getKey());
                writer.writeAttribute(GraphMLTokens.ATTR_TYPE, entry.getValue());
                writer.writeEndElement();
            }

            writer.writeStartElement(GraphMLTokens.GRAPH);
            writer.writeAttribute(GraphMLTokens.ID, GraphMLTokens.G);
            writer.writeAttribute(GraphMLTokens.EDGEDEFAULT, GraphMLTokens.UNDIRECTED);
        } catch(XMLStreamException e){
            System.err.println(e.getMessage());
        }
    }

    public void writeNode(ResultSet rs) {
        try{
            writer.writeStartElement(GraphMLTokens.NODE);
            writer.writeAttribute(GraphMLTokens.ID, rs.getString("uid"));
            for (String key : nodePropTypes.keySet()) {
                writer.writeStartElement(GraphMLTokens.DATA);
                writer.writeAttribute(GraphMLTokens.KEY, key);
                writer.writeCharacters(rs.getString(key));
                writer.writeEndElement();
            }
            writer.writeEndElement();
        } catch(XMLStreamException e){
            System.err.println(e.getMessage());
        } catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public void writeEdge(ResultSet rs) {
        try{
            writer.writeStartElement(GraphMLTokens.EDGE);
            writer.writeAttribute(GraphMLTokens.ID, rs.getString("uid"));
            writer.writeAttribute(GraphMLTokens.SOURCE, rs.getString("source"));
            writer.writeAttribute(GraphMLTokens.TARGET, rs.getString("target"));

            for (String key : edgePropTypes.keySet()) {
                writer.writeStartElement(GraphMLTokens.DATA);
                writer.writeAttribute(GraphMLTokens.KEY, key);
                writer.writeCharacters(rs.getString(key));
                writer.writeEndElement();
            }
            writer.writeEndElement();
        } catch(XMLStreamException e){
            System.err.println(e.getMessage());
        } catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public void flush(){
        try{
            writer.flush();
        } catch(XMLStreamException e){
            System.err.println(e.getMessage());
        }
    }

    public void writeFooter() {
        try{
            writer.writeEndElement(); // graph
            writer.writeEndElement(); // graphml
            writer.writeEndDocument();
        } catch(XMLStreamException e){
            System.err.println(e.getMessage());
        }
    }

    public void finalize(){
        try{
            writer.close();
        } catch(XMLStreamException e){
            System.err.println(e.getMessage());
        }
    }
}
