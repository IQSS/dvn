/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.text;

/**
 *
 * @author ekraffmiller
 */
public class ClusterInfo {
    int clusterId;
    int clusterCount;

    public ClusterInfo(int clusterId, int clusterCount) {
        this.clusterId = clusterId;
        this.clusterCount = clusterCount;
    }
    public int getClusterCount() {
        return clusterCount;
    }

    public void setClusterCount(int clusterCount) {
        this.clusterCount = clusterCount;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public String toString() {
        return "ClusterInfo clusterId:"+ clusterId+" clusterCount:"+clusterCount;
    }
    

}
