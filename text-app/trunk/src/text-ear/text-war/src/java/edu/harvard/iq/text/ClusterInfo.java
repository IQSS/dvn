

package edu.harvard.iq.text;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author ekraffmiller
 */
public class ClusterInfo implements Comparable<ClusterInfo> {

    private int clusterNumber;
    private float clusterPercent;
    private ArrayList<Document> documentList = new ArrayList<Document>();
    private ArrayList<WordValue> wordList = new ArrayList<WordValue>();
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<Document> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(ArrayList<Document> documentList) {
        this.documentList = documentList;
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

    public ClusterInfo(int[] matrixIndexes, DocumentSet documentSet ) {
        for (int i = 0; i < matrixIndexes.length; i++) {
            this.documentList.add(documentSet.getDocumentByIndex(matrixIndexes[i]));

            

        }
    }

    public String getTopWords() {
        String ret = "";
        for (int i = 0; i < 9; i++) {
            ret += wordList.get(i).title + ", ";
        }
        ret += wordList.get(9).title;
        return ret;
    }

    public int getClusterCount() {
        return this.documentList.size();
    }

    public int getClusterNumber() {
        return this.clusterNumber;
    }

    public float getClusterPercent() {
        return clusterPercent;
    }

    public void setClusterPercent(int numDocuments) {
        this.clusterPercent = (float) getClusterCount() / numDocuments;
    }

    public String getClusterPercentStr() {
        return MessageFormat.format("{0,number,#.##%}", clusterPercent);

    }

    public String toString() {
        String s = "ClusterInfo clusterNumber:" + clusterNumber + "\n clusterCount:" + getClusterCount() + "\nPercent:" + (float) clusterPercent + "+\n"
               + "\n" + "mutualInfoWords: ";
        for (int i = 0; i < 10; i++) {
            s += wordList.get(i) + ", ";
        }
        return s;
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


   
   
   

}
