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

    private VarGrouping measureGrouping;
    private VarGroup measureGroup;
    private List <VarGroup> filterGroups;
    private Long variableId = new Long(0);
    private String measureLabel = new String();
    private String label = new String();
    private String color = new String();
    private String border = new String();


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

    public VarGrouping getMeasureGrouping() {
        return measureGrouping;
    }

    public void setMeasureGrouping(VarGrouping measureGrouping) {
        this.measureGrouping = measureGrouping;
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

}
