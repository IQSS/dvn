/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.web.study;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import edu.harvard.iq.dvn.core.study.Metadata;
import edu.harvard.iq.dvn.core.study.TemplateField;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
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
public class TemplateFieldValue implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
     /**
     * Holds value of property templatefield.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
    private TemplateField templateField;

    /**
     * Getter for property template.
     * @return Value of property template.
     */
    public TemplateField getTemplateField() {
        return templateField;
    }

    /**
     * Setter for property template.
     * @param template New value of property template.
     */
    public void setTemplateField(TemplateField templateField) {
        this.templateField=templateField;
    }

    
    /**
     * Holds value of property Metadata.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
    private Metadata metadata;

    /**
     * Getter for property template.
     * @return Value of property template.
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Setter for property template.
     * @param template New value of property template.
     */
    public void setMetadata(Metadata metadata) {
        this.metadata=metadata;
    }
    
    
    @Column(columnDefinition="TEXT") 
    private String strValue;

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
        
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
        if (!(object instanceof TemplateFieldValue)) {
            return false;
        }
        TemplateFieldValue other = (TemplateFieldValue) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.study.TemplateFieldValue[ id=" + id + " ]";
    }
    
    

    private int displayOrder;
    public int getDisplayOrder() { return this.displayOrder;}
    public void setDisplayOrder(int displayOrder) {this.displayOrder = displayOrder;}    
}
