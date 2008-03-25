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
 * FileCategory.java
 *
 * Created on July 28, 2006, 2:55 PM
 *
 */

package edu.harvard.hmdc.vdcnet.study;

import edu.harvard.hmdc.vdcnet.util.AlphaNumericComparator;
import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.*;


/**
 *
 * @author Ellen Kraffmiller
 */
@Entity
public class FileCategory implements Comparable, java.io.Serializable {
    
    private static AlphaNumericComparator alphaNumericComparator = new AlphaNumericComparator();
    
    @Column(columnDefinition="TEXT")
    private String name;
    private String description;
    //  @OneToMany
    
    /**
     * Creates a new instance of FileCategory
     */
    public FileCategory() {
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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
    
    @SequenceGenerator(name="filecategory_gen", sequenceName="filecategory_id_seq")
    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="filecategory_gen")
    private Long id;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Holds value of property studyFiles.
     */
    @OneToMany(mappedBy="fileCategory", cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    private Collection<StudyFile> studyFiles;
    
    /**
     * Getter for property studyFiles.
     * @return Value of property studyFiles.
     */
    public Collection<StudyFile> getStudyFiles() {
        return this.studyFiles;
    }
    
    /**
     * Setter for property studyFiles.
     * @param studyFiles New value of property studyFiles.
     */
    public void setStudyFiles(Collection<StudyFile> studyFiles) {
        this.studyFiles = studyFiles;
    }
    
    @ManyToOne
    private Study study;
    
    public Study getStudy() {
        return study;
    }
    
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
    
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
    
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FileCategory)) {
            return false;
        }
        FileCategory other = (FileCategory)object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) return false;
        return true;
    }
    
    public int compareTo(Object obj) {
        FileCategory fc = (FileCategory)obj;
        return alphaNumericComparator.compare(this.getName(),fc.getName());
        
    }
    
}
