

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
    private ArrayList<Integer> fileIndices = new ArrayList<Integer>();
    private ArrayList<DocInfo> docInfoList = new ArrayList<DocInfo>();
    private ArrayList<WordValue> wordList = new ArrayList<WordValue>();
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

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

    public ClusterInfo(Cluster cluster, ArrayList<String> completeDocIdList) {
        for (int i = 0; i < cluster.getMemberIndexes().length; i++) {
            this.fileIndices.add(cluster.getMemberIndexes()[i]);
            String docId = completeDocIdList.get(cluster.getMemberIndexes()[i]);
            docInfoList.add(new DocInfo(docId, docInfoList.size()));
        }
    }

    public String getTopWords() {
        String ret = "";
        for (int i = 0; i < 10; i++) {
            ret += wordList.get(i).title + " ";
        }
        return ret;
    }

    public int getClusterCount() {
        return fileIndices.size();
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
                + "fileIndices: " + fileIndices + "\n" + "mutualInfoWords: ";
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

    public void calculateWordList(int[][] wordDocumentMatrix, ArrayList<String> words) {

        // 1. Split Matrix into two smaller matrices,
        //    for documents in cluster and documents not in cluster

        ArrayList<int[]> inCluster = new ArrayList<int[]>();
        ArrayList<int[]> outCluster = new ArrayList<int[]>();

        for (int i = 0; i < wordDocumentMatrix.length; i++) {
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
        for (int i = 0; i < inMean.size(); i++) {
            wordList.add(new WordValue(words.get(i), inMean.get(i) - outMean.get(i)));
        }

        // 4. sort this array by difference.
        Collections.sort(wordList);



    }

    /*
     * For this matrix,return an Array whose values are the mean of each column
     */
    private ArrayList<Float> getMean(ArrayList<int[]> docMatrix) {
        ArrayList<Float> mean = new ArrayList<Float>();
        for (int col = 0; col < docMatrix.get(0).length; col++) {
            float sum = 0;
            for (int row = 0; row < docMatrix.size(); row++) {
                sum += docMatrix.get(row)[col];
            }
            mean.add(sum / docMatrix.size());
        }
        return mean;
    }

    /**
     *  Silly class that we need in order to print the row index
     *  in the document list view tab
     */
    public class DocInfo {

        String docId;
        int index;

        public DocInfo(String docId, int index) {
            this.docId = docId;
            this.index = index;
        }

        public String getDocId() {
            return docId;
        }

        public void setDocId(String docId) {
            this.docId = docId;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public ArrayList<DocInfo> getDocInfoList() {
        return this.docInfoList;

    }


}
