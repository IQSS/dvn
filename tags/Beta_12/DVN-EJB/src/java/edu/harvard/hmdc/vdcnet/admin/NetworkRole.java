/*
 * Role.java
 *
 * Created on July 28, 2006, 2:18 PM
 *
 */

package edu.harvard.hmdc.vdcnet.admin;

import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class NetworkRole {
    private String name;
    private String description;
    
    /** Creates a new instance of Role */
    public NetworkRole() {
    }

   @SequenceGenerator(name="networkrole_gen", sequenceName="networkrole_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="networkrole_gen")        
    private Long id;

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

    public Long getId() {
        return id;
    }

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
        if (!(object instanceof NetworkRole)) {
            return false;
        }
        NetworkRole other = (NetworkRole)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }              
   
}
