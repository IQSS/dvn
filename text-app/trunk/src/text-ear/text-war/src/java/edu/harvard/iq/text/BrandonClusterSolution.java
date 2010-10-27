/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;

import com.vividsolutions.jts.geom.Coordinate;

/**
 *
 * @author ekraffmiller
 */
public class BrandonClusterSolution {

    //Cluster Variables
    float candidateXPoint; //Mapped into Methods Space
    float candidateYPoint;
    int numClusters;
    int[] members; //A (# of Documents) Length Array with a cluster number for each member,
                    //This is now the result of getEnsembleAssignments
    int[][] clusters; //Two Rows- a list of unique clusters (Row 0) and a counter for each one (Row 1)
    int[][] fileIndexes; //A list of file index numbers- Unique Cluster Number X FileList
                        // (its really a set of vectors...,  This is now the result of makeFileIndices

    //Constructor
    BrandonClusterSolution(float x, float y, int number) {
        candidateXPoint = x;
        candidateYPoint = y;
        numClusters = number;
    }

    //Methods
    void updateClusterNumber(int newNumber) {
        numClusters = newNumber;
    }

    float[] makeWeightsArray() { //Assumes access to the candidate X/Y Points and the methods object.
    /*
        In this method we establish the weights array.
        We calculate the distance between the candidate point and each method.
        We then convert those distances to the density under the normal curve
        at mean = 0 and sd = 0.25.
        We then divide each element of the array by the sum of the array.
        Then we return the array.
         */

        float[] distanceArray = new float[methods.length]; //There is one weight per method
        for (int i = 0; i < distanceArray.length; i++) {
            distanceArray[i] = dist(candidateXPoint, candidateYPoint, float
            (methodPointsTable .getString(i, 0)
            ), float(
            methodPointsTable.getString(i, 1)


           ));
    }

    float[] weights = new float[distanceArray.length]; //Normalize the distance array
    float sumWeights = 0;
        for (int i = 0; i < distanceArray.length; i++) {
            //Formula for the Normal PDF mu = 0, sigma = 0.25,
            //this is equivalent to the R code: dnorm(i, sd=0.25)
            weights[i] = 1 / (sqrt(TWO_PI) * .25) * exp(-(sq(distanceArray[i] - 0) / (2 * sq(.25)))); 
            sumWeights = weights[i] + sumWeights;
        }
        for (int i = 0; i < weights.length; i++) {  //Divide by the sum
            weights[i] = weights[i] / sumWeights;
        }
        return weights;
    }

    double[][] fillSimMatrix(float[] weights) {
        /*
        Takes in the weights matrix from the makeWeightsArray function.  Outputs a similarity matrix for K-means
         */
        double[][] simMatrix = new double[wordDocumentMatrix.rowCount][wordDocumentMatrix.rowCount]; // a D X D matrix
        //foreach cell
        //foreach method do these two documents share a cluster, multiplied by weight, added together
        //Plus we track the maximum value of each row for the next step
        float[] maxRowValue = new float[simMatrix.length];
        for (int r = 0; r < simMatrix.length; r++) { //Iterate over the rows
            for (int c = 0; c < simMatrix.length; c++) { //For each Row, Iterate over the columns
                float cellValue = 0;
                for (int m = 0; m < weights.length; m++) { //For each Cell, Iterate over the methods
                    if (float {
                        (clusterMembershipTable.getString(m, r)) == float(
                    }
                    clusterMembershipTable.getString(m, c)







                )) {//is the row document in the same cluster as the column document for this method?
            cellValue = cellValue + weights[m]; //If so then add the weight
          }
        }
        simMatrix[r][c] = cellValue;
      }

      float[] test = new float[simMatrix[r].length];
      for (int i=0; i < simMatrix[r].length; i++) {
                test[i] = (float) simMatrix[r][i];
            }
            maxRowValue[r] = max(test);
        }
        for (int r = 0; r < simMatrix.length; r++) { //Iterate over the rows
            for (int c = 0; c < simMatrix.length; c++) { //For each Row, Iterate over the columns
                simMatrix[r][c] = 1 - simMatrix[r][c] / maxRowValue[r]; //1 - Value divided by the maximum of each row
            }
        }

        return simMatrix;
    }

    int[] getEnsembleAssignments(double[][] simMatrix) {
        /*
        Takes the similarity matrix from "Fill Similarity Matrix"
         and the number of clusters, then runs K-Means and returns the assignments.
         */
        int ensembleClusterNum = numClusters;
        int[] ensembleAssignments = new int[wordDocumentMatrix.rowCount];
        long kmeansRandomSeed = (long) 12345;

        BasicKMeans kmeans = new BasicKMeans(simMatrix, ensembleClusterNum, 20, kmeansRandomSeed);

        kmeans.run();
        Cluster[] holder;
        holder = kmeans.getClusters();
        int[][] assignmentHolder = new int[ensembleClusterNum][wordDocumentMatrix.rowCount];
        for (int i = 0; i < ensembleClusterNum; i++) {
            assignmentHolder[i] = holder[i].getMemberIndexes();
            for (int j = 0; j < holder[i].getMemberIndexes().length; j++) {
                int index = assignmentHolder[i][j];
                ensembleAssignments[index] = i;
            }
        }
        members = ensembleAssignments;
        return ensembleAssignments;
    }

    int[][] parseMembership(int[] members) {
        /*
        This Processes the EnsembleAssignments into something more useful.
         */
        float[] unique = new float[1];
        float[] counter = new float[1];
        unique[0] = members[0]; //Prime with the first number...
        counter[0] = 1;         //and increment the counter accordingly
        for (int i = 1; i < members.length; i++) { //Loop through the Membership List
            int testcounter = 0; //Use this to check against unique length
            for (int j = 0; j < unique.length; j++) { //Loop through the List of Clusters Already Found
                if (unique[j] == members[i]) { //If its an existing cluster increment the counter
                    counter[j]++;
                } else if (unique[j] != members[i]) {
                    testcounter++;
                }
            }
            if (testcounter == unique.length) {
                //If there was no instance of the unique list matching the memberlist, add a new item
                unique = expand(unique, unique.length + 1);
                counter = expand(counter, counter.length + 1);
                unique[unique.length - 1] = members[i];
                counter[counter.length - 1] = 1;
            }
        }
        //So now we have a list of unique cluster numbers and a counter for each one.
        //Here we just process them for export.
        int[][] out = new int[2][unique.length];
        for (int i = 0; i < unique.length; i++) {
            out[0][i] = int
            (unique[i])
            ;
      out[1][i] = int
            (counter[i])

          ;

    }

     clusters
         =  out;
    return out;
  }


  int[][] makeFileIndices(int[] members) {
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

    String[] getMutualInfoWords(int[][] clusters, int clusterNumber) {
        /*
        This method only gets one set of Mutual Information words at a time.
         This allows us to split the task up over many frames.
         */

        String[] out = new String[wordDocumentMatrix.columnCount]; //The empty array for the output

        //Term Document Matrix for the Documents Within the Cluster Only
        int[] docList = subset(fileIndexes[clusterNumber], 0, int
        (clusters[1][clusterNumber])

            ); //We know the list is as long as the count of documents in the given cluster number

    float[][] docTermIn = new float[docList.length][wordDocumentMatrix.columnCount];
    for (int i=0; i < docList.length; i++) {
            docTermIn[i] = wordDocumentMatrix.getDataArray(docList[i]);
            //Pull the Document Term Matrix for those documents
        }

        //Term Document Matrix for those not in the cluster (Create a not-on-the-list list)

        int[] notdocList = new int[members.length - docList.length];
        int count = 0;
        for (int i = 0; i < members.length; i++) {
            if (clusters[0][clusterNumber] != members[i]) {
                notdocList[count] = i;
                count++;
            }
        }

        float[][] docTermOut = new float[notdocList.length][wordDocumentMatrix.columnCount];
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
