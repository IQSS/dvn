/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author ekraffmiller
 */
public class ClusterInfo implements Comparable<ClusterInfo> {
    private int clusterNumber;
    private float clusterPercent;
    private ArrayList<Integer> fileIndices = new ArrayList<Integer>(); 
    private ArrayList<WordValue> wordList = new ArrayList<WordValue>();

    public ArrayList<Integer> getFileIndices() {
        return fileIndices;
    }

    public void setFileIndices(ArrayList<Integer> fileIndices) {
        this.fileIndices = fileIndices;
    }

    public ArrayList<WordValue> getWordList() {
        return wordList;
    }

    public void setWordList(ArrayList<WordValue> wordList) {
        this.wordList = wordList;
    }



   
    public ClusterInfo(int clusterNumber) {
        this.clusterNumber = clusterNumber;
    }

    public int getClusterCount() {
        return  fileIndices.size();
    }

    

   public int getClusterNumber() {
       return this.clusterNumber;
   }

    public float getClusterPercent() {
        return clusterPercent;
    }

    public void setClusterPercent(int numDocuments) {
        this.clusterPercent = (float)getClusterCount()/numDocuments;
    }



    public String toString() {
        return "ClusterInfo clusterNumber:"+ clusterNumber+"\n clusterCount:"+getClusterCount() + "\nPercent:"+(float)clusterPercent+"+\n"
                +"fileIndices: " + fileIndices + "\n"
                +"mutualInfoWords: "+wordList+"\n\n";
    }

    /*
     *  Sorts the objects in descending order, by cluster size
     */
    public int compareTo(ClusterInfo ci) {
        if (this.getClusterCount() < ci.getClusterCount()) {
            return 1;
        } else if (this.getClusterCount() > ci.getClusterCount()) {
            return -1;
        } else {
            return 0;
        }
    }

    public void calculateWordList(int[][] wordDocumentMatrix, ArrayList<String> words ) {

        // 1. Split Matrix into two smaller matrices,
        //    for documents in cluster and documents not in cluster

        ArrayList<int[]> inCluster = new ArrayList<int[]>();
        ArrayList<int[]> outCluster = new ArrayList<int[]>();

        for (int i=0; i< wordDocumentMatrix.length; i++ ) {
            if (fileIndices.contains(i)) {
                inCluster.add(wordDocumentMatrix[i]);
            } else {
                outCluster.add(wordDocumentMatrix[i]);
            }
        }

        // 2. For each smaller matrix, get the mean of each column (word count)

        ArrayList<Float> inMean = getMean(inCluster);
        ArrayList<Float> outMean = getMean(outCluster);

        // 3. create a word list array, which contains the word and the difference in the means
        wordList = new ArrayList<WordValue>();
        for (int i=0;i<inMean.size(); i++ ) {
            wordList.add(new WordValue( words.get(i), inMean.get(i)-outMean.get(i)));
        }

        // 4. sort this array by difference.
        Collections.sort(wordList);



    }

    /*
     * For this matrix,return an Array whose values are the mean of each column
     */
    private ArrayList<Float> getMean(ArrayList<int []> docMatrix) {
        ArrayList<Float> mean = new ArrayList<Float>();
        for  (int col=0; col< docMatrix.get(0).length; col++) {
            float sum=0;
            for (int row = 0; row < docMatrix.size(); row++) {
                sum += docMatrix.get(row)[col];
            }
            mean.add(sum/docMatrix.size());
        }
        return mean;
    }

}
