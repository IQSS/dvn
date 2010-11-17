package edu.harvard.iq.text;

import com.icesoft.faces.component.ext.HtmlDataTable;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ekraffmiller
 */
public class ClusteringSpacePage {

    private String setId;
    private double xCoord;
    private double yCoord;
    private DocumentSet documentSet;
    private ClusterSolution clusterSolution;
    private String solutionLabel;  // This is where we store the text entered in the solution label edit field
    private Boolean editSolutionLabel = Boolean.FALSE;
    private ArrayList<ClusterSolution> savedSolutions = new ArrayList<ClusterSolution>();
    private int clusterNum;
    private HtmlDataTable clusterTable;
    private ArrayList<ClusterRow> clusterTableModel = new ArrayList<ClusterRow>();
    private int solutionIndex;  // This is passed from the form to indicate that we need to display a saved solution rather than calculate a new solution

    /** Creates a new instance of ClusterViewPage */
    public ClusteringSpacePage() {
      
    }

    @PostConstruct
    public void init() {
         xCoord = 0;
         yCoord = 0;
         clusterNum = 5;
        documentSet = new DocumentSet(setId);
        solutionIndex=-1;
        calculateClusterSolution(true);
    }

    public void testValueChange(ValueChangeEvent ve) {
        System.out.println("Got value change event: "+ ve.getComponent());
    }
    public void updateClusterSolutionListener(ActionEvent ea) {
        System.out.println("Got action event");
        updateClusterSolution();
    }
    public void updateClusterSolution() {
        if (solutionIndex<0) {
            calculateClusterSolution(true);
        } else {
            clusterSolution = savedSolutions.get(solutionIndex);
            solutionLabel = clusterSolution.getLabel();
            initClusterTableModel();
        }

    }
    public String doChangeClusterNum() {
        calculateClusterSolution(false);
        return "";
    }

    private void calculateClusterSolution(boolean newPoint) {
        if (newPoint) {
            clusterSolution = new ClusterSolution(documentSet, xCoord, yCoord, clusterNum);
        } else {
            // Calculate solution based on the existing solution, and the clusterNum
            clusterSolution = new ClusterSolution(clusterSolution,clusterNum);
        }
        solutionLabel="";
        initClusterTableModel();

    }
    public void saveClusterLabel(ActionEvent ae) {
        int rowIndex = clusterTable.getRowIndex();
        ClusterRow row = (ClusterRow) clusterTable.getRowData();
        clusterSolution.getClusterInfoList().get(rowIndex).setLabel(row.newValue);
    }


    public void saveSolutionLabel(ActionEvent ae) {
           clusterSolution.setLabel(solutionLabel);
           if (!savedSolutions.contains(clusterSolution)) {
               // The id corresponds to the index in the savedSolutions list
               clusterSolution.setId(savedSolutions.size());
               savedSolutions.add(clusterSolution);
           }
    }

    public int getSolutionIndex() {
        return solutionIndex;
    }

    public void setSolutionIndex(int solutionIndex) {
        this.solutionIndex = solutionIndex;
    }

    

    public Boolean getEditSolutionLabel() {
        return editSolutionLabel;
    }

    public void setEditSolutionLabel(Boolean editSolutionLabel) {
        this.editSolutionLabel = editSolutionLabel;
    }

    public ArrayList<ClusterSolution> getSavedSolutions() {
        return savedSolutions;
    }

    public void setSavedSolutions(ArrayList<ClusterSolution> savedSolutions) {
        this.savedSolutions = savedSolutions;
    }

    public String getSolutionLabel() {
        return solutionLabel;
    }

    public void setSolutionLabel(String solutionLabel) {
        this.solutionLabel = solutionLabel;
    }
    
    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public double getxCoord() {
        return xCoord;
    }

    public void setxCoord(double xCoord) {
        this.xCoord = xCoord;
    }

    public double getyCoord() {
        return yCoord;
    }

    public void setyCoord(double yCoord) {
        this.yCoord = yCoord;
    }

    public ClusterSolution getClusterSolution() {
        return clusterSolution;
    }

   

    // Run this whenever the ClusterSolution changes
    private void initClusterTableModel() {
        clusterTableModel.clear();
        for (ClusterInfo ci: clusterSolution.getClusterInfoList()) {
            clusterTableModel.add(new ClusterRow(ci));
        }
      
    }

    public int getClusterNum() {
        return clusterNum;
    }

    public void setClusterNum(int newClusterNum) {
       
            this.clusterNum = newClusterNum;
           
         
    }

    public HtmlDataTable getClusterTable() {
        return clusterTable;
    }

    public void setClusterTable(HtmlDataTable clusterTable) {
        this.clusterTable = clusterTable;
    }

    public ArrayList<ClusterRow> getClusterTableModel() {
        return clusterTableModel;
    }

    public void setClusterTableModel(ArrayList<ClusterRow> clusterTableModel) {
        this.clusterTableModel = clusterTableModel;
    }



   public class ClusterRow {
        Boolean viewEdit;
        String newValue;
        ClusterInfo clusterInfo;

        public ClusterInfo getClusterInfo() {
            return clusterInfo;
        }

        public void setClusterInfo(ClusterInfo clusterInfo) {
            this.clusterInfo = clusterInfo;
        }

        public String getNewValue() {
            return newValue;
        }

        public void setNewValue(String newValue) {
            this.newValue = newValue;
        }

        public Boolean getViewEdit() {
            return viewEdit;
        }

        public void setViewEdit(Boolean viewEdit) {
            this.viewEdit = viewEdit;
        }

        public ClusterRow(ClusterInfo clusterInfo) {
            viewEdit = Boolean.FALSE;
            this.clusterInfo = clusterInfo;
            newValue = clusterInfo.getLabel();
        }

    }

    
}
