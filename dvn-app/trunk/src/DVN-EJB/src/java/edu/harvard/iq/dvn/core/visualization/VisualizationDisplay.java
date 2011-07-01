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
    @GeneratedValue(strategy = GenerationType.AUTO)
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
