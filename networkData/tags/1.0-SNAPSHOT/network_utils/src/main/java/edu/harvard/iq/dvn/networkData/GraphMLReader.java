package edu.harvard.iq.dvn.networkData;

import org.neo4j.kernel.impl.batchinsert.BatchInserter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

//import GraphMLTokens;

/**
  * @author Marko Rodriguez adapted by Alex D'Amour
 */
public class GraphMLReader {
    private static final long WRITE_LIMIT = 10000;

    public static void inputGraph(final BatchInserter inserter, Connection conn, final InputStream graphMLInputStream)
        throws XMLStreamException, SQLException {

        configureSQL(conn);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLStreamReader reader = inputFactory.createXMLStreamReader(graphMLInputStream);

        long nodeCount = 0, edgeCount = 0;
        boolean preNodeInit = true, preEdgeInit = true;

        Map<String, String> NodeKeyTypesMaps = new LinkedHashMap<String, String>();
        Map<String, String> EdgeKeyTypesMaps = new LinkedHashMap<String, String>();
        Map<String, Long> vertexIdMap = new LinkedHashMap<String, Long>();
        Map<String, Object> dummyMap = new LinkedHashMap<String, Object>();
        Map<String, String> propertyMap = new LinkedHashMap<String, String>();

        Long vertexObj = null, edgeObj = null;
        Long inVertex = null, outVertex = null;
        String vertexStringId = null, outStringId = null, inStringId = null;
        String edgeLabel = null;
        //Vertex currentVertex = null;
        //Edge currentEdge = null;

        while (reader.hasNext()) {
            Integer eventType = reader.next();
            if (eventType.equals(XMLEvent.START_ELEMENT)) {
                String elementName = reader.getName().getLocalPart();
                if (elementName.equals(GraphMLTokens.KEY)) {
                    String attributeName = reader.getAttributeValue(null, GraphMLTokens.ATTR_NAME);
                    String attributeType = reader.getAttributeValue(null, GraphMLTokens.ATTR_TYPE);
                    String attributeElement = reader.getAttributeValue(null, GraphMLTokens.FOR);

                    if(attributeElement.toUpperCase().equals("NODE")){
                        if(!preNodeInit)
                            throw new XMLStreamException("All node properties must be declared before nodes are listed.");
                        NodeKeyTypesMaps.put(attributeName, attributeType);
                    }
                    else if(attributeElement.toUpperCase().equals("EDGE")){
                        if(!preEdgeInit)
                            throw new XMLStreamException("All edge properties must be declared before edges are listed.");
                        EdgeKeyTypesMaps.put(attributeName, attributeType);
                    }

                } else if (elementName.equals(GraphMLTokens.NODE)) {
                    if(preNodeInit){
                        preNodeInit = false;
                        initializeVertexTable(conn, NodeKeyTypesMaps);
                        conn.commit();
                    }
                        
                    propertyMap.clear();

                    vertexStringId = reader.getAttributeValue(null, GraphMLTokens.ID);

                } else if (elementName.equals(GraphMLTokens.EDGE)) {
                    if(preEdgeInit){
                        preEdgeInit=false;
                        initializeEdgeTable(conn, EdgeKeyTypesMaps);
                        conn.commit();
                    }

                    propertyMap.clear();

                    String edgeId = reader.getAttributeValue(null, GraphMLTokens.ID);
                    edgeLabel = reader.getAttributeValue(null, GraphMLTokens.LABEL);
                    outStringId = reader.getAttributeValue(null, GraphMLTokens.SOURCE);
                    inStringId = reader.getAttributeValue(null, GraphMLTokens.TARGET);

                    // TODO: current edge get by id first?
                    Object outObjectId = vertexIdMap.get(outStringId);
                    Object inObjectId = vertexIdMap.get(inStringId);

                    outVertex = (Long) outObjectId;
                    inVertex = (Long) inObjectId;

                   if (null == outVertex) {
                        throw new RuntimeException("Edge references vertex that doesn't exist!");
                    }
                    if (null == inVertex) {
                        throw new RuntimeException("Edge references vertex that doesn't exist!");
                    }


                } else if (elementName.equals(GraphMLTokens.DATA)) {
                    String key = reader.getAttributeValue(null, GraphMLTokens.KEY);
                    String value = reader.getElementText();
                    propertyMap.put(key, value);
                }
            } else if (eventType.equals(XMLEvent.END_ELEMENT)) {
                String elementName = reader.getName().getLocalPart();
                if (elementName.equals(GraphMLTokens.NODE)){
                    vertexObj = new Long(inserter.createNode(dummyMap));
                    bindProperties(conn, elementName, vertexObj, propertyMap, NodeKeyTypesMaps);
                    nodeCount++;
                    vertexIdMap.put(vertexStringId, vertexObj);
                }
                else if (elementName.equals(GraphMLTokens.EDGE)){
                    edgeObj = new Long(inserter.createRelationship(inVertex.longValue(), outVertex.longValue(), DVNGraphImpl.relType.DEFAULT, dummyMap));
                    bindProperties(conn, elementName, edgeObj, propertyMap, EdgeKeyTypesMaps);
                    edgeCount++;
                }
                if((nodeCount%10000)==1 || (edgeCount%10000)==1){
                    conn.commit();
                    System.out.println(String.format("%d Nodes and %d Edges inserted.", nodeCount, edgeCount));
                }
            }
        }
        /*index.optimize();*/
        conn.commit();
        cachePropertyTypes(inserter, NodeKeyTypesMaps, EdgeKeyTypesMaps);
        createIndices(conn, GraphMLTokens.NODE, NodeKeyTypesMaps);
        conn.commit();
        createIndices(conn, GraphMLTokens.EDGE, EdgeKeyTypesMaps);
        conn.commit();
        conn.close();
        reader.close();
    }

    public static Object typeCastValue(String key, String value, Map<String, String> keyTypes) {
        String type = keyTypes.get(key);
        if (null == type || type.equals(GraphMLTokens.STRING))
            return value;
        else if (type.equals(GraphMLTokens.FLOAT))
            return Float.valueOf(value);
        else if (type.equals(GraphMLTokens.INT))
            return Integer.valueOf(value);
        else if (type.equals(GraphMLTokens.DOUBLE))
            return Double.valueOf(value);
        else if (type.equals(GraphMLTokens.BOOLEAN))
            return Boolean.valueOf(value);
        else if (type.equals(GraphMLTokens.LONG))
            return Long.valueOf(value);
        else
            return value;
    }

    public static boolean configureSQL(Connection conn) throws SQLException{
        boolean ret = true;
        Statement stat = conn.createStatement();
        stat.executeUpdate("PRAGMA SYNCHRONOUS=1;");
        stat.close();
        conn.setAutoCommit(false);
        return true;
    }

    public static boolean initializeVertexTable(Connection conn, Map<String, String> typeMap) throws XMLStreamException, SQLException{
        return initializeTable(conn, GraphMLTokens.NODE, typeMap);
    }

    public static boolean cachePropertyTypes(BatchInserter inserter, Map<String, String> nodeTypeMap,
                                             Map<String, String> edgeTypeMap){
        Map<String, Object> fullTypeMap = new LinkedHashMap<String, Object>();
        for(Map.Entry<String, String> e : nodeTypeMap.entrySet())
            fullTypeMap.put(String.format("node_%s", e.getKey()), e.getValue());
        for(Map.Entry<String, String> e : edgeTypeMap.entrySet())
            fullTypeMap.put(String.format("edge_%s", e.getKey()), e.getValue());
        inserter.setNodeProperties(inserter.getReferenceNode(), fullTypeMap);
        return true;
    }

    public static boolean initializeEdgeTable(Connection conn, Map<String, String> typeMap) throws XMLStreamException, SQLException{
        return initializeTable(conn, GraphMLTokens.EDGE, typeMap);
    }

    public static boolean initializeTable(Connection conn, String element, Map<String, String> typeMap) throws XMLStreamException, SQLException{
        String colName, type, sql;
        String columnString = "uid INTEGER";
        String sqliteType;
        boolean firstElement = true;

        for(Map.Entry<String, String> e : typeMap.entrySet()){
            columnString += ", ";

            colName = e.getKey();
            sqliteType = type = e.getValue();
            if(type.equals(GraphMLTokens.STRING))
                sqliteType = "TEXT";
            else if(type.equals(GraphMLTokens.DOUBLE) || type.equals(GraphMLTokens.FLOAT))
                sqliteType = "REAL";
            else if(type.equals(GraphMLTokens.INT) || type.equals(GraphMLTokens.BOOLEAN) ||
                    type.equals(GraphMLTokens.LONG))
                sqliteType = "INTEGER";
            else
                throw new XMLStreamException(String.format("Unsupported type %s in %s keys.", type, element));

            columnString += String.format("%s %s", colName, sqliteType);
        }

        sql = String.format("CREATE TABLE %s_props (%s);", element, columnString);
        //System.out.println(sql);
        return conn.prepareStatement(sql).execute();
    }

    public static boolean bindProperties(Connection conn, String elementType, Long id, Map<String, String> properties, Map<String, String> typeMap)
        throws XMLStreamException, SQLException {
        String colName, value, type, sql;
        String colString = "uid";
        String valString = String.valueOf(id);
        boolean firstElement = true;
        boolean ret;
        for(Map.Entry<String, String> e : properties.entrySet()){
            colString += ", ";
            valString += ", ";

            colName = e.getKey();
            value = e.getValue();
            type = typeMap.get(colName);

            if(type==null || type.equals(GraphMLTokens.STRING))
                value = String.format("\'%s\'", value);
            else if(type.equals(GraphMLTokens.BOOLEAN))
                value = Boolean.valueOf(value) ? "1" : "0";

            colString += colName;
            valString += value;
        }
        sql = String.format("INSERT INTO %s_props (%s) VALUES (%s);", elementType, colString, valString);
//        System.out.println(sql);
        Statement stat = conn.createStatement();
        ret = (stat.executeUpdate(sql)==0); 
        stat.close();
        return ret;
    }

    public static boolean createIndices(Connection conn, String elem, Map<String, String> typeMap) throws SQLException {
        String sql = "CREATE INDEX %s_%s ON %s_props(%s);";
        boolean ret = true;

        for(String k : typeMap.keySet()){
            //System.out.println(String.format(sql, elem, k, elem, k));
            conn.prepareStatement(String.format(sql, elem, k, elem, k)).executeUpdate();
        }

        return ret;
    }
}
