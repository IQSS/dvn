/*
 * ReviewState.java
 *
 * Created on July 28, 2006, 2:46 PM
 *
 */

package edu.harvard.hmdc.vdcnet.vdc;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.*;

/**
 * Defines the workflow state of a Study as it goes thru the review process.
 *  Valid States:  New, Released, Reviewed
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class ReviewState {
    private String name;
    private String description;
    
    /** Creates a new instance of ReviewState */
    public ReviewState() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Holds value of property id.
     */
    @SequenceGenerator(name="reviewstate_gen", sequenceName="reviewstate_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="reviewstate_gen")    
    private Long id;

    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public Long getId() {
        return this.id;
    }

    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setId(Long id) {
        this.id = id;
    }
      public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ReviewState)) {
            return false;
        }
        ReviewState other = (ReviewState)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }       
}
