package edu.harvard.iq.dvn.networkData;

import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import javax.xml.stream.XMLStreamException;

import java.io.OutputStream;
import java.io.FileOutputStream;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.RelationshipExpander;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.transaction.TxModule;
import org.neo4j.kernel.impl.transaction.XaDataSourceManager;
import org.neo4j.kernel.impl.transaction.xaframework.XaDataSource;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class DVNGraphImpl implements DVNGraph, edu.uci.ics.jung.graph.Graph<LazyNode2, LazyRelationship2> {
    public enum relType implements RelationshipType {
        ACTIVE, ACTIVE_SUB, ACTIVE_NEXT, ACTIVE_NEXT_SUB,
            COMPONENT, COMPONENT_SUB, OWNS, DEFAULT
    }

    public enum flushMode {
        NODE_ONLY, RELATIONSHIP_ONLY, FULL
    }

    public enum elementType {
        NODE, RELATIONSHIP 
    }

    public long taggedNodes;
    private long writeCount;

    private final long TXN_LIMIT = 100000;
    private final long NEO_CACHE_LIMIT = 50000;

    //private final Graph graph;
    private final EmbeddedGraphDatabase neo;
    private final String GraphDatabaseName;

    private final UUID myId;

    private Connection conn;
    private String prop_db;

    private List<String> nodeUserProperties;
    private List<String> relationshipUserProperties;

    private static final Boolean True = new Boolean(true);
    private static final Boolean False = new Boolean(false);

    private TraversalDescription defaultDesc;

    private static final class ActiveExpander implements RelationshipExpander{
        private Direction dir;

        public ActiveExpander(Direction d){
            this.dir = d;
        }

        public Iterable<Relationship> expand(Node node){
            ArrayList<Relationship> rels = new ArrayList();
            for(Relationship r : node.getRelationships(dir)){
                if(r.getOtherNode(node).hasRelationship(relType.ACTIVE_SUB, Direction.INCOMING)){
                    //if(r.getType().equals(relType.DEFAULT) && r.hasProperty("active"))
                        rels.add(r);
                }
            }
            return rels;
        }

        public RelationshipExpander reversed(){
            return new ActiveExpander(dir.reverse());
        }
    }

    private static final class DepthPruner implements PruneEvaluator{
        private int n;

        public DepthPruner(int n){
            this.n = n;
        }

        public boolean pruneAfter(Path pos){
            return pos.length() >= n;
        }
    }
 
    public DVNGraphImpl(EmbeddedGraphDatabase neo_arg, String propertyDb)
        throws ClassNotFoundException{
        String driverName = "org.sqlite.JDBC";
        this.neo= neo_arg;
        neo.loadConfigurations("neodb.props");
        this.GraphDatabaseName = neo.getStoreDir();

        this.myId = UUID.randomUUID();
        nodeUserProperties = new ArrayList<String>();
        relationshipUserProperties = new ArrayList<String>();

        Traversal travFac = new Traversal();
        defaultDesc = travFac.description()
                             .breadthFirst()
                             .filter(new Predicate<Path>(){
                                            public boolean accept(Path item){
                                                return true;
                                            }
                                     });

        Class.forName(driverName);
        this.prop_db = propertyDb;

        try{
            conn = DriverManager.getConnection("jdbc:sqlite:"+propertyDb);
            conn.setReadOnly(true);
        }
        catch(SQLException e){
            System.err.println(e.getMessage());
        }

        TxModule txModule = ((EmbeddedGraphDatabase) neo).getConfig().getTxModule();
        XaDataSourceManager xaDsMgr = txModule.getXaDataSourceManager();
        XaDataSource xaDs = xaDsMgr.getXaDataSource( "nioneodb" );
        xaDs.setAutoRotate( false );

        writeCount = 0;
    }

    public DVNGraphImpl(String GraphDatabaseName, String propertyDb)
        throws ClassNotFoundException{
        this(new EmbeddedGraphDatabase(GraphDatabaseName), propertyDb);
    }

    public boolean addVertex(final LazyNode2 vertex) {
        Node n;
        n = neo.getNodeById(vertex.getId());
        if (null != n){
            addVertex(n);
        }
        else
            return false;
        return true;
    }

    private void addVertex(Node n){
        getActiveNode().createRelationshipTo(n, relType.ACTIVE_SUB);
        Relationship rel = n.getSingleRelationship(relType.ACTIVE_NEXT_SUB, Direction.INCOMING);
        if(rel != null)
            rel.delete();
    }

    public boolean removeVertex(final LazyNode2 vertex) {
        Node n;
        n = neo.getNodeById(vertex.getId());
        if(null != n){
            removeVertex(n);
        }
        else
            return false;
        return true;
    }

    private void removeVertex(Node n) {
        Relationship rel = n.getSingleRelationship(relType.ACTIVE_SUB, Direction.INCOMING);
        if(rel != null)
            rel.delete();
        /*
        for(rel : Node n.getRelationships(relType.DEFAULT, Direction.BOTH))
            rel.removeProperty("active");
            */
    }
    
    public boolean containsVertex(final LazyNode2 vertex) {
        Node l = neo.getNodeById(vertex.getId());
        if(null != l && isActive(l))
            return true;
        return false;
    }

    public int getVertexCount() {
        int i = 0;
        Iterator<Relationship> actives = neo.getReferenceNode().
                getSingleRelationship(relType.ACTIVE, Direction.OUTGOING).
                getEndNode().
                getRelationships(relType.ACTIVE_SUB, Direction.OUTGOING).iterator();

        for(i=0; actives.hasNext(); i++){ actives.next();}
        return i;
    }

    public boolean addEdge(final LazyRelationship2 edge){
        Relationship rel = neo.getRelationshipById(edge.getId());
        if(rel != null){
            addEdge(rel);
        }
        else
            return false;
        return true;        
    }

    private void addEdge(Relationship rel){
        rel.removeProperty("activeNext");
        rel.setProperty("active", True);
        for(Node n : rel.getNodes()){
            if(!isActive(n))
                addVertex(n);
        }
    }

    public boolean addEdge(final LazyRelationship2 edge, final Collection<? extends LazyNode2> vertices) {
        if (vertices.size() == 2) {
            return this.addEdge(edge);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public boolean addEdge(final LazyRelationship2 edge, final Collection<? extends LazyNode2> vertices, final EdgeType edgeType) {
        return this.addEdge(edge);
    }

    public boolean addEdge(final LazyRelationship2 edge, final LazyNode2 outVertex, final LazyNode2 inVertex) {
        this.addEdge(edge); 
        //this.addEdge(edge, outVertex, inVertex, EdgeType.valueOf(edge.getLabel()));
        return true;
    }

    public boolean addEdge(final LazyRelationship2 edge, final LazyNode2 outVertex, final LazyNode2 inVertex, final EdgeType edgeType) {
        return this.addEdge(edge);
        /*
        LazyRelationship2 l = relIdx.get(edge.getId());
        LazyNode2 n1 = nodeIdx.get(outVertex.getId());
        LazyNode2 n2 = nodeIdx.get(inVertex.getId());

        //If nodes don't exist, dealbreaker
        if (null == n1 || null == n2)
            return false;

        //If nodes exist but aren't active, silently activate
        if (!n1.isActive()){
            n1.setActive(True);
            nodeIdx.put(n1);
        }
        if (!n2.isActive()){
            n2.setActive(True);
            nodeIdx.put(n2);
        }
        
        //If edge exists activate
        if (null != l){
            l.setActive(True);
            relIdx.put(l);
        }

        else
            return false;

        return true;
        */
    }

    public boolean removeEdge(final LazyRelationship2 edge) {
        //If edge exists, deactivate
        Relationship rel = neo.getRelationshipById(edge.getId());
        if (rel != null){
            removeEdge(rel);
        }
        else
            return false;
        return true;
    }

    private void removeEdge(Relationship rel){
        rel.removeProperty("activeNext");
        rel.removeProperty("active");
    }

    public boolean containsEdge(final LazyRelationship2 edge) {
        Relationship rel = neo.getRelationshipById(edge.getId());
        if(null != rel && isActive(rel))
            return true;
        return false;
    }


    public int getEdgeCount(final EdgeType edgeType) {
        return (int)this.getEdgeCount();
    }

    public int getEdgeCount(){
        long travCount = 0, relCount = 0;
       
        for(Relationship act : getActiveNode().getRelationships(relType.ACTIVE_SUB, Direction.OUTGOING)){
            for(Relationship r : act.getEndNode().getRelationships(relType.DEFAULT, Direction.OUTGOING)){
                if(r.hasProperty("active") && isActive(r.getEndNode())) relCount++;
                if((travCount++%NEO_CACHE_LIMIT)==0){
                    //System.out.println(relCount);
                    clearNeoCache();
                }
            }
        }

        return (int)relCount;
    }

    public Collection<LazyRelationship2> getEdges(final EdgeType edgeType) {
        return this.getEdges();
    }

    /*
     * This function is dangerous to call -- will OOM very easily
     */
    public Collection<LazyRelationship2> getEdges() {
        long relCount=0;
        ArrayList l = new ArrayList();

        for(Path e : getActiveTraverser()){
            for(Path p : getFlushTraverser(e.endNode())){
                if((relCount++%NEO_CACHE_LIMIT)==0){
                    l.add(p.lastRelationship());
                    clearNeoCache();
                }
            }
        }
        return l; 
    }

    public LazyRelationship2 findEdge(final LazyNode2 outVertex, final LazyNode2 inVertex) {
        if(!this.containsVertex(outVertex) || !this.containsVertex(inVertex))
            return null;

        Node outN = neo.getNodeById(outVertex.getId());
        Node inN = neo.getNodeById(inVertex.getId());
        for(Relationship edge : outN.getRelationships()){
            if(edge.getOtherNode(outN).equals(inN)){
                if(isActive(edge))
                    return new LazyRelationship2(edge.getId());
            }
        }
        return null;
    }

    public Collection<LazyRelationship2> findEdgeSet(final LazyNode2 outVertex, final LazyNode2 inVertex) {
        if(!this.containsVertex(outVertex) || !this.containsVertex(inVertex))
            return null;

        Node outN = neo.getNodeById(outVertex.getId());
        Node inN = neo.getNodeById(inVertex.getId());
        Set<LazyRelationship2> edges = new HashSet<LazyRelationship2>();

        LazyRelationship2 rel;
        for (Relationship edge : outN.getRelationships()) {
            if (edge.getOtherNode(outN).equals(inN)){
                if(isActive(edge))
                    edges.add(new LazyRelationship2(edge.getId()));
            }
        }
        return edges;
    }

    public boolean isIncident(final LazyNode2 vertex, final LazyRelationship2 edge) {
        if(!this.containsVertex(vertex) || !this.containsEdge(edge))
            return false;

        Relationship r = neo.getRelationshipById(edge.getId());
        Node n = neo.getNodeById(vertex.getId());
        return r.getNodes()[0].equals(n) || r.getNodes()[1].equals(n);
    }

    public Collection<LazyRelationship2> getIncidentEdges(final LazyNode2 vertex) {
        Set<LazyRelationship2> edges = new HashSet<LazyRelationship2>();
        Node n = neo.getNodeById(vertex.getId());
        LazyRelationship2 tmp;
        for (Relationship r : n.getRelationships()) {
            if(isActive(r))
                edges.add(new LazyRelationship2(r.getId()));
        }
        return edges;
    }

    public EdgeType getDefaultEdgeType() {
        return EdgeType.UNDIRECTED;
    }

    public EdgeType getEdgeType(final LazyRelationship2 edge) {
        return EdgeType.UNDIRECTED;
    }

    public int getIncidentCount(final LazyRelationship2 edge) {
            return 2;
    }
    

    public Collection<LazyNode2> getVertices() {
        //TODO: Do this with a StoredSortedMap
        List<LazyNode2> l = new ArrayList();

        for(Path p : getActiveTraverser())
            //TODO: Add cache clearing
            l.add(new LazyNode2(p.endNode().getId()));

        return l;
    }

    public Collection<LazyNode2> getIncidentVertices(final LazyRelationship2 edge) {
        Relationship r = neo.getRelationshipById(edge.getId());

        List<LazyNode2> vertices = new ArrayList<LazyNode2>();
        vertices.add(new LazyNode2(r.getStartNode().getId()));
        vertices.add(new LazyNode2(r.getEndNode().getId()));
        return vertices;
    }

    public LazyNode2 getDest(final LazyRelationship2 edge) {
        if(!this.containsEdge(edge))
            return null;

        Relationship r = neo.getRelationshipById(edge.getId());

        return new LazyNode2(r.getEndNode().getId());
    }

    public LazyNode2 getSource(final LazyRelationship2 edge) {
        if(!this.containsEdge(edge))
            return null;

        Relationship r = neo.getRelationshipById(edge.getId());

        return new LazyNode2(r.getStartNode().getId());
    }

    public Pair<LazyNode2> getEndpoints(final LazyRelationship2 edge) {
        if(!this.containsEdge(edge))
            return null;

        Relationship r = neo.getRelationshipById(edge.getId());
        
        return new Pair<LazyNode2>(new LazyNode2(r.getStartNode().getId()),
                                  new LazyNode2(r.getEndNode().getId()));
    }

    public boolean isNeighbor(final LazyNode2 outVertex, final LazyNode2 inVertex) {
        Node n1 = neo.getNodeById(outVertex.getId());
        Node n2 = neo.getNodeById(inVertex.getId());

        for (Relationship r : n1.getRelationships(relType.DEFAULT, Direction.BOTH)) {
            if (r.getOtherNode(n1).equals(n2) &&
                isActive(r))
                    return true;
        }
        return false;
    }

    //TODO: Check to make sure that you can hash a Node
    public int getNeighborCount(final LazyNode2 vertex) {
        return getNeighborCount(neo.getNodeById(vertex.getId()));
    }

    private int getNeighborCount(Node n){
        Set<Long> vertices = new HashSet<Long>();
        for(Relationship r: n.getRelationships(relType.DEFAULT, Direction.BOTH)){
            if(r.hasProperty("active") && isActive(r.getOtherNode(n)))
                vertices.add(r.getOtherNode(n).getId());
        }
        return vertices.size();
    }

    public Collection<LazyNode2> getNeighbors(final LazyNode2 vertex) {
        Node n = neo.getNodeById(vertex.getId());
        Set<LazyNode2> vertices = new HashSet<LazyNode2>();
        LazyNode2 tmp;
        Node neo_tmp;
        for (Relationship r : n.getRelationships()) {
            if(isActive(r.getOtherNode(n)))
                vertices.add(new LazyNode2(r.getOtherNode(n).getId()));
            
        }
        return vertices;
    }

    public LazyNode2 getOpposite(final LazyNode2 vertex, final LazyRelationship2 edge) {
        Node n = neo.getNodeById(vertex.getId());
        Relationship r = neo.getRelationshipById(edge.getId());

        return new LazyNode2(r.getOtherNode(n).getId());
    }

    public Collection<LazyRelationship2> getOutEdges(final LazyNode2 vertex) {
        Node n = neo.getNodeById(vertex.getId());
        List<LazyRelationship2> relList = new ArrayList<LazyRelationship2>();

        for( Relationship r : n.getRelationships(relType.DEFAULT, Direction.OUTGOING) ){
            if(isActive(r))
                relList.add(new LazyRelationship2(r.getId()));
        }
        return relList;
    }

    public Collection<LazyRelationship2> getInEdges(final LazyNode2 vertex) {
        Node n = neo.getNodeById(vertex.getId());
        List<LazyRelationship2> relList = new ArrayList<LazyRelationship2>();

        for( Relationship r : n.getRelationships(relType.DEFAULT, Direction.INCOMING) ){
            if(isActive(r))
                relList.add(new LazyRelationship2(r.getId()));
        }
        return relList;
    }
    
    //TODO: Check that you can actually hash a Node
    public int getPredecessorCount(final LazyNode2 vertex) {
        Set<Node> vertices = new HashSet<Node>();

        Node n = neo.getNodeById(vertex.getId());

        for(Relationship r : n.getRelationships(relType.DEFAULT, Direction.INCOMING)){
            if(isActive(r))
                vertices.add(r.getStartNode());
        }
        n = null; 
                
        return vertices.size();
    }

    public Collection<LazyNode2> getPredecessors(final LazyNode2 vertex) {
        Set<LazyNode2> vertices = new HashSet<LazyNode2>();

        Node n = neo.getNodeById(vertex.getId());

        for(Relationship r : n.getRelationships(relType.DEFAULT, Direction.INCOMING)) {
            if(isActive(r))
                vertices.add(new LazyNode2(r.getStartNode().getId()));
        }
        n = null;

        return vertices;
    }

    public int getSuccessorCount(final LazyNode2 vertex) {
        Set<Node> vertices = new HashSet<Node>();

        Node n = neo.getNodeById(vertex.getId());

        for(Relationship r : n.getRelationships(relType.DEFAULT, Direction.OUTGOING)){
            if(isActive(r))
                vertices.add(r.getEndNode());
        }
        n = null; 
                
        return vertices.size();
    }

    public Collection<LazyNode2> getSuccessors(final LazyNode2 vertex) {
        Set<LazyNode2> vertices = new HashSet<LazyNode2>();

        Node n = neo.getNodeById(vertex.getId());

        for(Relationship r : n.getRelationships(relType.DEFAULT, Direction.OUTGOING)) {
            if(isActive(r))
                vertices.add(new LazyNode2(r.getEndNode().getId()));
        }
        n = null;

        return vertices;
    }

    private int degree(Node n, Direction d){
        int count = 0;
        for(Relationship r : n.getRelationships(relType.DEFAULT, d)){
            if(isActive(r)) count++;
        }
        return count;
    }

    public int inDegree(final LazyNode2 vertex) {
        return degree(neo.getNodeById(vertex.getId()), Direction.INCOMING); 
    }

    public int outDegree(final LazyNode2 vertex) {
        return degree(neo.getNodeById(vertex.getId()), Direction.OUTGOING); 
    }


    public int degree(final LazyNode2 vertex) {
        return degree(neo.getNodeById(vertex.getId()), Direction.BOTH);
    }

    public boolean isDest(final LazyNode2 vertex, final LazyRelationship2 edge) {
        Node n = neo.getNodeById(vertex.getId());
        Relationship r = neo.getRelationshipById(edge.getId());

        return r.getEndNode().equals(n);
    }

    public boolean isSource(final LazyNode2 vertex, final LazyRelationship2 edge) {
        Node n = neo.getNodeById(vertex.getId());
        Relationship r = neo.getRelationshipById(edge.getId());

        return r.getStartNode().equals(n);
    }

    public boolean isPredecessor(final LazyNode2 outVertex, final LazyNode2 inVertex) {
        Node n1 = neo.getNodeById(outVertex.getId());
        Node n2 = neo.getNodeById(inVertex.getId());

        for (Relationship r : n1.getRelationships(relType.DEFAULT, Direction.OUTGOING)) {
            if(r.getEndNode().equals(n2) && isActive(r)){
                return true;
            }
        }
        return false;
    }

    public boolean isSuccessor(final LazyNode2 outVertex, final LazyNode2 inVertex) {
        Node n1 = neo.getNodeById(outVertex.getId());
        Node n2 = neo.getNodeById(inVertex.getId());

        for (Relationship r : n1.getRelationships(Direction.INCOMING)) {
            if(r.getEndNode().equals(n2) && isActive(r)){
                return true;
            }
        }
        return false;
    }

    /* Implementation-specific routines */
    public boolean initialize(){
        long e=0, nm=0;
        Node refNode, activeNode, activeNextNode, componentNode;

        Transaction tx = neo.beginTx();

        System.out.println("Starting hash table initialization...");
        long startTimeMs = System.currentTimeMillis();
        
        try {
            refNode = neo.getReferenceNode();

            if(!refNode.hasRelationship(relType.ACTIVE, Direction.OUTGOING)){
                activeNode = neo.createNode();
                refNode.createRelationshipTo(activeNode, relType.ACTIVE);
            }
            else activeNode = getActiveNode();

            if(!refNode.hasRelationship(relType.ACTIVE_NEXT, Direction.OUTGOING)){
                activeNextNode = neo.createNode();
                refNode.createRelationshipTo(activeNextNode, relType.ACTIVE_NEXT);
            }
            else activeNextNode = getActiveNextNode();

            if(!refNode.hasRelationship(relType.COMPONENT, Direction.OUTGOING)){
                componentNode = neo.createNode();
                refNode.createRelationshipTo(componentNode, relType.COMPONENT);
            }
            else{
                componentNode = getComponentNode();
                clearComponents();
                tx.success();
                tx.finish();
                tx = neo.beginTx();
            }

            for(Node n : neo.getAllNodes()){
                if(n.equals(refNode) || n.equals(activeNode) ||
                   n.equals(activeNextNode) || n.equals(componentNode))
                    continue;

                if(!isActive(n))
                    activeNode.createRelationshipTo(n, relType.ACTIVE_SUB);
                ++nm;

                for(Relationship r : n.getRelationships(Direction.OUTGOING)){
                    r.setProperty("active", True);
                    if((++e%100000)==0 || (nm%100000)==0){
                        clearNeoCache();
                        tx.success();
                        tx.finish();
                        System.out.println(e+ " edges.");
                        System.out.println(n+ " nodes.");
                        tx = neo.beginTx();
                    }

                }
            }
            tx.success();
        }
        finally {
            tx.finish();
        }
        System.out.println(nm + " nodes activated.");
        System.out.println("Tagging components.");
        tagComponents();
        System.out.println("Initialization task took " + (((double)(System.currentTimeMillis()-startTimeMs))/1000) + " seconds.");
        return true;
    }

    private Node getActiveNode(){
        return neo.getReferenceNode().
                getSingleRelationship(relType.ACTIVE, Direction.OUTGOING).
                getEndNode();
    }

    private Node getActiveNextNode(){
        return neo.getReferenceNode().
                getSingleRelationship(relType.ACTIVE_NEXT, Direction.OUTGOING).
                getEndNode();
    }

    private Node getComponentNode(){
        return neo.getReferenceNode().
                getSingleRelationship(relType.COMPONENT, Direction.OUTGOING).
                getEndNode();
    }

    private boolean isActive(Node node){
        return !(null==node.getSingleRelationship(relType.ACTIVE_SUB, Direction.INCOMING));
    }

    private void setActive(Node n, boolean yes){
        Relationship rel;
        if(yes)
            getActiveNode().createRelationshipTo(n, relType.ACTIVE_SUB);
        else{
            rel = n.getSingleRelationship(relType.ACTIVE_SUB, Direction.INCOMING);
            if(rel != null)
                rel.delete();
        }
    }

    private boolean isActiveNext(Node node){
        return !(null==node.getSingleRelationship(relType.ACTIVE_NEXT_SUB, Direction.INCOMING));
    }

    private void setActiveNext(Node n, boolean yes){
        Relationship rel;
        if(yes)
            getActiveNextNode().createRelationshipTo(n, relType.ACTIVE_NEXT_SUB);
        else{
            rel = n.getSingleRelationship(relType.ACTIVE_NEXT_SUB, Direction.INCOMING);
            if(rel != null)
                rel.delete();
        }
    }

    private boolean isActive(Relationship rel){
        return isActive(rel.getStartNode()) && isActive(rel.getEndNode()) && rel.hasProperty("active");
    }

    private void setActiveNext(Relationship rel, boolean yes){
        if(!isActiveNext(rel.getStartNode())) setActiveNext(rel.getStartNode(), true);
        if(!isActiveNext(rel.getEndNode())) setActiveNext(rel.getEndNode(), true);
        rel.setProperty("activeNext", True);
    }

    public int getNumActiveNext(){
        int i=0;
        for( Relationship r : 
        getActiveNextNode().getRelationships(relType.ACTIVE_NEXT_SUB, Direction.OUTGOING))
            i++;
        return i;
    }

    public void finalize(){
        this.neo.shutdown();
        try{
            this.conn.close();
        }
        catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }
   
    public void markNodesByProperty(String query) throws SQLException{
        Transaction tx;
        Node n;
        String inStatement = "";
        String sql_format = "select uid from node_props where uid in (%s) and (%s);";
        Statement stat = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        int batch_size = 1000;
        boolean firstInList = true;
        long startTimeMs = System.currentTimeMillis();
        long currentId = 0, travCount = 0, relCount = 0, newRelCount=0;
        ResultSet rs;

        tx = neo.beginTx();
        try{
            for(Relationship act : getActiveNode().getRelationships(relType.ACTIVE_SUB, Direction.OUTGOING)){
                n = act.getEndNode();
                if(isActive(n)){
                    if(firstInList)
                        firstInList = false;
                    else
                        inStatement+=",";
                    inStatement+= String.format("%d", n.getId());

                    if((++relCount%batch_size)==0){
                        rs = stat.executeQuery(String.format(sql_format, inStatement, query));
                        while(rs.next()){
                            travCount++;
                            newRelCount++;
                            setActiveNext(neo.getNodeById(rs.getLong(1)), true);
                        }
                        rs.close();
                        tx.success();
                        tx.finish();
                        tx = neo.beginTx();
                        firstInList = true;
                        inStatement = "";
                    }

                    if(travCount++ > NEO_CACHE_LIMIT){
                        travCount %= NEO_CACHE_LIMIT;
                        clearNeoCache();
                    }
                }
            }
            rs = stat.executeQuery(String.format(sql_format, inStatement, query));
            while(rs.next()) {
                newRelCount++;
                setActiveNext(neo.getNodeById(rs.getLong(1)), true);
            }

            tx.success();
        } finally {
            tx.finish();
        }
        System.out.println("New Nodes: " + newRelCount);
        System.out.println("Node selection task took " + (((double)(System.currentTimeMillis()-startTimeMs))/1000) + " seconds.");
        flushMarks(flushMode.NODE_ONLY);
    }


    public void markRelationshipsByProperty(String query, boolean dropDisconnected)throws SQLException{
        Transaction tx;
        String inStatement = "";
        String sql_format = "select uid from edge_props where uid in (%s) and (%s);";
        Statement stat = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        int batch_size = 1000;
        boolean firstInList = true;
        long startTimeMs = System.currentTimeMillis();
        long currentId = 0, travCount = 0, relCount = 0, newRelCount=0;
        ResultSet rs;

        tx = neo.beginTx();
        try{
            for(Relationship act : getActiveNode().getRelationships(relType.ACTIVE_SUB, Direction.OUTGOING)){
                for(Relationship r : act.getEndNode().getRelationships(relType.DEFAULT, Direction.OUTGOING)){
                    if(r.hasProperty("active") && isActive(r.getEndNode())){
                        if(firstInList)
                            firstInList = false;
                        else
                            inStatement+=",";
                        inStatement+= String.format("%d", r.getId());

                        if((++relCount%batch_size)==0){
                            rs = stat.executeQuery(String.format(sql_format, inStatement, query));
                            while(rs.next()){
                                travCount++;
                                newRelCount++;
                                setActiveNext(neo.getRelationshipById(rs.getLong(1)), true);
                            }
                            rs.close();
                            tx.success();
                            tx.finish();
                            tx = neo.beginTx();
                            firstInList = true;
                            inStatement = "";
                        }

                        if(travCount++ > NEO_CACHE_LIMIT){
                            travCount %= NEO_CACHE_LIMIT;
                            clearNeoCache();
                        }
                    }
                }
            }
            rs = stat.executeQuery(String.format(sql_format, inStatement, query));
            while(rs.next()) 
                setActiveNext(neo.getRelationshipById(rs.getLong(1)), true);

            tx.success();
        } finally {
            tx.finish();
        }
        System.out.println("Edge selection task took " + (((double)(System.currentTimeMillis()-startTimeMs))/1000) + " seconds.");
        if(dropDisconnected)
            flushMarks(flushMode.FULL);
        else
            flushMarks(flushMode.RELATIONSHIP_ONLY);
    }

    public void markNodeNeighborhood(long n_num, final int nth){
        Transaction tx = neo.beginTx();
        try{
            markNodeNeighborhood(neo.getNodeById(n_num), nth);
            tx.success();
        }
        finally{
            tx.finish();
        }
    }
    private void markNodeNeighborhood(Node n, final int nth){
        Traverser trav = getNeighborhoodTraverser(n, nth);
        Node tmp_n;
        Relationship tmp_r;
        int i,j;

        /*
        //DEBUG
        i=0;
        for(Relationship r : n.getRelationships()){
            i++;
        }
        System.out.println("Degree: " + i);
        //END DEBUG
        */

        i=0;
        j=0;
        for(Path p : trav){
            tmp_n = p.endNode();
            if(p.lastRelationship()==null) continue;
            if(p.length() <= nth && !isActiveNext(tmp_n)){
                setActiveNext(tmp_n, true);
                i++;
            }

            tmp_r = p.lastRelationship();

            if(isActiveNext(tmp_n) && !tmp_r.hasProperty("activeNext")){
                tmp_r.setProperty("activeNext", True);
                j++;
            }
        }

        //System.out.println(i + " nodes and " + j + " relationships activated.");
    }

    public void markNeighborhood(int nth){
        long i=0;
        Transaction tx = neo.beginTx();
        try{
            for(Path p : getActiveTraverser()){
                //System.out.println(n.getId());
                markNodeNeighborhood(p.endNode(), nth);
                if((i%TXN_LIMIT)==0){
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                }
            }
            tx.success();
        }
        finally{
            tx.finish();
        }
        this.flushMarks(flushMode.FULL);
    }

    private Traverser getActiveTraverser(){
        Traversal travFac = new Traversal();
        TraversalDescription activeTravDesc;

        PruneEvaluator pruning = new DepthPruner(1);

        Predicate allButStart = 
            new Predicate<Path>(){
                public boolean accept(Path item){
                    return !item.endNode().equals(getActiveNode());
                }
            };


        activeTravDesc = travFac.description().breadthFirst().
                            prune(pruning).relationships(relType.ACTIVE_SUB).
                            filter(allButStart).
                            uniqueness(Uniqueness.NODE_GLOBAL);

        return activeTravDesc.traverse(getActiveNode());
    }

    private Traverser getFlushTraverser(Node n){
        Traversal travFac = new Traversal();
        TraversalDescription flushTravDesc;

        PruneEvaluator pruning = new DepthPruner(1);

        Predicate allButStart = 
            new Predicate<Path>(){
                public boolean accept(Path item){
                    return !item.endNode().equals(getActiveNode());
                }
            };


        Predicate onlyDefaultEdges =
            new Predicate<Path>(){
                public boolean accept(Path item){
                    return item.lastRelationship()==null ?
                        false : item.lastRelationship().getType().equals(relType.DEFAULT);
                }
            };

        Predicate hasLastEdge =
            new Predicate<Path>(){
                public boolean accept(Path item){
                    return item.lastRelationship()!=null;
                }
            };
        
        flushTravDesc = travFac.description().breadthFirst().
                          prune(pruning).
                          expand(new ActiveExpander(Direction.OUTGOING)).
                          filter(allButStart).
                          filter(hasLastEdge).
                          //filter(onlyDefaultEdges).
                          uniqueness(Uniqueness.RELATIONSHIP_GLOBAL);

        return flushTravDesc.traverse(n);
    }

    private Traverser getNeighborhoodTraverser(Node n, final int nth){
        Traversal travFac = new Traversal();
        TraversalDescription neighDesc;

        PruneEvaluator pruning =  new DepthPruner(nth+1);

        Predicate onlyDefaultEdges =
            new Predicate<Path>(){
                public boolean accept(Path item){
                    return item.lastRelationship()==null ?
                        false : item.lastRelationship().getType().equals(relType.DEFAULT);
                }
            };
        
        neighDesc = travFac.description().breadthFirst().
                          prune(pruning).
                          relationships(relType.DEFAULT).
                          filter(onlyDefaultEdges).
                          uniqueness(Uniqueness.RELATIONSHIP_GLOBAL);

        return neighDesc.traverse(n);
    }
/*
    private long tagNodeComponent2(Node node, Long tag){
        Transaction tx;
        Node compNode = neo.createNode();
        getComponentNode().createRelationshipTo(compNode, relType.COMPONENT_SUB);

        TraversalDescription travDesc = defaultDesc;
        Traverser trav;
        long i;

        //travDesc = travDesc.uniqueness(Uniqueness.NODE_GLOBAL).relationships(relType.DEFAULT, Direction.BOTH);
        travDesc = travDesc.uniqueness(Uniqueness.NODE_GLOBAL).expand(new ActiveExpander(Direction.BOTH));


        trav = travDesc.traverse(node);

        tx = neo.beginTx();
        i=0;
        try{
            for(Path p : trav){
                //System.out.println("\tTrav.");
                compNode.createRelationshipTo(p.endNode(), relType.OWNS);
                if((i++%NEO_CACHE_LIMIT)==0){
                    clearNeoCache();
                }

            }

            compNode.setProperty("tag", tag);
            compNode.setProperty("nodeCount", new Long(i));
            tx.success();
        }
        finally{
            tx.finish();
        }
        return i;
    }
    */

    private long compSize(Node n){
        long size=0;
        for(Relationship r : n.getRelationships(relType.OWNS, Direction.OUTGOING)) size++;
        return size;
    }
    
    private boolean tagNodeComponent(Node node, Long tag, Transaction tx){
        //Transaction tx;
        Node compNode;
        Node otherComp;
        Node tmpComp; // for swapping
        Node n;
        //Relationship r;
        boolean newTag = true;

        TraversalDescription travDesc = defaultDesc;
        Traverser trav;

        //PruneEvaluator pruning = new DepthPruner(20);
        PruneEvaluator pruning = new PruneEvaluator(){
            public boolean pruneAfter(Path p){
                return p.length() <= 20 || p.endNode().hasRelationship(relType.OWNS, Direction.INCOMING);
            }
        };

        long i;
        long nodeCount = 0;
        Long size1;

        //travDesc = travDesc.uniqueness(Uniqueness.NODE_GLOBAL).relationships(relType.DEFAULT, Direction.BOTH);
        travDesc = travDesc.uniqueness(Uniqueness.NODE_GLOBAL).
                            //relationships(relType.DEFAULT, Direction.BOTH).
                            expand(new ActiveExpander(Direction.OUTGOING));
                            //prune(pruning);

        trav = travDesc.traverse(node);

        //tx = neo.beginTx();
        i=0;
        //tx = neo.beginTx();
        //try{
            compNode = neo.createNode();
            getComponentNode().createRelationshipTo(compNode, relType.COMPONENT_SUB);
/*
            System.out.print("Component Nodes: ");
            for(Relationship r : getComponentNode().getRelationships(relType.COMPONENT_SUB, Direction.OUTGOING))
                System.out.print(r.getEndNode());
            System.out.print("\n");

            */
            for(Path p : trav){
                //System.out.println("\tTrav." + p.endNode().getId());
                if(p.endNode().hasRelationship(relType.OWNS, Direction.INCOMING)){
                    otherComp = p.endNode().getSingleRelationship(relType.OWNS, Direction.INCOMING).getStartNode();
                    if(otherComp.equals(compNode)) continue;

                    //Determine which has fewer relationships. A good proxy is which has a smaller id. This one will be deleted.
        //            System.out.println(String.format("Found other component. Current component: %s. Other component: %s", compNode, otherComp));
                    size1 = new Long(compSize(compNode));
                    //System.out.println(String.format("Sizes. %s: %d, %s: %d.", compNode, size1, otherComp, (Long)otherComp.getProperty("nodeCount")));
                    if(size1.longValue() > ((Long)otherComp.getProperty("nodeCount")).longValue()) {
                        //swap
                       tmpComp = compNode; compNode = otherComp; otherComp = tmpComp; tmpComp = null;
                    }
                    //System.out.println(String.format("Eliminating componeent " + compNode));

                    for(Relationship r : compNode.getRelationships(relType.OWNS, Direction.OUTGOING)) {
                        n = r.getEndNode();
                        r.delete();
                        otherComp.createRelationshipTo(n, relType.OWNS);
                        writeCount++;
                        /*
                        if(writeCount++ > TXN_LIMIT/2){
                            writeCount %= TXN_LIMIT/2;
                            tx.success();
                            tx.finish();
                            tx = neo.beginTx();
                        }
                       */
                
                    }
                    /*
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                    */

                    compNode.getSingleRelationship(relType.COMPONENT_SUB, Direction.INCOMING).delete();
                    for(Relationship rel : compNode.getRelationships())
                        System.out.println(String.format("%s, %s, %s, %s", rel, rel.getOtherNode(compNode), rel.getType(), rel.getStartNode()));
                    compNode.delete();
                    compNode = otherComp;
                    //System.out.println("Done");

                    

                }
                else//(!p.endNode().hasRelationship(relType.OWNS, Direction.INCOMING))
                    compNode.createRelationshipTo(p.endNode(), relType.OWNS);
                
                if((++nodeCount%(NEO_CACHE_LIMIT*5))==0){
                    System.out.println(nodeCount);
                    System.out.println("clearing...");
                    clearNeoCache();
                    System.out.println("done.");
                }

                if(writeCount++ > TXN_LIMIT){
                    writeCount %= TXN_LIMIT;
                    System.out.println("Committing...");
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                    System.out.println("done.");
                }
                
            }
                  
            if(!compNode.hasProperty("tag"))
                compNode.setProperty("tag", tag);
            else
                newTag = false;

            if(!compNode.hasProperty("nodeCount"))
                compNode.setProperty("nodeCount", new Long(nodeCount));
            else
                compNode.setProperty("nodeCount",
                        new Long(nodeCount) + (Long)compNode.getProperty("nodeCount"));

            taggedNodes += nodeCount;

        //    tx.success();
        //}
        //finally{
        //    tx.finish();
        //}
        return newTag;
    }

    public void tagComponents(){
        Transaction tx;
        long tag = 0, compsize = 0, i=0;
        Node cur;

        clearComponents();

        tx = neo.beginTx();
        try{
        //for(Path p : getActiveTraverser()){
        for(Relationship r : getActiveNode().getRelationships(relType.ACTIVE_SUB, Direction.OUTGOING)){
            cur = r.getEndNode();
            //if((i++%NEO_CACHE_LIMIT)==0)
            //    clearNeoCache();

            if(!cur.hasRelationship(relType.OWNS, Direction.INCOMING)){
                //System.out.println("Found one! " + cur.getId());
                    if(tagNodeComponent(cur, tag, tx))
                        tag++;
                    if((tag > 10000)){
                        tag %= 10000;
                        tx.success();
                        tx.finish();
                        neo.beginTx();
                        //clearNeoCache();
                        System.out.println("Tagged nodes: " + taggedNodes);
                    }
            }
        }
        tx.success();
        } finally {
            tx.finish();
        }
    }

    public void printComponents(){
        Transaction tx;
        Node comp;
        long num=0;

        tx = neo.beginTx();
        try{
            for(Relationship r : getComponentNode().getRelationships(relType.COMPONENT_SUB, Direction.OUTGOING)){
                comp = r.getEndNode();
                num += ((Long)comp.getProperty("nodeCount")).longValue();
                System.out.println("Component " + (Long)comp.getProperty("tag") + ": " +
                                   (Long)comp.getProperty("nodeCount") + " Nodes.");
            }
            System.out.println(num + " nodes total.");
            tx.success();
        }
        finally{
            tx.finish();
        }
    }

    public void clearComponents(){
        Transaction tx;
        boolean bigComp;
        long numComp = 0;
        Node tmp;
        tx = neo.beginTx();

        try{
            for(Relationship r : getComponentNode().getRelationships(relType.COMPONENT_SUB, Direction.OUTGOING)){
                tmp = r.getEndNode();

                for(Relationship sr : tmp.getRelationships(relType.OWNS, Direction.OUTGOING))
                    sr.delete();

                bigComp = (((Long)tmp.getProperty("nodeCount")).longValue() > 10000); 

                r.delete();
                tmp.delete();

                if(bigComp || (numComp++%1000)==0){
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                }
            }
            tx.success();
        }
        finally{
            tx.finish();
        }
    }


    private long degree(Node n){
        long deg=0;
        for(Relationship r : new ActiveExpander(Direction.BOTH).expand(n))
            deg++;
        return deg;
    }

    private Node getNthComponentNode(int nth){
        ArrayList<Node> componentList = new ArrayList();
        Node component;
        int i=0;

        Comparator<Node> compSizeComparator = new Comparator<Node>(){
            public int compare(Node o1, Node o2){
                return (int)(((Long)o1.getProperty("nodeCount")).longValue() -  
                             ((Long)o2.getProperty("nodeCount")).longValue());
            }
            public boolean equals(Node o1, Node o2){
                return (((Long)o1.getProperty("nodeCount")).longValue() ==
                        ((Long)o2.getProperty("nodeCount")).longValue());
            }
        };

        for(Relationship r : getComponentNode().getRelationships(relType.COMPONENT_SUB, Direction.OUTGOING)){ 
            component = r.getEndNode();
            if(i++ >= nth){
                Collections.sort(componentList, compSizeComparator);
                if(compSizeComparator.compare(componentList.get(0), component) < 0)
                    componentList.remove(0);
                else
                    continue;
           }
           componentList.add(component);
        }
        return componentList.get(0);
    }

    public void markComponent(int nth){
        long size=0, target_size, writeCount=0;
        Long key;
        long nodeCount=0;
        Node anNode = getActiveNextNode();
        Transaction tx;
        
        tx = neo.beginTx();
        try{
            for(Relationship r : getNthComponentNode(nth).getRelationships(relType.OWNS, Direction.OUTGOING)){
                anNode.createRelationshipTo(r.getEndNode(), relType.ACTIVE_NEXT_SUB);
                if((++writeCount%TXN_LIMIT)==0){
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                }
                nodeCount++;
            }
            tx.success();
            System.out.println(nodeCount + " nodes activated.");
        }
        finally{
            tx.finish();
        }
        flushMarks(flushMode.NODE_ONLY);
    }

    public void flushMarks(flushMode mode){
        Node cur;
        Relationship rel;
        long nodeCount=0, relCount=0, writeCount=0;

        Transaction tx;
        Traverser flushTrav;
        /* Keep nodes that stay activated */
        tx = neo.beginTx();
        try{
            for(Relationship r : getActiveNode().getRelationships(relType.ACTIVE_SUB, Direction.OUTGOING)){
                cur = r.getEndNode();
                rel = cur.getSingleRelationship(relType.ACTIVE_NEXT_SUB, Direction.INCOMING);
                if(rel==null){
                    removeVertex(cur);
                    writeCount++;
                }
                else{
                    rel.delete();
                    nodeCount++;
                }
                if(writeCount > TXN_LIMIT){
                    writeCount %= TXN_LIMIT;
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                }
            }

            //System.out.println(nodeCount + " nodes stay active.");

            /* Add nodes that get activated */
            for(Relationship r : getActiveNextNode().getRelationships(relType.ACTIVE_NEXT_SUB, Direction.OUTGOING)){
                cur = r.getEndNode();
                if(!isActive(cur)){
                    addVertex(cur);
                    writeCount++;
                    nodeCount++;
                }
                if(writeCount > TXN_LIMIT){
                    writeCount %= TXN_LIMIT;
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                }
            }

            //System.out.println(nodeCount + " nodes total activated.");

            if(!mode.equals(flushMode.NODE_ONLY)){
            for(Path e : getActiveTraverser()){
                for(Path p : getFlushTraverser(e.endNode())){
                    rel = p.lastRelationship();

                    if(rel.hasProperty("activeNext")){
                        ++relCount;
                        if(!rel.hasProperty("active")){
                            rel.setProperty("active", True);
                            writeCount++;
                        }
                    }
                    
                    else{
                        rel.removeProperty("active");
                        writeCount++;
                    }

                    rel.removeProperty("activeNext");
                    writeCount++;
                    if(writeCount > TXN_LIMIT){
                        writeCount %= TXN_LIMIT; 
                        tx.success();
                        tx.finish();
                        tx = neo.beginTx();
                    }
                }
            }
            }
            tx.success();
        }
        finally{
            tx.finish();
        }
        tagComponents();
        //printComponents();
    }

    public void printStatus(){
        //System.out.println(getVertexCount() + " nodes.");
        System.out.println("Nodes: " + 
        getVertexCount() + " Edges: " +  
        getEdgeCount()); 
        //and " + getEdgeCount() + " edges active.");
    }
    

    public Iterable<String> listNodeProperties(){
        return getPropTypes(elementType.NODE).keySet();
    }

    public Iterable<String> listRelationshipProperties(){
        return getPropTypes(elementType.RELATIONSHIP).keySet();
    }
    

    private String registerUserProperty(String name, elementType e, String type){
        String propName = name;
        String localName, globalName;
        Pattern tagFinder = Pattern.compile(String.format("%s_([0-9]*)", propName));
        Matcher m;
        int tag, tagmax=0;
        List<String> userProperties = e.equals(elementType.NODE) ?
                                           nodeUserProperties :
                                           relationshipUserProperties;
        for(String p : userProperties){
            m = tagFinder.matcher(p);
            System.out.println(p);
            if(m.matches()){
                //System.out.println("Found!");
                tag = Integer.valueOf(m.group(1)).intValue();
                if(tag > tagmax) tagmax = tag;
            }
        }
        tagmax++;
        localName = String.format("%s_%d", propName, tagmax);
        globalName = String.format("%s_%s", e.toString().toLowerCase(), localName);
        neo.getReferenceNode().setProperty(globalName, type);
        userProperties.add(localName);
        return localName;
    }

    public String calcPageRank(double d){
        return calcPageRank(d, 10);
    }
   
    public String calcPageRank(double d, int iters){
        PageRank<LazyNode2, LazyRelationship2> pr = new PageRank(this, d);
        LazyNode2 v;
        int i=0;
        long writeCount=0;
        //System.out.println(pr.getTolerance());
        pr.initialize();
        while(i++ < iters){
            pr.step();
            clearNeoCache();
            //System.out.println("Next step!");
        }

        //System.out.println("PageRank task took " + (((double)(System.currentTimeMillis()-startTimeMs))/1000) + " seconds.");
        
        System.out.println(pr.getIterations());
        HashMap<String, Object> propMap;
        Transaction tx = neo.beginTx();
        String name;
        try{
            name = registerUserProperty("PageRank", elementType.NODE, "double");
            for(Path p : getActiveTraverser()){
                //System.out.println(pr.getVertexScore(v));
                v = new LazyNode2(p.endNode().getId());
                p.endNode().setProperty(name, pr.getVertexScore(v));
                if((++writeCount%TXN_LIMIT)==0){
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                }
            }
            tx.success();
        }
        finally{
            tx.finish();
        }
        return name;
    }

    public String calcDegree(){
        Transaction tx;
        Node n;
        long writeCount=0;
        String name;
        tx = neo.beginTx();
        try{
            name = registerUserProperty("Degree", elementType.NODE, "integer");
            for(Path p : getActiveTraverser()){
                n = p.endNode();
                n.setProperty(name, degree(n));
                if((++writeCount%TXN_LIMIT)==0){
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                }
            }
            tx.success();
        }
        finally{
            tx.finish();
        }
        return name;
    }

    public String calcUniqueDegree(){
        Transaction tx;
        Node n;
        long writeCount=0;
        tx = neo.beginTx();
        String name;
        try{
            name = registerUserProperty("UDegree", elementType.NODE, "integer");
            for(Path p : getActiveTraverser()){
                n = p.endNode();
                n.setProperty(name, getNeighborCount(n));
                if((++writeCount%TXN_LIMIT)==0){
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                }
            }
            tx.success();
        }
        finally{
            tx.finish();
        }
        return name;
    }

    public String calcInLargestComponent(){
        Transaction tx;
        Node n;
        Node compNode;
        long writeCount=0;
        boolean yes;
        compNode = getNthComponentNode(1);
        tx = neo.beginTx();
        String name;
        try{
            name = registerUserProperty("InLargestComponent", elementType.NODE, "integer");
            for(Path p : getActiveTraverser()){
                n = p.endNode();
                yes = n.getSingleRelationship(relType.OWNS, Direction.INCOMING).
                    getStartNode().equals(compNode);
                n.setProperty(name, yes ? 1 : 0);
                if((++writeCount%TXN_LIMIT)==0){
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                }
            }
            tx.success();
        } finally{
            tx.finish();
        }
        return name;
    }

    public void printUserProps(){
        Node n;
        for(Path p : getActiveTraverser()){
            n = p.endNode();

            for(String k : n.getPropertyKeys())
                System.out.print(k + ": " + n.getProperty(k) + ", ");
            System.out.print(")\n");
        }
    }

    public void calcBetweenness(){
        System.out.println("hi!");
    }

    private void clearNeoCache(){
        neo.getConfig().getGraphDbModule().getNodeManager().clearCache();
    }

    private SortedMap<String, String> getPropTypes(elementType e){
        Node n = neo.getReferenceNode();
        String elem = e.equals(elementType.NODE) ? "node" : "edge";
        Pattern elemMatches = Pattern.compile(String.format("%s_(.*)", elem));
        Matcher m;
        SortedMap<String, String> propMap = new TreeMap<String, String>();
        for(String p : n.getPropertyKeys()){
            m = elemMatches.matcher(p);
            if(m.matches()){
                propMap.put(m.group(1), (String)n.getProperty(p));
                //System.out.println(m.group(1));
            }
        }
        return propMap;
    }

    private String[] userPropStrings(elementType e){
        String propString = "uid";
        String paramString = "?";
        String colString = "uid INTEGER";
        String joinString = "";
        List<String> userProperties =
            e.equals(elementType.NODE) ? nodeUserProperties :
                                         relationshipUserProperties;
        SortedMap<String, String> types = getPropTypes(e);
        for(String s : userProperties){
            propString += String.format(", %s",s);
            paramString += ", ?";
            colString += String.format(", %s %s", s, types.get(s));
            joinString += String.format(", b.%s",s);
        }
        String[] propStrings = {propString, paramString, colString, joinString};
        return propStrings;
    }

    public void dumpGraphML(String filename){
        OutputStream outfile;
        try{
            outfile = new FileOutputStream(filename);
        } catch(Exception e){
            System.err.println("Problem opening file: " + filename + ". " +
                                e.getMessage());
            return;
        }

        GraphWriter gmw = new GraphMLWriter(outfile, getPropTypes(elementType.NODE),
                                          getPropTypes(elementType.RELATIONSHIP));

        gmw.writeHeader();
        gmw.flush();
        writeVertexTab(gmw);
        gmw.flush();
        
        writeEdgeTab(gmw);
        gmw.flush();
        
        gmw.writeFooter();
        gmw.flush();
        gmw.finalize();

        try{
            outfile.close();
        } catch(Exception e){
            System.err.println("Problem closing file: " + filename + ". " +
                                e.getMessage());
        }
    }

    public void dumpTables(String vertFileName, String edgeFileName, String delim){
        OutputStream vertfile;
        OutputStream edgefile;
        
        try{
            vertfile = new FileOutputStream(vertFileName);
            edgefile = new FileOutputStream(edgeFileName);
        } catch(Exception e){
            System.err.println("Problem opening files: " + vertFileName + " or " +
                                edgeFileName + ". " +
                                e.getMessage());
            return;
        }

        GraphWriter gmw = new DelimitedWriter(vertfile, edgefile,
                                          getPropTypes(elementType.NODE),
                                          getPropTypes(elementType.RELATIONSHIP),
                                          delim);

        gmw.writeHeader();
        gmw.flush();
        writeVertexTab(gmw);
        gmw.flush();
        
        writeEdgeTab(gmw);
        gmw.flush();
        
        gmw.writeFooter();
        gmw.flush();
        gmw.finalize();

        try{
            vertfile.close();
            edgefile.close();
        } catch(Exception e){
            System.err.println("Problem opening files: " + vertFileName + " or " +
                                edgeFileName + ". " +
                                e.getMessage());

        }
    }
    
    public void writeVertexTab(GraphWriter rsw){
        long numCached = 0, batch_size = 500;
        String[] queryParts = userPropStrings(elementType.NODE);
        String insertCommand = String.format("INSERT INTO active_uid(%s) values (%s);",
                                        queryParts[0], queryParts[1]);
        SortedMap<String, String> types = getPropTypes(elementType.NODE);
        ResultSet rs;
        Connection memconn;
        long nodesWritten = 0;
        int i = 1;
        //Create in-memory sqlite table
        try{
            memconn = DriverManager.getConnection("jdbc:sqlite::memory::");
            
            Statement stat;
            stat = memconn.createStatement();
            stat.executeUpdate("DROP TABLE IF EXISTS active_uid;");
            stat.executeUpdate(String.format("CREATE TABLE active_uid (%s);", queryParts[2]));
            stat.executeUpdate(String.format("ATTACH DATABASE \"%s\" as prop;", prop_db));

            memconn.setAutoCommit(false);

            PreparedStatement inserter = memconn.prepareStatement(insertCommand);
            for(Relationship r : getActiveNode().getRelationships(relType.ACTIVE_SUB, Direction.OUTGOING)){
                i=1;
                inserter.setLong(1, r.getEndNode().getId());
                for(String p : nodeUserProperties){
                    if(types.get(p)=="double" && r.getEndNode().hasProperty(p))
                        inserter.setDouble(++i, ((Double)r.getEndNode().getProperty(p)).doubleValue());
                    else if(types.get(p)=="int" && r.getEndNode().hasProperty(p))
                        inserter.setInt(++i, ((Integer)r.getEndNode().getProperty(p)).intValue());
                    else if(r.getEndNode().hasProperty(p))
                        inserter.setString(++i, r.getEndNode().getProperty(p).toString());
                }

                inserter.addBatch();
                if((++numCached%batch_size)==0){
                    inserter.executeBatch();
                    memconn.commit();
                    stat = memconn.createStatement();
                    rs = stat.executeQuery(String.format("select a.*,%s from prop.node_props as a "+ 
                                                      "join active_uid as b on a.uid=b.uid;", queryParts[3]));

                    while(rs.next()){
                        rsw.writeNode(rs);
                        nodesWritten++;
                    }
                    rsw.flush();
                    rs.close();

                    stat.executeUpdate("delete from active_uid;");

                    inserter = memconn.prepareStatement(insertCommand);
                    System.out.println(nodesWritten + " nodes written so far...");
                }
            }
            inserter.executeBatch();
            memconn.commit();
            stat = memconn.createStatement();
            rs = stat.executeQuery(String.format("select a.*%s from prop.node_props as a "+ 
                                              "join active_uid as b on a.uid=b.uid;", queryParts[3]));

            while(rs.next()){
                rsw.writeNode(rs);
                nodesWritten++;
            }
            rsw.flush();
            rs.close();

            memconn.setAutoCommit(true);
            System.out.println(nodesWritten + " nodes written so far...");

            memconn.close();
        } catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public void writeEdgeTab(GraphWriter rsw){
        long numCached = 0, batch_size = 500;
        String[] queryParts = userPropStrings(elementType.RELATIONSHIP);
        String insertCommand = String.format("INSERT INTO active_rel_uid(source, target, %s) values (?, ?, %s);",
                                        queryParts[0], queryParts[1]);
        SortedMap<String, String> types = getPropTypes(elementType.RELATIONSHIP);
        ResultSet rs;
        Connection memconn;
        int i=1;
        long nodesWritten = 0;
        //Create in-memory sqlite table
        try{
            memconn = DriverManager.getConnection("jdbc:sqlite::memory::");
            
            Statement stat;
            stat = memconn.createStatement();
            stat.executeUpdate("DROP TABLE IF EXISTS active_rel_uid;");
            stat.executeUpdate(String.format("CREATE TABLE active_rel_uid (source INTEGER, target INTEGER, %s);", queryParts[2]));
            stat.executeUpdate(String.format("ATTACH DATABASE \"%s\" as prop;", prop_db));

            memconn.setAutoCommit(false);

            PreparedStatement inserter = memconn.prepareStatement(insertCommand);
            for(Relationship act : getActiveNode().getRelationships(relType.ACTIVE_SUB, Direction.OUTGOING)){
                for(Relationship r : act.getEndNode().getRelationships(relType.DEFAULT, Direction.OUTGOING)){
                if(!r.hasProperty("active") || !isActive(r.getEndNode())) continue;
                inserter.setLong(1, r.getStartNode().getId());
                inserter.setLong(2, r.getEndNode().getId());
                inserter.setLong(3, r.getId());
                i=1;
                for(String p : relationshipUserProperties){
                    if(types.get(p)=="double" && r.hasProperty(p))
                        inserter.setDouble(++i, ((Double)r.getProperty(p)).doubleValue());
                    else if(types.get(p)=="int" && r.hasProperty(p))
                        inserter.setInt(++i, ((Integer)r.getProperty(p)).intValue());
                    else if(r.hasProperty(p))
                        inserter.setString(++i, r.getProperty(p).toString());
                }
                inserter.addBatch();
                if((++numCached%batch_size)==0){
                    inserter.executeBatch();
                    memconn.commit();
                    stat = memconn.createStatement();
                    System.out.println(String.format("select a.*%s,b.source,b.target from prop.edge_props as a "+ 
                                                      "join active_rel_uid as b on a.uid=b.uid;", queryParts[3]));
                    rs = stat.executeQuery(String.format("select a.*%s,b.source,b.target from prop.edge_props as a "+ 
                                                      "join active_rel_uid as b on a.uid=b.uid;", queryParts[3]));

                    while(rs.next()){
                        rsw.writeEdge(rs);
                        nodesWritten++;
                    }
                    rsw.flush();
                    rs.close();

                    stat.executeUpdate("delete from active_rel_uid;");

                    inserter = memconn.prepareStatement(insertCommand);
                    System.out.println(nodesWritten + " rels written so far...");
                }
            }
            }
            inserter.executeBatch();
            memconn.commit();
            stat = memconn.createStatement();
            rs = stat.executeQuery(String.format("select a.*%s,b.source,b.target from prop.edge_props as a "+ 
                                              "join active_rel_uid as b on a.uid=b.uid;", queryParts[3]));

            while(rs.next()){
                rsw.writeEdge(rs);
                nodesWritten++;
            }
            rsw.flush();
            rs.close();

            memconn.setAutoCommit(true);
            System.out.println(nodesWritten + " rels written so far...");

            /*
            stat = memconn.createStatement();
            stat.executeUpdate(
                "Create table memorytab as SELECT a.* from prop.node_props as a join active_uid as b on a.uid=b.uid;");
                */
            memconn.close();
        } catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }
}
