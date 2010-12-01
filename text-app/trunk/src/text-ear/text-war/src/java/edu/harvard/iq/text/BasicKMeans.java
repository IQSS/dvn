
package edu.harvard.iq.text;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author ekraffmiller
 */
public class BasicKMeans {
  // Temporary clusters used during the clustering process.  Converted to
    // an array of the simpler class Cluster at the conclusion.
    private ProtoCluster[] mProtoClusters;

    // Cache of coordinate-to-cluster distances. Number of entries =
    // number of clusters X number of coordinates.
    private double[][] mDistanceCache;

    // Used in makeAssignments() to figure out how many moves are made
    // during each iteration -- the cluster assignment for coordinate n is
    // found in mClusterAssignments[n] where the N coordinates are numbered
    // 0 ... (N-1)
    private int[] mClusterAssignments;

    // 2D array holding the coordinates to be clustered.
    public double[][] mCoordinates;
    // The desired number of clusters and maximum number
    // of iterations.
    private int mK, mMaxIterations;
    // Seed for the random number generator used to select
    // coordinates for the initial cluster centers.
    private long mRandomSeed;

    // An array of Cluster objects: the output of k-means.
    private Cluster[] mClusters;

    /**
     * Constructor
     *
     * @param coordinates two-dimensional array containing the coordinates to be clustered.
     * @param k  the number of desired clusters.
     * @param maxIterations the maximum number of clustering iterations.
     * @param randomSeed seed used with the random number generator.
     */
    public BasicKMeans(double[][] coordinates, int k, int maxIterations,
            long randomSeed) {
        mCoordinates = coordinates;
        // Can't have more clusters than coordinates.
        mK = Math.min(k, mCoordinates.length);
        mMaxIterations = maxIterations;
        mRandomSeed = randomSeed;
    }


    /**
     * Get the clusters computed by the algorithm.  This method should
     * not be called until clustering has completed successfully.
     *
     * @return an array of Cluster objects.
     */
    public Cluster[] getClusters() {
        return mClusters;
    }

    /**
     * Run the clustering algorithm.
     */
    public void run() {

        try {

            // Note the start time.
            long startTime = System.currentTimeMillis();

            // Randomly initialize the cluster centers creating the
            // array mProtoClusters.
            initCenters();

            // Perform the initial computation of distances.
            computeDistances();

            // Make the initial cluster assignments.
            makeAssignments();

            // Number of moves in the iteration and the iteration counter.
            int moves = 0;
            int it = 0;


            // Main Loop:
            //
            // Two stopping criteria:
            // - no moves in makeAssignments
            //   (moves == 0)
            // OR
            // - the maximum number of iterations has been reached
            //   (it == mMaxIterations)
            //
            do {

                // Compute the centers of the clusters that need updating.
                computeCenters();

                // Compute the stored distances between the updated clusters and the
                // coordinates.
                computeDistances();

                // Make this iteration's assignments.
                moves = makeAssignments();

                it++;

            } while (moves > 0 && it < mMaxIterations);

            // Transform the array of ProtoClusters to an array
            // of the simpler class Cluster.

            mClusters = generateFinalClusters();

            long executionTime = System.currentTimeMillis() - startTime;

        }  finally {

            // Clean up temporary data structures used during the algorithm.
            cleanup();

        }
    }

    /**
     * Randomly select coordinates to be the initial cluster centers.
     */
    private void initCenters() {

        Random random = new Random(mRandomSeed);

        int coordCount = mCoordinates.length;

        // The array mClusterAssignments is used only to keep track of the cluster
        // membership for each coordinate.  The method makeAssignments() uses it
        // to keep track of the number of moves.
        if (mClusterAssignments == null) {
            mClusterAssignments = new int[coordCount];
            // Initialize to -1 to indicate that they haven't been assigned yet.
            Arrays.fill(mClusterAssignments, -1);
        }

        // Place the coordinate indices into an array and shuffle it.
        int[] indices = new int[coordCount];
        for (int i = 0; i < coordCount; i++) {
            indices[i] = i;
        }
        for (int i = 0, m = coordCount; m > 0; i++, m--) {
            int j = i + random.nextInt(m);
            if (i != j) {
                // Swap the indices.
                indices[i] ^= indices[j];
                indices[j] ^= indices[i];
                indices[i] ^= indices[j];
            }
        }

        mProtoClusters = new ProtoCluster[mK];
        for (int i=0; i<mK; i++) {
            int coordIndex = indices[i];
            mProtoClusters[i] = new ProtoCluster(mCoordinates[coordIndex], coordIndex);
            mClusterAssignments[indices[i]] = i;
        }
    }

    /**
     * Recompute the centers of the protoclusters with
     * update flags set to true.
     */
    private void computeCenters() {

        int numClusters = mProtoClusters.length;

        // Sets the update flags of the protoclusters that haven't been deleted and
        // whose memberships have changed in the iteration just completed.
        //
        for (int c = 0; c < numClusters; c++) {
            ProtoCluster cluster = mProtoClusters[c];
            if (cluster.getConsiderForAssignment()) {
                if (!cluster.isEmpty()) {
                    // This sets the protocluster's update flag to
                    // true only if its membership changed in last call
                    // to makeAssignments().
                    cluster.setUpdateFlag();
                    // If the update flag was set, update the center.
                    if (cluster.needsUpdate()) {
                        cluster.updateCenter(mCoordinates);
                    }
                } else {
                    // When a cluster loses all of its members, it
                    // falls out of contention.  So it is possible for
                    // k-means to return fewer than k clusters.
                    cluster.setConsiderForAssignment(false);
                }
            }
        }
    }

    /**
     * Compute distances between coodinates and cluster centers,
     * storing them in the distance cache.  Only distances that
     * need to be computed are computed.  This is determined by
     * distance update flags in the protocluster objects.
     */
    private void computeDistances() /*throws InsufficientMemoryException*/ {

        int numCoords = mCoordinates.length;
        int numClusters = mProtoClusters.length;

        if (mDistanceCache == null) {
            // Explicit garbage collection to reduce likelihood of insufficient
            // memory.
            System.gc();
            // Ensure there is enough memory available for the distances.
            // Throw an exception if not.
            long memRequired = 8L * numCoords * numClusters;
           // if (Runtime.getRuntime().freeMemory() < memRequired) {
           //     throw new InsufficientMemoryException();
           // }
            // Instantiate an array to hold the distances between coordinates
            // and cluster centers
            mDistanceCache = new double[numCoords][numClusters];
        }

        for (int coord=0; coord < numCoords; coord++) {
            // Update the distances between the coordinate and all
            // clusters currently in contention with update flags set.
            for (int clust=0; clust<numClusters; clust++) {
                ProtoCluster cluster = mProtoClusters[clust];
                if (cluster.getConsiderForAssignment() && cluster.needsUpdate()) {
                    mDistanceCache[coord][clust] =
                        distance(mCoordinates[coord], cluster.getCenter());
                }
            }
        }

    }

    /**
     * Assign each coordinate to the nearest cluster.  Called once
     * per iteration.  Returns the number of coordinates that have
     * changed their cluster membership.
     */
    private int makeAssignments() {

        int moves = 0;
        int coordCount = mCoordinates.length;

        // Checkpoint the clusters, so we'll be able to tell
        // which ones have changed after all the assignments have been
        // made.
        int numClusters = mProtoClusters.length;
        for (int c = 0; c < numClusters; c++) {
            if (mProtoClusters[c].getConsiderForAssignment()) {
                mProtoClusters[c].checkPoint();
            }
        }

        // Now do the assignments.
        for (int i = 0; i < coordCount; i++) {
            int c = nearestCluster(i);
            mProtoClusters[c].add(i);
            if (mClusterAssignments[i] != c) {
                mClusterAssignments[i] = c;
                moves++;
            }
        }

        return moves;
    }

    /**
     * Find the nearest cluster to the coordinate identified by
     * the specified index.
     */
    private int nearestCluster(int ndx) {
        int nearest = -1;
        double min = Double.MAX_VALUE;
        int numClusters = mProtoClusters.length;
        for (int c = 0; c < numClusters; c++) {
            if (mProtoClusters[c].getConsiderForAssignment()) {
                double d = mDistanceCache[ndx][c];
                if (d < min) {
                    min = d;
                    nearest = c;
                }
            }
        }
        return nearest;
    }

    /**
     * Compute the euclidean distance between the two arguments.
     */
    private double distance(double[] coord, double[] center) {
        int len = coord.length;
        double sumSquared = 0.0;
        for (int i=0; i<len; i++) {
            double v = coord[i] - center[i];
            sumSquared += v*v;
        }
        return Math.sqrt(sumSquared);
    }

    /**
     * Generate an array of Cluster objects from mProtoClusters.
     *
     * @return array of Cluster object references.
     */
    private Cluster[] generateFinalClusters() {

        int numClusters = mProtoClusters.length;

        // Convert the proto-clusters to the final Clusters.
        //
        // - accumulate in a list.

        Cluster[] clusterArray = new Cluster[numClusters];
        int counter = 0;
        for (int c = 0; c < numClusters; c++) {
            ProtoCluster pcluster = mProtoClusters[c];
            if (!pcluster.isEmpty()) {
                // from the mDistanceCache matrix, get the column of values that 
                // contains the distances for this cluster.
                double[] clusterDistances = new double[mDistanceCache.length];
                for (int i=0;i<clusterDistances.length;i++) {
                    clusterDistances[i] = mDistanceCache[i][c];
                }
                Cluster cluster = new Cluster(pcluster.getMembership(), pcluster.getCenter(), clusterDistances);
                clusterArray[counter] = cluster;
            }
            counter++;
        }

        clusterArray = (Cluster[]) Arrays.copyOfRange(clusterArray, 0, counter);
        return clusterArray;
    }

    /**
     * Clean up items used by the clustering algorithm that are no longer needed.
     */
    private void cleanup() {
        mProtoClusters = null;
        mDistanceCache = null;
        mClusterAssignments = null;
    }

    /**
     * Cluster class used temporarily during clustering.  Upon completion,
     * the array of ProtoClusters is transformed into an array of
     * Clusters.
     */
    private class ProtoCluster {

        // The previous iteration's cluster membership and
        // the current iteration's membership.  Compared to see if the
        // cluster has changed during the last iteration.
        private int[] mPreviousMembership;
        private int[] mCurrentMembership;
        private int mCurrentSize;

        // The cluster center.
        private double[] mCenter;

        // Born true, so the first call to updateDistances() will set all the
        // distances.
        private boolean mUpdateFlag = true;
        // Whether or not this cluster takes part in the operations.
        private boolean mConsiderForAssignment = true;

        /**
         * Constructor
         *
         * @param center  the initial cluster center.
         * @param coordIndex  the initial member.
         */
        ProtoCluster(double[] center, int coordIndex) {
            mCenter = (double[]) center.clone();
            // No previous membership.
            mPreviousMembership = new int[0];
            // Provide space for 10 members to be added initially.
            mCurrentMembership = new int[10];
            mCurrentSize = 0;
            add(coordIndex);
        }

        /**
         * Get the members of this protocluster.
         *
         * @return an array of coordinate indices.
         */
        int[] getMembership() {
            trimCurrentMembership();
            return mCurrentMembership;
        }

        /**
         * Get the protocluster's center.
         *
         * @return
         */
        double[] getCenter() {
            return mCenter;
        }

        /**
         * Reduces the length of the array of current members to
         * the number of members.
         */
        void trimCurrentMembership() {
            if (mCurrentMembership.length > mCurrentSize) {
                int[] temp = new int[mCurrentSize];
                System.arraycopy(mCurrentMembership, 0, temp, 0, mCurrentSize);
                mCurrentMembership = temp;
            }
        }

        /**
         * Add a coordinate to the protocluster.
         *
         * @param ndx index of the coordinate to be added.
         */
        void add(int ndx) {
            // Ensure there's space to add the new member.
            if (mCurrentSize == mCurrentMembership.length) {
                // If not, double the size of mCurrentMembership.
                int newCapacity = Math.max(10, 2*mCurrentMembership.length);
                int[] temp = new int[newCapacity];
                System.arraycopy(mCurrentMembership, 0, temp, 0, mCurrentSize);
                mCurrentMembership = temp;
            }
            // Add the index.
            mCurrentMembership[mCurrentSize++] = ndx;
        }

        /**
         * Does the protocluster contain any members?
         *
         * @return true if the cluster is empty.
         */
        boolean isEmpty() {
            return mCurrentSize == 0;
        }

        /**
         * Compares the previous and the current membership.
         * Sets the update flag to true if the membership
         * changed in the previous call to makeAssignments().
         */
        void setUpdateFlag() {
            // Trim the current membership array length down to the
            // number of members.
            trimCurrentMembership();
            mUpdateFlag = false;
            if (mPreviousMembership.length == mCurrentSize) {
                for (int i=0; i<mCurrentSize; i++) {
                    if (mPreviousMembership[i] != mCurrentMembership[i]) {
                        mUpdateFlag = true;
                        break;
                    }
                }
            } else { // Number of members has changed.
                mUpdateFlag = true;
            }
        }

        /**
         * Clears the current membership after copying it to the
         * previous membership.
         */
        void checkPoint() {
            mPreviousMembership = mCurrentMembership;
            mCurrentMembership = new int[10];
            mCurrentSize = 0;
        }

        /**
         * Is this protocluster currently in contention?
         *
         * @return true if this cluster is still in the running.
         */
        boolean getConsiderForAssignment() {
            return mConsiderForAssignment;
        }

        /**
         * Set the flag to indicate that this protocluster is
         * in or out of contention.
         *
         * @param b
         */
        void setConsiderForAssignment(boolean b) {
            mConsiderForAssignment = b;
        }

        /**
         * Get the value of the update flag.  This value is
         * used to determine whether to update the cluster center and
         * whether to recompute distances to the cluster.
         *
         * @return the value of the update flag.
         */
        boolean needsUpdate() {
            return mUpdateFlag;
        }

        /**
         * Update the cluster center.
         *
         * @param coordinates the array of coordinates.
         */
        void updateCenter(double[][] coordinates) {
            Arrays.fill(mCenter, 0.0);
            if (mCurrentSize > 0) {
                for (int i=0; i<mCurrentSize; i++) {
                    double[] coord = coordinates[mCurrentMembership[i]];
                    for (int j=0; j<coord.length; j++) {
                        mCenter[j] += coord[j];
                    }
                }
                for (int i=0; i<mCenter.length; i++) {
                    mCenter[i] /= mCurrentSize;
                }
            }
        }
    }

}


