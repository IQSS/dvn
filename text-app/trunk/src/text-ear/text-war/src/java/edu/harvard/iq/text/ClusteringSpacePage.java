package edu.harvard.iq.text;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ekraffmiller
 */
public class ClusteringSpacePage {

    private String setId;
    private double xCoord;
    private double yCoord;
    private DocumentSet documentSet;
    private ClusterSolution clusterSolution;
    private int clusterNum;

    /** Creates a new instance of ClusterViewPage */
    public ClusteringSpacePage() {
        System.out.println("creating ClusterViewPage");

    }

    @PostConstruct
    public void init() {
         xCoord = 0;
         yCoord = 0;
         clusterNum = 5;
        documentSet = new DocumentSet(setId);
        clusterSolution = new ClusterSolution(documentSet, xCoord, yCoord, clusterNum);
    }

    public String doCluster() {
        clusterSolution = new ClusterSolution(documentSet, xCoord, yCoord, clusterNum);
        return "";
    }
    public String doChangeClusterNum() {
        clusterSolution = new ClusterSolution(documentSet, xCoord, yCoord, clusterNum);
        return "";
    }
    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public double getxCoord() {
        return xCoord;
    }

    public void setxCoord(double xCoord) {
        this.xCoord = xCoord;
    }

    public double getyCoord() {
        return yCoord;
    }

    public void setyCoord(double yCoord) {
        this.yCoord = yCoord;
    }

    public ClusterSolution getClusterSolution() {
        return clusterSolution;
    }

    public void setClusterSolution(ClusterSolution clusterSolution) {
        this.clusterSolution = clusterSolution;
    }

    public int getClusterNum() {
        return clusterNum;
    }

    public void setClusterNum(int newClusterNum) {
        if (newClusterNum!=clusterNum) {
            this.clusterNum = newClusterNum;
            doCluster();
        } 
    }

    
}
