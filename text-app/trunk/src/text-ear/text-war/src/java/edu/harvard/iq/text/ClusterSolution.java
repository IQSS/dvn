/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ekraffmiller
 */
public class ClusterSolution {

    private static final double TWO_PI = Math.PI*2;

    // the document set we are analyzing
    private DocumentSet documentSet;

    //Cluster Variables
    private double candidateXPoint; //Mapped into Methods Space
    private double candidateYPoint;
    private int numClusters;

    // this is needed for mutualInfo words, so keep it as member variable for now
    // ensembleAssignments is a (# of Documents) Length Array with a cluster number for each member
    int[] ensembleAssignments;

    // Save this data because it is re-used when numClusters is updated
    double[] weightsArray;
    double[][] simMatrix;

    

    // This is the solution
    int[][] clusters; //Two Rows- a list of unique clusters (Row 0) and a counter for each one (Row 1)
                      // in Brandon's code, this is "members"
 
    // This is the file info for the solution
    private int[][] fileIndexes; //A list of file index numbers- Unique Cluster Number X FileList
                        // (its really a set of vectors...,  This is now the result of makeFileIndices

    //Constructor
    ClusterSolution(DocumentSet documentSet, double candidateXPoint, double candidateYPoint, int numClusters) {
        this.documentSet = documentSet;
        this.candidateXPoint = candidateXPoint;
        this.candidateYPoint = candidateYPoint;
        this.numClusters = numClusters;
        calculateSolution();
    }

    private void calculateSolution() {
        // These two calculations don't depend on numClusters
        // so when we change numClusters, we don't have to recalculate them
        weightsArray = makeWeightsArray();
        simMatrix = fillSimMatrix(weightsArray);
        
        // These calculations depend on numClusters
        doClusterCalculations();
        
    }

    private void doClusterCalculations() {
        // ensembleAssignments is a (# of Documents) Length Array with a cluster number for each member
        ensembleAssignments = getEnsembleAssignments(simMatrix);
        clusters = parseMembership(ensembleAssignments);
        fileIndexes = this.makeFileIndices(ensembleAssignments);

        System.out.println("finished calculating, clusterInfo:" + getClusterInfoList());
    }

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
                for (int m = 0; m < weights.length; m++) { //For each Cell, Iterate over the methods
                    if (documentSet.getClusterMembership()[m][r] == documentSet.getClusterMembership()[m][c]) {//is the row document in the same cluster as the column document for this method?
                        cellValue = cellValue + weights[m]; //If so then add the weight
                    }
                }
                simMatrix[r][c] = cellValue;
            }
            /* original code from brandon:
            float[] temp = new float[simMatrix[r].length];
            for (int i = 0; i < simMatrix[r].length; i++) {
                temp[i] = (float) simMatrix[r][i];  // TODO: why convert to float?
            }
            maxRowValue[r] = max(temp);
            */

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

    private int[] getEnsembleAssignments(double[][] simMatrix) {
        /*
        Takes the similarity matrix from "Fill Similarity Matrix"
         and the number of clusters, then runs K-Means and returns the assignments.
         */
       
        int[] ensembleAssignments = new int[documentSet.getWordDocumentMatrix().length];
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
                ensembleAssignments[index] = i;
            }
        }
        
        return ensembleAssignments;
    }

    private int[][] parseMembership(int[] ensembleAssignments) {
        /*
        This Processes the EnsembleAssignments into something more useful.
         */
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
        ArrayList<ClusterInfo> outList = new ArrayList<ClusterInfo>();
        //So now we have a list of unique cluster numbers and a counter for each one.
        //Here we just process them for export.
        int[][] out = new int[2][unique.size()];
        for (int i = 0; i < unique.size(); i++) {
         
            out[0][i] = unique.get(i);
            out[1][i] =  counter.get(i);

        }

     
    return out;
  }


  public ArrayList<ClusterInfo> getClusterInfoList() {
      ArrayList<ClusterInfo> cList = new ArrayList<ClusterInfo>();
      for (int i=0; i<numClusters; i++ ) {
          cList.add(new ClusterInfo(clusters[0][i], clusters[1][i]));
      }
      return cList;
  }
  
  private int[][] makeFileIndices(int[] members) {
    /*
    Another Painfully inefficient way to process files
    */
    fileIndexes = new int[clusters[0].length][members.length];
        for (int i = 0; i < clusters[0].length; i++) {
            int count = 0;
            for (int j = 0; j < members.length; j++) {
                if (clusters[0][i] == members[j]) {
                    fileIndexes[i][count] = j;
                    count++;
                }
            }
        }
        return fileIndexes;
    }
/*
    public String[] getMutualInfoWords(  int clusterNumber) {
        
        //This method only gets one set of Mutual Information words at a time.
        // This allows us to split the task up over many frames.
         

        String[] out = new String[documentSet.getWordDocumentMatrix().length]; //The empty array for the output

        //Term Document Matrix for the Documents Within the Cluster Only
        int[] docList = subset(fileIndexes[clusterNumber], 0, int
        (clusters[1][clusterNumber])

            ); //We know the list is as long as the count of documents in the given cluster number

    float[][] docTermIn = new float[docList.length][documentSet.getWordDocumentMatrix().length];
    for (int i=0; i < docList.length; i++) {
            docTermIn[i] = wordDocumentMatrix.getDataArray(docList[i]);
            //Pull the Document Term Matrix for those documents
        }

        //Term Document Matrix for those not in the cluster (Create a not-on-the-list list)

        int[] notdocList = new int[ensembleAssignments.length - docList.length];
        int count = 0;
        for (int i = 0; i < ensembleAssignments.length; i++) {
            if (clusters[0][clusterNumber] != ensembleAssignments[i]) {
                notdocList[count] = i;
                count++;
            }
        }

        float[][] docTermOut = new float[notdocList.length][documentSet.getWordDocumentMatrix().length];
        for (int i = 0; i < notdocList.length; i++) {
            docTermOut[i] = wordDocumentMatrix.getDataArray(notdocList[i]);
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
        WordList words = new WordList(diff, wordDocumentMatrix.columnNames);
        for (int i = 0; i < diff.length; i++) {
            out[words.getRank(i)] = wordDocumentMatrix.columnNames[i];
        }
        return out;
    }
*/

    float[] getColumnMeans(float[][] matrix) {
        /*
        An ancillary useful method...
         */
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
}
