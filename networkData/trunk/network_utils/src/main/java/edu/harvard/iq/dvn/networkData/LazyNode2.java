package edu.harvard.iq.dvn.networkData;

import java.util.HashMap;

import org.neo4j.graphdb.Node;
/*
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import static com.sleepycat.persist.model.Relationship.*;
*/

//@Entity
public class LazyNode2 {
//    @PrimaryKey
    private Long nodeId;

    private HashMap<String, Object> props;

    public LazyNode2(){}

    public LazyNode2(Long i){
        this.nodeId = i;
        this.props = new HashMap();
    }


    public LazyNode2(Node n){
    //    this.underlyingNode = n;
        this(n.getId());
    }

    /* Identifiers */
    public Long getId(){
        return this.nodeId;
    }

    public void setId(Long id){
        this.nodeId = id;
    }

    /* Masking functionality */
    public HashMap<String, Object> getProps(){
        return this.props;
    }

    public void setProps(String name, Object obj){
        this.props.put(name, obj);
    }

    @Override
    public boolean equals(Object aThat) {
        if ( this == aThat ) return true;
        return aThat instanceof LazyNode2 &&
            ((LazyNode2)aThat).getId().longValue() == this.nodeId.longValue();
    }

    @Override
    public int hashCode() {
        return nodeId.hashCode() + props.hashCode();
    }
}
