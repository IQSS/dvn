/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.visualization;

import edu.harvard.iq.dvn.core.study.DataTable;
import edu.harvard.iq.dvn.core.study.DataVariable;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author skraffmiller
 */
@Entity
public class DataVariableMapping implements Serializable {
    private boolean x_axis;
    private String label;



    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable=true)
    private VarGroup varGroup;

    @ManyToOne
    @JoinColumn(nullable=false)
    private DataTable dataTable;

    @ManyToOne
    @JoinColumn(nullable=true)
    private VarGrouping varGrouping;

    public DataVariable getDataVariable() {
        return dataVariable;
    }

    public void setDataVariable(DataVariable dataVariable) {
        this.dataVariable = dataVariable;
    }

    @ManyToOne
    @JoinColumn(nullable=false)
    private DataVariable dataVariable;

    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isX_axis() {
        return x_axis;
    }

    public void setX_axis(boolean x_axis) {
        this.x_axis = x_axis;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public VarGroup getGroup() {
        return varGroup;
    }

    public void setGroup(VarGroup group) {
        this.varGroup = group;
    }


    public VarGrouping getVarGrouping() {
        return varGrouping;
    }

    public void setVarGrouping(VarGrouping varGrouping) {
        this.varGrouping = varGrouping;
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
        if (!(object instanceof DataVariableMapping)) {
            return false;
        }
        DataVariableMapping other = (DataVariableMapping) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.visualization.DataVaraibleMapping[id=" + id + "]";
    }

}
