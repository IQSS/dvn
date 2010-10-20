package edu.harvard.iq.text;


import javax.annotation.PostConstruct;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author ekraffmiller
 */
public class ClusterViewPage {


    /** Creates a new instance of ClusterViewPage */
    public ClusterViewPage() {
        System.out.println("creating ClusterViewPage");

    }

    @PostConstruct
    public void init() {
        initConvexHullFile(setId);
    }
    public String setId;

    /**
     * Get the value of setId
     *
     * @return the value of setId
     */
    public String getSetId() {
        return setId;
    }

    /**
     * Set the value of setId
     *
     * @param setId new value of setId
     */
    public void setSetId(String setId) {
        this.setId = setId;
    }

    private void initConvexHullFile(String setId) {
        System.out.println("initializingConvexHull: SetId is "+setId+"!");
        // Read methodpoints.text:

        // get JVM parameter for root directory

        // look for subfolder matching setId

        // if no subfolder found throw text exception

        // look for MethodPoints.txt

        // if not found throw text exception

        // get methodpoints coordinates and send them to convex hull calculator

        //  convert convex hull coordinates to an OpenLayers Polygon document

        // Save this file in subfolder
    }
}
