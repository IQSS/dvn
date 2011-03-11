/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.dvn.core.web;

import edu.harvard.iq.dvn.core.visualization.VarGroup;
import edu.harvard.iq.dvn.core.visualization.VarGrouping;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author skraffmiller
 */
public class VisualizationLineDefinition {

    private VarGroup measureGroup;
    private List <VarGroup> filterGroups;
    private List <VarGroup> sourceGroups;

    private Long variableId = new Long(0);
    private String measureLabel = new String();
    private String label = new String();
    private String color = new String();
    private String border = new String();
    private String variableName = new String();
    private String variableLabel = new String();

    public List<VarGroup> getFilterGroups() {
        return filterGroups;
    }

    public void setFilterGroups(List<VarGroup> filterGroups) {
        this.filterGroups = filterGroups;
    }

    public VarGroup getMeasureGroup() {
        return measureGroup;
    }

    public void setMeasureGroup(VarGroup measureGroup) {
        this.measureGroup = measureGroup;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getVariableId() {
        return variableId;
    }

    public void setVariableId(Long variableId) {
        this.variableId = variableId;
    }

    public String getMeasureLabel() {
        return measureLabel;
    }

    public void setMeasureLabel(String measureLabel) {
        this.measureLabel = measureLabel;
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableLabel() {
        return variableLabel;
    }

    public void setVariableLabel(String variableLabel) {
        this.variableLabel = variableLabel;
    }


    public List<VarGroup> getSourceGroups() {
        return sourceGroups;
    }

    public void setSourceGroups(List<VarGroup> sourceGroups) {
        this.sourceGroups = sourceGroups;
    }

}
