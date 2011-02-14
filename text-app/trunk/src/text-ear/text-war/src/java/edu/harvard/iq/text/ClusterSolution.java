/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;

import com.vividsolutions.jts.geom.Coordinate;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;


/**
 *
 * @author ekraffmiller
 */
public class ClusterSolution {
    private static final Logger logger = Logger.getLogger(ClusterSolution.class.getCanonicalName());
    private static final String CLUSTER_LABEL_DELIMITER = ";"; // For generating/parsing cluster solution URL
    private static final double TWO_PI = Math.PI*2;

    // the document set we are analyzing
    private DocumentSet documentSet;

    //Input parameters for this cluster solution
    private double candidateXPoint; //Mapped into Methods Space
    private double candidateYPoint;
    // Do we need to calculate the discoverable numClusters?
    private Boolean discoverable = Boolean.FALSE;
    // The number of clusters (either calculated, or provided in constructor)
    private int numClusters;

    // This is user's description of the cluster solution
    private String label;

    // Used to identify saved solutions (right now just corresponds to index, but may
    // be a database id later)
    private Integer id;

    // These two variables depend on the point, but not the number of clusters
    double[] weightsArray;
    double[][] simMatrix;

    // The solution organized as a list of ClusterInfo objects
    ArrayList<ClusterInfo> clusterInfoList = new ArrayList<ClusterInfo>();

    

    public Boolean getDiscoverable() {
       
        return discoverable;
    }
    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    /**
     * Constructor for calculating a solution from scratch, with a given number
     * of clusters
     * @param documentSet - pre-processed document data
     * @param candidateXPoint - user click x
     * @param candidateYPoint - user click y
     * @param requestedClusters - the desired number of clusters
     */
    ClusterSolution(DocumentSet documentSet, double candidateXPoint, double candidateYPoint, int requestedClusters) {
       init(documentSet, candidateXPoint,candidateYPoint,requestedClusters,null,null);
    }
    /**
     * Constructor of calculating solution from scratch, with a discoverable clusterNumber
     * @param documentSet
     * @param candidateXPoint
     * @param candidateYPoint
     */
    ClusterSolution(DocumentSet documentSet, double candidateXPoint, double candidateYPoint) {
      init(documentSet,candidateXPoint,candidateYPoint,null,null,null);
    }

   /**
     * Constructor that uses an existing solution, and recalculates
     * based on the existing solution point, and a different number of clusters
     *
     * @param existingSolution - the previously calculated solution
     * @param requestedClusters - the desired number of clusters (if = 0, then calculate the discoverable cluster Number)
     */
    ClusterSolution(ClusterSolution existingSolution, int requestedClusters) {
        init(existingSolution.documentSet, existingSolution.candidateXPoint, existingSolution.candidateYPoint, requestedClusters,existingSolution.weightsArray,existingSolution.simMatrix);
    }

/**
     * Constructor that uses an existing solution, and recalculates
     * based on the existing solution point, and a discoverable number of clusters
     *
     * @param existingSolution - the previously calculated solution
     */
    ClusterSolution(ClusterSolution existingSolution) {
        init(existingSolution.documentSet, existingSolution.candidateXPoint, existingSolution.candidateYPoint, null,existingSolution.weightsArray,existingSolution.simMatrix);
    }
    private void init(DocumentSet documentSet, double candidateXPoint, double candidateYPoint,  Integer requestedClusters,double[]weightArray, double simMatrix[][]) {
        this.documentSet = documentSet;
    
        this.candidateXPoint = candidateXPoint;
        this.candidateYPoint = candidateYPoint;

        if (weightsArray==null) {
            this.weightsArray = makeWeightsArray();
        } else {
            this.weightsArray = weightArray;
        }
        // If necessary, use the weightsArray to calculate the discoverable numClusters
        if (requestedClusters==null) {
            discoverable = Boolean.TRUE;
            numClusters = this.getDiscoverableClusterNum();
        } else {
            numClusters = requestedClusters;
        }
        if (simMatrix==null) {
            this.simMatrix = fillSimMatrix(weightsArray);
        } else {
            this.simMatrix = simMatrix;
        }
        
        doClusterCalculations();
    }

    public void initClusterLabels(String labels) {
        if (labels!=null) {
            String[] arr = labels.split(CLUSTER_LABEL_DELIMITER,-1);

            for (int i=0;i<clusterInfoList.size() && i< arr.length; i++) {
                if (!arr[i].isEmpty()){
                    clusterInfoList.get(i).setLabel(arr[i]);
                }
                
            }
        }
    }
    public String getEncodedLabel() {
        if (label!=null) {
            try {
                return URLEncoder.encode(label, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                        throw new ClusterException(e.getMessage());
            }
        } else {
            return label;
        }
    }

    public String getClusterLabels() {
        String str = "";
        for (ClusterInfo ci : clusterInfoList) {
                // str+=(ci.getLabel());
                    if (ci.getLabel()!=null) {  
                    try {

                    str+=URLEncoder.encode(ci.getLabel(),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new ClusterException(e.getMessage());
                    }
                    }
            str+= this.CLUSTER_LABEL_DELIMITER;
        }
        return str;
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
                    str+=(ci.getLabel());
                //    try {
                //    str+=URLEncoder.encode(ci.getLabel(),"UTF-8");
                 //   } catch (UnsupportedEncodingException e) {
                 //       throw new ClusterException(e.getMessage());
                 //   }
                    count++;
                }
            }

         }
        return str;
    }
    
    public int getNumClusters() {
        return numClusters;
    }

    // This property is read only, but it must have a setter
    // to be used in JSF.  need to investigate a better option.
    public void setNumClusters(int numClusters) {
         
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

    //
    // These methods are just used to display the coordinates in the cluster
    // info sections
    public double getFormatX() {
        DecimalFormat twoDForm = new DecimalFormat("#.#####");
		return Double.valueOf(twoDForm.format(candidateXPoint));

    }
    // we need a setter in order for JSF to allow this to be used as
    // a property
    public void setFormatX() {
       
    }

    public double getFormatY() {
        DecimalFormat twoDForm = new DecimalFormat("#.#####");
		return Double.valueOf(twoDForm.format(candidateYPoint));

    }
    // we need a setter in order for JSF to allow this to be used as
    // property
    public void setFormatY() {

    }

    public double getY() {
        return this.candidateYPoint;
    }

    public String getFormatClusterNum() {
        String ret = ""+numClusters;
        if (discoverable) {
            ret+="*";
        }
        return ret;
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

    
    public String toString() {
        String str = "Cluster Solution: ";
        
        for (ClusterInfo ci : this.clusterInfoList) {
            str+= ci.toString();
        }
        return str;
    }
   

    private void doClusterCalculations() {
        
        Cluster[] clusters = doKMeansCalc(simMatrix);

        createClusterInfoList(clusters);

        Collections.sort(clusterInfoList);

        logger.fine("finished calculating, x="+this.candidateXPoint+", y="+this.candidateYPoint+" clusterInfo:" + clusterInfoList);
    }

    private void createClusterInfoList(Cluster[] clusters ) {

        
        for (int i=0; i< clusters.length; i++ ) {
                if (clusters[i]!=null) {
                clusterInfoList.add(new ClusterInfo(clusters[i],documentSet.getDocIdList(), documentSet.getTitleList()));
            }
        }

        // set cluster percent & figure out the word list for each cluster
        for (ClusterInfo ci : clusterInfoList) {
            ci.setClusterPercent(documentSet.getWordDocumentMatrix().length);
            ci.calculateWordList(documentSet.getWordDocumentMatrix(), documentSet.getWords());
        }
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
        int smallestDistanceIndex = 0;
        for (int i = 0; i < distanceArray.length; i++) {
            if (distanceArray[i] < distanceArray[smallestDistanceIndex]) {
                smallestDistanceIndex=i;
            }
            //Formula for the Normal PDF mu = 0, sigma = 0.25,
            //this is equivalent to the R code: dnorm(i, sd=0.25)

            // Original line from processing file:
            // weights[i] = 1 / (sqrt(TWO_PI) * .25) * exp(-(sq(distanceArray[i] - 0) / (2 * sq(.25))));
            // TODO:  why  subtract 0 from distance array?

            // original calculation:
   //            weights[i] = 1 / (Math.sqrt(TWO_PI) * .25) * Math.exp(-(Math.pow((distanceArray[i] - 0),2) / (2 * Math.pow(.25,2))));
           
            // New calculation from Brandon, using the Epanechnikov Kernel to normalize the weights array
             if ((distanceArray[i]/2) <= 1) {
               weights[i] = .75 * (1 - Math.pow((distanceArray[i]/2),2));
           } else {
               weights[i] = 0;
           }
           logger.fine("distanceArray["+i+"]="+distanceArray[i]+"weights[i]"+weights[i]);

       
            sumWeights = weights[i] + sumWeights;
        }
       //  if distanceArray has no value <= 1, the smallest distance gets a weight equal to 1.
        if (sumWeights == 0.0) {
            sumWeights = 1;
            weights[smallestDistanceIndex] = 1;

        }
        
        String wts = "weights array: ";
        for (int i = 0; i < weights.length; i++) {  //Divide by the sum
            weights[i] = weights[i] / sumWeights;
            wts += weights[i] + ",";

        }
        logger.fine(wts);

        return weights;
    }

    private int getDiscoverableClusterNum()
    {
        double discov = 0;
        for (int i=0; i<weightsArray.length;i++) {
            discov += (weightsArray[i] * documentSet.getMethodPoints()[i].numberOfClusters);
        }
        return (int)Math.round(discov);
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
        logger.fine("simMatrix is"+ simMatrix);
        return simMatrix;
    }


    /*
     * Takes the similarity matrix from "Fill Similarity Matrix"
     *    and the number of clusters, then runs K-Means and returns the assignments.
     *
     */
    private Cluster[] doKMeansCalc(double[][] simMatrix) {
  

       
        long kmeansRandomSeed = (long) 12345;
        logger.fine("Calling BasicKMeans");
        logger.fine("numClusters="+numClusters);
        logger.fine("coordinates="+this.candidateXPoint+","+this.candidateYPoint);


   
        
        BasicKMeans kmeans = new BasicKMeans(simMatrix, numClusters, 20, kmeansRandomSeed);

        kmeans.run();
       
        return kmeans.getClusters();
     
    }

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
/*
    private void malletKMeans(double[][] simMatrix, int numClusters) {
        InstanceList instanceList = new InstanceList(new Alphabet(),new Alphabet());
        for (int i=0; i< simMatrix.length; i++) {
            SparseVector sp = new SparseVector(simMatrix[i]);  // this is dense because we don't create the object with indices
            Instance instance = new Instance(sp, "target","name","source");

            instanceList.add(instance);
        }
        Metric metric = new Minkowski(1.0);

        KMeans kMeans = new KMeans(instanceList.getPipe(),numClusters,metric  );
        Clustering clustering = kMeans.cluster(instanceList);
        System.out.println("Mallet clustering result: " + clustering);
    }
 
 */
}
