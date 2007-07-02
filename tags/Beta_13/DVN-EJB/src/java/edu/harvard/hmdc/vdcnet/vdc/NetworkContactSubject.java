/*
 * ReviewState.java
 *
 * Created on July 28, 2006, 2:46 PM
 *
 */

package edu.harvard.hmdc.vdcnet.vdc;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.*;

/**
 *  Pre-defined email subject text for contacting an administrator of
 *  VDC/VDCNetword
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class NetworkContactSubject {
    private String subjectText;
    
    /** Creates a new instance of ReviewState */
    public NetworkContactSubject() {
    }

    public String getSubjectText() {
        return subjectText;
    }

    public void setSubjectText(String subjectText) {
        this.subjectText = subjectText;
    }

    /**
     * Holds value of property id.
     */
    @SequenceGenerator(name="networkcontactsubject_gen", sequenceName="networkcontactsubject_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="networkcontactsubject_gen")    
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
     * Holds value of property vdcNetwork.
     */
    @ManyToOne
    private VDCNetwork vdcNetwork;

    /**
     * Getter for property vdcNetwork.
     * @return Value of property vdcNetwork.
     */
    public VDCNetwork getVdcNetwork() {
        return this.vdcNetwork;
    }

    /**
     * Setter for property vdcNetwork.
     * @param vdcNetwork New value of property vdcNetwork.
     */
    public void setVdcNetwork(VDCNetwork vdcNetwork) {
        this.vdcNetwork = vdcNetwork;
    }
     public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NetworkContactSubject)) {
            return false;
        }
        NetworkContactSubject other = (NetworkContactSubject)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }        
}
