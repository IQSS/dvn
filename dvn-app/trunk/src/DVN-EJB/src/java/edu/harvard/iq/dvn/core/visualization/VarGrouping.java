/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.visualization;

import edu.harvard.iq.dvn.core.study.DataTable;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author skraffmiller
 */
@Entity
public class VarGrouping implements Serializable {

    private String name;
    public enum GroupingType { MEASURE, FILTER };

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;



    @Enumerated(EnumType.STRING)
    private GroupingType groupingType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public GroupingType getGroupingType() {
        return groupingType;
    }

    public void setGroupingType(GroupingType groupingType) {
        this.groupingType = groupingType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Holds value of property groups.
     */
    @OneToMany(mappedBy="varGrouping", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private List<VarGroup> varGroups;

     /**
     * Getter for property loginAffiliates.
     * @return Value of property loginAffiliates.
     */
    public List<VarGroup> getVarGroups() {
        return this.varGroups;
    }

    /**
     * Setter for property loginAffiliates.
     * @param loginAffiliates New value of property loginAffiliates.
     */
    public void setGroups(List<VarGroup> varGroups) {
        this.varGroups = varGroups;
    }


    /**
     * Holds value of property groups.
     */
    @OneToMany(mappedBy="varGrouping", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<VarGroupType> varGroupTypes;

     /**
     * Getter for property loginAffiliates.
     * @return Value of property loginAffiliates.
     */
    public Collection<VarGroupType> getVarGroupTypes() {
        return this.varGroupTypes;
    }

    /**
     * Setter for property loginAffiliates.
     * @param loginAffiliates New value of property loginAffiliates.
     */
    public void setVarGroupTypes(Collection<VarGroupType> varGroupTypes) {
        this.varGroupTypes = varGroupTypes;
    }


    /**
     * Holds value of the DataTable
     */
    @ManyToOne
    @JoinColumn(nullable=false)
    private DataTable dataTable;


    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    @OneToMany (mappedBy="varGrouping", cascade={ CascadeType.REMOVE, CascadeType.MERGE,CascadeType.PERSIST})
    private Collection<DataVariableMapping> dataVariableMapping;

    public Collection<DataVariableMapping> getDataVariableMappings() {
        return this.dataVariableMapping;
    }

    public void setDataVariableMappings(Collection<DataVariableMapping> dataVariableMapping) {
        this.dataVariableMapping = dataVariableMapping;
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
        if (!(object instanceof VarGrouping)) {
            return false;
        }
        VarGrouping other = (VarGrouping) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.visualization.VarGrouping[id=" + id + "]";
    }

}
