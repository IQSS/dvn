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

package edu.harvard.iq.dvn.core.study;

import java.math.BigDecimal;
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
public class StudyGeoBounding implements java.io.Serializable, MetadataFieldGroup {
    
    /** Creates a new instance of StudyAuthor */
    public StudyGeoBounding() {
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
     * Holds value of property metadata.
     */
    @ManyToOne
    @JoinColumn(nullable=false)
    private Metadata metadata;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
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
    
    /**
     * Holds value of property westLongitude.
     */
    private String westLongitude;
    
    /**
     * Getter for property westLongitude.
     * @return Value of property westLongitude.
     */
    public String getWestLongitude() {
        return this.westLongitude;
    }
    
    /**
     * Setter for property westLongitude.
     * @param westLongitude New value of property westLongitude.
     */
    public void setWestLongitude(String westLongitude) {
        this.westLongitude = westLongitude;
    }
    
    /**
     * Holds value of property eastLongitude.
     */
    private String eastLongitude;
    
    /**
     * Getter for property eastLongitude.
     * @return Value of property eastLongitude.
     */
    public String getEastLongitude() {
        return this.eastLongitude;
    }
    
    /**
     * Setter for property eastLongitude.
     * @param eastLongitude New value of property eastLongitude.
     */
    public void setEastLongitude(String eastLongitude) {
        this.eastLongitude = eastLongitude;
    }
    
    /**
     * Holds value of property northLatitude.
     */
    private String northLatitude;
    
    /**
     * Getter for property northLatitude.
     * @return Value of property northLatitude.
     */
    public String getNorthLatitude() {
        return this.northLatitude;
    }
    
    /**
     * Setter for property northLatitude.
     * @param northLatitude New value of property northLatitude.
     */
    public void setNorthLatitude(String northLatitude) {
        this.northLatitude = northLatitude;
    }
    
    /**
     * Holds value of property southLatitude.
     */
    private String southLatitude;
    
    /**
     * Getter for property southLatitude.
     * @return Value of property southLatitude.
     */
    public String getSouthLatitude() {
        return this.southLatitude;
    }
    
    /**
     * Setter for property southLatitude.
     * @param southLatitude New value of property southLatitude.
     */
    public void setSouthLatitude(String southLatitude) {
        this.southLatitude = southLatitude;
    }
    
    public boolean isEmpty() {
        return ((eastLongitude==null || eastLongitude.trim().equals(""))
                && (westLongitude==null || westLongitude.trim().equals(""))
                && (northLatitude==null || northLatitude.trim().equals(""))
                && (southLatitude==null || southLatitude.trim().equals("")));
    }
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
    public String toString() {
        
        return eastLongitude+" East Longitude,"+ westLongitude+" West Longitude, "+northLatitude+" North Latitude, "+southLatitude+" South Latitude ";
    }
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StudyGeoBounding)) {
            return false;
        }
        StudyGeoBounding other = (StudyGeoBounding)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
}
