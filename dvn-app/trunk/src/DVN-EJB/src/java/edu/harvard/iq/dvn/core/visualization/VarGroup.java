/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.visualization;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.persistence.*;

/**
 *
 * @author skraffmiller
 */
@Entity
public class VarGroup implements Serializable {
    private String name;
    private String units;

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
     * Holds value of property userGroup.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
    private VarGrouping varGrouping;

    /**
     * Getter for property userGroup.
     * @return Value of property userGroup.
     */
    public VarGrouping getGroupAssociation() {
        return this.varGrouping;
    }

    /**
     * Setter for property userGroup.
     * @param userGroup New value of property userGroup.
     */
    public void setGroupAssociation(VarGrouping varGrouping) {
        this.varGrouping = varGrouping;
    }



    @ManyToMany
    @JoinTable(name = "GROUP_GROUPTYPES",
    joinColumns = @JoinColumn(name = "group_id"),
    inverseJoinColumns = @JoinColumn(name = "group_type_id"))
    private List<VarGroupType> groupTypes;

    /**
     * Getter for property issues.
     * @return Value of property issues.
     */
    public List<VarGroupType> getGroupTypes() {
        return this.groupTypes;
    }

    public void setGroupTypes(List<VarGroupType> groupTypes) {
        this.groupTypes = groupTypes;
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
        if (!(object instanceof VarGroup)) {
            return false;
        }
        VarGroup other = (VarGroup) object;
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

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

}
