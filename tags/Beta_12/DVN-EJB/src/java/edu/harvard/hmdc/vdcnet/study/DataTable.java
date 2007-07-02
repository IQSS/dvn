/*
 * DataTable.java
 *
 * Created on August 9, 2006, 3:28 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.persistence.*;
/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class DataTable {
    
    /** Creates a new instance of DataTable */
    public DataTable() {
    }

    /**
     * Holds value of property id.
     */
    @SequenceGenerator(name="datatable_gen", sequenceName="datatable_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="datatable_gen") 
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
    
    /**
     * Holds value of property study.
     */
    @OneToOne 
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
    @OneToMany (mappedBy="dataTable", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @OrderBy ("name")
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
        
}
