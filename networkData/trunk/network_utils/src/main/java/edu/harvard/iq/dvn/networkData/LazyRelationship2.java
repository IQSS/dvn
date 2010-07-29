package edu.harvard.iq.dvn.networkData;

import java.util.HashMap;
import java.util.Collection;

import org.neo4j.graphdb.Relationship;

/*
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import static com.sleepycat.persist.model.Relationship.*;
*/

import edu.uci.ics.jung.graph.util.EdgeType;

//@Entity
public class LazyRelationship2 {
//    @PrimaryKey
    private Long relId;

//    private Relationship underlyingRelationship;
    private HashMap<String, Object> props;

    public LazyRelationship2(){}

    public LazyRelationship2(Long id){
        this.relId = id;
        this.props = new HashMap();
    }

    public LazyRelationship2(Relationship r){
//        this.underlyingRelationship = r;
        this(r.getId());
    }

    public Long getId(){
        return this.relId;
    }

    public Collection<String> listRelationshipAttributes(){
        return this.props.keySet();
    }

    public HashMap<String, ? extends Object> getAllProps(){
        return this.props;
    }

    public Object getProp(String key){
        return this.props.get(key);
    }

    public void setAllProps(HashMap<String, Object> props){
        this.props = props;
    }

    public void setProp(String key, Object value){
        this.props.put(key, value);
    }

    @Override
    public boolean equals(Object aThat) {
        if ( this == aThat ) return true;
        return aThat instanceof LazyRelationship2 &&
            ((LazyRelationship2)aThat).getId().longValue() == this.relId.longValue();
    }

    @Override
    public int hashCode() {
        return this.relId.hashCode() + this.props.hashCode();
    }
}
