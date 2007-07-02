/*
 * StudyAuthor.java
 *
 * Created on August 7, 2006, 9:49 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package edu.harvard.hmdc.vdcnet.study;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;
import javax.persistence.*;

/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class StudyRelStudy {
    
    /** Creates a new instance of StudyRelStudy */
    public StudyRelStudy() {
    }

    /**
     * Holds value of property id.
     */

   
    
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="studyrelstudy_gen") 
    @SequenceGenerator(name="studyrelstudy_gen", sequenceName="studyrelstudy_id_seq")
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
     * Holds value of property text.
     */
   @Column(columnDefinition="TEXT")
    private String text;

    /**
     * Getter for property value.
     * @return Value of property value.
     */
     public String getText() {
        return this.text;
    }

    /**
     * Setter for property value.
     * @param value New value of property value.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Holds value of property displayOrder.
     */
    private int displayOrder;

    /**
     * Getter for property order.
     * @return Value of property order.
     */
    public int getDisplayOrder() {
        return this.displayOrder;
    }

    /**
     * Setter for property order.
     * @param order New value of property order.
     */
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * Holds value of property study.
     */
    @ManyToOne 
    @JoinColumn(nullable=false)
    private Study study;

    /**
     * Getter for property study.
     * @return Value of property study.
     */
    public Study getStudy() {
        return this.study;
    }

    /**
     * Setter for property study.
     * @param study New value of property study.
     */
    public void setStudy(Study study) {
        this.study = study;
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
    
    public boolean isEmpty() {
        return ((text==null || text.trim().equals("")));
    }
    
  public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyRelStudy)) {
            return false;
        }
        StudyRelStudy other = (StudyRelStudy)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }    
}
