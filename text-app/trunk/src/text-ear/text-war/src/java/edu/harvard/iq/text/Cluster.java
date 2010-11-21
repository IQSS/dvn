

package edu.harvard.iq.text;


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
     * @param memberIndexes indices of the member coordinates.
     * @param center the cluster center.
     */
    public Cluster(int[] memberIndexes, double[] center) {
        mMemberIndexes = memberIndexes;
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


}
