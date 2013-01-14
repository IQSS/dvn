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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.dvn.core.visualization;

import edu.harvard.iq.dvn.core.study.DataTable;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 *
 * @author skraffmiller
 */
@Entity
public class VisualizationDisplay implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private boolean showImageGraph;
    private boolean showFlashGraph;
    private boolean showDataTable;
    private int defaultDisplay;
    private String sourceInfoLabel;
    private String measureTypeLabel;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @OneToOne
    @JoinColumn(nullable=false)
    private DataTable dataTable;


    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }
    
    
    public int getDefaultDisplay() {
        return defaultDisplay;
    }

    public void setDefaultDisplay(int defaultDisplay) {
        this.defaultDisplay = defaultDisplay;
    }

    public boolean isShowDataTable() {
        return showDataTable;
    }

    public void setShowDataTable(boolean showDataTable) {
        this.showDataTable = showDataTable;
    }

    public boolean isShowFlashGraph() {
        return showFlashGraph;
    }

    public void setShowFlashGraph(boolean showFlashGraph) {
        this.showFlashGraph = showFlashGraph;
    }

    public boolean isShowImageGraph() {
        return showImageGraph;
    }

    public void setShowImageGraph(boolean showImageGraph) {
        this.showImageGraph = showImageGraph;
    }
    
    public String getSourceInfoLabel() {
        return sourceInfoLabel;
    }

    public void setSourceInfoLabel(String sourceInfoLabel) {
        this.sourceInfoLabel = sourceInfoLabel;
    }
    
    public String getMeasureTypeLabel() {
        return measureTypeLabel;
    }

    public void setMeasureTypeLabel(String measureTypeLabel) {
        this.measureTypeLabel = measureTypeLabel;
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
        if (!(object instanceof VisualizationDisplay)) {
            return false;
        }
        VisualizationDisplay other = (VisualizationDisplay) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "edu.harvard.iq.dvn.core.visualization.VisualizationDisplay[ id=" + id + " ]";
    }
    
}
