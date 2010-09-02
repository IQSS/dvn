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
import java.util.HashMap;
import java.util.LinkedHashMap;
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

import org.neo4j.index.IndexService;
import org.neo4j.index.IndexHits;
import org.neo4j.index.lucene.LuceneIndexService;

import org.neo4j.helpers.Predicate;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.transaction.TxModule;
import org.neo4j.kernel.impl.transaction.XaDataSourceManager;
import org.neo4j.kernel.impl.transaction.xaframework.XaDataSource;


public class DVNGraphImpl implements DVNGraph, edu.uci.ics.jung.graph.Graph<LazyNode2, LazyRelationship2>, GraphWriter {
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

    private boolean dirty;
    private flushMode currentFlushMode;

    private final long TXN_LIMIT = 100000;
    private final long NEO_CACHE_LIMIT = 50000;
    private Transaction writerTx;
    private long writerWriteCount;

    //private final Graph graph;
    private final EmbeddedGraphDatabase neo;
    private final IndexService index;
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
 
    public DVNGraphImpl(EmbeddedGraphDatabase neo_arg, String propertyDb, String neoDBProperties)
        throws ClassNotFoundException{
        String driverName = "org.sqlite.JDBC";
        this.neo= neo_arg;
        neo.loadConfigurations(neoDBProperties);
        this.GraphDatabaseName = neo.getStoreDir();

        this.myId = UUID.randomUUID();
        nodeUserProperties = new ArrayList<String>();
        relationshipUserProperties = new ArrayList<String>();

        index = new LuceneIndexService(neo);
        //index.setLazySearchResultThreshold(100000);

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

        /* Log rotation settings */
        TxModule txModule = ((EmbeddedGraphDatabase) neo).getConfig().getTxModule();
        XaDataSourceManager xaDsMgr = txModule.getXaDataSourceManager();
        XaDataSource xaDs = xaDsMgr.getXaDataSource( "nioneodb" );
        //xaDs.setAutoRotate( false );
        xaDs.setLogicalLogTargetSize(100 * 1024 * 1024L);

        writeCount = 0;
        currentFlushMode = null;
    }

    public DVNGraphImpl(String GraphDatabaseName, String propertyDb, String neoDBProps)
        throws ClassNotFoundException{
        this(new EmbeddedGraphDatabase(GraphDatabaseName), propertyDb, neoDBProps);
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
        n.setProperty("active", True);
        index.index(n, "active", True);
        index.removeIndex(n, "activeNext");
        n.removeProperty("activeNext");
    }

    private void addVertexBatch(Node n){
        n.setProperty("active", True);
        index.index(n, "active", True);
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
        index.removeIndex(n, "active");
        index.removeIndex(n, "comp");
        n.removeProperty("active");
        n.removeProperty("comp");
    }
    
    public boolean containsVertex(final LazyNode2 vertex) {
        Node l = neo.getNodeById(vertex.getId());
        if(null != l && isActive(l))
            return true;
        return false;
    }

    public int getVertexCount() {
        int cnt;
        if(dirty && !currentFlushMode.equals(flushMode.RELATIONSHIP_ONLY))
            return getUncommittedVertexCount();

        IndexHits<Node> idx = getActiveNodes();
        cnt = getActiveNodes().size();
        idx.close();
        return cnt;
    }

    public int getUncommittedVertexCount() {
        int cnt;
        IndexHits<Node> idx = getActiveNextNodes();
        cnt = getActiveNextNodes().size();
        idx.close();
        return cnt;
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

        if(dirty)
            return getUncommittedEdgeCount();
       
        for(Node n : getActiveNodes()){
            for(Relationship r : n.getRelationships(relType.DEFAULT, Direction.OUTGOING)){
                if(r.hasProperty("active") && isActive(r.getEndNode())) relCount++;
                if((travCount++%NEO_CACHE_LIMIT)==0){
                    //System.out.println(relCount);
                    clearNeoCache();
                }
            }
        }

        return (int)relCount;
    }

    public int getUncommittedEdgeCount(){
        long travCount = 0, relCount = 0;
       
        for(Node n : getActiveNextNodes()){
            for(Relationship r : n.getRelationships(relType.DEFAULT, Direction.OUTGOING)){
                if((currentFlushMode.equals(flushMode.NODE_ONLY) ||
                    r.hasProperty("activeNext")) && isActiveNext(r.getEndNode()))
                    relCount++;
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
        ArrayList l = new ArrayList<LazyRelationship2>();

        for(Node n : getActiveNodes()){
            //for(Path p : getFlushTraverser(n)){
            for(Relationship r : 
              n.getRelationships(relType.DEFAULT, Direction.OUTGOING)){
                if((relCount++%NEO_CACHE_LIMIT)==0){
                    l.add(new LazyRelationship2(r.getId()));
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

        for(Node n : getActiveNodes())
            //TODO: Add cache clearing
            l.add(new LazyNode2(n));

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

        if(dirty)
            undo();

        Transaction tx = neo.beginTx();

        long startTimeMs = System.currentTimeMillis();
        
        try {
            refNode = neo.getReferenceNode();

            for(Node n : neo.getAllNodes()){
                if(n.equals(refNode))
                    continue;

                if(!isActive(n)){
                    n.setProperty("active", True);
                    index.index(n, "active", True);
                }
                ++nm;

                for(Relationship r : n.getRelationships(relType.DEFAULT, Direction.OUTGOING)){
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

    /*
    private Node getComponentNode(){
        return neo.getReferenceNode().
                getSingleRelationship(relType.COMPONENT, Direction.OUTGOING).
                getEndNode();
    }
    */

    private boolean isActive(Node node){
        return node.hasProperty("active");
    }

    private void setActive(Node n, boolean yes){
        if(yes){
            n.setProperty("active", True);
            index.index(n, "active", True);
        }
        else{
            index.removeIndex(n, "active");
            n.removeProperty("active");
        }
    }

    private boolean isActiveNext(Node node){
        return node.hasProperty("activeNext");
    }

    private void setActiveNext(Node n, boolean yes){
        if(yes){
            n.setProperty("activeNext", True);
            index.index(n, "activeNext", True);
        }
        else{
            index.removeIndex(n, "activeNext");
            n.removeProperty("activeNext");
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

    public void finalize(){
        if(dirty)
            undo();

        index.shutdown();
        this.neo.shutdown();
        try{
            this.conn.close();
        }
        catch(SQLException e){
            System.err.println(e.getMessage());
        }
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

        i=0;
        j=0;
        for(Path p : trav){
            tmp_n = p.endNode();
            //if(p.lastRelationship()==null) continue;
            if((p.lastRelationship() == null || p.length() <= nth) && !isActiveNext(tmp_n)){
                setActiveNext(tmp_n, true);
                i++;
            }

            tmp_r = p.lastRelationship();

            if(tmp_r != null && isActiveNext(tmp_n) && !tmp_r.hasProperty("activeNext")){
                tmp_r.setProperty("activeNext", True);
                j++;
            }
        }

        //System.out.println(i + " nodes and " + j + " relationships activated.");
    }

    public void markNeighborhood(int nth){
        long i=0;

        if(dirty)
            commit();

        Transaction tx = neo.beginTx();
        try{
            for(Node n : getActiveNodes()){
                //System.out.println(n.getId());
                markNodeNeighborhood(n, nth);
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
        this.currentFlushMode = flushMode.FULL;
        this.dirty = true;
    }

    private IndexHits<Node> getActiveNodes(){
        return index.getNodes("active", True);
    }

    private IndexHits<Node> getActiveNextNodes(){
        return index.getNodes("activeNext", True);
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
                          relationships(relType.DEFAULT, Direction.BOTH).
                          //filter(onlyDefaultEdges).
                          uniqueness(Uniqueness.RELATIONSHIP_GLOBAL);

        return neighDesc.traverse(n);
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
                return !(isActive(p.endNode()) && (p.lastRelationship() == null || p.lastRelationship().hasProperty("active")));
            }
        };

        Predicate activeFilter = new Predicate<Path>(){
            public boolean accept(Path p){
                return isActive(p.endNode()) && (p.lastRelationship() == null || p.lastRelationship().hasProperty("active")); 
            }
        };

        long i;
        long nodeCount = 0;
        Long size1;

        //travDesc = travDesc.uniqueness(Uniqueness.NODE_GLOBAL).relationships(relType.DEFAULT, Direction.BOTH);
        travDesc = travDesc.uniqueness(Uniqueness.NODE_GLOBAL).
                            relationships(relType.DEFAULT, Direction.BOTH).
                            //expand(new ActiveExpander(Direction.OUTGOING));
                            filter(activeFilter).
                            prune(pruning);

        trav = travDesc.traverse(node);

        i=0;
        for(Path p : trav){
            n = p.endNode();
            //System.out.println("\tTrav." + p.endNode().getId());
            
            if(n.hasProperty("comp"))
                index.removeIndex(n, "comp");
            n.setProperty("comp", tag);
            index.index(n, "comp", tag);
            
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
        return newTag;
    }

    public void tagComponents(){
        Transaction tx;
        long tag = 0, compsize = 0, i=0;

        if(neo.getReferenceNode().hasProperty("numComps")){
        //    System.out.println(neo.getReferenceNode().getProperty("numComps"));
            clearComponents();
        }

        tx = neo.beginTx();
        try{
        for(Node cur : getActiveNodes()){
            //if((i++%NEO_CACHE_LIMIT)==0)
            //    clearNeoCache();

            if(!cur.hasProperty("comp")){
                //System.out.println("Found one! " + cur.getId());
                if(tagNodeComponent(cur, tag, tx)){
                    tag++;
                }
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
        neo.getReferenceNode().setProperty("numComps", new Long(tag));
        tx.success();
        } finally {
            tx.finish();
        }
    }

    public void printComponents(){
        Transaction tx;
        Node comp;
        long num=0;
        long numComps = ((Long)neo.getReferenceNode().getProperty("numComps")).longValue();
        IndexHits<Node> idx;

        tx = neo.beginTx();
        try{
            for(long tag = 0; tag < numComps; ++tag){
                idx = index.getNodes("comp", new Long(tag));
                System.out.println("Component " + tag + ": " + idx.size() + " Nodes.");
                num += idx.size();
                idx.close();
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
        long numComps;
        long numWrites = 0;
        IndexHits<Node> idx;

        numComps = ((Long)neo.getReferenceNode().getProperty("numComps")).longValue();
        Node tmp;
        tx = neo.beginTx();
        System.out.println("Clearing components...");

        try{
            for(long tag = 0; tag < numComps; ++tag){
                idx = index.getNodes("comp", new Long(tag));
                numWrites += idx.size();
                for(Node n : idx){
                    n.removeProperty("comp");
                }
                if(numWrites > TXN_LIMIT){
                    numWrites %= TXN_LIMIT;
                    tx.success();
                    tx.finish();
                    tx=neo.beginTx();
                }
            }
            index.removeIndex("comp");
            tx.success();
        }
        finally{
            tx.finish();
        }
        System.out.println("done.");
    }

    private IndexHits<Node> getNthComponentIdx(int nth){
        ArrayList<IndexHits<Node>> componentList = new ArrayList();
        Node component;
        IndexHits<Node> tmp;
        long numComps = ((Long)neo.getReferenceNode().getProperty("numComps")).longValue();
        int i=0;

        Comparator<IndexHits<Node>> compSizeComparator = new Comparator<IndexHits<Node>>(){
            public int compare(IndexHits<Node> o1, IndexHits<Node> o2){
                return (int)(o1.size() - o2.size());
            }
            public boolean equals(IndexHits<Node> o1, IndexHits<Node> o2){
                return (o1.size() == o2.size());
            }
        };

        for(long tag=0; tag < numComps; ++tag){ 
            tmp = index.getNodes("comp", new Long(tag));
            if(i++ >= nth){
                Collections.sort(componentList, compSizeComparator);
                if(compSizeComparator.compare(componentList.get(0), tmp) < 0)
                    componentList.remove(0).close();
                else
                    continue;
           }
           componentList.add(tmp);
        }
        tmp = componentList.get(0);
        for(IndexHits<Node> idx : componentList){ idx.close(); }
        return tmp;
    }

    public void markComponent(int nth){
        long size=0, target_size, writeCount=0;
        Long key;
        long nodeCount=0;
        Transaction tx;

        if(dirty)
            commit();
        
        tx = neo.beginTx();
        try{
            for(Node n : getNthComponentIdx(nth)){
                setActiveNext(n, true);
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
        this.currentFlushMode = flushMode.NODE_ONLY;
        this.dirty = true;
    }

    private void commit(){
        flushMarks(this.currentFlushMode);
        this.dirty = false;
        //this.currentFlushMode = null;
    }

    public void undo(){
        eraseMarks(this.currentFlushMode);
        this.dirty = false;
        //this.currentFlushMode = null;
    }
    
    public void eraseMarks(flushMode mode){
        Transaction tx;
        Traverser flushTrav;
        IndexHits<Node> idx;
        long numWrites = 0;

        tx = neo.beginTx();
        try{
            //Get rid of all of the activeNext relationships
            idx = getActiveNextNodes();
                for(Node cur : getActiveNextNodes()){
                    if(!mode.equals(flushMode.NODE_ONLY)){
                        for(Relationship r1 : cur.getRelationships(relType.DEFAULT, Direction.OUTGOING)){ 
                            if(r1.hasProperty("activeNext")) r1.removeProperty("activeNext");
                        }
                    }
                    if(!mode.equals(flushMode.RELATIONSHIP_ONLY)){
                        setActiveNext(cur, false);
                    }
                    if(numWrites++ > TXN_LIMIT){
                        numWrites %= TXN_LIMIT;
                        //System.out.println("ping");
                        //idx.close();
                        tx.success();
                        tx.finish();
                        tx = neo.beginTx();
                        //idx = getActiveNextNodes();
                    }
                }
            tx.success();
        }
        finally {
            tx.finish();
        }
    }

    public void flushMarks(flushMode mode){
        long nodeCount=0, relCount=0, writeCount=0;

        Transaction tx;
        Traverser flushTrav;
        /* Keep nodes that stay activated */
        tx = neo.beginTx();
        try{
            for(Node cur : getActiveNodes()){
                if(!isActiveNext(cur)){
                    if(!(mode.equals(flushMode.RELATIONSHIP_ONLY))){
                        removeVertex(cur);
                        writeCount++;
                    }
                }
                else{
                    index.removeIndex(cur, "activeNext");
                    cur.removeProperty("activeNext");
                    nodeCount++;
                }
                
                if(writeCount > TXN_LIMIT){
                    writeCount %= TXN_LIMIT;
                    System.out.println(writeCount + " nodes deactivated.");
                    clearNeoCache();
                    /*
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                    */
                }
            }
            tx.success();
            tx.finish();
            tx = neo.beginTx();
            System.out.println(nodeCount + " nodes stay active.");

            if(!mode.equals(flushMode.RELATIONSHIP_ONLY)){

            /* Add nodes that get activated */
            for(Node cur : getActiveNextNodes()){
                if(!isActive(cur)){
                    addVertex(cur); //Allows for batch removal of "activeNext" index.
                    writeCount++;
                    nodeCount++;
                }
                
                if(writeCount > TXN_LIMIT){
                    writeCount %= TXN_LIMIT;
                    System.out.println(writeCount + " nodes activated.");
                    clearNeoCache();
                    /*
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                    */
                }
            }
            index.removeIndex("activeNext"); //Batch removal of "activeNext" index.

            tx.success();
            tx.finish();
            tx = neo.beginTx();

            System.out.println(nodeCount + " nodes total activated.");
            }

            if(!mode.equals(flushMode.NODE_ONLY)){
            for(Node n : getActiveNodes()){
                for(Relationship rel : n.getRelationships(relType.DEFAULT, Direction.OUTGOING)){

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
        clearNeoCache();
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

    public void printUncommittedStatus(){
        System.out.println("Nodes: " + getUncommittedVertexCount() +
                           " Edge: " + getUncommittedEdgeCount());
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
        LazyNode2 v;
        int i=0;
        long writeCount=0;

        if(dirty)
            commit();

        PageRank<LazyNode2, LazyRelationship2> pr = new PageRank(this, d);


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
            for(Node n : getActiveNodes()){
                //System.out.println(pr.getVertexScore(v));
                v = new LazyNode2(n.getId());
                n.setProperty(name, pr.getVertexScore(v));
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
        long writeCount=0;
        String name;

        if(dirty)
            commit();

        tx = neo.beginTx();
        try{
            name = registerUserProperty("Degree", elementType.NODE, "int");
            for(Node n : getActiveNodes()){
                n.setProperty(name, degree(n, Direction.BOTH));
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
        long writeCount=0;
        String name;

        if(dirty)
            commit();

        tx = neo.beginTx();
        try{
            name = registerUserProperty("UDegree", elementType.NODE, "int");
            for(Node n : getActiveNodes()){
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
        long writeCount=0;

        if(dirty)
            commit();

        tx = neo.beginTx();
        String name;
        try{
            name = registerUserProperty("InLargestComponent", elementType.NODE, "int");
            for(Node n : getNthComponentIdx(1)){
                n.setProperty(name, 1);
                if((++writeCount%TXN_LIMIT)==0){
                    tx.success();
                    tx.finish();
                    tx = neo.beginTx();
                }
            }
            for(Node n : getActiveNodes()){
                if(!n.hasProperty(name))
                    n.setProperty(name, 0);
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
        for(Node n : getActiveNodes()){
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

    private Map<String, String> getPropTypes(elementType e){
        Node n = neo.getReferenceNode();
        String elem = e.equals(elementType.NODE) ? "node" : "edge";
        Pattern elemMatches = Pattern.compile(String.format("%s_(.*)", elem));
        Matcher m;
        Map<String, String> propMap = new LinkedHashMap<String, String>();
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
        Map<String, String> types = getPropTypes(e);
        for(String s : userProperties){
            propString += String.format(", %s",s);
            paramString += ", ?";
            colString += String.format(", %s %s", s, types.get(s));
            joinString += String.format(", b.%s",s);
        }
        String[] propStrings = {propString, paramString, colString, joinString};
        return propStrings;
    }

    public void markNodesByProperty(String query) throws SQLException{
        if(dirty)
            commit();

        String sql_query = String.format("select a.uid from "+
                                           "prop.node_props as a join active_uid as b " +
                                           "on a.uid=b.uid where (%s);", query);

        GraphWriter gwView = this;

        gwView.writeHeader(); //hack for starting surrounding Tx. 
        writeVertexTab(gwView, sql_query);
        gwView.writeFooter(); //hack for finalizeing surrounding Tx.

        this.currentFlushMode = flushMode.NODE_ONLY;
        this.dirty = true;
    }

    public void markRelationshipsByProperty(String query, boolean dropIsolates) throws SQLException{
        if(dirty)
            commit();

        String sql_query = String.format("select a.uid from "+
                                           "prop.edge_props as a join active_rel_uid as b " +
                                           "on a.uid=b.uid where (%s);", query);

        GraphWriter gwView = this;

        gwView.writeHeader();
        writeEdgeTab(gwView, sql_query);
        gwView.writeFooter();

        if(dropIsolates)
            this.currentFlushMode = flushMode.FULL;
        else
            this.currentFlushMode = flushMode.RELATIONSHIP_ONLY;
        this.dirty = true;
    }

    public void dumpGraphML(String filename){
        OutputStream outfile;
        
        if(dirty)
            commit();

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

        if(dirty)
            commit();
        
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
        String[] queryParts = userPropStrings(elementType.NODE);
        String queryCommand = String.format("select a.*%s from prop.node_props as a join active_uid as b on a.uid=b.uid;", queryParts[3]);
        writeVertexTab(rsw, queryCommand);
    }
    
    public void writeVertexTab(GraphWriter rsw, String query){
        long numCached = 0, batch_size = 50000;
        String[] queryParts = userPropStrings(elementType.NODE);
        String insertCommand = String.format("INSERT INTO active_uid(%s) values (%s);",
                                        queryParts[0], queryParts[1]);
        String queryCommand = query;
        Map<String, String> types = getPropTypes(elementType.NODE);
        ResultSet rs;
        Connection memconn;
        long nodesWritten = 0;
        int i = 1;
        //Create in-memory sqlite table
        try{
            memconn = DriverManager.getConnection("jdbc:sqlite::memory:");
            
            Statement stat;
            stat = memconn.createStatement();
            stat.executeUpdate("DROP TABLE IF EXISTS active_uid;");
            stat.executeUpdate(String.format("CREATE TABLE active_uid (%s);", queryParts[2]));
            stat.executeUpdate(String.format("CREATE UNIQUE INDEX active_uid_idx on active_uid(uid);"));
            stat.executeUpdate(String.format("ATTACH DATABASE \"%s\" as prop;", prop_db));

            memconn.setAutoCommit(false);

            PreparedStatement inserter = memconn.prepareStatement(insertCommand);
            for(Node n : getActiveNodes()){
                i=1;
                inserter.setLong(1, n.getId());
                for(String p : nodeUserProperties){
                    if(types.get(p)=="double" && n.hasProperty(p))
                        inserter.setDouble(++i, ((Double)n.getProperty(p)).doubleValue());
                    else if(types.get(p)=="int" && n.hasProperty(p))
                        inserter.setInt(++i, ((Integer)n.getProperty(p)).intValue());
                    else if(n.hasProperty(p))
                        inserter.setString(++i, n.getProperty(p).toString());
                }

                inserter.addBatch();
                if((++numCached%batch_size)==0){
                    inserter.executeBatch();
                    memconn.commit();
                    stat = memconn.createStatement();
                    rs = stat.executeQuery(queryCommand);

                    while(rs.next()){
                        rsw.writeNode(rs);
                        nodesWritten++;
                    }
                    rsw.flush();
                    rs.close();

                    stat.executeUpdate("delete from active_uid;");

                    inserter.close();
                    inserter = memconn.prepareStatement(insertCommand);
                    System.out.println(nodesWritten + " nodes written so far...");
                }
            }
            inserter.executeBatch();
            memconn.commit();
            stat = memconn.createStatement();
            rs = stat.executeQuery(queryCommand);

            while(rs.next()){
                rsw.writeNode(rs);
                nodesWritten++;
            }
            rsw.flush();
            rs.close();
            inserter.close();

            memconn.setAutoCommit(true);
            System.out.println(nodesWritten + " nodes written so far...");

            memconn.close();
        } catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public void writeEdgeTab(GraphWriter rsw){
        String[] queryParts = userPropStrings(elementType.RELATIONSHIP);
        String queryCommand = String.format("select a.*%s,b.source,b.target from prop.edge_props as a "+ 
                                                      "join active_rel_uid as b on a.uid=b.uid;", queryParts[3]);
        writeEdgeTab(rsw, queryCommand);
    }

    public void writeEdgeTab(GraphWriter rsw, String query){
        long numCached = 0, batch_size = 50000;
        String[] queryParts = userPropStrings(elementType.RELATIONSHIP);
        String insertCommand = String.format("INSERT INTO active_rel_uid(source, target, %s) values (?, ?, %s);",
                                        queryParts[0], queryParts[1]);
        String queryCommand = query;
        Map<String, String> types = getPropTypes(elementType.RELATIONSHIP);
        ResultSet rs;
        Connection memconn;
        int i=1;
        long nodesWritten = 0;
        //Create in-memory sqlite table
        try{
            memconn = DriverManager.getConnection("jdbc:sqlite::memory:");
            
            Statement stat;
            stat = memconn.createStatement();
            stat.executeUpdate("DROP TABLE IF EXISTS active_rel_uid;");
            stat.executeUpdate(String.format("CREATE TABLE active_rel_uid (source INTEGER, target INTEGER, %s);", queryParts[2]));
            stat.executeUpdate(String.format("CREATE UNIQUE INDEX active_rel_idx on active_rel_uid(uid);"));
            stat.executeUpdate(String.format("ATTACH DATABASE \"%s\" as prop;", prop_db));

            memconn.setAutoCommit(false);

            PreparedStatement inserter = memconn.prepareStatement(insertCommand);
            for(Node n : getActiveNodes()){
                for(Relationship r : n.getRelationships(relType.DEFAULT, Direction.OUTGOING)){
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
                    //System.out.println(String.format("select a.*%s,b.source,b.target from prop.edge_props as a "+ 
                    //                                  "join active_rel_uid as b on a.uid=b.uid;", queryParts[3]));
                    rs = stat.executeQuery(queryCommand);

                    while(rs.next()){
                        rsw.writeEdge(rs);
                        nodesWritten++;
                    }
                    rsw.flush();
                    rs.close();
                    inserter.close();

                    stat.executeUpdate("delete from active_rel_uid;");

                    inserter = memconn.prepareStatement(insertCommand);
                    System.out.println(nodesWritten + " rels written so far...");
                }
                clearNeoCache();
            }
            }
            inserter.executeBatch();
            memconn.commit();
            stat = memconn.createStatement();
            rs = stat.executeQuery(queryCommand);

            while(rs.next()){
                rsw.writeEdge(rs);
                nodesWritten++;
            }
            rsw.flush();
            rs.close();
            inserter.close();

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

    /*Graph Writer View*/
    public void writeHeader(){
        this.writerTx = neo.beginTx();
        this.writerWriteCount = 0;
    }
    public void writeNode(ResultSet rs) {
        try{
            setActiveNext(neo.getNodeById(rs.getLong(1)), true);
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    public void writeEdge(ResultSet rs){
        try{
            setActiveNext(neo.getRelationshipById(rs.getLong(1)), true);
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    public void writeFooter(){
        writerTx.success();
        writerTx.finish();
    }
    public void flush(){
        writerTx.success();
        writerTx.finish();
        writerTx = neo.beginTx();
    }
}
