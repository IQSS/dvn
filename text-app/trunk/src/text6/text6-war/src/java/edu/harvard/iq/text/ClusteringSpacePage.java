package edu.harvard.iq.text;

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.context.Resource;
import com.icesoft.faces.context.Resource.Options;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;

import javax.faces.bean.*;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;


/**
 *
 * @author ekraffmiller
 */
@ManagedBean (name="ClusteringSpacePage")
@ViewScoped
public class ClusteringSpacePage {
    private static final Logger logger = Logger.getLogger(ClusteringSpacePage.class.getCanonicalName());
    
   
    private String setId = "NewFormat";
    private Double xCoord;
    private Double yCoord;
    private Integer clusterNum;
    private Boolean discoverable;
    private Boolean displayMethodPoints;
    private DocumentSet documentSet;

    // The currently displayed solution
    private ClusterSolution clusterSolution;
    
    // Labels passed in the clustering URL
    private String clusterLabelParam;
    private String solutionLabelParam;  

    // history of solutions in this session
    private ArrayList<ClusterSolution> savedSolutions = new ArrayList<ClusterSolution>();
    
    private HtmlDataTable clusterTable;
    private ArrayList<ClusterRow> clusterTableModel = new ArrayList<ClusterRow>();
    private int solutionIndex;  // This is passed from the form to indicate that we need to display a saved solution rather than calculate a new solution

    // List for selecting metadata export fields
    private List<ArrayList> exportFieldList = new ArrayList<ArrayList>();
    // flag for opening and closing export poput form
    private Boolean showExportPopup = Boolean.FALSE;


     /** Creates a new instance of ClusterViewPage */
    public ClusteringSpacePage() {

    }

    @PostConstruct
    public void init() {
        logger.fine("initializing page");

        // Initialize inputs to Cluster calculation
        if (xCoord==null) {
            xCoord = new Double(0);
        }
        if (yCoord==null) {
            yCoord = new Double(0);
        } if (clusterNum==null) {
            clusterNum = 5;
        }
        if (setId==null) {
            throw new ClusterException("missing setId parameter.");
        }
        if (discoverable==null) {
            discoverable=false;
        }

        documentSet = new DocumentSet(setId);
        solutionIndex=-1;

        // Do calculation
        calculateClusterSolution(true);

        // Update cluster solution with labels
        // from request, if necessary

        clusterSolution.setLabel(solutionLabelParam);
        if (clusterLabelParam!=null) {
            clusterSolution.initClusterLabels(clusterLabelParam);
        }

        // If we have labels for this solution, added to the saved
        // solutions list

        if (clusterLabelParam!=null || solutionLabelParam !=null) {
            saveSolution(clusterSolution);
        }

        // initialize Export field list values
        for(int i=0;i< documentSet.getAllFields().size(); i++) {
            ArrayList<Object> exportField = new ArrayList<Object>();
            exportField.add(Boolean.FALSE);
            exportField.add(documentSet.getAllFields().get(i));
            exportFieldList.add(exportField);
        }
    }

    public String getDescription() {
        return documentSet.getDescription();
    }

    public List<ArrayList> getExportFieldList() {
        return exportFieldList;
    }

    public void setExportFieldList(List<ArrayList> exportFieldList) {
        this.exportFieldList = exportFieldList;
    }

    
    public Boolean getShowExportPopup() {
        return showExportPopup;
    }

    public void setShowExportPopup(Boolean showExportPopup) {
        this.showExportPopup = showExportPopup;
    }

    public void openExportPopup(ActionEvent ae) {
        showExportPopup = true;
    }
    public void closeExportPopup(ActionEvent ae) {
        showExportPopup = false;
    }

    /**
     *
     * @return MethodPoints array as JSON object
     */
    public String getMethodPointsString() {
      
        String str = "{\"MethodPoints\":[";
        for (int i= 0; i< documentSet.getMethodPoints().length;i++) {
            MethodPoint mp = documentSet.getMethodPoints()[i];
            str+= "{\"methodName\":"+"\""+mp.methodName+"\""+
                   ",\"numberOfClusters\":"+mp.numberOfClusters+
                   ",\"xCoord\":"+mp.xCoord+
                   ",\"yCoord\":"+mp.yCoord+
                   "}";
            if (i<documentSet.getMethodPoints().length-1) {
                str+=",";
            }
        }
       str+="]}";
        
        return str;

    }

    public void setMethodPointsString(String s) {

    }

    public String getHost() {
        try {
        return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new ClusterException(e.getMessage());
        }

    }

    public Boolean getDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }  

   



  
    // This is called either when the user clicks a point on the map,
    // or is browsing thru the history of points on the map.
    public void updateClusterSolutionListener(ActionEvent ae) {
        if (solutionIndex < 0) {
            // This is not a saved solution, so calculate it.
            calculateClusterSolution(true);
        } else {

            // the request is for a saved solution, so update the page
            // with the saved solution data.
            clusterSolution = savedSolutions.get(solutionIndex);
            populateClusterTableModel();
        }
    }

   
    /**
     *  This is called when the user wants to get a new solution based on the
     * existing point, but a different cluster number
     */
    public void changeClusterNumberListener(ActionEvent ae) {
        calculateClusterSolution(false);
    }




    private void calculateClusterSolution(boolean newPoint) {
        try {
            if (newPoint) {
                // calculate a new solution from scratch
                if (discoverable) {
                    clusterSolution = new ClusterSolution(documentSet, xCoord, yCoord);
                } else {
                    clusterSolution = new ClusterSolution(documentSet, xCoord, yCoord, clusterNum);
                }
            } else {
                // Calculate solution based on the existing solution,
                // and the new clusterNum (or discoverable)
                if (discoverable) {
                    clusterSolution = new ClusterSolution(clusterSolution);
                } else {
                    clusterSolution = new ClusterSolution(clusterSolution, clusterNum);
                }
            }
        } catch (Exception e) {
            DecimalFormat form = new DecimalFormat("#.#####");
            String errMessage =  "Could not calculate solution for ("
                    + form.format(this.xCoord) + "," + form.format(this.yCoord) + "), number of clusters = " + this.clusterNum+".";
            logger.warning(errMessage);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Clustering Error- ", errMessage+" Please try fewer clusters or a point closer to the convex hull."));
        }

        populateClusterTableModel();
       
    }
    
    
    public void saveClusterLabel(ActionEvent ae) {
        saveSolution(clusterSolution);
    }


    public void saveSolutionLabel(ActionEvent ae) {
           saveSolution(clusterSolution);
    }

    private void saveSolution(ClusterSolution cs) {
        if (!savedSolutions.contains(cs)) {
               // The id corresponds to the index in the savedSolutions list
               cs.setId(savedSolutions.size());
               savedSolutions.add(cs);
           }
    }
    public int getSolutionIndex() {
        return solutionIndex;
    }

    public void setSolutionIndex(int solutionIndex) {
        this.solutionIndex = solutionIndex;
    }


    public ArrayList<ClusterSolution> getSavedSolutions() {
        return savedSolutions;
    }

    public void setSavedSolutions(ArrayList<ClusterSolution> savedSolutions) {
        this.savedSolutions = savedSolutions;
    }

    
    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public Double getxCoord() {
        return xCoord;
    }

    public void setxCoord(Double xCoord) {
        this.xCoord = xCoord;
    }

    public Double getyCoord() {
        return yCoord;
    }

    public void setyCoord(Double yCoord) {
        this.yCoord = yCoord;
    }

    public Boolean getDisplayMethodPoints() {
        return displayMethodPoints;
    }

    public void setDisplayMethodPoints(Boolean displayMethodPoints) {
        this.displayMethodPoints = displayMethodPoints;
    }


    public String getClusterLabelParam() {
        return clusterLabelParam;
    }

    public void setClusterLabelParam(String clusterLabelParam) {
        this.clusterLabelParam = clusterLabelParam;
    }

    public String getSolutionLabelParam() {
        return solutionLabelParam;
    }

    public void setSolutionLabelParam(String solutionLabelParam) {
        this.solutionLabelParam = solutionLabelParam;
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
            clusterTableModel.add(new ClusterRow(setId, ci, documentSet.getSummaryFields()));
        }
      
    }

    public Integer getClusterNum() {
        return clusterNum;
    }

    public void setClusterNum(Integer newClusterNum) {
       
            this.clusterNum = newClusterNum;
           
         
    }
    public void clickActionListener(ActionEvent ae) {
        System.out.println("button was clicked!");
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

    public FileResource getExportResource() {
      
        return new FileResource(this.clusterSolution, this.setId, this.getHost(), exportFieldList);
    }

    class FileResource implements Resource, Serializable{
       
        ClusterSolution solution;
        String setId;
        String host;
        List<ArrayList> exportFieldList;
        ClusteringSpacePage pg;
        public FileResource(ClusterSolution cs, String setId, String host, List<ArrayList> exportFieldList) {
            solution = cs;
            this.setId = setId;
            this.host = host;
            this.exportFieldList = exportFieldList;
          

        }



        public String calculateDigest() {
            return null;
        }

        public Date lastModified() {
            return null;
        }

        public InputStream open() throws IOException {
            String exportStr = "Clustering Export, Set Id = "+ setId;
            exportStr += "\nExport Time: " + new Date()+"\n";
            exportStr +="\nLink: http://"+host+"/text/faces/ClusteringSpacePage.xhtml?setId="+setId+"&x="+solution.getFormatX()+"&y="+solution.getFormatY()+"&clusterNum="+solution.getNumClusters()+"&discoverable="+solution.getDiscoverable()+"&solutionLabel="+solution.getEncodedLabel()+"&clusterLabels="+solution.getClusterLabels();
            exportStr+=getClusteringExportString(exportFieldList);
         
            return new ByteArrayInputStream(exportStr.getBytes());
        }

        public void withOptions(Options arg0) throws IOException {
        }

        public String getClusteringExportString(List<ArrayList> exportFieldList) {
            StringBuffer str = new StringBuffer();
            str.append("\nCoordinates: " + solution.getFormatX() + ", " + solution.getFormatY());
            str.append("\nClusters: " + solution.getFormatClusterNum());

            if (solution.getLabel() != null && !solution.getLabel().isEmpty()) {
                str.append("\nLabel: ");
                str.append(solution.getLabel());
            }
            str.append("\n");

            int count = 1;
            for (ClusterInfo ci : solution.getClusterInfoList()) {
                str.append("\nCluster " + count);

                if (ci.getLabel() != null && !ci.getLabel().isEmpty()) {
                    str.append("\nLabel: ");
                    str.append(ci.getLabel());
                }
                str.append("\nDocument Count: " + ci.getClusterCount());
                str.append("\nDocument Percentage: " + ci.getClusterPercentStr());
                str.append("\nWord List: " + ci.getTopWords());
                str.append("\nDocument Listing: Document Number");
                for (ArrayList exportField : exportFieldList) {
                    if (exportField.get(0).equals(Boolean.TRUE)) {
                        str.append(", "+ exportField.get(1));
                    }
                }

                int docCount = 1;
                for (Document doc : ci.getDocumentList()) {
                    str.append("\n" + docCount);
                    for (ArrayList exportField : exportFieldList) {
                        if (exportField.get(0).equals(Boolean.TRUE)) {
                            str.append(", " + doc.getMetadata().get(exportField.get(1)));
                        }
                    }
                    docCount++;
                }
                str.append("\n");

                count++;
            }
            return str.toString();
        }

    }


   
       

      

    

    
}
