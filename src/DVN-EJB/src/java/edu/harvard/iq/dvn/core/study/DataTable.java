/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
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
import edu.harvard.iq.dvn.core.visualization.VisualizationDisplay;
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
    
    @OneToOne (mappedBy="dataTable",cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST })
    private  VisualizationDisplay visualizationDisplay;

    public VisualizationDisplay getVisualizationDisplay() {
        return visualizationDisplay;
    }

    public void setVisualizationDisplay(VisualizationDisplay visualizationDisplay) {
        this.visualizationDisplay = visualizationDisplay;
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
