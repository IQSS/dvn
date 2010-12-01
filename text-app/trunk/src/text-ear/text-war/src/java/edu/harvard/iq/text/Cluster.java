

package edu.harvard.iq.text;

import java.util.ArrayList;
import java.util.Collections;


/**
 * Class to represent a cluster of coordinates.
 * Adapted from Randall Scarberry
 */
public class Cluster {

    // Indices of the member coordinates.
    private int[] mMemberIndexes;
    // The cluster center.
    private double[] mCenter;

    /**
     * Constructor.
     *
     * @param memberIndexes indices of the member coordinates.  The indices are in ascending order
     *  based on the distance from the center of the cluster (examplar document first)
     * @param center the cluster center.
     */
    public Cluster(int[] memberIndexes, double[] center, double[] distances) {
        ArrayList<ClusteredDoc>  orderedDocs = getClusteredDocs(memberIndexes, distances);
        mMemberIndexes = new int[orderedDocs.size()];
        for (int i=0; i<mMemberIndexes.length; i++) {
            mMemberIndexes[i] = orderedDocs.get(i).index;
        }
        mCenter = center;
    }

    /**
     * Get the member indices.
     *
     * @return an array containing the indices of the member coordinates.
     */
    public int[] getMemberIndexes() {
        return mMemberIndexes;
    }

    /**
     * Get the cluster center.
     *
     * @return a reference to the cluster center array.
     */
    public double[] getCenter() {
        return mCenter;
    }

    private ArrayList<ClusteredDoc> getClusteredDocs(int[] memberIndexes, double[] distances) {

        ArrayList<ClusteredDoc> cdList = new ArrayList<ClusteredDoc>();
        for (int i=0;i<memberIndexes.length;i++) {
            cdList.add(new ClusteredDoc(memberIndexes[i],distances[memberIndexes[i]]));
        }
        Collections.sort(cdList);

        return cdList;
    }


    private class ClusteredDoc implements Comparable<ClusteredDoc> {
    int index;  // the index of the document in the document set list
    double distance;  // the distance between the document and the centroid of the cluster
    /*
     *  Sorts the objects in ascending order, by distance
     */
    public ClusteredDoc(int index, double distance) {
        this.index=index;
        this.distance=distance;
    }
    public int compareTo(ClusteredDoc cd) {
        if (this.distance < cd.distance) {
            return -1;
        } else if (this.distance > cd.distance) {
            return 1;
        } else {
            return 0;
        }
    }
}
}
