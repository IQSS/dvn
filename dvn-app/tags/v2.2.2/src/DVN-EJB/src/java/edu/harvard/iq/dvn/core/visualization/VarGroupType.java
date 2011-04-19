/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.visualization;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 *
 * @author skraffmiller
 */
@Entity
public class VarGroupType implements Serializable {


    private String name;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

/**
     * Holds value of property grouping.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
    private VarGrouping varGrouping;

    /**
     * Getter for property userGroup.
     * @return Value of property userGroup.
     */
    public VarGrouping getVarGrouping() {
        return this.varGrouping;
    }

    /**
     * Setter for property userGroup.
     * @param userGroup New value of property userGroup.
     */
    public void setVarGrouping(VarGrouping varGrouping) {
        this.varGrouping = varGrouping;
    }

    @ManyToMany(mappedBy="groupTypes")
    private Collection<VarGroup> varGroups;



    public Collection<VarGroup> getGroups() {
        return varGroups;
    }

    public void setGroups(Collection<VarGroup> varGroups) {
        this.varGroups = varGroups;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof VarGroupType)) {
            return false;
        }
        VarGroupType other = (VarGroupType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
