package edu.harvard.iq.text;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.icesoft.faces.context.effects.JavascriptContext;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

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
    private Boolean discoverable;

    public Boolean getDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }  

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



  

    // This is called either when the user clicks a point on the map,
    // or is browsing thru the history of points on the map.
    public void updateClusterSolutionListener(ActionEvent ae) {
         if (solutionIndex<0) {
            // This is not a saved solution, so calculate it.
            calculateClusterSolution(true);
        } else {

            // the request is for a saved solution, so update the page
            // with the saved solution data.
             clusterSolution = savedSolutions.get(solutionIndex);
            solutionLabel = clusterSolution.getLabel();
            populateClusterTableModel();
        }
    }

   
    /**
     *  This is called when the user wants to get a new solution based on the
     * existing point, but a different cluster number
     */
    public void changeClusterNumberListener(ActionEvent ae) {

     //   if (discoverable) {
     //       calculateDiscoverable();
     //   }
     //   else {
            calculateClusterSolution(false);
     //   }
    }

    public void getDiscoverableListener(ActionEvent ae) {
          this.clusterNum = clusterSolution.getDiscoverableClusterNum();
    }

    private void calculateDiscoverable() {
            // Get the discoverable cluster number
            // for this point, and set
            // it to the clusterNum.
            this.clusterNum = clusterSolution.getDiscoverableClusterNum();

            // Now calculate a new solution with the existing point
            // and the discovered clusterNum.
            calculateClusterSolution(false);

            // Call javascript to add the point to the map with the calculated clusterNum
            JavascriptContext.addJavascriptCall(FacesContext.getCurrentInstance(), "addPointForClusterNum('"+clusterNum+"');");
        
   }

    private void calculateClusterSolution(boolean newPoint) {
        if (newPoint) {
            // calculate a new solution from scratch
            clusterSolution = new ClusterSolution(documentSet, xCoord, yCoord, clusterNum);
        } else {
            // Calculate solution based on the existing solution,
            // and the new clusterNum
            clusterSolution = new ClusterSolution(clusterSolution,clusterNum);
        }
        // This is a new solution, so clear out the solution label.
        solutionLabel="";
        populateClusterTableModel();

    }
    
    public void saveClusterLabel(ActionEvent ae) {
        int rowIndex = clusterTable.getRowIndex();
        ClusterRow row = (ClusterRow) clusterTable.getRowData();
        clusterSolution.getClusterInfoList().get(rowIndex).setLabel(row.newValue);
        if (!savedSolutions.contains(clusterSolution)) {
               // The id corresponds to the index in the savedSolutions list
               clusterSolution.setId(savedSolutions.size());
               savedSolutions.add(clusterSolution);
        }
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

    public int getDocumentCount() {
        return documentSet.getDocumentCount();
    }

    // Run this whenever the ClusterSolution changes
    private void populateClusterTableModel() {
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
       
        PanelTabSet panelTabSet;
        String newValue;
        Boolean showPopup = Boolean.FALSE;
        ClusterInfo clusterInfo;
        int viewDocumentIndex;

        public ClusterRow(ClusterInfo clusterInfo) {          
            this.clusterInfo = clusterInfo;
            viewDocumentIndex = 0;  // Show the examplar document first
            newValue = clusterInfo.getLabel();
        }
       

        public PanelTabSet getPanelTabSet() {
            return panelTabSet;
        }

        public void setPanelTabSet(PanelTabSet panelTabSet) {
            this.panelTabSet = panelTabSet;
        }

       public int getRandomDocumentIndex() {
           Random ran = new Random();
           return ran.nextInt(clusterInfo.getFileIndices().size());
       }

       public int getViewDocumentIndex() {
           return this.viewDocumentIndex;
       }

       public void setViewDocumentIndex(int i) {
           viewDocumentIndex = i;
       }

       /**
        * ActionListener that is called when user clicks on a document in the ViewList tab
        * @param ae
        */
       public void viewDocumentInList(ActionEvent ae) {
           UIComponent comp = ae.getComponent().getParent();
           while(!(comp instanceof HtmlDataTable)) {
               comp = comp.getParent();
           }
           HtmlDataTable table = (HtmlDataTable)comp;
           comp = comp.getParent();
           while(!(comp instanceof PanelTabSet)) {
               comp = comp.getParent();
           }
           PanelTabSet tabSet = (PanelTabSet)comp;
           viewDocumentIndex = table.getRowIndex();
           tabSet.setSelectedIndex(0);  // Select the Document tab
       }


       public void viewRandom(ActionEvent ae) {
           viewDocumentIndex=getRandomDocumentIndex();
       }
       public void viewFirst(ActionEvent ae) {
           viewDocumentIndex=0;
       }
       public void viewLast(ActionEvent ae) {
           viewDocumentIndex=clusterInfo.getFileIndices().size()-1;
       }
       public void viewNext(ActionEvent ae) {
           if (viewDocumentIndex<clusterInfo.getFileIndices().size()) {
                viewDocumentIndex++;
           }
       }

       public void viewPrevious(ActionEvent ae) {
           if (viewDocumentIndex>0) {
                viewDocumentIndex--;
           }
       }

       public void openPopup(ActionEvent ae) {
           
            showPopup = Boolean.TRUE;

        }

        public void closePopup(ActionEvent ae) {
            showPopup=Boolean.FALSE;
        }

        public Boolean getShowPopup() {
            return showPopup;
        }

        public void setShowPopup(Boolean showPopup) {
            this.showPopup = showPopup;
        }

        public String getViewDocumentName() {
            String docName = clusterInfo.getDocInfoList().get(this.viewDocumentIndex).getDocId();
            if (setId.equals("1")) {
                    docName += "Bush02.txt";
            }
            return  docName;
        }

        public String getViewDocumentPreview() {

            int previewLength = 60;
            String preview = null;
            byte[] byteArray = new byte[previewLength];
            try {
                FileInputStream fin = getViewDocumentInputStream();
                BufferedInputStream bis = new BufferedInputStream(fin);
                bis.read(byteArray,0, previewLength);
                preview = new String(byteArray, "utf-8");
               if (bis.available()>0) {
                   preview+="...";
               }
            } catch (IOException e) {
                throw new ClusterException(e.getMessage());
            }
            return preview;

        }

       public String getViewDocumentText() {

           ByteArrayOutputStream baos = new ByteArrayOutputStream();

           try {
               FileInputStream fin = getViewDocumentInputStream();
               BufferedInputStream bis = new BufferedInputStream(fin);

               // Now read the buffered stream.
               while (bis.available() > 0) {
                   baos.write(bis.read());
                   //   .print((char) bis.read());
               }
               fin.close();
           } catch (IOException e) {
               throw new ClusterException(e.getMessage());
           }
           return baos.toString();

       }

       private FileInputStream getViewDocumentInputStream() throws IOException {
           String docRoot = System.getProperty("text.documentRoot");
           File setDir = new File(docRoot, setId);
           File docDir = new File(setDir, "docs");
           File document = new File(docDir, getViewDocumentName());

           return new FileInputStream(document);

       }

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

       

      

    }

    
}
