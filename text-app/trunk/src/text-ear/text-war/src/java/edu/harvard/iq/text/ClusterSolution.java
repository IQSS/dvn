/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ekraffmiller
 */
public class ClusterSolution {

    private static final double TWO_PI = Math.PI*2;

    // the document set we are analyzing
    private DocumentSet documentSet;

    //Input parameters for this cluster solution
    private double candidateXPoint; //Mapped into Methods Space
    private double candidateYPoint;
    private int numClusters;

    // This is user's description of the cluster solution
    private String label;

    // Used to identify saved solutions (right now just corresponds to index, but may
    // be a database id later)
    private Integer id;
    // Save this data because it can be used to create another solution with the same point,
    // but different number of clusters.
    double[] weightsArray;
    double[][] simMatrix;

    

    // The solution organized as a list of ClusterInfo objects
    ArrayList<ClusterInfo> clusterInfoList = new ArrayList<ClusterInfo>();

    //Constructor - here you are starting from scratch
    ClusterSolution(DocumentSet documentSet, double candidateXPoint, double candidateYPoint, int numClusters) {
        this.documentSet = documentSet;
        this.candidateXPoint = candidateXPoint;
        this.candidateYPoint = candidateYPoint;
        this.numClusters = numClusters;
        calculateSolution();
    }

    /**
     * Here you are using an existing solution, but recalculating
     * based on the existing solution point, and a different number of clusters
     */
    ClusterSolution(ClusterSolution existingSolution, int numClusters) {
        this.documentSet = existingSolution.documentSet;
        this.candidateXPoint = existingSolution.candidateXPoint;
        this.weightsArray = existingSolution.weightsArray;
        this.simMatrix = existingSolution.simMatrix;
        this.numClusters = numClusters;
        doClusterCalculations();
    }

    public String getInfoLabel() {
        String str = "";
        if (label!=null && !label.isEmpty()) {
            return label;
        }
         else {
            int count =0;
            for (ClusterInfo ci : clusterInfoList) {
                if (ci.getLabel()!=null && !ci.getLabel().isEmpty() && count<3) {
                    if (!str.equals("")) {
                        str+=", ";
                    }
                    str+=ci.getLabel();
                    count++;
                }
            }

         }
        return str;
    }
    
    public int getNumClusters() {
        return clusterInfoList.size();
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getX() {
        return this.candidateXPoint;
    }

    public double getY() {
        return this.candidateYPoint;
    }

    public ArrayList<ClusterInfo> getClusterInfoList() {
        return clusterInfoList;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    

    private void calculateSolution() {
        // These two calculations depend only on the candidate point
        // so when we change numClusters, we don't have to recalculate them
        weightsArray = makeWeightsArray();
        simMatrix = fillSimMatrix(weightsArray);
        
        // These calculations depend on numClusters and the candidate point
        doClusterCalculations();
        
    }

    private void doClusterCalculations() {
        // ensembleAssignments is a (# of Documents) Length Array with a cluster number for each member
        int[] ensembleAssignments = getEnsembleAssignments(simMatrix);

        createClusterObjects(ensembleAssignments, documentSet.getWordDocumentMatrix());

        Collections.sort(clusterInfoList);
        System.out.println("finished calculating, x="+this.candidateXPoint+", y="+this.candidateYPoint+" clusterInfo:" + clusterInfoList);
    }

    private void createClusterObjects(int[] ensembleAssignments, int[][] wordDocMatrix ) {

        // first read through ensemble assignments tocreate the objects
        //and add the file (ie document) indices to each cluster
        
        for (int i=0; i< ensembleAssignments.length; i++ ) {
            int clusterNumber = ensembleAssignments[i];
            ClusterInfo foundCluster = getCluster(clusterNumber);
            if (foundCluster!=null) {
                foundCluster.getFileIndices().add(i);
            } else {
                ClusterInfo newCluster = new ClusterInfo(clusterNumber);
                newCluster.getFileIndices().add(i);
                clusterInfoList.add(newCluster);
            }
        }

        // set cluster percent & figure out the word list for each cluster
        for (ClusterInfo ci : clusterInfoList) {
            ci.setClusterPercent(documentSet.getWordDocumentMatrix().length);
            ci.calculateWordList(documentSet.getWordDocumentMatrix(), documentSet.getWords());
        }
    }

    private ClusterInfo getCluster(int clusterNum) {
        for (ClusterInfo ci : clusterInfoList) {
            if (ci.getClusterNumber() == clusterNum) {
                return ci;
            }
        }
        return null;
    }
    /*

    public void updateCandidatePoint(float candidateXPoint, float candidateYPoint) {
        this.candidateXPoint = candidateXPoint;
        this.candidateYPoint = candidateYPoint;
        // When we change the candidate point, we have to re-do all the calculations
        calculateSolution();
    }

    //Methods
    public void updateClusterNumber(int newNumber) {
        numClusters = newNumber;
        // when we change the cluster number, we have to recalculate
        doClusterCalculations();
       
    }
*/
    // TODO: I converted these from floats to doubles - is this ok?
    private double[] makeWeightsArray() {
    /*
        In this method we establish the weights array.
        We calculate the distance between the candidate point and each method.
        We then convert those distances to the density under the normal curve
        at mean = 0 and sd = 0.25.
        We then divide each element of the array by the sum of the array.
        Then we return the array.
         */

        double[] distanceArray = new double[documentSet.getMethodPoints().length]; //There is one weight per method
        Coordinate candCoord = new Coordinate(candidateXPoint, candidateYPoint);
        for (int i = 0; i < distanceArray.length; i++) {
            Coordinate methodCoord = new Coordinate(documentSet.getMethodPoints()[i].xCoord, documentSet.getMethodPoints()[i].yCoord);
            distanceArray[i] = candCoord.distance(methodCoord);

        }

        double[] weights = new double[distanceArray.length]; //Normalize the distance array
        double sumWeights = 0;
        for (int i = 0; i < distanceArray.length; i++) {
            //Formula for the Normal PDF mu = 0, sigma = 0.25,
            //this is equivalent to the R code: dnorm(i, sd=0.25)

            // Original line from processing file:
            // weights[i] = 1 / (sqrt(TWO_PI) * .25) * exp(-(sq(distanceArray[i] - 0) / (2 * sq(.25))));
            // TODO:  why  subtract 0 from distance array?
            weights[i] = 1 / (Math.sqrt(TWO_PI) * .25) * Math.exp(-(Math.pow((distanceArray[i] - 0),2) / (2 * Math.pow(.25,2))));
            sumWeights = weights[i] + sumWeights;
        }
        for (int i = 0; i < weights.length; i++) {  //Divide by the sum
            weights[i] = weights[i] / sumWeights;
        }
        return weights;
    }

    private double[][] fillSimMatrix(double[] weights) {
        /*
        Takes in the weights matrix from the makeWeightsArray function.  Outputs a similarity matrix for K-means
         */
        int docCount = documentSet.getWordDocumentMatrix().length;
        double[][] simMatrix = new double[docCount][docCount]; // a D X D matrix
        //foreach cell
        //foreach method do these two documents share a cluster, multiplied by weight, added together
        //Plus we track the maximum value of each row for the next step
        float[] maxRowValue = new float[simMatrix.length];
        for (int r = 0; r < simMatrix.length; r++) { //Iterate over the rows
            for (int c = 0; c < simMatrix.length; c++) { //For each Row, Iterate over the columns
                double cellValue = 0;
                for (int m = 0; m < weights.length; m++) { 
                    // For each Cell, Iterate over the methods
                    // Is the row document in the same cluster as the column document for this method?
                    if (documentSet.getClusterMembership()[m][r] == documentSet.getClusterMembership()[m][c]) {
                        cellValue = cellValue + weights[m]; //If so then add the weight
                    }
                }
                simMatrix[r][c] = cellValue;
            }
           

            float max = 0;
            for (int i = 0; i< simMatrix[r].length; i++) {
                if (simMatrix[r][i] > max) {
                    max = (float)simMatrix[r][i];
                }
            }
            maxRowValue[r] = max;
        }
        for (int r = 0; r < simMatrix.length; r++) { //Iterate over the rows
            for (int c = 0; c < simMatrix.length; c++) { //For each Row, Iterate over the columns
                simMatrix[r][c] = 1 - simMatrix[r][c] / maxRowValue[r]; //1 - Value divided by the maximum of each row
            }
        }

        return simMatrix;
    }


    /*
     * Takes the similarity matrix from "Fill Similarity Matrix"
     *    and the number of clusters, then runs K-Means and returns the assignments.
     *
     */
    private int[] getEnsembleAssignments(double[][] simMatrix) {


        int[] assignments = new int[documentSet.getWordDocumentMatrix().length];
        long kmeansRandomSeed = (long) 12345;

        BasicKMeans kmeans = new BasicKMeans(simMatrix, numClusters, 20, kmeansRandomSeed);

        kmeans.run();
        Cluster[] holder;
        holder = kmeans.getClusters();
        int[][] assignmentHolder = new int[numClusters][documentSet.getWordDocumentMatrix().length];
        for (int i = 0; i < numClusters; i++) {
            assignmentHolder[i] = holder[i].getMemberIndexes();
            for (int j = 0; j < holder[i].getMemberIndexes().length; j++) {
                int index = assignmentHolder[i][j];
                assignments[index] = i;
            }
        }

        return assignments;
    }
/*
    public int hashCode() {
        int hash = 0;
        hash += (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ClusterSolution)) {
            return false;
        }
        ClusterSolution other = (ClusterSolution) object;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
*/
    // The following is translated from Brandon's processing code:

/*
    private int[][] parseMembership(int[] ensembleAssignments) {
        
       // This Processes the EnsembleAssignments into something more useful.
         
        List<Integer> unique = new ArrayList<Integer>();
        List<Integer>  counter = new ArrayList<Integer>();
        unique.add(ensembleAssignments[0]); //Prime with the first number...
        counter.add(1);         //and increment the counter accordingly
        for (int i = 1; i < ensembleAssignments.length; i++) { //Loop through the Membership List
            int testcounter = 0; //Use this to check against unique length
            for (int j = 0; j < unique.size(); j++) { //Loop through the List of Clusters Already Found
                if (unique.get(j) == ensembleAssignments[i]) { //If its an existing cluster increment the counter
                    Integer val = counter.get(j);
                    val++;
                    counter.set(j, val);
                    //counter[j]++;
                } else if (unique.get(j) != ensembleAssignments[i]) {
                    testcounter++;
                }
            }
            if (testcounter == unique.size()) {
                //If there was no instance of the unique list matching the memberlist, add a new item
                unique.add(ensembleAssignments[i]);
                counter.add(1);
                //unique  = expand(unique, unique.length + 1);
                //counter = expand(counter, counter.length + 1);
                //unique[unique.length - 1] = members[i];
                //counter[counter.length - 1] = 1;
            }
        }
      
        //So now we have a list of unique cluster numbers and a counter for each one.
        //Here we just process them for export.
        int[][] out = new int[2][unique.size()];
        for (int i = 0; i < unique.size(); i++) {
         
            out[0][i] = unique.get(i);
            out[1][i] =  counter.get(i);

        }

     
    return out;
  }
*/

 

  /*
   * Returns 2 dim array, rowSize = numClusters, colSize = numDocuments
   * Loops thru the cluster numbers (clusters[0])
   * For each cluster number, loops thru ensembleAssignments (clusterNumber assigned to each doc)
   *    If cluster number == assigned cluster
   *        then assign the index of the ensembleAssignments array to the fileIndexes cell
   *
   * Basically, for each clusterNum, we are getting a list of the indices in which this clusterNum appears in the ensempleAssignements array
   */
  /*
  private int[][] makeFileIndices(int[] ensembleAssignments) {
    
  //  Another Painfully inefficient way to process files
    
    fileIndexes = new int[clusters[0].length][ensembleAssignments.length];
        for (int i = 0; i < clusters[0].length; i++) {
            int count = 0;
            for (int j = 0; j < ensembleAssignments.length; j++) {
                if (clusters[0][i] == ensembleAssignments[j]) {
                    fileIndexes[i][count] = j;
                    count++;
                }
            }
        }
        return fileIndexes;
    }
*/
  /*
    public String[] getMutualInfoWords(  int clusterIndex, int[] ensembleAssignments) {
        
        //This method only gets one set of Mutual Information words at a time.
        // This allows us to split the task up over many frames.
         
        // this array is wordSize length (the # columns in wordDocMatrix)
        String[] out = new String[documentSet.getWordDocumentMatrix()[0].length]; //The empty array for the output

        //Term Document Matrix for the Documents Within the Cluster Only

        // docList - this is just subsettingthe fileIndexes array because the array length varies based on #docs in cluster
        int[] docList = Arrays.copyOfRange(fileIndexes[clusterIndex], 0, (clusters[1][clusterIndex]));
        // docTermIn has the same structure as wordDocumentMatrix, but the rows
        // are from documents in this cluster only

        int[][] docTermIn = new int[docList.length][];
        for (int i=0; i < docList.length; i++) {
            //Pull the Document Term Matrix for those documents
            docTermIn[i] = documentSet.getWordDocumentMatrix()[docList[i]];
            
        }

        //Term Document Matrix for those not in the cluster (Create a not-on-the-list list)

        int[] notdocList = new int[ensembleAssignments.length - docList.length];
        int count = 0;
        for (int i = 0; i < ensembleAssignments.length; i++) {
            if (clusters[0][clusterIndex] != ensembleAssignments[i]) {
                notdocList[count] = i;
                count++;
            }
        }

        int[][] docTermOut = new int[notdocList.length][documentSet.getWordDocumentMatrix().length];
        for (int i = 0; i < notdocList.length; i++) {
            docTermOut[i] = documentSet.getWordDocumentMatrix()[notdocList[i]];
            //Pull the Document Term Matrix for those documents
        }

        //Means of Terms in cluster and outside of cluster
        float[] meanInCluster = getColumnMeans(docTermIn);
        float[] meanOutCluster = getColumnMeans(docTermOut);
        //Difference between those two
        float[] diff = new float[meanInCluster.length];
        for (int i = 0; i < diff.length; i++) {
            diff[i] = meanInCluster[i] - meanOutCluster[i];
        }

        //Put the biggest differences at the top (This is pretty hackish and can repeat words...)
        
        
        ArrayList<WordValue> words = new ArrayList<WordValue>();
        for (int i= 0; i< diff.length; i++ ) {
            words.add( new WordValue(documentSet.getWords().get(i),diff[i]));
        }
        Collections.sort(words);
        for (int i = 0; i < diff.length; i++) {
            out[i] = words.get(i).title;
        }
        return out;
    }

*/
  /*
    float[] getColumnMeans(int[][] matrix) {
        
        An ancillary useful method...
         
        float[] out = new float[matrix[0].length];
        for (int c = 0; c < matrix[0].length; c++) {
            float sum = 0;
            for (int r = 0; r < matrix.length; r++) {
                sum = sum + matrix[r][c];
            }
            out[c] = sum / matrix.length;
        }
        return out;
    }

   */
}
