/*
 * Dataverse Network - A web application to distribute, share and analyze quantitative data.
 * Copyright (C) 2007
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses
 * or write to the Free Software Foundation,Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

/*
 * DataTable.java
 *
 * Created on August 9, 2006, 3:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.study;

import edu.harvard.iq.dvn.core.visualization.VarGrouping;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.persistence.*;

import org.apache.commons.lang.builder.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class DataTable implements java.io.Serializable {

    public static String TYPE_VERTEX = "vertex";
    public static String TYPE_EDGE = "edge";
    

    /** Creates a new instance of DataTable */
    public DataTable() {
    }

    /**
     * Holds value of property id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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


    /**
     * Holds value of property unf.
     */
    private String unf;

    /**
     * Getter for property unf.
     * @return Value of property unf.
     */
    public String getUnf() {
        return this.unf;
    }

    /**
     * Setter for property unf.
     * @param unf New value of property unf.
     */
    public void setUnf(String unf) {
        this.unf = unf;
    }

    private Long caseQuantity;    

    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public Long getCaseQuantity() {
        return this.caseQuantity;
    }    
    
    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setCaseQuantity(Long caseQuantity) {
        this.caseQuantity = caseQuantity;
    }
    
    private Long varQuantity;

    /**
     * Getter for property id.
     * @return Value of property id.
     */
    public Long getVarQuantity() {
        return this.varQuantity;
    }

    /**
     * Setter for property id.
     * @param id New value of property id.
     */
    public void setVarQuantity(Long varQuantity) {
        this.varQuantity = varQuantity;
    }   
    
    private Long recordsPerCase;

    public Long getRecordsPerCase() {
        return recordsPerCase;
    }

    public void setRecordsPerCase(Long recordsPerCase) {
        this.recordsPerCase = recordsPerCase;
    }

    private boolean visualizationEnabled;

    public boolean isVisualizationEnabled() {
        return visualizationEnabled;
    }

    public void setVisualizationEnabled(boolean visualizationEnabled) {
        this.visualizationEnabled = visualizationEnabled;
    }

    private String visualizationShowDataTable;
    private String visualizationShowImageGraph;
    private String visualizationShowFlashGraph;
    private String visualizationSourceInfoLabel;

    public String getVisualizationSourceInfoLabel() {
        return visualizationSourceInfoLabel;
    }

    public void setVisualizationSourceInfoLabel(String visualizationSourceInfoLabel) {
        this.visualizationSourceInfoLabel = visualizationSourceInfoLabel;
    }
    
    public String getVisualizationShowDataTable() {
        return visualizationShowDataTable;
    }

    public void setVisualizationShowDataTable(String visualizationShowDataTable) {
        this.visualizationShowDataTable = visualizationShowDataTable;
    }

    public String getVisualizationShowFlashGraph() {
        return visualizationShowFlashGraph;
    }

    public void setVisualizationShowFlashGraph(String visualizationShowFlashGraph) {
        this.visualizationShowFlashGraph = visualizationShowFlashGraph;
    }

    public String getVisualizationShowImageGraph() {
        return visualizationShowImageGraph;
    }

    public void setVisualizationShowImageGraph(String visualizationShowImageGraph) {
        this.visualizationShowImageGraph = visualizationShowImageGraph;
    }

    
    /**
     * Holds value of property study.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
    private StudyFile studyFile;

    /**
     * Getter for property study.
     * @return Value of property study.
     */
    public StudyFile getStudyFile() {
        return this.studyFile;
    }

    /**
     * Setter for property study.
     * @param study New value of property study.
     */
    public void setStudyFile(StudyFile studyFile) {
        this.studyFile = studyFile;
    }

    /**
     * Holds value of property dataVariables.
     */
    @OneToMany (mappedBy="dataTable", cascade={ CascadeType.REMOVE, CascadeType.MERGE,CascadeType.PERSIST})
    @OrderBy ("fileOrder")
    private List<DataVariable> dataVariables;

    /**
     * Getter for property dataVariables.
     * @return Value of property dataVariables.
     */
    public List<DataVariable> getDataVariables() {
        return this.dataVariables;
    }

    /**
     * Setter for property dataVariables.
     * @param dataVariables New value of property dataVariables.
     */
    public void setDataVariables(List<DataVariable> dataVariables) {
        this.dataVariables = dataVariables;
    }      

    /**
     * Holds value of property varGroupings.
     */
    @OneToMany (mappedBy="dataTable", cascade={ CascadeType.REMOVE, CascadeType.MERGE,CascadeType.PERSIST})
    @OrderBy ("name")
    private List<VarGrouping> varGroupings;

    public List<VarGrouping> getVarGroupings() {
        return varGroupings;
    }

    public void setVarGroupings(List<VarGrouping> varGroupings) {
        this.varGroupings = varGroupings;
    }
    
    
    /**
     * Holds value of property version.
     */
    @Version
    private Long version;

    /**
     * Getter for property version.
     * @return Value of property version.
     */
    public Long getVersion() {
        return this.version;
    }

    /**
     * Setter for property version.
     * @param version New value of property version.
     */
    public void setVersion(Long version) {
        this.version = version;
    }    
    
     public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DataTable)) {
            return false;
        }
        DataTable other = (DataTable)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    

//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this,
//            ToStringStyle.MULTI_LINE_STYLE);
//    }
}
